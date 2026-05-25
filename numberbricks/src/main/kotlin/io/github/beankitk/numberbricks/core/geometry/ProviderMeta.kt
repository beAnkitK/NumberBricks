package io.github.beankitk.numberbricks.core.geometry

/**
 * Represents a typed key that identifies auxiliary data shared between geometry
 * providers for use by other providers to compute their results.
 *
 * Meta refers to auxiliary data or values shared between [GeometryProvider]s
 * through a [ProviderScope] during result computation. These values can be used
 * by other providers to compute their own results and can include provider
 * inputs, calculated values, or other intermediate values. Each such value is
 * called a meta value, or simply meta, and is identified by a [MetaKey].
 *
 * Each meta key has a single owning [GeometryProvider], which is responsible for
 * providing its value. Using this key, other providers can check for or retrieve
 * the associated value from the [ProviderScope].
 *
 * @param P The [GeometryProvider] that owns this meta key.
 * @param M The type of the meta value identified by this key.
 * @property name The name used to identify this meta key.
 * @see ProviderScope
 */
class MetaKey<P : GeometryProvider<*>, M> internal constructor(
    val name: String,
) {
    override fun toString(): String = "MetaKey:$name"
}

/**
 * Defines a meta key for a geometry provider.
 *
 * The key identifies a meta value of type [M] owned by the [GeometryProvider]
 * type [P]. The owning provider can provide a value for the key, while other
 * providers can use the key to check for or retrieve the value from the
 * [ProviderScope].
 *
 * @param P The [GeometryProvider] that owns the meta key.
 * @param M The type of the meta value identified by the key.
 * @param name The name used to identify the meta key. Defaults to `"Unknown"`
 *   when not specified.
 * @return A [MetaKey] identifying the meta value.
 */
fun <P : GeometryProvider<*>, M> defineMeta(
    name: String = "Unknown"
): MetaKey<P, M> = MetaKey(name)

/**
 * A provider-bound scope for providing meta to a [ProviderScope].
 *
 * The scope is bound to a specific [GeometryProvider] through [P]. Only [MetaKey]s
 * owned by that provider can be provided through this scope. This enforces meta
 * ownership at compile time.
 *
 * Use [providedBy] to associate a meta key with its value.
 *
 * @param P The [GeometryProvider] that owns the meta keys accepted by this scope.
 */
interface MetaProviderScope<P : GeometryProvider<*>> {

    /**
     * Provides [value] for this meta key. The key must be owned by the provider
     * associated with this scope.
     *
     * @param M The type of the meta value.
     * @param value The value to provide for this meta key.
     */
    infix fun <M> MetaKey<P, M>.providedBy(value: M)
}