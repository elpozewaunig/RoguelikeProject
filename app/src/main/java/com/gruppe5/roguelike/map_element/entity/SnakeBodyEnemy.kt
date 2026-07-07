package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.utility.Pathfinding
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/** Schwanzsegment: bleibt liegen, beißt jede Runde, verfällt nach l Runden */
@Serializable
@SerialName("snakebody")
class SnakeBodyEnemy(
    override var stats: StatModifier,
    override var position: Position,
    var lifetime: Int,
    val headPos: Position, //kotlinx kann leider keine objekt-referenzen serializen, also verlink ma des derweil amal über position //TODO bessere Lösung?
) : Enemy() {
    override val resId: Int get() = R.drawable.entity_snakebody

    @Transient
    var head: SnakeHeadEnemy? = null //zur laufzeit die echte referenz, nach load neu verlinkt

    //spawn-ctor: nimmt die echte kopf-referenz und leitet headPos ab
    constructor(stats: StatModifier, position: Position, lifetime: Int, head: SnakeHeadEnemy)
        : this(stats, position, lifetime, head.position) {
        this.head = head
    }

    override fun onDeserialized(all: List<Enemy>) {
        head = all.filterIsInstance<SnakeHeadEnemy>().firstOrNull { it.position == headPos }
    }
    override fun act(ctx: TurnContext, times: Int): List<Action> {
        if (head?.alive != true) return listOf(Action.Die) //kopf hin => schwanz hin

        val actions = mutableListOf<Action>()
        val target = ctx.nearestTo(position, targets, this)
        if (target != null && Pathfinding.ORTHOGONAL_MOVES.any { position + it == target.position }) {
            repeat(times) { actions += Action.Attack(target) }
        }
        if (--lifetime <= 0) actions += Action.Die
        return actions.ifEmpty { listOf(Action.Wait) }
    }
}
