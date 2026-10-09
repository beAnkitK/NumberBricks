package io.github.beankitk.numberbricks.core.geometry

/**
 * Defines a pluggable unit that produces geometry data for digit geometry composition.
 *
 * A [GeometryProvider] produces a single aspect of digit geometry (for example, `position`,`size`,
 * `offset`) for each brick. For a given digit, it returns a value of type [R] for every brick
 * defined by [ProviderScope.gridSpec].
 *
 * Every provider is identified by a unique [ProviderKey] and may declare dependencies on other
 * providers using [dependsOn]. Providers execute within a [ProviderScope], which provides access to
 * the current digit, grid constraints, results and meta values of their dependencies.
 *
 * Providers participate in the digit geometry construction pipeline coordinated by [DigitBuilder].
 * Each provider contributes one dimension of geometry that is combined with the results of other
 * providers to produce the final brick model. Dependencies are resolved and executed by the
 * [DigitBuilder] before this provider.
 *
 * To create a provider, extend any of the following:
 * - [ComputedProvider] for a provider that computes its result independently of the [GridSpec] and
 *     can be shared across different [DigitBuilder]
 * - [AdaptiveProvider] for a provider that works with any [GridSpec] supplied by the [DigitBuilder].
 * - [FixedProvider] for a provider that works only with one specific [GridSpec].
 *
 * To create a provider family and override [key] with the family-specific key type, use the following
 * pattern:
 *
 * ```kotlin
 * sealed interface OffsetProvider {
 *     val delegate: GeometryProvider<Offset>
 *         get() = when (this) {
 *             is Fixed -> this
 *             is Adaptive -> this
 *             is Computed -> this
 *         }
 *
 *     interface Key : ProviderKey<Offset> {
 *         override val family: Key
 *             get() = OffsetProvider.Key
 *
 *         companion object : Key
 *     }
 *
 *     abstract val key: OffsetProvider.Key
 *     abstract class Adaptive : AdaptiveProvider<Offset>(), OffsetProvider
 *     abstract class Fixed(gridSpec: GridSpec) : FixedProvider<Offset>(gridSpec), OffsetProvider
 *     abstract class Computed : ComputedProvider<Offset>(), OffsetProvider
 * }
 * ```
 *
 * Use [delegate] to access the underlying [GeometryProvider] represented by this provider family.
 *
 * @param R The type of result produced for each brick.
 * @see ComputedProvider
 * @see AdaptiveProvider
 * @see FixedProvider
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
     * Declares providers whose results and meta are required before this provider executes.
     *
     * All declared dependencies will be executed before this provider. Their results and meta can
     * be accessed from [ProviderScope]. Use the family key when you depend on a result, as a
     * provider for the family is always available. Use the provider key when you depend on meta
     * from a specific provider; that meta is available only when that provider is registered.
     */
    val dependsOn: Set<ProviderKey<*>>

    /**
     * Computes and returns this provider's result for the current digit.
     *
     * Implementations execute within a [ProviderScope], which provides access to the current digit,
     * grid constraints, results and meta values from providers declared as dependencies via [dependsOn]
     * for use during result computation.
     *
     * Returns the provider result as a list of values of type [R], containing exactly
     * [ProviderScope.gridSpec.brickCount] elements, one for each brick in the current digit.
     *
     * @receiver The [ProviderScope] that provides the execution context required to compute this
     *   provider's result.
     */
    fun ProviderScope.provide(): List<R>
}

/** Represents the result of a provider compatibility check. */
sealed interface Consent {

    /** Indicates that the provider is compatible. */
    data object Accept : Consent

    /** Indicates that the provider is incompatible, with an optional [reason]. */
    @JvmInline value class Reject(val reason: String? = null) : Consent

    /** Returns `true` if this represents a rejection. */
    fun hasRejected(): Boolean = this is Reject
}

/**
 * Returns the rejection reason if this is [Consent.Reject] and the reason is specified, or `null`
 * otherwise.
 */
fun Consent.getRejectionReason(): String? = (this as? Consent.Reject)?.reason

/**
 * Base implementation of [GeometryProvider] that manages the provider lifecycle, including
 * compatibility checking and configuration before producing results.
 *
 * This class handles compatibility validation, attachment, detachment, and tracks the provider's
 * attachment state. Subclasses define their compatibility requirements through [doMatch] and may
 * initialize or release provider-specific state through [onAttach] and [onDetach].
 *
 * Before producing results, the provider's compatibility with a [DigitBuilder] is evaluated using
 * [matches]. If compatible, the provider is attached using [attach] with the resolved grid
 * constraints and shared [GeometryProps]. Once attached, the provider can produce its results
 * until it is detached from the [DigitBuilder] using [detach].
 *
 * Use [AdaptiveProvider] when the provider can operate with any grid spec, or [FixedProvider] when
 * the provider requires a specific grid spec.
 *
 * @param R The type of result produced for each brick.
 * @see AdaptiveProvider
 * @see FixedProvider
 * @see GeometryProvider
 */
sealed class LifecycleProvider<R : Any> : GeometryProvider<R> {

    private var isCompatible: Boolean? = null

    /**
     * Indicates whether this provider is currently attached to a [DigitBuilder].
     *
     * Returns `true` after [attach] succeeds and `false` after [detach] completes. An attached
     * provider cannot be matched or attached again until it has been detached.
     */
    var isAttached: Boolean = false
        private set

    /**
     * Evaluates whether this provider is compatible with the given grid constraints.
     *
     * Called during builder construction before the provider is attached. A [FixedProvider] requires
     * the supplied [digitGridSpec] to match its configured grid specification, while an
     * [AdaptiveProvider] accepts the grid by default. Additional provider-specific compatibility
     * requirements can be evaluated by [doMatch].
     *
     * Returning [Consent.Reject] prevents this provider from being attached.
     *
     * @param digitGridSpec The grid constraints to evaluate
     * @return [Consent.Accept] if this provider is compatible, otherwise [Consent.Reject]
     * @throws IllegalStateException if this provider is already attached
     */
    final fun matches(digitGridSpec: GridSpec): Consent {
        check(!isAttached) { "This provider has already been attached and cannot be validated." }
        if (this is FixedProvider<R>) {
            val gridSpec = this.gridSpec
            val matches =
                gridSpec.rows == digitGridSpec.rows &&
                    gridSpec.cols == digitGridSpec.cols &&
                    gridSpec.brickCount == digitGridSpec.brickCount

            if (!matches) {
                isCompatible = false
                return Consent.Reject(
                    "Provider requires gridSpec ${gridSpec.asString()} " +
                        "but got ${digitGridSpec.asString()}"
                )
            }
        }

        return try {
            val consent = doMatch(digitGridSpec)
            isCompatible = !consent.hasRejected()
            consent
        } catch (throwable: Throwable) {
            isCompatible = null
            throw throwable
        }
    }

    /**
     * Attaches this provider to the [DigitBuilder] with the given grid constraints and geometry
     * configuration.
     *
     * Called during builder construction after compatibility has been accepted by [matches]. Marks
     * the provider as [isAttached] and calls [onAttach], where implementations may cache values or
     * initialize any state required during execution.
     *
     * @param digitGridSpec The resolved grid constraints
     * @param geometryProps The shared geometry configuration
     * @throws IllegalStateException if compatibility has not been evaluated, the provider is
     *   incompatible, or it is already attached.
     */
    final fun attach(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        checkAttachable(isCompatible, isAttached)
        try {
            isAttached = true
            onAttach(digitGridSpec, geometryProps)
        } catch (throwable: Throwable) {
            isCompatible = null
            isAttached = false
            throw throwable
        }
    }

    /**
     * Detaches this provider from the current [DigitBuilder] and resets all lifecycle state.
     *
     * Called by [DigitBuilder] when the provider is no longer needed. Calls [onDetach] so that
     * implementations can release resources and clear any state initialized by [onAttach]. After
     * [onDetach] completes, the provider state is reset and [isAttached] returns `false`. After
     * detaching, the provider must be matched again before it can be re-attached.
     *
     * This operation is a no-op if the provider is not currently attached.
     */
    final fun detach() {
        if (!isAttached) return
        try {
            onDetach()
        } finally {
            isCompatible = null
            isAttached = false
        }
    }

    /**
     * Called by [matches] after the provider's grid constraints have been satisfied by
     * [digitGridSpec]. Override to perform any additional checks required before the provider can
     * be attached.
     *
     * @param digitGridSpec The grid constraints to evaluate.
     * @return [Consent.Accept] if the provider is compatible; otherwise, [Consent.Reject]. By
     *   default, returns [Consent.Accept].
     */
    protected open fun doMatch(digitGridSpec: GridSpec): Consent = Consent.Accept

    /**
     * Called after this provider is attached to a [DigitBuilder]. Override to cache values or
     * initialize any state required for execution. If this callback throws, the provider is
     * returned to the detached state, [onDetach] is not called, and the provider must be
     * matched again before it can be re-attached.
     *
     * @param digitGridSpec The resolved grid constraints.
     * @param geometryProps The shared geometry configuration.
     */
    protected open fun onAttach(digitGridSpec: GridSpec, geometryProps: GeometryProps) {}

    /**
     * Called before this provider is detached from a [DigitBuilder]. Override to release any
     * resources or clear any state initialized by [onAttach]. After this callback returns, the provider
     * is detached and [isAttached] returns `false`.
     */
    protected open fun onDetach() {}
}

/**
 * A [GeometryProvider] that produces its result based on the grid spec provided by the [DigitBuilder].
 *
 * The provider can produce its result for different grid specs, adapting its calculation to the grid
 * for which the result is being produced.
 *
 * By default, an adaptive provider accepts any grid spec. Override [doMatch] to perform additional
 * compatibility checks. Use this provider when the geometry is calculated dynamically and is not
 * tied to a specific grid spec.
 *
 * @param R The type of result produced for each brick.
 * @see FixedProvider
 * @see LifecycleProvider
 * @see GeometryProvider
 */
abstract class AdaptiveProvider<R : Any> : LifecycleProvider<R>()

/**
 * A [GeometryProvider] that produces its result for a specific grid spec.
 *
 * The provider is configured with [gridSpec], which defines the grid for which the provider
 * produces its result.
 *
 * The provider requires the [GridSpec] supplied by the [DigitBuilder] to exactly match [gridSpec].
 * Override [doMatch] to perform additional compatibility checks. Use this provider when the geometry
 * is defined for a specific grid spec and should not be adapted to a different grid spec.
 *
 * @param R The type of result produced for each brick.
 * @property gridSpec The grid spec required by this provider.
 * @see AdaptiveProvider
 * @see LifecycleProvider
 * @see GeometryProvider
 */
abstract class FixedProvider<R : Any>(val gridSpec: GridSpec) : LifecycleProvider<R>()

/**
 * A [GeometryProvider] that produces its result independently of the [GridSpec] and can be shared
 * across different [DigitBuilder] instances.
 *
 * The provider can produce its result for different grid specs, either by computing the result
 * based on [ProviderScope.gridSpec] or by deriving it from the results of its dependencies.
 *
 * Unlike [AdaptiveProvider], a computed provider has no lifecycle of its own and is not attached to
 * any [DigitBuilder]. Its provider instance can therefore be shared safely across multiple builders,
 * provided it does not hold builder-specific state or caches.
 *
 * Use this provider when its result can be dynamically computed for any grid spec without requiring
 * builder-specific lifecycle management or state.
 *
 * @param R The type of result produced for each brick.
 * @see AdaptiveProvider
 * @see FixedProvider
 * @see GeometryProvider
 */
abstract class ComputedProvider<R : Any> : GeometryProvider<R>

private fun checkAttachable(isCompatible: Boolean?, isAttached: Boolean) {
    when {
        isCompatible == null ->
            error("Provider compatibility has not been evaluated. Call matches() first.")

        !isCompatible ->
            error("This provider is incompatible with the DigitBuilder and cannot be attached.")

        isAttached ->
            error(
                "This provider has already been attached to a DigitBuilder and cannot be attached again."
            )
    }
}
