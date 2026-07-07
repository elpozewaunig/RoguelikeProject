package com.gruppe5.roguelike.property

import kotlinx.serialization.Serializable

@Serializable
data class StatModifier(
    var maxHealth: Int = 0, //i glaub wenn health > maxHealth passiert schlimmes
    var health: Int = 0,
    var attack: Int = 0,
    var minDamageBuff: Int = 0,
    var maxDamageBuff: Int = 0,
    var defense: Int = 0,
    var intellect: Int = 0,
    var stealth: Int = 0,
    var speed: Int = 0,
) {
    // + und - auf buff definieren ist sehr elegant imo
    operator fun plus(other: StatModifier) = StatModifier(
        maxHealth + other.maxHealth,
        health + other.health,
        attack + other.attack,
        minDamageBuff + other.minDamageBuff,
        maxDamageBuff + other.maxDamageBuff,
        defense + other.defense,
        intellect + other.intellect,
        stealth + other.stealth,
        speed + other.speed,
    )

    operator fun minus(other: StatModifier) = StatModifier(
        maxHealth - other.maxHealth,
        health - other.health,
        attack - other.attack,
        minDamageBuff - other.minDamageBuff,
        maxDamageBuff - other.maxDamageBuff,
        defense - other.defense,
        intellect - other.intellect,
        stealth - other.stealth,
        speed - other.speed,
    )
}
