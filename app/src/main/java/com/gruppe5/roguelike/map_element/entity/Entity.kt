package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.map_element.MapElement
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier

interface Entity: MapElement {
    override val resId: Int
    var position: Position
    var stats: StatModifier
}
