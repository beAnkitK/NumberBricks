package io.github.beankitk.numberbricks.sample.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path

fun getCirclePath(radius: Float) = 
    Path().apply {
        addOval(Rect(center = Offset.Zero, radius = radius))
    }

fun getDoubleDashPath(
    lineWidth: Float, 
    dashSpacing: Float, 
    strokeWidth: Float
) = Path().apply {
        moveTo(0f, 0f)
        lineTo(lineWidth, 0f)
        lineTo(lineWidth, strokeWidth)
        lineTo(0f, strokeWidth)
        close() 
        moveTo(0f, strokeWidth + dashSpacing)
        lineTo(lineWidth, strokeWidth + dashSpacing)
        lineTo(lineWidth, 2 * strokeWidth + dashSpacing)
        lineTo(0f, 2 * strokeWidth + dashSpacing)
        close()
        translate(Offset(0f, -(strokeWidth + dashSpacing)/2))
    }

fun getSquarePath(
    side: Float, 
    strokeWidth: Float
) = Path().apply {
        moveTo(0f, 0f)
        lineTo(side, 0f)
        lineTo(side, strokeWidth)
        lineTo(0f, strokeWidth)
        close()
        
        moveTo(side, 0f)
        lineTo(side, side)
        lineTo(side - strokeWidth, side)
        lineTo(side - strokeWidth, 0f)
        close()
        
        moveTo(side, side)
        lineTo(0f, side)
        lineTo(0f, side - strokeWidth)
        lineTo(side, side - strokeWidth)
        close()
        
        moveTo(0f, 0f)
        lineTo(strokeWidth, 0f)
        lineTo(strokeWidth, side)
        lineTo(0f, side)
        close()
        
        translate(Offset(-side/2, -side/2))
    }

fun getWoodenPath(
    lineWidth: Float,
    dashSpacing: Float,
    strokeWidth: Float
) = Path().apply {
        moveTo(0f, 0f)
        lineTo(lineWidth, 0f)
        lineTo(lineWidth, strokeWidth)
        lineTo(-lineWidth, 0f)
        close()
        moveTo(0f, strokeWidth + dashSpacing)
        lineTo(lineWidth, strokeWidth + dashSpacing)
        lineTo(lineWidth, 2 * strokeWidth + dashSpacing)
        lineTo(-lineWidth,  2 * strokeWidth + dashSpacing)
        close()
        translate(Offset(0f, -(strokeWidth + dashSpacing)/2))
    }
    
fun getZigZagPath(
    height: Float,
    width: Float,
    lineWidth: Float
) = Path().apply {
        val zigZagWidth = width
        val zigZagHeight = height
        val zigZagLineWidth = lineWidth
        val shapeVerticalOffset = (zigZagHeight / 2) / 2
        val shapeHorizontalOffset = (zigZagHeight / 2) / 2
            
        moveTo(0f, 0f)
        lineTo(zigZagWidth / 2, zigZagHeight / 2)
        lineTo(zigZagWidth, 0f)
        lineTo(zigZagWidth, 0f + zigZagLineWidth)
        lineTo(zigZagWidth / 2, zigZagHeight / 2 + zigZagLineWidth)
        lineTo(0f, 0f + zigZagLineWidth)
        translate(Offset(-shapeHorizontalOffset, -shapeVerticalOffset))
    }