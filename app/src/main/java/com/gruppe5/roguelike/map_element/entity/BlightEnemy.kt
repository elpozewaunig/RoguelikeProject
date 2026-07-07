package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Conway's Game of Life: prüft jede Runde die Nachbarn und stirbt oder wächst */
@Serializable
@SerialName("blight")
class BlightEnemy(
    override var stats: StatModifier,
    override var position: Position,
) : Enemy() {
    override val resId: Int get() = R.drawable.entity_blight

    override fun act(ctx: TurnContext, times: Int): List<Action> {
        val living = blightNeighbours(ctx, position)
        if (living < 2 || living > 3) return listOf(Action.Die)

        val blocked = MOORE.mapNotNull { ctx.getEntityAt(position + it) }
            .firstOrNull { it.groups.any(targets::contains) }
        if (blocked != null) return List(times) { Action.Attack(blocked) }

        val births = MOORE.map { position + it }
            .filter { isBirthCell(ctx, it) }
            .map { Action.Spawn(BlightEnemy(StatModifier(maxHealth = 1, health = 1, attack = 1), it)) }
        return births.ifEmpty { listOf(Action.Wait) }
    }

    private fun isBirthCell(ctx: TurnContext, pos: Position): Boolean {
        val row = ctx.map.getOrNull(pos.y) ?: return false
        val tile = row.getOrNull(pos.x) ?: return false
        if (tile.type.isWall || ctx.getEntityAt(pos) != null) return false
        return blightNeighbours(ctx, pos) == 3
    }

    private fun blightNeighbours(ctx: TurnContext, pos: Position): Int =
        MOORE.count { ctx.getEntityAt(pos + it) is BlightEnemy }

    companion object {
        private val MOORE: List<Position> = listOf(
            Position(-1, -1), Position(0, -1), Position(1, -1),
            Position(-1, 0), Position(1, 0),
            Position(-1, 1), Position(0, 1), Position(1, 1)
        )
    }
}
