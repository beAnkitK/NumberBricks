package io.github.beankitk.numberbricks.core.geometry

/**
 * Defines the contract for constructing digit geometry as a collection of [Brick]s.
 *
 * A [DigitBuilder] defines how a digit is represented as an ordered list of bricks,
 * called the brick model of digit. It operates on a given [GridSpec] and [GeometryProps],
 * which together describe the structural grid and shared geometry configuration used
 * during geometry composition.
 *
 * The builder coordinates a set of [GeometryProvider]s to compute the data required
 * to construct the brick model of a digit. Each provider contributes a specific aspect
 * of geometry, and their execution is resolved based on declared dependencies.
 *
 * Before building bricks, the builder must be initialized using [construct].
 * This prepares the builder by registering providers via [bindProviders],
 * resolving their dependencies, and attaching configuration to each provider.
 *
 * Once constructed, [buildBricks] returns the brick model for a given digit by
 * executing providers in dependency order and combining their results.
 * [buildDefaultBricks] returns a default brick model that can be used as a
 * placeholder model for initial or fallback state.
 *
 * The builder does not retain digit-specific state and can be reused across
 * multiple digits after construction. Calling [destruct] resets the builder
 * to an uninitialized state.
 *
 * @param T The concrete [Brick] type produced by this builder
 * @see BaseDigitBuilder
 * @see GeometryProvider
 * @see GridSpec
 * @see GeometryProps
 */
interface DigitBuilder<T : Brick<T>> {

    /**
     * Initializes the builder with the given grid constraints and geometry properties.
     *
     * This method must be called before building bricks. It prepares the builder for execution by:
     * - Registering providers via [bindProviders]
     * - Resolving provider dependencies and execution order
     * - Attaching configuration to all providers
     *
     * @param digitGridSpec Defines the grid constraints for the digit
     * @param geometryProps Defines shared geometry configuration
     * @throws IllegalStateException if already constructed
     */
    fun construct(
        digitGridSpec: GridSpec,
        geometryProps: GeometryProps
    )

    /**
     * Registers all [GeometryProvider]s required for digit construction.
     *
     * This method is invoked during [construct]. Implementations should register
     * all required providers. The registration order does not affect execution,
     * as dependencies are resolved automatically.
     */
    fun bindProviders()

    /**
     * Builds the brick model for the given digit.
     *
     * This executes all registered providers in dependency order and assembles
     * their outputs to produce the final brick model for the [digit].
     *
     * @param digit The digit to construct (`0..9`) or `-1` for default state
     * @return An ordered list of bricks representing the digit
     * @throws IllegalStateException if the builder is not constructed
     * @throws IllegalArgumentException if the digit is out of range
     */
    fun buildBricks(digit: Int): List<T>

    /**
     * Builds a default brick model.
     *
     * This returns a default brick model that can be used as placeholder before
     * a specific digit brick model is resolved or during transitions.
     *
     * @return An ordered list of default bricks
     * @throws IllegalStateException if the builder is not constructed
     */
    //TODO: Add digit parameter to return digit aware default bricks
    fun buildDefaultBricks(): List<T>

    /**
     * Resets the builder to an unconstructed state and releases internal resources.
     * After calling this, [construct] must be invoked again before builder can be reused.
     *
     * @throws IllegalStateException if the builder is not constructed
     */
    fun destruct()
}

/**
 * Base implementation of [DigitBuilder] that provides provider orchestration and
 * lifecycle management.
 *
 * This class handles:
 * - Builder lifecycle (construction, execution, destruction)
 * - Provider registration and validation
 * - Dependency resolution and execution ordering
 * - Provider execution and result aggregation
 *
 * Subclasses are responsible for:
 * 1. registering all providers using [registerProvider] during [bindProviders]
 * 2. assembling the final bricks by implementing:
 *    - [assembleBricks]
 *    - [assembleDefaultBricks]
 *
 * ### Lifecycle
 *
 * 1. **Construction** via [construct]
 *    - Calls [bindProviders] to register providers
 *    - Validates provider compatibility with current [GridSpec]
 *    - Computes execution order based on dependencies
 *    - Attaches [GridSpec] and [GeometryProps] to all providers
 *
 * 2. **Execution** via [buildBricks], [buildDefaultBricks]
 *    - Creates a digit-scoped [ProviderScope]
 *    - Executes providers in dependency order
 *    - Validates provider outputs
 *    - Delegates to [assembleBricks] for final assembly
 *    - Delegates directly to [assembleDefaultBricks] for default bricks
 *
 * 3. **Destruction** via [destruct]
 *    - Clears provider registry and execution state
 *
 * Providers are executed in dependency order and validated for cyclic dependencies, if found,
 * result in a failure during construction.
 *
 * @param T The concrete [Brick] type produced by this builder
 * @see GeometryProvider
 * @see ProviderScope
 */
abstract class BaseDigitBuilder<T : Brick<T>> : DigitBuilder<T> {

    private var isConstructed = false
    private val providersRegistry = mutableListOf<GeometryProvider<*>>()
    private var executionOrder: List<GeometryProvider<*>> = emptyList()

    protected lateinit var digitGridSpec: GridSpec
        private set

    protected lateinit var geometryProps: GeometryProps
        private set

    /**
     * Initializes the builder with the given grid constraints and geometry properties.
     *
     * This method must be called before building bricks. It prepares the builder for execution by:
     * - Registering providers via [bindProviders]
     * - Resolving provider dependencies and execution order
     * - Attaching configuration to all providers
     *
     * Subclasses may override this method to perform additional setup. Implementations
     * must call `super.construct(...)` as the first operation before accessing any
     * builder state.
     *
     * @param digitGridSpec Defines the grid constraints for the digit
     * @param geometryProps Defines shared geometry configuration
     * @throws IllegalStateException if already constructed
     */
    override fun construct(
        digitGridSpec: GridSpec,
        geometryProps: GeometryProps
    ) {
        check(!isConstructed) { "Builder already constructed" }
        this.digitGridSpec = digitGridSpec
        this.geometryProps = geometryProps
        bindProviders()
        executionOrder = computeExecutionOrder()
        providersRegistry.forEach { it.attachWith(digitGridSpec, geometryProps) }
        isConstructed = true
    }

    final override fun buildBricks(digit: Int): List<T> {
        checkConstructed()
        require(digit in 0..9 || digit == -1) {
            "Builder accepts digit values from 0 to 9 to construct bricks and -1 for default bricks, but got $digit"
        }

        return ProviderScope(digit).use { providerScope ->
            executionOrder.forEach { provider -> executeProvider(provider, providerScope) }
            providerScope.assembleBricks()
        }
    }

    final override fun buildDefaultBricks(): List<T> {
        checkConstructed()
        return assembleDefaultBricks()
    }

    /**
     * Resets the builder to an unconstructed state and releases internal resources.
     * After calling this, [construct] must be invoked again before builder can be reused.
     *
     * Subclasses may override this method to perform additional cleanup. Any subclass
     * cleanup must be completed before calling `super.destruct()`, which must be invoked
     * as the final operation.
     *
     * @throws IllegalStateException if the builder is not constructed
     */
    override fun destruct() {
        checkConstructed()
        providersRegistry.clear()
        executionOrder = emptyList()
        isConstructed = false
    }

    /**
     * Registers a [GeometryProvider] for use during geometry composition.
     *
     * This method must be called during [bindProviders] before construction completes.
     * It validates that the provider is unique for a specific aspect of geometry, identified
     * by its key and compatible with the current [GridSpec] and registers this [provider] with
     * the builder.
     *
     * @param provider The provider to register
     * @throws IllegalStateException if called after construction or if a duplicate provider is registered
     * @throws IllegalStateException if the provider is incompatible with the grid
     */
    protected final fun <P> registerProvider(provider: GeometryProvider<P>) {
        check(!isConstructed) { "Cannot register providers after construction" }
        require(providersRegistry.none { it.key == provider.key }) {
            "Provider with ${provider.key} already registered"
        }

        val providerConsent = provider.matchesWith(digitGridSpec)
        if (providerConsent.hasRejected()) {
            error(providerConsent.getRejectionReason() ?: "Provider '${provider.key}' incompatible with layout")
        }

        providersRegistry.add(provider)
    }

    /**
     * Assembles the final list of bricks from provider outputs.
     *
     * This is called after all providers have executed. Implementations should read
     * data from the current [ProviderScope] and construct the resulting bricks model
     * for [current digit][ProviderScope.digit].
     *
     * @return An ordered list of bricks representing the digit
     */
    protected abstract fun ProviderScope.assembleBricks(): List<T>

    /**
     * Assembles the default brick model.
     *
     * This is used as a placeholder when no specific digit is requested. Implementations
     * define how the default or initial state is represented.
     *
     * @return An ordered list of default bricks
     */
    protected abstract fun assembleDefaultBricks(): List<T>

    private fun <P> executeProvider(
        provider: GeometryProvider<P>,
        providerScope: DefaultProviderScope
    ) {
        providerScope.withProvider(provider) {
            val providerResult = providerScope.provideData()
            check(providerResult.size == digitGridSpec.brickCount) {
                "Provider result must have ${digitGridSpec.brickCount} size, but was ${providerResult.size} for ${provider.key}"
            }
            providerScope.commitResult<P>(key, providerResult)
        }
    }

    /**
     * Computes the execution order of registered providers by resolving dependencies
     * using depth first traversal and fails when cyclic dependencies are detected.
     */
    private fun computeExecutionOrder(): List<GeometryProvider<*>> {
        if (providersRegistry.all { it.dependsOn.isEmpty() }) {
            return providersRegistry.toList()
        }

        val providersByKey = providersRegistry.associateBy { it.key }
        val visitedProviders = mutableMapOf<ProviderKey<*>, VisitState>()
        val orderedProvider = mutableListOf<GeometryProvider<*>>()

        fun dfs(key: ProviderKey<*>) {
            when (visitedProviders[key]) {
                VisitState.VISITING -> error("Failed due to cyclic provider dependency detected at $key")
                VisitState.VISITED -> return
                else -> { /* continue */ }
            }

            visitedProviders[key] = VisitState.VISITING
            val provider = providersByKey[key] ?: error("Unknown provider dependency: $key")
            provider.dependsOn.forEach { depKey -> dfs(depKey) }
            visitedProviders[key] = VisitState.VISITED
            orderedProvider.add(provider)
        }

        providersByKey.keys.forEach { key ->
            if (visitedProviders[key] == null) dfs(key)
        }

        return orderedProvider
    }

    private enum class VisitState { VISITING, VISITED }

    private fun checkConstructed() {
        check(isConstructed) { "Builder not constructed. Call construct() first" }
    }
}