package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Group
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.utility.Pathfinding

/** Folgt dem Spieler und kämpft gegen Gegner mit */
class FriendlyEnemy(
    stats: StatModifier,
    position: Position,
) : ChaseEnemy(stats, position, R.drawable.entity_friendly) {
    override var groups: Set<Group> = setOf(Group.NEUTRAL)
    override val targets: Set<Group> = setOf(Group.ENEMY)

    override fun act(ctx: TurnContext, times: Int): List<Action> {
        val player = ctx.nearestTo(position, setOf(Group.PLAYER), this) ?: return listOf(Action.Wait)

        if (Group.NEUTRAL in groups) {
            if (position.distanceTo(player.position) > JOIN_DISTANCE) return approach(ctx, player, times)
            groups = setOf(Group.PLAYERFRIEND)
        }

        val target = ctx.nearestTo(position, targets, this)
        if (target != null && moves.any { position + it == target.position }) {
            return List(times) { Action.Attack(target) }
        }
        if (target != null) {
            path = Pathfinding.findPath(ctx.map, ctx.entities - target, position, target.position, moves, heuristic, ignoreWalls)
            path.getOrNull(1)?.let { step ->
                if (step.distanceTo(player.position) <= LEASH) return listOf(Action.Move(step))
            }
        }
        return approach(ctx, player, times)
    }

    private fun approach(ctx: TurnContext, player: Entity, times: Int): List<Action> {
        path = Pathfinding.findPath(ctx.map, ctx.entities - player, position, player.position, moves, heuristic, ignoreWalls)
        return path.drop(1).take(times).map { Action.Move(it) }.ifEmpty { listOf(Action.Wait) }
    }

    companion object {
        private const val JOIN_DISTANCE = 5
        private const val LEASH = 10
    }
}
