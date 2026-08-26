package io.github.beankitk.numberbricks.core.geometry

import androidx.collection.MutableScatterMap
import androidx.collection.ScatterMap

/**
 * Defines the contract for constructing digit geometry as a collection of [Brick]s.
 *
 * A [DigitBuilder] defines how a digit is represented as an ordered list of bricks, called the
 * brick model of digit. It operates on a given [GridSpec] and [GeometryProps], which together
 * describe the structural grid and shared geometry configuration used during geometry composition.
 *
 * The builder coordinates a set of [GeometryProvider]s to compute the result required to construct
 * the brick model of a digit. Each provider contributes a specific aspect of geometry, and their
 * execution is resolved based on declared dependencies.
 *
 * Before building bricks, the builder must be initialized using [construct]. This prepares the
 * builder by registering declared providers, checking their compatibility, resolving their
 * dependencies, and attaching configuration to each provider.
 *
 * Once constructed, [buildBricks] returns the brick model for a given digit by executing providers
 * in dependency order and combining their results. [buildDefaultBricks] returns a default brick
 * model that can be used as a placeholder model for initial or fallback state.
 *
 * The builder does not retain digit-specific state and can be reused across multiple digits after
 * construction. Calling [destroy] resets the builder to an uninitialized state.
 *
 * @param B The concrete [Brick] type produced by this builder
 * @see BaseDigitBuilder
 * @see GeometryProvider
 * @see GridSpec
 * @see GeometryProps
 */
interface DigitBuilder<B : Brick<B>> {

    /**
     * Defines the geometry providers used by this builder to construct digit brick models.
     *
     * Implementations must include every provider required for geometry composition. The order in
     * which providers are declared does not matter; provider dependencies are resolved when the
     * builder is constructed.
     */
    val providers: List<GeometryProvider<*>>

    /**
     * Initializes the builder with the given grid constraints and geometry properties.
     *
     * This method must be called before building bricks. It prepares the builder for execution by:
     * - Validating providers declared for this builder
     * - Resolving provider dependencies and execution order
     * - Attaching configuration to all providers
     *
     * @param digitGridSpec Defines the grid constraints for the digit
     * @param geometryProps Defines shared geometry configuration
     * @throws IllegalStateException if already constructed
     * @throws IllegalStateException if any provider is incompatible or has a duplicate key
     */
    fun construct(digitGridSpec: GridSpec, geometryProps: GeometryProps)

    /**
     * Builds the brick model for the given digit.
     *
     * This executes all declared providers in dependency order and assembles their outputs to
     * produce the final brick model for the [digit].
     *
     * @param digit The digit to construct (`0..9`) or `-1` for default state
     * @return An ordered list of bricks representing the digit
     * @throws IllegalStateException if the builder is not constructed
     * @throws IllegalArgumentException if the digit is out of range
     */
    fun buildBricks(digit: Int): List<B>

    /**
     * Builds a default brick model.
     *
     * This returns a default brick model that can be used as placeholder before a specific digit
     * brick model is resolved or during transitions.
     *
     * @return An ordered list of default bricks
     * @throws IllegalStateException if the builder is not constructed
     */
    // TODO: Add digit parameter to return digit aware default bricks
    fun buildDefaultBricks(): List<B>

    /**
     * Resets the builder to an unconstructed state and releases internal resources. After calling
     * this, [construct] must be invoked again before builder can be reused.
     *
     * @throws IllegalStateException if the builder is not constructed
     */
    fun destroy()
}

/**
 * Base implementation of [DigitBuilder] that provides provider orchestration and lifecycle
 * management.
 *
 * This class handles:
 * - Builder lifecycle (construction, execution, destruction)
 * - Provider validation, dependency resolution, and lifecycle management
 * - Provider execution and result aggregation
 *
 * Subclasses are responsible for:
 * 1. Providing the geometry providers used by the builder through [providers]
 * 2. Assembling the final bricks by implementing:
 *     - [assembleBricks]
 *     - [assembleDefaultBricks]
 *
 * ### Lifecycle
 *
 * 1. **Construction** via [construct]
 *     - Validates provider compatibility with current [GridSpec]
 *     - Computes execution order based on dependencies
 *     - Attaches [GridSpec] and [GeometryProps] to all providers
 *
 * 2. **Execution** via [buildBricks], [buildDefaultBricks]
 *     - Creates a digit-scoped [ProviderScope]
 *     - Executes providers in dependency order
 *     - Validates provider outputs
 *     - Delegates to [assembleBricks] for final assembly
 *     - Delegates directly to [assembleDefaultBricks] for default bricks
 *
 * 3. **Destruction** via [destroy]
 *     - Detaches all resolved providers
 *     - Clears the resolved provider list and construction state
 *
 * Providers are executed in dependency order and validated for cyclic dependencies, if found,
 * result in a failure during construction.
 *
 * @param B The concrete [Brick] type produced by this builder
 * @see GeometryProvider
 * @see ProviderScope
 */
abstract class BaseDigitBuilder<B : Brick<B>> : DigitBuilder<B> {

    private var isConstructed = false
    private var _digitGridSpec: GridSpec? = null
    private var _geometryProps: GeometryProps? = null
    private var resolvedProviders: List<GeometryProvider<*>> = emptyList()

    /**
     * Represents the grid constraints used to construct each digit, inherited from [NumberComposer].
     *
     * @throws IllegalStateException If accessed before [construct] is called
     */
    protected val digitGridSpec: GridSpec
        get() = checkNotNull(_digitGridSpec) { "DigitBuilder not constructed. Call construct() first." }

    /**
     * Represents shared geometry configuration used across all digits, inherited from [NumberComposer].
     *
     * @throws IllegalStateException If accessed before [construct] is called
     */
    protected val geometryProps: GeometryProps
        get() = checkNotNull(_geometryProps) { "DigitBuilder not constructed. Call construct() first." }

    final override fun construct(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        check(!isConstructed) { "DigitBuilder already constructed" }
        try {
            _digitGridSpec = digitGridSpec
            _geometryProps = geometryProps
            resolvedProviders = resolveProviders()
            resolvedProviders.forEach { it.attach(digitGridSpec, geometryProps) }
        } catch (throwable: Throwable) {
            _digitGridSpec = null
            _geometryProps = null
            resolvedProviders.forEach {
                if (it is BaseGeometryProvider<*> && it.isAttached) it.detach()
            }
            resolvedProviders = emptyList()
            throw throwable
        }

        isConstructed = true
        onConstructed()
    }

    final override fun buildBricks(digit: Int): List<B> {
        checkConstructed()
        require(digit in 0..9 || digit == -1) {
            "DigitBuilder accepts digit values from 0 to 9 to construct bricks and -1 for default bricks, but got $digit"
        }

        return ProviderScope(digit).use { providerScope ->
            resolvedProviders.forEach { provider -> executeProvider(provider, providerScope) }
            providerScope.assembleBricks()
        }
    }

    final override fun buildDefaultBricks(): List<B> {
        checkConstructed()
        return assembleDefaultBricks()
    }

    final override fun destroy() {
        checkConstructed()
        resolvedProviders.forEach { it.detach() }
        resolvedProviders = emptyList()
        _digitGridSpec = null
        _geometryProps = null
        isConstructed = false
        onDestroyed()
    }

    /**
     * Called after the builder has been successfully constructed and all providers have been
     * attached. Override to perform additional initialization.
     *
     * @see construct
     */
    protected open fun onConstructed() {}

    /**
     * Assembles the final list of bricks from provider results.
     *
     * This is called after all providers have executed. Implementations should read provider
     * results from the current [ProviderScope] and construct the resulting bricks model for
     * [current digit][ProviderScope.digit].
     *
     * @return An ordered list of bricks representing the digit
     */
    protected abstract fun ProviderScope.assembleBricks(): List<B>

    /**
     * Assembles the default brick model.
     *
     * This is used as a placeholder when no specific digit is requested. Implementations define how
     * the default or initial state is represented.
     *
     * @return An ordered list of default bricks
     */
    protected abstract fun assembleDefaultBricks(): List<B>

   /**
    * Called after the builder has been successfully destroyed and all providers have been
    * detached. Override this method to release additional resources.
    *
    * @see destroy
    */
    protected open fun onDestroyed() {}

    private fun resolveProviders(): List<GeometryProvider<*>> {
        val declaredProviders = providers.toList()
        val providerCount = declaredProviders.size
        if (providerCount == 0) return emptyList()

        // Contains both provider keys and family keys, each resolving to its provider.
        val providersByKey = MutableScatterMap<ProviderKey<*>, GeometryProvider<*>>(providerCount * 2)
        var hasDependencies = false

        for (provider in declaredProviders) {
            val key = provider.key
            val familyKey = key.family

            check(!providersByKey.contains(key)) {
                "Cannot register provider '$key': key already registered"
            }

            check(!providersByKey.contains(familyKey)) {
                "Cannot register provider '$key': another provider already registered for family '$familyKey'"
            }

            val consent = provider.matches(digitGridSpec)
            if (consent.hasRejected()) {
                error(consent.getRejectionReason()
                    ?: "Cannot register provider '$key': incompatible with this DigitBuilder"
                )
            }

            providersByKey[key] = provider
            providersByKey[familyKey] = provider

            if (provider.dependsOn.isNotEmpty()) hasDependencies = true
        }

        if (!hasDependencies) return declaredProviders
        return computeExecutionOrder(providerCount, declaredProviders, providersByKey)
    }

    private fun <R : Any> executeProvider(
        provider: GeometryProvider<R>,
        providerScope: DefaultProviderScope,
    ) {
        providerScope.withProvider(provider) {
            val providerResult = providerScope.provide()
            check(providerResult.size == digitGridSpec.brickCount) {
                "Provider result must have ${digitGridSpec.brickCount} size, but was ${providerResult.size} for ${provider.key}"
            }
            providerScope.storeResult<R>(key, providerResult)
        }
    }

    private fun computeExecutionOrder(
        providerCount: Int,
        providers: List<GeometryProvider<*>>,
        providersByKey: ScatterMap<ProviderKey<*>, GeometryProvider<*>>,
    ): List<GeometryProvider<*>> {
        val visitStateByKey = MutableScatterMap<ProviderKey<*>, VisitState>(providerCount)
        val orderedProviders = ArrayList<GeometryProvider<*>>(providerCount)

        fun visit(provider: GeometryProvider<*>) {
            val key = provider.key
            when (visitStateByKey[key]) {
                VisitState.VISITING ->
                    error("Cyclic provider dependency detected at '$key'")
                VisitState.VISITED -> return
                else -> {
                    /* continue */
                }
            }

            visitStateByKey[key] = VisitState.VISITING
            provider.dependsOn.forEach { dependency ->
                providersByKey[dependency]?.let(::visit)
            }
            visitStateByKey[key] = VisitState.VISITED
            orderedProviders.add(provider)
        }

        providers.forEach { if (visitStateByKey[it.key] == null) visit(it) }
        return orderedProviders
    }

    private enum class VisitState {
        VISITING,
        VISITED,
    }

    private fun checkConstructed() {
        check(isConstructed) { "DigitBuilder not constructed. Call construct() first." }
    }
}
