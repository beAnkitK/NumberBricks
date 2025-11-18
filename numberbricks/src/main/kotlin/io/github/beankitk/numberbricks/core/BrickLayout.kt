package io.github.beankitk.numberbricks.core

import io.github.beankitk.numberbricks.core.data.BrickData
import io.github.beankitk.numberbricks.core.data.LayoutData

interface BrickLayout<T : BrickData<T>> : LayoutData {

    fun brickDataFor(digit: Int): Array<T>

    fun defaultBrickData(digit: Int): Array<T>
}