package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Group
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.turn.TurnTaker

class Player(
    override var stats: StatModifier,
    override var position: Position,
    override val resId: Int = R.drawable.entity_miku
) : Entity, TurnTaker {
    override val groups: Set<Group> = setOf(Group.PLAYER, Group.PLAYERFRIEND)

    var queued: List<Action> = listOf(Action.Wait) //viewmodel interpretiert input zu action (MVVM und so)
    var inventory: List<ItemInstance> = emptyList()

    override fun decideAction(ctx: TurnContext): List<Action> = queued
}
