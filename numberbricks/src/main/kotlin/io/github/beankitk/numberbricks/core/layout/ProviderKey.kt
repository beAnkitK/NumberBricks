package io.github.beankitk.numberbricks.core.layout

@JvmInline
value class ProviderKey<T>(val id: String) {
    override fun toString(): String = id
}