package io.github.beankitk.numberbricks.core.geometry

@JvmInline
value class ProviderKey<T>(val id: String) {
    override fun toString(): String = id
}