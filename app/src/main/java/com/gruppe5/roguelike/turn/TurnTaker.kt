package com.gruppe5.roguelike.turn

interface TurnTaker { //des superceded somit die planned generic "EnemyAI" interface, feel free to rename wenns stört
    fun decideAction(ctx: TurnContext): List<Action>
}
