package com.gruppe5.roguelike.turn

import com.gruppe5.roguelike.GameConfig
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.ActiveBuff

/**
 * !! Achtung nit wundern
 * Is lei cosmetic refactor damit des ViewModel nit so fett is
 */
object TurnEngine {

    //true, wenn der player diese runde gestorben ist
    fun runTurn(
        ctx: TurnContext,
        player: Player,
        enemies: MutableList<Enemy>,
        activeBuffs: MutableList<ActiveBuff>,
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

        handleExpiredBuffs(player, activeBuffs)

        player.queued = listOf(Action.Wait)
        return gameOver
    }

    fun useItem(
        index: Int,
        inventory: MutableList<ItemInstance>,
        player: Player,
        activeBuffs: MutableList<ActiveBuff>,
    ) {
        val currentItem = inventory.getOrNull(index) ?: return

        val buffs = currentItem.use(player)
        buffs.forEach { applyBuff(it, player, activeBuffs) }

        //-1 triggered recomposition
        if (currentItem.usages - 1 <= 0) {
            //perma item use = entfernen
            if (currentItem.isPermanent) {
                removePermaBuffsForInstance(currentItem, player, activeBuffs)
                //TODO hier könnte drop logik hinzugefügt werden falls ma noch zeit ham
            }
            inventory.removeAt(index)
        } else {
            inventory[index] = ItemInstance(currentItem.definition, currentItem.usages - 1)
        }
    }

    fun addPlayerInventory(
        items: List<ItemInstance>,
        inventory: MutableList<ItemInstance>,
        player: Player,
        activeBuffs: MutableList<ActiveBuff>,
    ) {
        inventory.addAll(items.take(GameConfig.INVENTORY_SLOTS))
        inventory.forEach { processPermaBuffs(it, player, activeBuffs) }
    }

    private fun handleExpiredBuffs(player: Player, activeBuffs: MutableList<ActiveBuff>) {
        val expired = mutableListOf<ActiveBuff>()
        activeBuffs.forEach { buff ->
            if (buff.remainingTurns > 0) {
                buff.remainingTurns -= 1
                if (buff.remainingTurns <= 0) expired.add(buff)
            }
        }
        expired.forEach { removeBuff(it, player, activeBuffs) }
    }

    private fun applyBuff(buff: ActiveBuff, player: Player, activeBuffs: MutableList<ActiveBuff>) {
        activeBuffs.add(buff)
        player.stats += buff.statsMod
    }

    private fun removeBuff(buff: ActiveBuff, player: Player, activeBuffs: MutableList<ActiveBuff>) {
        player.stats -= buff.statsMod

        // falls overhealing möglich sein soll, hier entfernen
        if (player.stats.health > player.stats.maxHealth) {
            player.stats.health = player.stats.maxHealth
        }

        activeBuffs.remove(buff)
    }

    //erlaubt für später mehrere buffs pro item
    private fun processPermaBuffs(instance: ItemInstance, player: Player, activeBuffs: MutableList<ActiveBuff>) {
        if (!instance.isPermanent) return

        //perma buff: remainingTurns = -1, referenz aufs item statt string-id + timestamp
        val buffs = instance.definition.onUse(instance, player)
        buffs.forEach { base ->
            val perm = ActiveBuff(statsMod = base.statsMod, remainingTurns = -1, sourceItem = instance)
            applyBuff(perm, player, activeBuffs)
        }
    }

    private fun removePermaBuffsForInstance(instance: ItemInstance, player: Player, activeBuffs: MutableList<ActiveBuff>) {
        val toRemove = activeBuffs.filter { it.sourceItem === instance }
        toRemove.forEach { removeBuff(it, player, activeBuffs) }
    }
}
