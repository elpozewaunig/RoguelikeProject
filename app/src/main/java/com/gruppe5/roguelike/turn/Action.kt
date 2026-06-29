package com.gruppe5.roguelike.turn

import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.property.Position

// i hab mir dabei actually viel gedacht, z.B. bogenschütze der moved und attacked gleichzeitig usw.
// des is außerdem des groundwork für "do multiple things in one turn" nacher
sealed class Action {
    data class Move(val to: Position) : Action()
    data class Attack(val target: Entity) : Action()
    data class Spawn(val entity: Enemy) : Action()
    object Die : Action()
    object Wait : Action()
}
