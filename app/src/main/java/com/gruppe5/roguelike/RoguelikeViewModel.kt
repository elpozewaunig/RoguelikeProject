package com.gruppe5.roguelike

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.gruppe5.roguelike.level_generators.BasicLevelGenerator
import com.gruppe5.roguelike.level_generators.LevelGenerator
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.inventory.Items
import com.gruppe5.roguelike.property.ActiveBuff
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.turn.TurnTaker

class RoguelikeViewModel : ViewModel() { //TODO die enemies werden most likely internal states haben die auch mit serialized werden müssen, entweder memento pattern oder etwas schönes kotlin-idiomatic dafür pullen
    private val mapGenerator: LevelGenerator = BasicLevelGenerator()
    val currentMap: List<List<MapTile>> = mapGenerator.getMap()
    val inventory = mutableStateListOf<ItemInstance>()
    val activeBuffs = mutableStateListOf<ActiveBuff>()

    val player: Player = Player(
        StatModifier(maxHealth = 500, health = 500, attack = 5, defense = 2),
        position = mapGenerator.getStartPos()
    )

    val enemies = mapGenerator.getEnemies().toMutableStateList() //TODO des sollt recomposition-mäßig passen (i glaub)
    var turn by mutableIntStateOf(0)
    var isGameOver by mutableStateOf(false)
        private set

    init {
        val items = listOf(
            Items.mediumHealthPotion(),
            Items.opStrengthPotion(),
            Items.sword()
        )

        addPlayerInventory(items)
        player.inventory = inventory
    }

    private fun submitPlayerMove(dx: Int, dy: Int) {
        if (isGameOver) return

        val target = Position(player.position.x + dx, player.position.y + dy)

        // des muss nur hier beim resolven von player tap to move gemacht werden, die enemies müssen sich selber drum kümmern nicht zum cheaten
        if (!isValidPosition(target)) return
        if (currentMap[target.y][target.x].type.isWall) return

        val ctx = buildContext()
        player.queued = ctx.getEntityAt(target)
            ?.let { listOf(Action.Attack(it)) }
            ?: listOf(Action.Move(target))

        runTurn(ctx)
    }

    private fun runTurn(ctx: TurnContext = buildContext()) {
        takeTurn(player, ctx)
        enemies.toList().forEach { takeTurn(it, ctx) }

        handleExpiredBuffs()

        player.queued = listOf(Action.Wait)
        turn++
    }

    private fun handleExpiredBuffs(){
        val expired = mutableListOf<ActiveBuff>()
        activeBuffs.forEach { buff ->
            if (buff.remainingTurns > 0) {
                buff.remainingTurns -= 1
                if (buff.remainingTurns <= 0) expired.add(buff)
            }
        }
        expired.forEach { removeBuff(it) }
    }

    private fun <T> takeTurn(actor: T, ctx: TurnContext) where T : Entity, T : TurnTaker {
        actor.decideAction(ctx).forEach { action -> execute(actor, action, ctx) }
    }

    /**
     * schaut de-facto kompliziert aus, is es aber nit
     */
    private fun execute(actor: Entity, action: Action, ctx: TurnContext) {
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

    //TODO enemy dies -> despawn, player dies -> crash app; ok?
    private fun checkDeath(target: Entity) {
        if (target.stats.health > 0) return
        when (target) {
            is Enemy -> enemies.remove(target)
            is Player -> isGameOver = true
        }
    }

    private fun buildContext(): TurnContext = TurnContext(currentMap, listOf(player) + enemies)

    private fun isValidPosition(pos: Position): Boolean =
        pos.y >= 0 && pos.x >= 0 &&
            pos.y < currentMap.size && pos.x < currentMap[pos.y].size

    fun moveSkip() {
        if (isGameOver) return
        player.queued = listOf(Action.Wait)
        runTurn()
    }

    fun moveRight() = submitPlayerMove(1, 0)

    fun moveLeft() = submitPlayerMove(-1, 0)

    fun moveDown() = submitPlayerMove(0, 1)

    fun moveUp() = submitPlayerMove(0, -1)

    fun onInventorySlotClicked(index: Int) {
        val currentItem = inventory.getOrNull(index)
        if (currentItem == null) return

        val buffs = currentItem.use(player)
        buffs.forEach { applyBuff(it) }

        currentItem.usages -= 1

        if (currentItem.usages <= 0) {
            //anlicken von perma item entfernt dieses item + buffs
            if (currentItem.isPermanent) {
                removePermaBuffsForInstance(currentItem)
                //TODO hier könnte drop logik hinzugefügt werden falls ma noch zeit ham
            }
            inventory.removeAt(index)
        }
    }

    //wenn vorher clearen notwendig ist
    fun setPlayerInventory(items: List<ItemInstance>) {
        resetInventory()
        addPlayerInventory(items)
    }

    fun addPlayerInventory(items: List<ItemInstance>){
        inventory.addAll(items.take(GameConfig.INVENTORY_SLOTS))
        inventory.forEach { processPermaBuffs(it) }
    }

    private fun resetInventory(){
        val permPrefix = "perm-"
        val toRemove = activeBuffs.filter { it.id.startsWith(permPrefix) }
        toRemove.forEach { removeBuff(it) }
        inventory.clear()
    }

    private fun applyBuff(buff: ActiveBuff) {
        activeBuffs.add(buff)

        player.stats.maxHealth += buff.statsMod.maxHealth
        player.stats.attack += buff.statsMod.attack
        player.stats.minDamageBuff += buff.statsMod.minDamageBuff
        player.stats.maxDamageBuff += buff.statsMod.maxDamageBuff
        player.stats.defense += buff.statsMod.defense
        player.stats.intellect += buff.statsMod.intellect
        player.stats.stealth += buff.statsMod.stealth
        player.stats.speed += buff.statsMod.speed
    }

    private fun removeBuff(buff: ActiveBuff) {

        player.stats.maxHealth -= buff.statsMod.maxHealth
        player.stats.attack -= buff.statsMod.attack
        player.stats.minDamageBuff -= buff.statsMod.minDamageBuff
        player.stats.maxDamageBuff -= buff.statsMod.maxDamageBuff
        player.stats.defense -= buff.statsMod.defense
        player.stats.intellect -= buff.statsMod.intellect
        player.stats.stealth -= buff.statsMod.stealth
        player.stats.speed -= buff.statsMod.speed

        // falls overhealing möglich sein soll, hier entfernen
        if (player.stats.health > player.stats.maxHealth) {
            player.stats.health = player.stats.maxHealth
        }

        activeBuffs.remove(buff)
    }

    //jedes Item im game selbst is a eigenes Instance objekt, weshalb es sicher ist
    //erlaubt für später mehrere buffs pro item
    private fun processPermaBuffs(instance: ItemInstance) {
        if (!instance.isPermanent) return

        //perma buff
        val buffs = instance.definition.onUse(instance, player)
        buffs.forEach { base ->
            val id = "perm-${System.identityHashCode(instance)}-${System.currentTimeMillis()}"
            val perm = ActiveBuff(id = id, statsMod = base.statsMod, remainingTurns = -1)
            applyBuff(perm)
        }
    }

    private fun removePermaBuffsForInstance(instance: ItemInstance) {
        val prefix = "perm-${System.identityHashCode(instance)}"
        val toRemove = activeBuffs.filter { it.id.startsWith(prefix) } //zeit is nid relevant
        toRemove.forEach { removeBuff(it) }
    }
}
