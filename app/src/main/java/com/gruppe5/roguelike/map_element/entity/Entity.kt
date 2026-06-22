package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.map_element.VisualMapElement
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier

interface Entity: VisualMapElement {
    override val resId: Int
    override var position: Position
    var stats: StatModifier
}
