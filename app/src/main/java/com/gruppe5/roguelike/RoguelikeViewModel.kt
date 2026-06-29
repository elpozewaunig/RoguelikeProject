package com.gruppe5.roguelike

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.gruppe5.roguelike.level_generators.BasicLevelGenerator
import com.gruppe5.roguelike.level_generators.LevelGenerator
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
    val player: Player = Player(
        StatModifier(maxHealth = 500, health = 500, attack = 5, defense = 2),
        position = mapGenerator.getStartPos()
    )

    val enemies = mapGenerator.getEnemies().toMutableStateList() //TODO des sollt recomposition-mäßig passen (i glaub)
    var turn by mutableIntStateOf(0)
    var isGameOver by mutableStateOf(false)
        private set

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

        player.queued = listOf(Action.Wait)
        turn++
    }

    private fun <T> takeTurn(actor: T, ctx: TurnContext) where T : Entity, T : TurnTaker {
        actor.decideAction(ctx).forEach { action -> execute(actor, action, ctx) }
    }

    /**
     * schaut de-facto kompliziert aus, is es aber nit
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * :::::::::::::::::::::--==+***++=--::::::::::::::::::::
     * :::::::::::::::::-=*%%%%%%%%%%%%%%%*+-::::::::::::::::
     * :::::::::::::::=#%%%%%%%%%%%%%%%%%%%%%#+-:::::::::::::
     * :::::::::::::=#%%%#****%%%%%%%%%%%%%%%%%#+-:::::::::::
     * :::::::::::-*%%+--====---+%%%%%%%%%%%%%%%%#-::::::::::
     * ::::::::::-#%%%%%%%%%%%%%%%%%%%%%%%%%%%####%=:::::::::
     * :::::::::-#%%%%%%%*==+#%%%%%#+======+*%#**#%#=::::::::
     * :::::::::*%%%%%%%#=---+%%%%%#**----++-=%#**#%#-:::::::
     * ::::::::+%%%%%%%%%+---*%%%%%%%*=---*%%%%#***#%+:::::::
     * ::::::::+%%%%%%%%%%##%%%%%%%%%%+--+#%%##****#%*-::::::
     * :::::::-*%%%%%%%%%%%%%%%%%%%%%%%%%##********#%*-::::::
     * ::::::::+%%%%%%%%%%%%%%%%%%%%%%%%#**********##*-::::::
     * ::::::::+%%%%%%%%%%#+*#%%%%%##%%%%************+:::::::
     * ::::::::-#%%%%%%%%%%*+==-----=#%%%#***********-:::::::
     * :::::::::=%%%%%%%%%%%%%%%%%%%%%%%%%#*********=::::::::
     * ::::::::::=%%%%%%%%%%%%%%%%%%%%%%%%%#*******=:::::::::
     * :::::::::::-#%%%%%%%%%%%%%%%%%%%%%%%%%%%%%#=::::::::::
     * ::::::::::::-+%%%%%%%%%%%%%%%%%%%%%%%%%%%*-:::::::::::
     * ::::::::::::::-+%%%%%%%%%%%%%%%%%%%%%%%*-:::::::::::::
     * :::::::::::::::::-+#%%%%%%%%%%%%%%%%*-::::::::::::::::
     * :::::::::::::::::::::-=+###%###*=-::::::::::::::::::::
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::
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
}
