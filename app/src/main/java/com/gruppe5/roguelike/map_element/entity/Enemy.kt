package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.TurnTaker

abstract class Enemy(
    override var stats: StatModifier,
    override var position: Position,
) : Entity, TurnTaker {
    var path: List<Position> = emptyList() //nur für debug displayment
}
