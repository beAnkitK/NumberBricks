package io.github.beankitk.numberbricks.testing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.BaseDigitBuilder
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderScope

/** Test implementation of [BaseDigitBuilder]. */
class TestDigitBuilder(
    providers: List<TestGeometryProvider<*>>
) : BaseDigitBuilder<TestBrick>() {

    private val declaredProviders = providers

    override val providers: List<GeometryProvider<*>>
        get() = declaredProviders

    override fun ProviderScope.assembleBricks(): List<TestBrick> {
        return List(digitGridSpec.brickCount) { index ->
            TestBrick(
                index = index,
                position = DEFAULT_POSITION,
                offset = DEFAULT_OFFSET,
                size = DEFAULT_SIZE,
            )
        }
    }

    override fun assembleDefaultBricks(): List<TestBrick> {
        return List(digitGridSpec.brickCount) { index ->
            TestBrick(
                index = index,
                position = DEFAULT_POSITION,
                offset = DEFAULT_OFFSET,
                size = DEFAULT_SIZE,
            )
        }
    }
}
