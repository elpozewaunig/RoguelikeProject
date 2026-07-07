package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.property.Group
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.turn.TurnTaker
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.roundToInt

/**
 * Warum is des alles so?
 * Gute Frage, I hab viel considered, des Problem is dass Enemy OOP Inheritance / Polymorphism macht. Des kann der serializer nit wirklich.
 * Des is beste choice imo, alternativen wären:
 *
 * - DIY switch case registry (schiach)
 * - factory für jedes entity (weniger schiach, aber boilerplate)
 *
 * Deshalb: https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-core/kotlinx.serialization/-polymorphic-serializer/
 * (is auch kind of der intended pattern für polymorphism.)
 *
 * Enemy ist jetzt sealed statt abstract weil sich sonst der compiler anblärt weil auto-discovering nit geht.
 * Und noch a pain mehr:
 * (Achtung in kotlin is jede sealed class auch abstract!; auto-discover geht nur durch abstract/sealed, deshalb is chaser auch so awkward)
 *
 * Sachen die von Enemy inheriten wird automatisch statically ermittelt ?! (fancy..) <-- des exploiten wir da jz, dann spar ma uns registry/factory etc.
 * Und es müssen jetzt alle constructor parameters auch vollwertige overriden properties sein, weil sich sonst der compiler anblärt.
 * Sonst gibt's `This class is not serializable automatically because it has primary constructor parameters that are not properties`
 *
 * Nächster pain-point:
 * Es muss wegen dem plugin jeder constructor parameter gscheid val/var property sein. I kann es nit als param haben und in super() durchreichen zum parent.
 * Die leaf node muss selber halten (weil reconstruction arg bei deserialization sunst bumm)
 * Des is a extra-regel die's in plain kotlin nie gab: das plugin verlangt dass JEDER ctor-param von am serialisierbaren typ a val/var property is, egal wofür er verwendet wird.
 * https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md#constructor-properties-requirement
 *
 * Bessere code introspection libraries könnten des wrsl aber :ugh:
 *
 * tbh kann i damit leben dass i 3 zeilen pro enemy boilerplate hab
 *
 * @SerialName könnt man sich sparen (dann wäre es full-qualified class name), aber so is code-declaration von savefile entkoppelt
 *
 */
@Serializable
sealed class Enemy : Entity, TurnTaker {
    override val groups: Set<Group> get() = setOf(Group.ENEMY)
    open val targets: Set<Group> get() = setOf(Group.PLAYER, Group.PLAYERFRIEND)
    open val speed: Float get() = 1f

    @Transient
    var path: List<Position> = emptyList() //nur für debug displayment

    var tick = 0 //interner rhythmus, muss mit-serialisiert werden

    final override fun decideAction(ctx: TurnContext): List<Action> {
        if (speed < 1f && tick++ % (1f / speed).roundToInt() != 0) return listOf(Action.Wait)
        return act(ctx, speed.roundToInt().coerceAtLeast(1))
    }

    protected abstract fun act(ctx: TurnContext, times: Int): List<Action>

    /**
     * Für die extrawürsteln (z.B. snakebody) die custom logic dazu noch brauchen
     */
    open fun onDeserialized(all: List<Enemy>) {}
}
