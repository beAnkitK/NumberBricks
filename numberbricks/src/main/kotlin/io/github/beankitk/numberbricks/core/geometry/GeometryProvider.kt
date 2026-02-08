package io.github.beankitk.numberbricks.core.geometry

/**
 * A pluggable component that contributes geometry data for digit construction.
 *
 * Providers are composable units that each handle a specific aspect of digit geometry
 * (e.g., brick positions, sizes, angle, corner-radius, etc.). They produce deterministic,
 * digit-specific data and can depend on outputs from other providers.
 *
 * Providers declare structural constraints, validate compatibility with geometry
 * properties, and generate layout data that is assembled by the digit builder.
 *
 * **Important:** Declare each provider `key` in a `companion object` so other
 * providers can reference this without creating an instance. The property name
 * must be `key` for consistent discovery and access. for example,
 *
 * ```kotlin
 * class PositionProvider : GeometryProvider<Offset> {
 *     override val key = PositionProvider.key
 *
 *     companion object {
 *         val key = ProviderKey<Offset>("provider.position.base")
 *     }
 * }
 *
 * @param T The type of geometry data this provider produces. Eg. [Offset], [Size]
 *
 * @see AdaptiveProvider
 * @see FixedProvider
 */
interface GeometryProvider<T> {

    /**
     * Unique, type-safe identifier for this provider.
     */
    val key: ProviderKey<T>

    /**
     * Grid configuration this provider expects or produces.
     */
    val providerConfig: GridConfig

    /**
     * Whether this provider can adapt to varying geometry properties.
     *
     * - `true`: Provider adjusts to any grid configuration
     * - `false`: Provider requires exact grid dimensions specified in [providerConfig]
     */
    val isAdaptive: Boolean

    /**
     * Set of provider keys this provider depends on.
     *
     * Dependencies are guaranteed to execute before this provider. Use the
     * [ProviderStore] in [getProviderData] to access dependency outputs.
     */
    val dependsOn: Set<ProviderKey<*>>

    /**
     * Checks compatibility with the given geometry properties.
     *
     * Called during builder construction to validate this provider can work
     * with the configured geometry.
     *
     * @param properties The geometry properties to validate against
     * @return [Consent.Accept] if compatible, [Consent.Reject] with reason(optional) if not
     */
    fun matchesWith(properties: GeometryProps): Consent

    /**
     * Performs one-time initialization with the resolved geometry properties.
     *
     * Called once during builder construction after [matchesWith] succeeds.
     * Use this to cache computed values or prepare internal state.
     *
     * @param properties The geometry properties to attach to
     */
    fun attachWith(properties: GeometryProps)

    /**
     * Generates geometry data for a specific digit.
     *
     * Called for each digit construction. Should return a list of data elements,
     * one per brick as specified by `providerConfig.bricks`. The list size must be
     * `providerConfig.bricks` else [ProviderStore] would throw while storing.
     *
     * @param digit The digit to generate data for (0-9, or -1 for default state)
     * @param providerStore Store containing outputs from dependency providers
     * @return List of geometry data elements for this digit
     */
    fun getProviderData(
        digit: Int,
        providerStore: ProviderStore
    ): List<T>
}

/**
 * Base class for providers that work with any grid configuration.
 *
 * Adaptive providers automatically accept any geometry properties and update
 * their [providerConfig] during attachment. Subclasses implement [onAttachWith]
 * for custom initialization and must define [key], [dependsOn], and [getProviderData].
 * It may perform additional checks to define it validity by overriding [matchesWith].
 *
 * @param T The type of data this provider produces
 */
abstract class AdaptiveProvider<T>: GeometryProvider<T> {

    final override val isAdaptive = true

    final override var providerConfig = emptyGridConfig()
        private set

    /**
     * Checks compatibility with the given geometry properties.
     *
     * Called during builder construction to validate this provider can work
     * with the configured geometry. This always return [Consent.Accept], override to
     * perform more checks.
     *
     * @param properties The geometry properties to validate against
     * @return [Consent.Accept] if compatible, [Consent.Reject] with reason(optional) if not
     */
    override fun matchesWith(properties: GeometryProps): Consent = Consent.Accept

    final override fun attachWith(properties: GeometryProps) {
        providerConfig = properties.config
        onAttachWith(properties)
    }

    /**
     * Performs one-time initialization with the resolved geometry properties. The [providerConfig]
     * is already set when this method is invoked.
     *
     * Called once during builder construction after [matchesWith] succeeds.
     * Use this to cache computed values or prepare internal state.
     *
     * @param properties The geometry properties to attach to
     */
    protected abstract fun onAttachWith(properties: GeometryProps)
}

/**
 * Base class for providers that require exact grid dimensions.
 *
 * Fixed providers only work with geometry properties matching their [providerConfig].
 * Subclasses must set [providerConfig], implement [onAttachWith], and define [key],
 * [dependsOn], and [getProviderData].
 *
 * @param T The type of data this provider produces
 */
abstract class FixedProvider<T>: GeometryProvider<T> {

    final override val isAdaptive = false

    /**
     * Checks compatibility with the given geometry properties.
     *
     * Called during builder construction to validate this provider can work
     * with the configured geometry. This always compares the [providerConfig]
     * with the [NumberComposer.layoutConfig] and return [Consent.Accept] if equals.
     *
     * @param properties The geometry properties to validate against
     * @return [Consent.Accept] if compatible, [Consent.Reject] with reason(optional) if not
     */
    override fun matchesWith(properties: GeometryProps): Consent {
        val matches = providerConfig == properties.config
        if (!matches) {
            return Consent.Reject("Provider requires layout ${providerConfig} but got ${properties.config}")
        }

        return Consent.Accept
    }

    final override fun attachWith(properties: GeometryProps) {
        onAttachWith(properties)
    }

    /**
     * Performs one-time initialization with the resolved geometry properties.
     *
     * Called once during builder construction after [matchesWith] succeeds.
     * Use this to cache computed values or prepare internal state.
     *
     * @param properties The geometry properties to attach to
     */
    protected abstract fun onAttachWith(properties: GeometryProps)
}

/**
 * Represents the result of a provider compatibility check.
 */
sealed interface Consent {
    /**
     * Provider is compatible with the geometry properties.
     */
    data object Accept : Consent

    /**
     * Provider is incompatible with the geometry properties.
     *
     * @property reason Optional explanation of why compatibility failed
     */
    data class Reject(val reason: String? = null) : Consent

    /**
     * Checks if this consent represents a rejection.
     */
    fun hasRejected(): Boolean = this is Consent.Reject
}

/**
 * Extracts the rejection reason if this consent is a rejection.
 *
 * @return The rejection reason, or null if accepted or no reason provided
 */
fun Consent.getRejectionReason(): String? = (this as? Consent.Reject)?.reason

/**
 * Helper function to build provider data using a factory function.
 *
 * Creates a list with [providerConfig.bricks][GridConfig.bricks] elements,
 * where each element is produced by calling the factory with the brick index.
 *
 * @param factory Function that creates a data element given a brick index (0-based)
 * @return List of data elements, one per brick
 */
inline fun <reified T> GeometryProvider<T>.buildProviderData(factory: (Int) -> T): List<T> {
    return List(providerConfig.bricks) { factory(it) }
}