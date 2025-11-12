package io.github.beankitk.numberbricks.data

enum class CornerPoint {
    TopLeft, TopRight, BottomRight, BottomLeft
}

enum class CornerType {
    Edge,
    Outer,
    CornerNeighbor,
    Corner,
    JointInline,
    Joint,
    Inner
}

data class CornerProfile(
    val topLeft: CornerType,
    val topRight: CornerType,
    val bottomRight: CornerType,
    val bottomLeft: CornerType
)