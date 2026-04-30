package io.github.beankitk.numberbricks.core.geometry

import androidx.collection.ObjectList
import androidx.collection.MutableObjectList
import androidx.collection.MutableScatterMap

/**
 * Defines the contract for constructing digit geometry as a collection of [Brick]s.
 *
 * A [DigitBuilder] defines how a digit is represented as an ordered list of bricks, called the
 * brick model of digit. It operates on a given [GridSpec] and [GeometryProps], which together
 * describe the structural grid and shared geometry configuration used during geometry composition.
 *
 * The builder coordinates a set of [GeometryProvider]s to compute the data required to construct
 * the brick model of a digit. Each provider contributes a specific aspect of geometry, and their
 * execution is resolved based on declared dependencies.
 *
 * Before building bricks, the builder must be initialized using [construct]. This prepares the
 * builder by registering providers via [bindProviders], resolving their dependencies, and attaching
 * configuration to each provider.
 *
 * Once constructed, [buildBricks] returns the brick model for a given digit by executing providers
 * in dependency order and combining their results. [buildDefaultBricks] returns a default brick
 * model that can be used as a placeholder model for initial or fallback state.
 *
 * The builder does not retain digit-specific state and can be reused across multiple digits after
 * construction. Calling [destruct] resets the builder to an uninitialized state.
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
    fun construct(digitGridSpec: GridSpec, geometryProps: GeometryProps)

    /**
     * Registers and returns all [GeometryProvider]s required for digit construction.
     *
     * This method is invoked during [construct]. Implementations are expected to register providers
     * using the implementation-specific registration mechanism and return the resulting providers as
     * a list.
     *
     * The registration order does not affect execution, as dependencies are resolved automatically.
     *
     * @return A list containing all registered [GeometryProvider] instances required for digit construction.
     */
    fun bindProviders(): List<GeometryProvider<*>>

    /**
     * Builds the brick model for the given digit.
     *
     * This executes all registered providers in dependency order and assembles their outputs to
     * produce the final brick model for the [digit].
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
     * This returns a default brick model that can be used as placeholder before a specific digit
     * brick model is resolved or during transitions.
     *
     * @return An ordered list of default bricks
     * @throws IllegalStateException if the builder is not constructed
     */
    // TODO: Add digit parameter to return digit aware default bricks
    fun buildDefaultBricks(): List<T>

    /**
     * Resets the builder to an unconstructed state and releases internal resources. After calling
     * this, [construct] must be invoked again before builder can be reused.
     *
     * @throws IllegalStateException if the builder is not constructed
     */
    fun destruct()
}

/**
 * Base implementation of [DigitBuilder] that provides provider orchestration and lifecycle
 * management.
 *
 * This class handles:
 * - Builder lifecycle (construction, execution, destruction)
 * - Provider registration and validation
 * - Dependency resolution and execution ordering
 * - Provider execution and result aggregation
 *
 * Subclasses are responsible for:
 * 1. registering all providers using [buildProviders] during [bindProviders]
 * 2. assembling the final bricks by implementing:
 *     - [assembleBricks]
 *     - [assembleDefaultBricks]
 *
 * ### Lifecycle
 *
 * 1. **Construction** via [construct]
 *     - Calls [bindProviders] to register providers
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
 * 3. **Destruction** via [destruct]
 *     - Clears provider registry and execution state
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
    private var providersRegistry: List<GeometryProvider<*>> = emptyList()

    private var _digitGridSpec: GridSpec? = null
    private var _geometryProps: GeometryProps? = null

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

    /**
     * Initializes the builder with the given grid constraints and geometry properties.
     *
     * This method must be called before building bricks. It prepares the builder for execution by:
     * - Registering providers via [bindProviders]
     * - Resolving provider dependencies and execution order
     * - Attaching configuration to all providers
     *
     * Subclasses may override this method to perform additional setup. Implementations must call
     * `super.construct(...)` as the first operation before accessing any builder state.
     *
     * @param digitGridSpec Defines the grid constraints for the digit
     * @param geometryProps Defines shared geometry configuration
     * @throws IllegalStateException if already constructed
     */
    override fun construct(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        check(!isConstructed) { "DigitBuilder already constructed" }
        try {
            _digitGridSpec = digitGridSpec
            _geometryProps = geometryProps
            providersRegistry = bindProviders()
        } catch (throwable: Throwable) {
            _digitGridSpec = null
            _geometryProps = null
            providersRegistry = emptyList()
            throw throwable
        }

        providersRegistry.forEach { it.attachWith(digitGridSpec, geometryProps) }
        isConstructed = true
    }

    final override fun buildBricks(digit: Int): List<T> {
        checkConstructed()
        require(digit in 0..9 || digit == -1) {
            "DigitBuilder accepts digit values from 0 to 9 to construct bricks and -1 for default bricks, but got $digit"
        }

        return ProviderScope(digit).use { providerScope ->
            providersRegistry.forEach { provider -> executeProvider(provider, providerScope) }
            providerScope.assembleBricks()
        }
    }

    final override fun buildDefaultBricks(): List<T> {
        checkConstructed()
        return assembleDefaultBricks()
    }

    /**
     * Resets the builder to an unconstructed state and releases internal resources. After calling
     * this, [construct] must be invoked again before builder can be reused.
     *
     * Subclasses may override this method to perform additional cleanup. Any subclass cleanup must
     * be completed before calling `super.destruct()`, which must be invoked as the final operation.
     *
     * @throws IllegalStateException if the builder is not constructed
     */
    override fun destruct() {
        checkConstructed()
        _digitGridSpec = null
        _geometryProps = null
        providersRegistry = emptyList()
        isConstructed = false
    }

    /**
     * Builds the [GeometryProvider] list used by [BaseDigitBuilder] for geometry composition.
     *
     * Providers must be registered through [ProviderRegistrar.register] within the
     * supplied [block]. The resulting list is returned in dependency-resolved order
     * and is used during geometry composition.
     *
     * @param block Scope used to register [GeometryProvider] instances for this builder.
     * @return A list of registered [GeometryProvider] instances in dependency-resolved order.
     * @see bindProviders
     */
    protected inline fun buildProviders(
        block: ProviderRegistrar.() -> Unit,
    ): List<GeometryProvider<*>> {
        val registry = ProviderRegistry(digitGridSpec)
        registry.block()
        return registry.providerRegistry
    }

    /**
     * Registers and returns all [GeometryProvider]s required for digit construction.
     *
     * Implementations should register providers using [buildProviders] and return
     * the resulting list. Registration order does not affect execution, as
     * dependencies are automatically resolved in the returned list.
     *
     * Usage:
     * ```
     * override fun bindProviders(): List<GeometryProvider<*>> {
     *     return buildProviders {
     *         register(SomeProvider)
     *         register(AnotherProvider)
     *     }
     * }
     * ```
     *
     * @return A dependency-resolved list of registered [GeometryProvider]
     * instances required for digit construction.
     */
    abstract override fun bindProviders(): List<GeometryProvider<*>>

    /**
     * Assembles the final list of bricks from provider outputs.
     *
     * This is called after all providers have executed. Implementations should read data from the
     * current [ProviderScope] and construct the resulting bricks model for
     * [current digit][ProviderScope.digit].
     *
     * @return An ordered list of bricks representing the digit
     */
    protected abstract fun ProviderScope.assembleBricks(): List<T>

    /**
     * Assembles the default brick model.
     *
     * This is used as a placeholder when no specific digit is requested. Implementations define how
     * the default or initial state is represented.
     *
     * @return An ordered list of default bricks
     */
    protected abstract fun assembleDefaultBricks(): List<T>

    private fun <P> executeProvider(
        provider: GeometryProvider<P>,
        providerScope: DefaultProviderScope,
    ) {
        providerScope.withProvider(provider) {
            val providerResult = providerScope.provideData()
            check(providerResult.size == digitGridSpec.brickCount) {
                "Provider result must have ${digitGridSpec.brickCount} size, but was ${providerResult.size} for ${provider.key}"
            }
            providerScope.commitResult<P>(key, providerResult)
        }
    }

    private fun checkConstructed() {
        check(isConstructed) { "DigitBuilder not constructed. Call construct() first." }
    }
}

/**
 * Contract for registering [GeometryProvider] instances used during geometry composition.
 *
 * Used by [BaseDigitBuilder] to define the provider registration mechanism.
 *
 * @see BaseDigitBuilder.buildProviders
 */
interface ProviderRegistrar {

    /**
     * Registers a [GeometryProvider] for the current builder context.
     *
     * The provider must be unique for its geometry key and compatible with the
     * active [GridSpec].
     *
     * @param provider The provider to register.
     * @throws IllegalStateException If a provider with the same key is already registered.
     * @throws IllegalStateException If the provider is incompatible with the active [GridSpec].
     * @see BaseDigitBuilder.buildProviders
     */
    fun <P> register(provider: GeometryProvider<P>)
}

@PublishedApi
internal class ProviderRegistry(private val gridSpec: GridSpec) : ProviderRegistrar {

    private val providers = MutableObjectList<GeometryProvider<*>>(5)
    private var resolved = false

    val providerRegistry: List<GeometryProvider<*>>
        get() {
            if (!resolved) {
                var hasDependencies = false
                for (i in 0 until providers.size) {
                    if(providers[i].dependsOn.isNotEmpty()) {
                        hasDependencies = true
                        break
                    }
                }

                if (!hasDependencies) {
                    resolved = true
                } else {
                    val orderedProvider = computeExecutionOrder(providers)
                    providers.clear()
                    providers.addAll(orderedProvider)
                    resolved = true
                }
            }

            return List<GeometryProvider<*>>(providers.size) { index -> providers[index] }
        }

    override fun <P> register(provider: GeometryProvider<P>) {
        check(!resolved) { "Cannot register providers after execution order is resolved" }
        providers.forEach {
            check(it.key != provider.key) { "Provider with key '${provider.key}' is already registered" }
        }
        val providerConsent = provider.matchesWith(gridSpec)
        if (providerConsent.hasRejected()) {
            error(providerConsent.getRejectionReason()
                ?: "Provider '${provider.key}' is incompatible with the current grid")
        }
        providers.add(provider)
    }

    /**
     * Computes the execution order of registered providers by resolving dependencies using depth
     * first traversal and fails with [IllegalStateException] when cyclic dependencies are detected.
     */
    private fun computeExecutionOrder(
        providers: ObjectList<GeometryProvider<*>>
    ): ObjectList<GeometryProvider<*>> {
        val size = providers.size
        val providersByKey = MutableScatterMap<ProviderKey<*>, GeometryProvider<*>>(size)
        providers.forEach { providersByKey.put(it.key, it) }

        val providerVisitState = MutableScatterMap<ProviderKey<*>, VisitState>(size)
        val orderedProvider = MutableObjectList<GeometryProvider<*>>(size)

        fun dfs(key: ProviderKey<*>) {
            when (providerVisitState[key]) {
                VisitState.VISITING ->
                    error("Failed due to cyclic provider dependency detected at $key")
                VisitState.VISITED -> return
                else -> {
                    /* continue */
                }
            }

            providerVisitState[key] = VisitState.VISITING
            val provider = providersByKey[key] ?: error("Unknown provider with key: $key found")
            provider.dependsOn.forEach { depKey -> dfs(depKey) }
            providerVisitState[key] = VisitState.VISITED
            orderedProvider.add(provider)
        }

        providersByKey.forEachKey { if (providerVisitState[it] == null) dfs(it) }
        return orderedProvider
    }

    private enum class VisitState {
        VISITING,
        VISITED,
    }
}
