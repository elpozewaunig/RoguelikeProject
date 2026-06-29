package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.property.Group
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.turn.TurnTaker
import kotlin.math.roundToInt

abstract class Enemy(
    override var stats: StatModifier,
    override var position: Position,
) : Entity, TurnTaker {
    override val groups: Set<Group> = setOf(Group.ENEMY)
    open val targets: Set<Group> = setOf(Group.PLAYER, Group.PLAYERFRIEND)
    open val speed: Float = 1f

    var path: List<Position> = emptyList() //nur für debug displayment

    private var tick = 0

    final override fun decideAction(ctx: TurnContext): List<Action> {
        if (speed < 1f && tick++ % (1f / speed).roundToInt() != 0) return listOf(Action.Wait)
        return act(ctx, speed.roundToInt().coerceAtLeast(1))
    }

    protected abstract fun act(ctx: TurnContext, times: Int): List<Action>
}
