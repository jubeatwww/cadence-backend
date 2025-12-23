package com.cadence.cadence.habit.domain

import java.time.Instant
import java.util.UUID

enum class WindowType {
  DAILY, WEEKLY, BIWEEKLY, MONTHLY
}

enum class ResetMode {
  WINDOW_BOUND, INFINITE
}

data class Habit(
  val id: UUID,
  val playerId: UUID,
  val name: String,
  val description: String? = null,
  val windowType: WindowType,
  val resetMode: ResetMode,
  val isActive: Boolean = true,
  val createdAt: Instant = Instant.now(),
  val steps: List<Step> = emptyList(),
  val policies: List<Policy> = emptyList()
)
