package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.utility.Pathfinding

/** Jagt per A*, beißt orthogonal und lässt eine Schwanzspur der Länge l zurück */
class SnakeHeadEnemy(
    stats: StatModifier,
    position: Position,
    val bodyLifetime: Int = 4,
) : ChaseEnemy(stats, position, R.drawable.entity_snakehead) {

    override fun decideAction(ctx: TurnContext): List<Action> {
        val target = ctx.nearestTo(position, targets, this) ?: return listOf(Action.Wait)
        val actions = mutableListOf<Action>()
        val from = position

        if (moves.any { from + it == target.position }) actions += Action.Attack(target)

        path = Pathfinding.findPath(ctx.map, ctx.entities - target, from, target.position, moves, heuristic, ignoreWalls)
        path.getOrNull(1)?.let { step ->
            actions += Action.Move(step)
            actions += Action.Spawn(
                SnakeBodyEnemy(StatModifier(maxHealth = 1, health = 1, attack = stats.attack), from, bodyLifetime)
            )
        }
        return actions.ifEmpty { listOf(Action.Wait) }
    }
}
