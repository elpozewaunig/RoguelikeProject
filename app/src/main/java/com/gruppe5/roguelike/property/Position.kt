package com.gruppe5.roguelike.property

import kotlin.math.abs

data class Position(val x: Int, val y: Int) {
    fun distanceTo(other: Position): Int = abs(x - other.x) + abs(y - other.y) //Manhattan und so

    operator fun plus(other: Position): Position = Position(x + other.x, y + other.y) //infix override :pog:
}
