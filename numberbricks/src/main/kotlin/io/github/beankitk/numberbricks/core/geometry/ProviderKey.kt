package io.github.beankitk.numberbricks.core.geometry

/**
 * Represents the identity of a `GeometryProvider` and its provider family.
 *
 * A provider uses its key to declare dependencies on other providers and access their
 * results during execution. Each provider creates its own key, which is associated with
 * a [family] key representing providers that produce the same type of data.
 *
 * A provider representing a family should create a key type for its providers and use
 * its companion object as the family key. The provider's [GeometryProvider.key] should
 * use this key type. Providers extending the family should create their own key by
 * extending the family key type.
 *
 * All keys should be named `Key` and nested inside the provider they belong to for
 * discoverability.
 *
 * Example:
 * ```kotlin
 * abstract class OffsetProvider : BaseGeometryProvider<Offset> {
 *     abstract override val key: Key
 *
 *     interface Key : ProviderKey<Offset> {
 *         override val family: Key
 *             get() = OffsetProvider.Key
 *
 *         companion object : Key
 *     }
 * }
 *
 * class UniformOffset : OffsetProvider() {
 *     override val key: OffsetProvider.Key
 *         get() = Key
 *
 *     object Key : OffsetProvider.Key
 * }
 * ```
 *
 * @param R The type of geometry data produced by the provider.
 * @see GeometryProvider
 */
interface ProviderKey<R : Any> {

    /**
     * Returns the family key representing providers that produce the same type of data.
     *
     * The [family] must always reference the root family key for the corresponding geometry
     * data type. It must not be overridden by derived keys or form a chain of family keys.
     */
    val family: ProviderKey<R>
}
