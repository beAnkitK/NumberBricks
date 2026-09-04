package io.github.beankitk.numberbricks.testing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.BaseDigitBuilder
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderScope

/**
 * Test implementation of [BaseDigitBuilder]. Uses the supplied providers when specified; otherwise
 * uses default uniform position, offset, and size providers.
 */
class TestDigitBuilder(providers: List<TestGeometryProvider<*>>? = null) :
    BaseDigitBuilder<TestBrick>() {

    /** Creates a test builder with the specified position, offset, and size providers. */
    constructor(
        positionProvider: TestGeometryProvider<Position>,
        offsetProvider: TestGeometryProvider<Offset>,
        sizeProvider: TestGeometryProvider<Size>,
    ) : this(listOf(positionProvider, offsetProvider, sizeProvider))

    private val declaredProviders = providers

    var onConstructed: (() -> Unit)? = null
    var onDestroying: (() -> Unit)? = null

    override val providers: List<GeometryProvider<*>>
        get() {
            return if (declaredProviders == null) {
                listOf(
                    UniformPosition(Position(row = 0, col = 0)),
                    UniformOffset(Offset(x = 0f, y = 0f)),
                    UniformSize(Size(width = 1f, height = 1f)),
                )
            } else declaredProviders
        }

    override fun onConstructed() {
        onConstructed?.invoke()
    }

    override fun ProviderScope.assembleBricks(): List<TestBrick> {
        val positions = if (hasResult(PositionKey)) resultOf<Position>(PositionKey) else null
        val offsets = if (hasResult(OffsetKey)) resultOf<Offset>(OffsetKey) else null
        val sizes = if (hasResult(SizeKey)) resultOf<Size>(SizeKey) else null

        return List(digitGridSpec.brickCount) { index ->
            TestBrick(
                index = index,
                position = positions?.get(index) ?: DEFAULT_POSITION,
                offset = offsets?.get(index) ?: DEFAULT_OFFSET,
                size = sizes?.get(index) ?: DEFAULT_SIZE,
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

    override fun onDestroying() {
        onDestroying?.invoke()
    }
}
