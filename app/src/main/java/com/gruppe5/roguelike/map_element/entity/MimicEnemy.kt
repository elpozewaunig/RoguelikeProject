package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Tarnt sich als die Kachel, auf der er spawnt, bis der Spieler nah ist */
@Serializable
@SerialName("mimic")
class MimicEnemy(
    override var stats: StatModifier,
    override var position: Position,
    val dormantResId: Int, //die kachel-optik, ausm die er sich tarnt
) : Chaser() {
    override val resId: Int get() = if (awake) R.drawable.entity_mimic else dormantResId //getter für reconstruction
    var awake = false

    override fun act(ctx: TurnContext, times: Int): List<Action> {
        if (!awake) {
            val target = ctx.nearestTo(position, targets, this) ?: return listOf(Action.Wait)
            if (position.distanceTo(target.position) > 2) return listOf(Action.Wait)
            awake = true
        }
        return super.act(ctx, times)
    }
}
