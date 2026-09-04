package io.github.beankitk.numberbricks.testing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

/** Test key for [Position] providers. */
interface PositionKey : ProviderKey<Position> {
    override val family: PositionKey
        get() = Companion

    companion object : PositionKey
}

data object UniformPositionKey : PositionKey

/** Creates a test provider returning [position] for every brick. */
fun UniformPosition(row: Int, col: Int) = UniformPosition(Position(row, col))

/** Creates a test provider returning [position] for every brick. */
fun UniformPosition(position: Position) =
    AdaptiveTestProvider<Position>(
        key = UniformPositionKey,
        provideData = { List(it.brickCount) { position } },
    )

/** Test key for [Offset] providers. */
interface OffsetKey : ProviderKey<Offset> {
    override val family: OffsetKey
        get() = Companion

    companion object : OffsetKey
}

data object UniformOffsetKey : OffsetKey

/** Creates a test provider returning [offset] for every brick. */
fun UniformOffset(x: Float, y: Float) = UniformOffset(Offset(x, y))

/** Creates a test provider returning [offset] for every brick. */
fun UniformOffset(offset: Offset) =
    AdaptiveTestProvider<Offset>(
        key = UniformOffsetKey,
        provideData = { List(it.brickCount) { offset } },
    )

/** Test key for [Size] providers. */
interface SizeKey : ProviderKey<Size> {
    override val family: SizeKey
        get() = Companion

    companion object : SizeKey
}

data object UniformSizeKey : SizeKey

/** Creates a test provider returning [size] for every brick. */
fun UniformSize(width: Float, height: Float = width) = UniformSize(Size(width, height))

/** Creates a test provider returning [size] for every brick. */
fun UniformSize(size: Size) =
    AdaptiveTestProvider<Size>(key = UniformSizeKey, provideData = { List(it.brickCount) { size } })
