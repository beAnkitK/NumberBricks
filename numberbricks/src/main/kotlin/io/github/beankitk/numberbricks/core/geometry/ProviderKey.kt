package io.github.beankitk.numberbricks.core.geometry

/**
 * Represents a type-safe key used to identify and access geometry providers.
 *
 * Each [ProviderKey] is associated with a specific result type [R], ensuring that only
 * matching provider data can be requested or retrieved. Each provider family should use
 * a distinct [ProviderKey].
 *
 * Example:
 * ```kotlin
 * val offsetProviderKey = ProviderKey<Offset>("offsetprovider.base")
 * val angleProviderKey = ProviderKey<Angle>("angleprovider.base")
 * ```
 *
 * @param R The result type associated with this provider key
 * @property providerId The unique identifier for this provider key
 * @see GeometryProvider
 */
@JvmInline
value class ProviderKey<R : Any>(val providerId: String) {
    override fun toString(): String = providerId
}
