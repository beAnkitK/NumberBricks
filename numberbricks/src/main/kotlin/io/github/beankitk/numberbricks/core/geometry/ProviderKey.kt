package io.github.beankitk.numberbricks.core.geometry

/**
 * Represents a type-safe key used to identify and access geometry providers.
 *
 * Each [ProviderKey] is associated with a specific data type [T], ensuring that only matching
 * provider data can be requested or retrieved. Each provider aspect should use a distinct
 * [ProviderKey] type.
 *
 * Example:
 * ```kotlin
 * val offsetProviderKey = ProviderKey<Offset>("provider.offset.base")
 * val angleProviderKey = ProviderKey<Angle>("angleprovider.base")
 * ```
 *
 * @param T The type of data associated with this provider key
 * @property providerId The unique identifier for this key
 * @see GeometryProvider
 */
@JvmInline
value class ProviderKey<T>(val providerId: String) {
    override fun toString(): String = providerId
}
