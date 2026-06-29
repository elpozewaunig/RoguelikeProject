package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext

/** Conway's Game of Life: prüft jede Runde die Nachbarn und stirbt oder wächst */
class BlightEnemy(
    stats: StatModifier,
    position: Position,
) : Enemy(stats, position) {
    override val resId: Int = R.drawable.entity_blight

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
