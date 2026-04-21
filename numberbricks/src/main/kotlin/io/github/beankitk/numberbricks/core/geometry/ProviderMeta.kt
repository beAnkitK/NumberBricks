package io.github.beankitk.numberbricks.core.geometry

/**
 * Represents a typed key for metadata associated with a [GeometryProvider] and
 * accessible within a [ProviderScope].
 *
 * A [Meta] instance defines a strongly-typed metadata entry where:
 * - [P] is the provider this metadata is associated with
 * - [M] is the type of the stored value
 *
 * Meta instances are declared inside a [MetaGroup] and used by providers to
 * store and retrieve scoped metadata during execution.
 *
 * Each Meta provides a [default] value, computed lazily using [defaultFactory],
 * which is returned when no explicit value is available in the [ProviderScope].
 *
 * @param P The geometry provider type this meta is associated with
 * @param M The type of metadata value
 * @param defaultFactory Produces the default metadata value
 * @see ProviderScope
 */
@ExperimentalProviderMetaApi
class Meta<P : GeometryProvider<*>, M> internal constructor(
    internal val defaultFactory: () -> M
) {
    /**
     * Returns the default value for this [Meta], lazily initialized on first access.
     *
     * This value is used when no explicit metadata is present in the [ProviderScope].
     */
    val default: M by lazy { defaultFactory() }
}

/**
 * Base class for defining and grouping [Meta] keys for geometry providers.
 *
 * A [MetaGroup] provides a structured way to declare multiple [Meta] keys
 * associated with a specific provider type. This enables strongly-typed and
 * discoverable [Meta] definitions.
 *
 * Define the MetaGroup as a `companion object` named `Meta` inside the corresponding
 * [GeometryProvider]. This establishes a consistent and discoverable location for
 * [Meta] keys across providers.
 *
 * Example:
 * ```kotlin
 * class SomeProvider : GeometryProvider<Some> {
 *     companion object Meta : MetaGroup<SomeProvider>() {
 *         val padding = defineMeta { 0f }
 *     }
 * }
 * ```
 *
 * @param P The [GeometryProvider] type this metagroup belongs to
 */
@ExperimentalProviderMetaApi
abstract class MetaGroup<P : GeometryProvider<*>> {

    /**
     * Defines a meta key with a default value factory.
     *
     * @param M The type of metadata value
     * @param defaultFactory Produces the default value for this metadata
     * @return A new [Meta] instance used to store and retrieve this metadata
     * @see Meta
     */
    protected fun <M> defineMeta(
        defaultFactory: () -> M
    ): Meta<P, M> = Meta(defaultFactory)
}