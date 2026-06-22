package com.gruppe5.roguelike.entity

import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier

interface Entity {
    val resId: Int
    var position: Position
    var stats: StatModifier
}
