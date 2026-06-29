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
    override val groups: Set<Group> = setOf(Group.FRIENDLY)
    override val targets: Set<Group> = setOf(Group.ENEMY)

    override fun decideAction(ctx: TurnContext): List<Action> {
        if (ctx.nearestTo(position, targets, this) != null) return super.decideAction(ctx)

        val player = ctx.nearestTo(position, setOf(Group.PLAYER), this) ?: return listOf(Action.Wait)
        path = Pathfinding.findPath(ctx.map, ctx.entities - player, position, player.position, moves, ignoreWalls)
        return if (path.size > 1) listOf(Action.Move(path[1])) else listOf(Action.Wait)
    }
}
