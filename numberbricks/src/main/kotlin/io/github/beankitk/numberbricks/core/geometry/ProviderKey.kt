package io.github.beankitk.numberbricks.core.geometry

/**
 * A type-safe identifier for geometry providers.
 *
 * This inline value class wraps a string ID to provide compile-time type safety
 * when working with different geometry provider implementations. Each provider
 * type should use a distinct [ProviderKey] type parameter.
 *
 * @param T The type of geometry data this key identifies
 * @property id The unique string identifier for this provider
 *
 * Example usage:
 * ```
 * val offsetProviderKey = ProviderKey<Offset>("provider.offset.base")
 * val angleProviderKey = ProviderKey<Angle>("angleprovider.base")
 * ```
 */
@JvmInline
value class ProviderKey<T>(val id: String) {
    override fun toString(): String = id
}