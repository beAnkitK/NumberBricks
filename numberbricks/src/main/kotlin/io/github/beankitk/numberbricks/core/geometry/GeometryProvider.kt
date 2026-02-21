package io.github.beankitk.numberbricks.core.geometry

interface GeometryProvider<T> {

    val key: ProviderKey<T>

    val providerConfig: GridConfig

    val isAdaptive: Boolean

    val dependsOn: Set<ProviderKey<*>>

    fun matchesWith(properties: GeometryProps): Consent

    fun attachWith(properties: GeometryProps)

    fun getProviderData(
        digit: Int,
        providerStore: ProviderStore
    ): List<T>
}

abstract class AdaptiveProvider<T>: GeometryProvider<T> {

    final override val isAdaptive = true

    private var _providerConfig: GridConfig? = null
    final override val providerConfig
        get() = _providerConfig ?: error("providerConfig accessed before attachWith() was called.")

    override fun matchesWith(properties: GeometryProps): Consent = Consent.Accept

    final override fun attachWith(properties: GeometryProps) {
        _providerConfig = properties.config
        onAttachWith(properties)
    }

    protected abstract fun onAttachWith(properties: GeometryProps)
}

abstract class FixedProvider<T>: GeometryProvider<T> {

    final override val isAdaptive = false

    override fun matchesWith(properties: GeometryProps): Consent {
        val matches = providerConfig == properties.config
        if (!matches) {
            return Consent.Reject("Provider requires layout ${providerConfig} but got ${properties.config}")
        }

        return Consent.Accept
    }

    final override fun attachWith(properties: GeometryProps) {
        onAttachWith(properties)
    }

    protected abstract fun onAttachWith(properties: GeometryProps)
}

sealed interface Consent {
    data object Accept : Consent
    data class Reject(val reason: String? = null) : Consent

    fun hasRejected(): Boolean = this is Consent.Reject
}

fun Consent.getRejectionReason(): String? = (this as? Consent.Reject)?.reason

inline fun <reified T> GeometryProvider<T>.buildProviderData(factory: (Int) -> T): List<T> {
    return List(providerConfig.bricks) { factory(it) }
}