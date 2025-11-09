package io.github.beankitk.numberbricks.core

interface BrickLayout<T : BrickData<T>> {

    val rows: Int
    
    val cols: Int
    
    val brickCount: Int

    fun brickDataFor(digit: Int): Array<T>

    fun defaultBrickData(digit: Int): Array<T>
}