package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier

abstract class Enemy(
    override var stats: StatModifier,
    override var position: Position,
): AIEntity
