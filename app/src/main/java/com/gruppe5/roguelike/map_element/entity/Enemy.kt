package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.property.Group
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.TurnTaker

abstract class Enemy(
    override var stats: StatModifier,
    override var position: Position,
) : Entity, TurnTaker {
    override val groups: Set<Group> = setOf(Group.ENEMY)
    open val targets: Set<Group> = setOf(Group.PLAYER)

    var path: List<Position> = emptyList() //nur für debug displayment
}
