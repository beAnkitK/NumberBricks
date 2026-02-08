package io.github.beankitk.numberbricks.core.geometry

/**
 * Defines the contract for building brick-based digit representations.
 *
 * A [DigitBuilder ]is responsible for constructing the visual representation of digits (0-9)
 * as a collection of [Brick] elements. It orchestrates geometry providers to compute brick
 * positions, sizes, and other properties based on the digit's geometry configuration.
 *
 * Implementations must be stateless and reusable across multiple digit construction requests.
 * A single shared instance is typically used for all digits in a number display.
 *
 * @param T The concrete [Brick] type produced by this builder
 *
 * @see BaseDigitBuilder
 * @see Brick
 * @see GeometryProps
 */
interface DigitBuilder<T : Brick<T>> {

    /**
     * Initializes the builder with geometry properties and prepares it for brick construction.
     *
     * This method must be called before any brick construction operations. It should triggers
     * provider registration via [bindProviders], handle dependency resolution and execution ordering
     * and locks the builder configuration.
     *
     * @param properties The geometry configuration defining grid dimensions, brick sizing,
     *                   and other layout parameters
     * @throws IllegalStateException if the builder is already constructed
     *
     * @see destruct
     */
    fun construct(properties: GeometryProps)

    /**
     * Registers all geometry providers required for brick construction.
     *
     * This method should be called internally by [construct] and should register all necessary
     * providers defined by implementing [DigitBuilder]. Provider registration is only
     * allowed during builder construction phase and its order does not matter. Implementations
     * can register providers in any order;
     *
     * @see construct
     * @see BaseDigitBuilder.registerProvider
     */
    fun bindProviders()

    /**
     * Constructs the brick representation for a specific digit.
     *
     * Executes all registered geometry providers in dependency order, collects their
     * outputs, and assembles the final list of positioned, sized bricks that form
     * the visual representation of the digit.
     *
     * @param digit The digit to construct (must be in range 0-9)
     * @return An ordered list of bricks forming the digit's visual representation
     * @throws IllegalStateException if the builder is not constructed
     */
    fun getBricksFor(digit: Int): List<T>

    /**
     * Constructs a default brick representation used as a placeholder or initial state.
     *
     * Returns a neutral brick configuration that can be displayed before any specific
     * digit is shown, or used as a fallback state during animations.
     *
     * @return An ordered list of bricks forming the default representation
     * @throws IllegalStateException if the builder is not constructed
     */
     //TODO: Add digit parameter to return digit aware deafult bricks
    fun defaultBricks(): List<T>

    /**
     * Destroy the builder and releases all resources.
     *
     * Clears the provider registry, execution order, and resets the builder to an
     * unconstructed state. After calling this method, [construct] must be called
     * again before the builder can be used.
     *
     * @see construct
     */
    fun destruct()
}

/**
 * Abstract base implementation of [DigitBuilder] providing standard orchestration logic.
 *
 * This class implements the complete builder lifecycle and provider coordination system.
 * Subclasses only need to implement [buildBricks] to define how providers' outputs are
 * assembled into the final brick list.
 *
 * ## Lifecycle
 *
 * 1. **Construction**: Call [construct] with geometry properties
 *    - Invokes [bindProviders] to register all providers
 *    - Computes provider execution order via dependency resolution
 *    - Attaches properties to all providers
 *    - Locks further provider registration
 *
 * 2. **Brick Construction**: Call [getBricksFor] or [defaultBricks]
 *    - Creates a digit-scoped provider store
 *    - Executes providers in computed dependency order
 *    - Collects provider outputs in the store
 *    - Calls [buildBricks] to assemble final brick list
 *
 * 3. **Destruction**: Call [destruct]
 *    - Clears provider registry and execution order
 *    - Resets to unconstructed state
 *
 * Providers are registered via [registerProvider] during [bindProviders], executed in dependency order, and
 * validated for cyclic dependencies.
 *
 * @param T The concrete brick type produced by this builder
 *
 * @see DigitBuilder
 * @see GeometryProvider
 * @see ProviderStore
 */
abstract class BaseDigitBuilder<T : Brick<T>> : DigitBuilder<T> {

    private var isConstructed = false
    private val providersRegistry = mutableListOf<GeometryProvider<*>>()
    private var executionOrder: List<GeometryProvider<*>> = emptyList()

    /**
     * The geometry properties configured during construction.
     *
     * Available to subclasses after [construct] is called.
     */
    protected lateinit var properties: GeometryProps
        private set

    /**
     * Initializes the builder with the provided geometry properties.
     *
     * Subclasses may override this method to extend the construction process;
     * however, they must invoke `super.construct(properties)` as the first
     * operation before executing any subclass-specific logic.
     *
     * @throws IllegalStateException if already constructed
     */
    override fun construct(properties: GeometryProps) {
        require(!isConstructed) { "Builder already constructed" }
        this.properties = properties
        bindProviders()
        executionOrder = computeExecutionOrder()
        providersRegistry.forEach { it.attachWith(properties) }
        isConstructed = true
    }

    final override fun getBricksFor(digit: Int): List<T> {
        checkConstructed()
        val providerStore = DefaultProviderStore(digit, properties.config)
        executionOrder.forEach { provider ->
            computeDataFor(digit, provider, providerStore)
        }
        return buildBricks(digit, providerStore)
    }

    // TODO: Allow overriding in impl class when the interface todo is solved
    final override fun defaultBricks(): List<T> {
        checkConstructed()
        return getBricksFor(-1)
    }

    /**
    * Disposes the builder and releases associated resources.
    *
    * Subclasses may override this method to extend the teardown process; however,
    * any subclass-specific cleanup must be performed before invoking
    * `super.destruct()`, which must be called as the final operation.
    */
    override fun destruct() {
        providersRegistry.clear()
        executionOrder = emptyList()
        isConstructed = false
    }

    /**
     * Registers a geometry provider for use during brick construction.
     *
     * Must be called by implementing [DigitBuilder] within [bindProviders] before
     * construction completes. Validates provider uniqueness and compatibility with geometry
     * properties.
     *
     * @param P The type of data this provider produces
     * @param provider The geometry provider to register
     * @throws IllegalStateException if called after construction, provider key is duplicate,
     *                               or provider is incompatible with properties
     */
    protected final fun <P> registerProvider(provider: GeometryProvider<P>) {
        require(!isConstructed) { "Cannot register providers after construction" }
        require(providersRegistry.none { it.key == provider.key }) {
            "Provider with ${provider.key} already registered"
        }

        val providerConsent = provider.matchesWith(properties)
        if (providerConsent.hasRejected()) {
            error(providerConsent.getRejectionReason() ?:
                "Provider '${provider.key}' incompatible with layout")
        }

        providersRegistry.add(provider)
    }

    /**
     * Assembles the final list of bricks from provider outputs.
     *
     * Called by [getBricksFor] after all providers have executed and their data is stored in [ProviderStore].
     * Implement this to define how provider data is transformed into positioned, sized bricks. Use [ProviderStore.get]
     * from [store] using the [ProviderKey] to get the provider data.
     *
     * @param digit The digit being constructed (0-9, or -1 for default state)
     * @param store Provider store containing all computed provider outputs
     * @return An ordered list of bricks forming the digit's representation
     */
    protected abstract fun buildBricks(digit: Int, store: ProviderStore): List<T>

    //protected abstract fun buildDefaultBricks(digit: Int): List<T>

    private fun <P> computeDataFor(
        digit: Int,
        provider: GeometryProvider<P>,
        providerStore: DefaultProviderStore
    ) {
        val providerData = provider.getProviderData(digit, providerStore)
        providerStore.store<P>(provider.key, providerData)
    }

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
        require(isConstructed) { "Builder not constructed. Call construct() first" }
    }
}