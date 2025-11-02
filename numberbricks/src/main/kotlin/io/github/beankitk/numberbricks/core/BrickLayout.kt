package io.github.beankitk.numberbricks.core

interface BrickLayout<T : BrickData<T>> {

    val rows: Int
    val cols: Int
    val brickCount: Int

    fun getBrickData(digit: Int, isDefault: Boolean): Array<T>
}