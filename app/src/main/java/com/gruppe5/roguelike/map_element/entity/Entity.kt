package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.map_element.VisualMapElement
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import kotlin.random.Random

interface Entity : VisualMapElement {
    override val resId: Int
    override var position: Position
    var stats: StatModifier

    //TODO review pls
    fun getAttackDamage(): Int =
        stats.attack + Random.nextInt(stats.minDamageBuff, stats.maxDamageBuff + 1)

    //TODO review pls
    fun takeDamage(amount: Int) {
        stats.health -= (amount - stats.defense).coerceAtLeast(0)
    }
}
