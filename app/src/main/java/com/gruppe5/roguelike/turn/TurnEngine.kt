package com.gruppe5.roguelike.turn

import com.gruppe5.roguelike.GameConfig
import com.gruppe5.roguelike.inventory.EquipSlot
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.inventory.item_types.Consumable
import com.gruppe5.roguelike.inventory.item_types.Equipment
import com.gruppe5.roguelike.inventory.item_types.TrinketItem
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.Effect
import com.gruppe5.roguelike.property.StatModifier

/**
 * !! Achtung nit wundern
 * Is lei cosmetic refactor damit des ViewModel nit so fett is
 *
 * Stats sind base + modifications (+=/-=)
 * Consumables geben Effects, ggf ticken pro runde runter
 * Equipment onequip += und unequip -=
 */
object TurnEngine {

    //true, wenn der player diese runde gestorben ist
    fun runTurn(
        ctx: TurnContext,
        player: Player,
        enemies: MutableList<Enemy>,
        effects: MutableList<Effect>,
    ): Boolean {
        var gameOver = false

        fun checkDeath(target: Entity) {
            if (target.stats.health > 0) return
            when (target) {
                is Enemy -> enemies.remove(target)
                is Player -> gameOver = true
            }
        }

        /**
         * schaut de-facto kompliziert aus, is es aber nit
         */
        fun execute(actor: Entity, action: Action) {
            when (action) {
                is Action.Move -> {
                    if (ctx.getEntityAt(action.to) == null) {
                        actor.position = action.to
                    }
                }
                is Action.Attack -> {
                    action.target.takeDamage(actor.getAttackDamage())
                    checkDeath(action.target)
                }
                is Action.Spawn -> {
                    val pos = action.entity.position
                    if (player.position != pos && enemies.none { it.position == pos }) {
                        enemies.add(action.entity)
                    }
                }
                Action.Die -> if (actor is Enemy) enemies.remove(actor)
                Action.Wait -> Unit
            }
        }

        fun <T> takeTurn(actor: T) where T : Entity, T : TurnTaker {
            actor.decideAction(ctx).forEach { action -> execute(actor, action) }
        }

        takeTurn(player)
        enemies.toList().forEach { takeTurn(it) }

        tickEffects(player, effects)

        player.queued = listOf(Action.Wait)
        return gameOver
    }

    //tap auf inventar-slot: consumable verbrauchen oder equipment in seinen slot legen
    fun useItem(
        index: Int,
        inventory: MutableList<ItemInstance>,
        equipment: MutableMap<EquipSlot, ItemInstance>,
        player: Player,
        effects: MutableList<Effect>,
    ) {
        val item = inventory.getOrNull(index) ?: return

        when (val def = item.definition) {
            is Consumable -> {
                def.onUse(player).forEach { applyEffect(it, player, effects) }
                if (item.usages - 1 <= 0) {
                    inventory.removeAt(index)
                } else {
                    inventory[index] = ItemInstance(def, item.usages - 1)
                }
            }
            is Equipment -> equip(index, item, def, inventory, equipment, player)
            else -> Unit //trinkets landen gar nicht im inventar (eigene reihe)
        }
    }

    //tap auf equip-slot: ablegen
    fun unequip(
        slot: EquipSlot,
        equipment: MutableMap<EquipSlot, ItemInstance>,
        player: Player,
    ) {
        val item = equipment.remove(slot) ?: return
        removeStats(player, (item.definition as Equipment).statsMod)
        //TODO drop item am boden
    }

    //tap auf trinket-slot: ablegen
    fun dropTrinket(index: Int, trinkets: MutableList<ItemInstance>) {
        if (index !in trinkets.indices) return
        trinkets.removeAt(index)
        //TODO drop item am boden
    }

    //routet nach typ: trinkets in die eigene reihe, der rest ins inventar
    fun addPlayerInventory(
        items: List<ItemInstance>,
        inventory: MutableList<ItemInstance>,
        trinkets: MutableList<ItemInstance>,
    ) {
        val (trinketItems, rest) = items.partition { it.definition is TrinketItem }
        inventory.addAll(rest.take(GameConfig.INVENTORY_SLOTS - inventory.size))
        trinkets.addAll(trinketItems.take(GameConfig.TRINKET_SLOTS - trinkets.size))
    }

    //swap slot ; TODO wollen wir lieber only-hold-one haben?
    private fun equip(
        index: Int,
        item: ItemInstance,
        def: Equipment,
        inventory: MutableList<ItemInstance>,
        equipment: MutableMap<EquipSlot, ItemInstance>,
        player: Player,
    ) {
        val old = equipment.put(def.slot, item)
        if (old != null) removeStats(player, (old.definition as Equipment).statsMod)
        player.stats += def.statsMod

        if (old != null) inventory[index] = old else inventory.removeAt(index)
    }

    private fun tickEffects(player: Player, effects: MutableList<Effect>) {
        val expired = effects.filter { --it.remaining <= 0 }
        expired.forEach { effect ->
            removeStats(player, effect.statsMod)
            effects.remove(effect)
        }
    }

    private fun applyEffect(effect: Effect, player: Player, effects: MutableList<Effect>) {
        effects.add(effect)
        player.stats += effect.statsMod
    }

    private fun removeStats(player: Player, statsMod: StatModifier) {
        player.stats -= statsMod
        // TODO overhealing möglich? dann des entfernen
        if (player.stats.health > player.stats.maxHealth) {
            player.stats.health = player.stats.maxHealth
        }
    }
}
