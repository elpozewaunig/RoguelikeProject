package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.utility.Pathfinding
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Der Läufer aus Schach (farbgebunden wie im echten Schach) */
@Serializable
@SerialName("bishop")
class BishopEnemy(
    override var stats: StatModifier,
    override var position: Position,
) : Chaser() {
    override val resId: Int get() = R.drawable.entity_bishop
    override val moves: List<Position> get() = Pathfinding.DIAGONAL_MOVES
    override val heuristic: (Position, Position) -> Double get() = Pathfinding.CHEBYSHEV
}
