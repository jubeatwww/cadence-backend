package com.cadence.cadence.habit.domain

import java.util.UUID

interface HabitRepository {
  fun save(habit: Habit): Habit
  fun findById(id: UUID): Habit?
  fun findAllByPlayerId(playerId: UUID): List<Habit>
  fun delete(id: UUID)
}
