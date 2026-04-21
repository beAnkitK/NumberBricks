package io.github.beankitk.numberbricks.core.geometry

/**
 * Defines a pluggable unit that produces geometry data for digit geometry composition.
 *
 * A [GeometryProvider] produces a specific aspect of digit geometry (for example,
 * `position`, `size`, `offset`) for each brick. For a given digit, it returns a list of elements
 * of type [T], where each element corresponds to exactly one brick in the current [ProviderScope].
 *
 * Providers participate in the geometry composition pipeline coordinated by [DigitBuilder].
 * Each provider contributes one dimension of data that is later combined to form
 * the final brick model.
 *
 * A provider is identified by a unique [ProviderKey], which is used to declare
 * dependencies via [dependsOn] and to retrieve results from other providers.
 *
 * Providers declare compatibility with grid constraints using [matchesWith].
 * After acceptance, they are initialized with [attachWith], which supplies the
 * resolved grid constraints and shared [GeometryProps].
 *
 * Providers execute within a [ProviderScope], where they can access the current
 * digit and results of other providers. Dependencies are resolved by the [DigitBuilder]
 * and executed before this provider. Implementations must ensure that [provideData]
 * returns exactly `providerGridSpec.brickCount` elements.
 *
 * **Important:** Declare each provider [key] in a `companion object` so it can be
 * referenced without creating an instance. The property name must be `key` for
 * consistent referencing and usage across providers.
 *
 * This key is required to:
 * - Declare dependencies via [dependsOn]
 * - Retrieve results from other providers
 *
 * For example:
 *
 * ```kotlin
 * class OffsetProvider : GeometryProvider<Offset> {
 *     override val key = OffsetProvider.key
 *
 *     companion object {
 *         val key = ProviderKey<Offset>("provider.offset.base")
 *     }
 * }
 * ```
 *
 * @param T The type of data this provider produces for each brick
 * @see AdaptiveProvider
 * @see FixedProvider
 * @see ProviderScope
 */
interface GeometryProvider<T> {

    /**
     * Identifies this provider and the type of data it produces. Used to declare dependencies
     * and retrieve results across providers.
     */
    val key: ProviderKey<T>

    /**
     * Indicates whether this provider adapts to incoming grid constraints.
     *
     * - `true`: Adapts to the provided grid (see [AdaptiveProvider])
     * - `false`: Requires an exact match (see [FixedProvider])
     */
    val isAdaptive: Boolean

    /**
     * Defines the grid constraints this provider operates on.
     *
     * For adaptive providers, this is derived during [attachWith]. For fixed providers,
     * this is predefined and must match the incoming grid constraints.
     */
    val providerGridSpec: GridSpec

    /**
     * Declares providers whose results and meta are required before this provider executes.
     *
     * All dependencies are guaranteed to execute before this provider. Their results
     * and metadata can be accessed from [ProviderScope].
     */
    val dependsOn: Set<ProviderKey<*>>

    /**
     * Validates compatibility with the given grid constraints.
     *
     * Invoked during builder construction before initialization. Implementations
     * should verify that the provider can operate with the given [digitGridSpec].
     * Returning [Consent.Reject] excludes this provider from execution.
     *
     * @param digitGridSpec Provides the grid constraints to validate against
     * @return [Consent.Accept] if compatible, or [Consent.Reject] otherwise
     */
    fun matchesWith(digitGridSpec: GridSpec): Consent

    /**
     * Initializes the provider with resolved grid constraints and geometry configuration.
     *
     * Executed once during builder construction after compatibility is accepted.
     * Implementations may cache values or prepare internal state required during execution.
     *
     * @param digitGridSpec Provides the resolved grid constraints
     * @param geometryProps Provides the shared geometry configuration
     */
    fun attachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps)

    /**
     * Produces geometry data for the current digit.
     *
     * Implementations execute within a [ProviderScope], which provides access to the
     * current digit, results from other providers declared via [dependsOn], and any
     * provider-scoped metadata required during computation.
     *
     * Returns a list of values aligned with bricks, where each element corresponds
     * to a single brick for the current digit. The returned list size must be equal
     * to `providerGridSpec.brickCount`.
     *
     * @receiver ProviderScope that provides the execution environment required to compute
     *           this provider's data
     */
    fun ProviderScope.provideData(): List<T>
}

/**
 * Base implementation of [GeometryProvider] that adapts to incoming grid constraints.
 *
 * Adaptive providers accept any grid constraints by default and derive their
 * [providerGridSpec] during [attachWith]. This allows them to operate across
 * different grid configurations.
 *
 * Subclasses implement [onAttachWith] to perform initialization using the resolved
 * grid constraints and geometry properties. The [providerGridSpec] is available after
 * attachment.
 *
 * Additional compatibility checks can be enforced by overriding [matchesWith].
 *
 * @param T The type of data this provider produces for each brick
 */
abstract class AdaptiveProvider<T> : GeometryProvider<T> {

    final override val isAdaptive = true

    private var _providerGridSpec: GridSpec? = null
    final override val providerGridSpec
        get() = _providerGridSpec ?: error("providerGridSpec accessed before attachWith() was called.")

    /**
     * Always accepts the provided grid constraints. Override to enforce additional compatibility checks.
     */
    override fun matchesWith(digitGridSpec: GridSpec): Consent = Consent.Accept

    final override fun attachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        _providerGridSpec = digitGridSpec
        onAttachWith(digitGridSpec, geometryProps)
    }

    /**
     * Performs initialization using the resolved grid constraints and geometry properties.
     *
     * Called once during builder construction after [matchesWith] succeeds.
     * Use this to cache computed values or prepare internal state required during execution.
     *
     * @param digitGridSpec Provides the resolved grid constraints
     * @param geometryProps Provides the shared geometry configuration
     */
    protected abstract fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps)
}

/**
 * Base implementation of [GeometryProvider] that requires exact grid constraints.
 *
 * Fixed providers operate on predefined grid constraints and only accept
 * configurations that exactly match [providerGridSpec]. This makes them
 * suitable for layouts with strict structural requirements.
 *
 * Subclasses must define [providerGridSpec] and implement [onAttachWith]
 *
 * @param T The type of data this provider produces for each brick
 */
abstract class FixedProvider<T> : GeometryProvider<T> {

    final override val isAdaptive = false

    /**
     * Accepts the provided grid constraints only if they match [providerGridSpec].
     */
    override fun matchesWith(digitGridSpec: GridSpec): Consent {
        val matches = providerGridSpec.rows == digitGridSpec.rows
            && providerGridSpec.cols == digitGridSpec.cols
            && providerGridSpec.brickCount == digitGridSpec.brickCount

        if (!matches) {
            return Consent.Reject("Provider requires gridSpec ${providerGridSpec.asString()} but got ${digitGridSpec.asString()}")
        }

        return Consent.Accept
    }

    final override fun attachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        onAttachWith(digitGridSpec, geometryProps)
    }

    /**
     * Performs initialization using the grid constraints and geometry properties.
     *
     * Called once during builder construction after [matchesWith] succeeds.
     * Use this to cache computed values or prepare internal state required during execution.
     *
     * @param digitGridSpec Provides the resolved grid constraints
     * @param geometryProps Provides the shared geometry configuration
     */
    protected abstract fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps)
}

/**
 * Represents the result of a provider compatibility check.
 */
sealed interface Consent {

    /**
     * Indicates that the provider is compatible.
     */
    data object Accept : Consent

    /**
     * Indicates that the provider is incompatible.
     *
     * @property reason Optional explanation of the failure
     */
    data class Reject(val reason: String? = null) : Consent

    /**
     * Returns `true` if this represents a rejection.
     */
    fun hasRejected(): Boolean = this is Reject
}

/**
 * Returns the rejection reason if this is [Consent.Reject] and the reason is specified,
 * or `null` otherwise.
 */
fun Consent.getRejectionReason(): String? = (this as? Consent.Reject)?.reason

/**
 * Builds provider data aligned with this provider's grid constraints.
 *
 * The returned list is aligned with this provider's grid constraints and always contains
 * `providerGridSpec.brickCount` elements, where each index corresponds to a single brick.
 *
 * Prefer using this function to construct provider results. Alternative approaches
 * (such as manual iteration or mapping) may introduce additional allocations
 * (for example, iterators), while this uses direct indexed construction.
 *
 * @receiver The provider whose grid constraints determine the output size
 * @param factory Produces a value for a given brick index
 */
inline fun <reified T> GeometryProvider<T>.buildProviderData(factory: (Int) -> T): List<T> {
    return List(providerGridSpec.brickCount) { factory(it) }
}