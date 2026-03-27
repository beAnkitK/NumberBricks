package io.github.beankitk.numberbricks.core.geometry

interface GeometryProvider<T> {

    val key: ProviderKey<T>

    val isAdaptive: Boolean

    val providerGridSpec: GridSpec

    val dependsOn: Set<ProviderKey<*>>

    fun matchesWith(digitGridSpec: GridSpec): Consent

    fun attachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps)

    fun ProviderScope.provideData(): List<T>
}

abstract class AdaptiveProvider<T>: GeometryProvider<T> {

    final override val isAdaptive = true

    private var _providerGridSpec: GridSpec? = null
    final override val providerGridSpec
        get() = _providerGridSpec ?: error("providerGridSpec accessed before attachWith() was called.")

    override fun matchesWith(digitGridSpec: GridSpec): Consent = Consent.Accept

    final override fun attachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        _providerGridSpec = digitGridSpec
        onAttachWith(digitGridSpec, geometryProps)
    }

    protected abstract fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps)
}

abstract class FixedProvider<T>: GeometryProvider<T> {

    final override val isAdaptive = false

    override fun matchesWith(digitGridSpec: GridSpec): Consent {
        val matches = providerGridSpec.rows == digitGridSpec.rows
            && providerGridSpec.cols == digitGridSpec.cols
            && providerGridSpec.brickCount == digitGridSpec.brickCount

        if (!matches) {
            return Consent.Reject("Provider requires gridSpec ${providerGridSpec.asString()} but got ${digitGridSpec.asString()}")
        }

        return Consent.Accept
    }

    final override fun attachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        onAttachWith(digitGridSpec, geometryProps)
    }

    protected abstract fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps)
}

sealed interface Consent {
    data object Accept : Consent
    data class Reject(val reason: String? = null) : Consent

    fun hasRejected(): Boolean = this is Consent.Reject
}

fun Consent.getRejectionReason(): String? = (this as? Consent.Reject)?.reason

inline fun <reified T> GeometryProvider<T>.buildProviderData(factory: (Int) -> T): List<T> {
    return List(providerGridSpec.brickCount) { factory(it) }
}