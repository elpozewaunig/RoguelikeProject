package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.utility.Pathfinding
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Jagt per A*, beißt orthogonal und lässt eine Schwanzspur der Länge l zurück */
@Serializable
@SerialName("snakehead")
class SnakeHeadEnemy(
    override var stats: StatModifier,
    override var position: Position,
    val bodyLifetime: Int = 4,
) : Chaser() {
    override val resId: Int get() = R.drawable.entity_snakehead
    var alive: Boolean = true

    override fun act(ctx: TurnContext, times: Int): List<Action> {
        val target = ctx.nearestTo(position, targets, this)
        val from = position
        val actions = mutableListOf<Action>()

        if (target != null && moves.any { from + it == target.position }) actions += Action.Attack(target)

        val step = chooseStep(ctx, target, from)
        if (step == null) {
            alive = false
            actions += Action.Die
            return actions
        }

        actions += Action.Move(step)
        actions += Action.Spawn(
            SnakeBodyEnemy(StatModifier(maxHealth = 1, health = 1, attack = stats.attack), from, bodyLifetime, this)
        )
        return actions
    }

    private fun chooseStep(ctx: TurnContext, target: Entity?, from: Position): Position? {
        val free = moves.map { from + it }.filter { isFree(ctx, it) }
        if (free.isEmpty()) return null
        if (target == null) return free.first()

        path = Pathfinding.findPath(ctx.map, ctx.entities - target, from, target.position, moves, heuristic, ignoreWalls)
        return path.getOrNull(1)?.takeIf { it in free } ?: free.minByOrNull { heuristic(it, target.position) }
    }

    private fun isFree(ctx: TurnContext, pos: Position): Boolean {
        val tile = ctx.map.getOrNull(pos.y)?.getOrNull(pos.x) ?: return false
        return !tile.type.isWall && ctx.getEntityAt(pos) == null
    }
}
