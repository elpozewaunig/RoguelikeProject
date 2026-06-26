package com.gruppe5.roguelike.property

data class Buff(
    var duration: Int = 0,
    var statsMod: StatModifier = StatModifier()
)
