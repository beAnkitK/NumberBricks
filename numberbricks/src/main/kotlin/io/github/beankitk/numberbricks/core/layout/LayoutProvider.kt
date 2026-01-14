package io.github.beankitk.numberbricks.core.layout

interface LayoutProvider<T> {

    val key: ProviderKey<T>

    val config: LayoutConfig

    val isAdaptive: Boolean

    val dependsOn: Set<ProviderKey<*>>

    fun matchesWith(properties: LayoutProperties): Consent

    fun attachWith(properties: LayoutProperties)

    fun getProviderData(
        digit: Int,
        providerStore: ProviderStore
    ): List<T>
}

abstract class AdaptiveProvider<T>: LayoutProvider<T> {

    final override val isAdaptive = true

    override var config = emptyLayoutConfig()

    override fun matchesWith(properties: LayoutProperties): Consent = Consent.Accept

    final override fun attachWith(properties: LayoutProperties) {
        config = properties.config
        onAttachWith(properties)
    }

    protected abstract fun onAttachWith(properties: LayoutProperties)
}

abstract class FixedProvider<T>: LayoutProvider<T> {

    final override val isAdaptive = false

    override fun matchesWith(properties: LayoutProperties): Consent {
        val matches = (config.rows == properties.config.rows &&
               config.cols == properties.config.cols &&
               config.bricks == properties.config.bricks)

        if (!matches) {
            return Consent.Reject("Provider requires layout ${config} but got ${properties.config}")
        }

        return Consent.Accept
    }

    final override fun attachWith(properties: LayoutProperties) {
        onAttachWith(properties)
    }

    protected abstract fun onAttachWith(properties: LayoutProperties)
}

sealed interface Consent {
    data object Accept : Consent
    data class Reject(val reason: String? = null) : Consent

    fun hasRejected(): Boolean = this is Consent.Reject
}

fun Consent.getRejectionReason(): String? = (this as? Consent.Reject)?.reason