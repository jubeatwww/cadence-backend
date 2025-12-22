package com.cadence.cadence.habit.domain

import java.util.UUID

enum class Difficulty {
  EASY, MEDIUM, HARD
}

data class Step(
  val id: UUID,
  val name: String,
  val sortOrder: Int,
  val difficulty: Difficulty,
  val baseXp: Int,
  val dependsOn: Set<UUID> = emptySet()
)
