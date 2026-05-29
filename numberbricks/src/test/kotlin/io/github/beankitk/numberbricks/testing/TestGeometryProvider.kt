package io.github.beankitk.numberbricks.testing

import io.github.beankitk.numberbricks.core.geometry.AdaptiveGridPolicy
import io.github.beankitk.numberbricks.core.geometry.BaseGeometryProvider
import io.github.beankitk.numberbricks.core.geometry.Consent
import io.github.beankitk.numberbricks.core.geometry.FixedGridPolicy
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderGridPolicy
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope

/**
 * Creates a [TestGeometryProvider] with a fixed [GridSpec]. Use [provideData] to
 * define the data returned by the provider.
 */
fun <T : Any> FixedTestProvider(
    key: ProviderKey<T>,
    gridSpec: GridSpec,
    dependsOn: Set<ProviderKey<*>> = emptySet(),
    doMatch: ((GridSpec) -> Consent)? = null,
    onAttach: ((GridSpec, GeometryProps) -> Unit)? = null,
    onDetach: (() -> Unit)? = null,
    provideData: ProviderScope.(GridSpec) -> List<T>,
) = TestGeometryProvider<T>(
    key = key,
    dependsOn = dependsOn,
    providerGridPolicy = FixedGridPolicy(gridSpec),
    doMatch = doMatch,
    onAttach = onAttach,
    onDetach = onDetach,
    provideData = provideData
)

/**
 * Creates a [TestGeometryProvider] with an adaptive grid policy. Use [provideData] to
 * define the data returned by the provider.
 */
fun <T : Any> AdaptiveTestProvider(
    key: ProviderKey<T>,
    dependsOn: Set<ProviderKey<*>> = emptySet(),
    doMatch: ((GridSpec) -> Consent)? = null,
    onAttach: ((GridSpec, GeometryProps) -> Unit)? = null,
    onDetach: (() -> Unit)? = null,
    provideData: ProviderScope.(GridSpec) -> List<T>,
) = TestGeometryProvider<T>(
    key = key,
    dependsOn = dependsOn,
    providerGridPolicy = AdaptiveGridPolicy,
    doMatch = doMatch,
    onAttach = onAttach,
    onDetach = onDetach,
    provideData = provideData
)

/** Configurable [BaseGeometryProvider] implementation for testing. */
class TestGeometryProvider<T : Any>(
    override val key: ProviderKey<T>,
    override val dependsOn: Set<ProviderKey<*>>,
    override val providerGridPolicy: ProviderGridPolicy,
    private val doMatch: ((GridSpec) -> Consent)? = null,
    private val onAttach: ((GridSpec, GeometryProps) -> Unit)? = null,
    private val onDetach: (() -> Unit)? = null,
    private val provideData: ProviderScope.(GridSpec) -> List<T>
) : BaseGeometryProvider<T>() {

    override fun doMatch(digitGridSpec: GridSpec): Consent {
        return doMatch?.invoke(digitGridSpec) ?: super.doMatch(digitGridSpec)
    }

    override fun onAttach(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        onAttach?.invoke(digitGridSpec, geometryProps)
    }

    override fun ProviderScope.provideData(): List<T> = provideData(providerGridSpec)

    override fun onDetach() {
        onDetach?.invoke()
    }
}
