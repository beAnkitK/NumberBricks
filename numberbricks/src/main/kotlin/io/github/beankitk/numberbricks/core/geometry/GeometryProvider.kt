package io.github.beankitk.numberbricks.core.geometry

/**
 * Defines a pluggable unit that produces geometry data for digit geometry composition.
 *
 * A [GeometryProvider] produces a single aspect of digit geometry (for example, `position`,`size`,
 * `offset`) for each brick. For a given digit, it returns a value of type [R] for every brick in
 * the current [ProviderScope]. Extend [BaseGeometryProvider] for creating new providers.
 *
 * Providers participate in the geometry composition pipeline coordinated by [DigitBuilder]. Each
 * provider contributes one dimension of geometry that is combined with the results of other
 * providers to produce the final brick model.
 *
 * Every provider is identified by a unique [ProviderKey] and may declare dependencies on other
 * providers using [dependsOn]. The [isAdaptive] and [providerGridSpec] properties define the
 * grid on which this provider produces its result, either by adapting to the builder's grid
 * constraints or by using a predefined grid.
 *
 * Before execution, providers evaluate their compatibility with a [DigitBuilder] using
 * [matches]. This includes validating the grid constraints and performing any additional
 * compatibility checks. If compatible, this provider is attached via [attach] with the resolved
 * grid constraints and shared [GeometryProps].
 *
 * Providers execute within a [ProviderScope], where they can access the current digit, results
 * and meta values of their dependencies. Dependencies are resolved and executed by the
 * [DigitBuilder] before this provider. Implementations must ensure that [provide] returns
 * exactly `providerGridSpec.brickCount` elements.
 *
 * @param R The type of result produced for each brick.
 * @see BaseGeometryProvider
 * @see ProviderScope
 * @see ProviderKey
 */
sealed interface GeometryProvider<R : Any> {

    /**
     * Identifies this provider and the type of result it produces.
     *
     * The key is used to declare dependencies via [dependsOn] and access results from other
     * providers. See [ProviderKey] for defining provider keys and families.
     */
    val key: ProviderKey<R>

    /**
     * Indicates whether this provider's result can adapt to the grid constraints defined by
     * [NumberComposer] and supplied during attachment.
     *
     * `true` - if the provider adapts to the supplied grid constraints
     * `false` - if it requires the supplied grid constraints to match exactly.
     */
    val isAdaptive: Boolean

    /**
     * Returns the grid constraints this provider operates on.
     *
     * For adaptive providers, this is initialized when [attach] is called and represents the supplied
     * grid constraints. For fixed providers, it is predefined and must match the incoming grid constraints.
     */
    val providerGridSpec: GridSpec

    /**
     * Declares providers whose results and meta are required before this provider executes.
     *
     * All declared dependencies will be executed before this provider. Their results and meta can
     * be accessed from [ProviderScope]. Use the family key when you depend on a result, as a provider
     * for the family is always available. Use the provider key when you depend on meta from a specific
     * provider; that meta is available only when that provider is registered.
     */
    val dependsOn: Set<ProviderKey<*>>

    /**
     * Evaluates whether this provider is compatible with the given grid constraints or any provider-specific
     * requirements.
     *
     * Called during builder construction before initialization. Implementations should verify that
     * the provider can operate with the given [digitGridSpec] and is compatible with producing its result.
     * Returning [Consent.Reject] prevents this provider from being attached.
     *
     * @param digitGridSpec The grid constraints to evaluate
     * @return [Consent.Accept] if this provider is compatible, otherwise [Consent.Reject]
     * @throws IllegalStateException if this provider is already attached
     */
    fun matches(digitGridSpec: GridSpec): Consent

    /**
     * Attaches this provider to the [DigitBuilder] with the given grid constraints and geometry
     * configuration.
     *
     * Called once during builder construction after compatibility has been accepted. Implementations
     * may cache values or initialize any state required during execution.
     *
     * @param digitGridSpec The resolved grid constraints
     * @param geometryProps The shared geometry configuration
     * @throws IllegalStateException if compatibility has not been evaluated, the provider is
     *   incompatible, or it is already attached.
     */
    fun attach(digitGridSpec: GridSpec, geometryProps: GeometryProps)

    /**
     * Computes and returns this provider's result for the current digit.
     *
     * Implementations execute within a [ProviderScope], which provides access to the current digit,
     * results and meta values from providers declared as dependencies via [dependsOn] for use
     * during result computation.
     *
     * Returns the provider result as a list of values of type [R], containing exactly
     * `providerGridSpec.brickCount` elements, one for each brick in the current digit.
     *
     * @receiver The [ProviderScope] that provides the execution context required to compute this
     * provider's result.
     * @throws IllegalStateException if this provider is not attached
     */
    fun ProviderScope.provide(): List<R>

    /**
     * Detaches this provider from the current [DigitBuilder] and resets all lifecycle state.
     *
     * Called once during builder destruction. Implementations should release any resources and
     * clear any state initialized by [attach]. This function is a no-op if the provider is not attached.
     */
    fun detach()
}

/** Represents the result of a provider compatibility check. */
sealed interface Consent {

    /** Indicates that the provider is compatible. */
    data object Accept : Consent

    /**
     * Indicates that the provider is incompatible.
     *
     * @property reason Optional reason of the failure
     */
    @JvmInline
    value class Reject(val reason: String? = null) : Consent

    /** Returns `true` if this represents a rejection. */
    fun hasRejected(): Boolean = this is Reject
}

/**
 * Returns the rejection reason if this is [Consent.Reject] and the reason is specified, or `null`
 * otherwise.
 */
fun Consent.getRejectionReason(): String? = (this as? Consent.Reject)?.reason

/**
 * Builds the provider result aligned with this provider's grid constraints.
 *
 * The returned list contain values of type [R] provided for each brick in [providerGridSpec] and always
 * contains exactly `providerGridSpec.brickCount` elements. Prefer this function when constructing
 * provider results.
 *
 * @param factory Provides the result for the specified brick index.
 * @receiver The provider whose grid constraints determine the output size.
 */
inline fun <R : Any> GeometryProvider<R>.buildProviderData(factory: (Int) -> R): List<R> {
    return List(providerGridSpec.brickCount) { factory(it) }
}

/**
 * Base implementation of [GeometryProvider] that manages the provider lifecycle and grid behavior.
 *
 * This class handles compatibility validation, attachment, detachment, and execution while
 * tracking the provider's attachment state and resolved grid constraints. Subclasses configure
 * the provider by defining a [key] and [providerGridPolicy], and by overriding [provideData] to
 * produce the provider result. Override the lifecycle hooks [doMatch], [onAttach], and
 * [onDetach] as needed to customize the provider's behavior.
 *
 * @param R The provider result type provided for each brick.
 * @see GeometryProvider
 * @see ProviderKey
 * @see ProviderGridPolicy
 */
abstract class BaseGeometryProvider<R : Any> : GeometryProvider<R> {

    private var isCompatible: Boolean? = null
    internal var isAttached: Boolean = false
    private var _providerGridSpec: GridSpec? = null

    final override val isAdaptive: Boolean
        get() = providerGridPolicy is AdaptiveGridPolicy

    final override val providerGridSpec: GridSpec
        get() {
            val gridPolicy = providerGridPolicy
            return if (gridPolicy is FixedGridPolicy) {
                gridPolicy.gridSpec
            } else {
                _providerGridSpec
                    ?: error("providerGridSpec accessed before attach() was called.")
            }
        }

    final override fun matches(digitGridSpec: GridSpec): Consent {
        check(!isAttached) { "This provider has already been attached and can no longer be validated." }
        val gridPolicy = providerGridPolicy
        if (gridPolicy is FixedGridPolicy) {
            val matches = gridPolicy.gridSpec.rows == digitGridSpec.rows &&
                gridPolicy.gridSpec.cols == digitGridSpec.cols &&
                gridPolicy.gridSpec.brickCount == digitGridSpec.brickCount

            if (!matches) {
                isCompatible = false
                return Consent.Reject(
                    "Provider requires gridSpec ${gridPolicy.gridSpec.asString()} " +
                        "but got ${digitGridSpec.asString()}"
                )
            }
        }

        val consent = doMatch(digitGridSpec)
        isCompatible = !consent.hasRejected()
        return consent
    }

    final override fun attach(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        checkAttachable(isCompatible, isAttached)
        if (providerGridPolicy is AdaptiveGridPolicy) {
            _providerGridSpec = digitGridSpec
        }
        isAttached = true
        onAttach(digitGridSpec, geometryProps)
    }

    final override fun ProviderScope.provide(): List<R> {
        check(isAttached) { "This provider must be attached before providing data." }
        return provideData()
    }

    final override fun detach() {
        if (!isAttached) return
        onDetach()
        _providerGridSpec = null
        isAttached = false
        isCompatible = null
    }

    /**
     * Specifies how this provider determines the grid constraints used to produce its result.
     *
     * The configured policy determines the value of [isAdaptive]. Return [AdaptiveGridPolicy]
     * to adapt to the builder's grid constraints, or [FixedGridPolicy] to require a predefined grid.
     */
    protected abstract val providerGridPolicy: ProviderGridPolicy

    /**
     * Called by [matches] after the provider's grid constraints have been satisfied by
     * [digitGridSpec]. Override to perform any additional checks required before the provider
     * can be attached.
     *
     * @param digitGridSpec The grid constraints to evaluate.
     * @return [Consent.Accept] if the provider is compatible; otherwise, [Consent.Reject]. By
     *   default, returns [Consent.Accept].
     */
    protected open fun doMatch(digitGridSpec: GridSpec): Consent = Consent.Accept

    /**
     * Called after this provider is attached to a [DigitBuilder]. Override to cache
     * values or initialize any state required for execution.
     *
     * @param digitGridSpec The resolved grid constraints.
     * @param geometryProps The shared geometry configuration.
     */
    protected open fun onAttach(digitGridSpec: GridSpec, geometryProps: GeometryProps) {}

    /**
     * Computes and returns this provider's result for the current digit.
     *
     * Implementations execute within a [ProviderScope], which provides access to the current digit,
     * results and meta values from providers declared as dependencies via [dependsOn] for use
     * during result computation.
     *
     * Returns the provider result as a list of values of type [R], containing exactly
     * `providerGridSpec.brickCount` elements, one for each brick in the current digit.
     *
     * @receiver The [ProviderScope] that provides the execution context required to compute this
     *   provider's result.
     */
    protected abstract fun ProviderScope.provideData(): List<R>

    /**
     * Called before this provider is detached from a [DigitBuilder]. Override to release any resources
     * or clear any state initialized by [onAttach].
     */
    protected open fun onDetach() {}
}

/**
 * Defines how a provider determines the grid constraints used to produce its result.
 *
 * Used by [BaseGeometryProvider] to determine whether a provider adapts to the
 * builder's grid constraints or operates on a predefined grid.
 *
 * @see AdaptiveGridPolicy
 * @see FixedGridPolicy
 */
sealed interface ProviderGridPolicy

/**
 * A [ProviderGridPolicy] that allows the provider to adapt to the grid constraints
 * supplied by the owning [DigitBuilder].
 */
data object AdaptiveGridPolicy : ProviderGridPolicy

/**
 * A [ProviderGridPolicy] that requires the provider to operate on a predefined
 * [GridSpec].
 *
 * @param gridSpec The fixed grid constraints for the provider.
 */
@JvmInline
value class FixedGridPolicy(internal val gridSpec: GridSpec)  : ProviderGridPolicy

private fun checkAttachable(
    isCompatible: Boolean?,
    isAttached: Boolean,
) {
    when {
        isCompatible == null ->
            error("Provider compatibility has not been evaluated. Call matches() first.")

        !isCompatible ->
            error("This provider is incompatible with the DigitBuilder and cannot be attached.")

        isAttached ->
            error("This provider has already been attached to a DigitBuilder and cannot be attached again.")
    }
}
