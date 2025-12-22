package com.cadence.cadence.habit.infrastructure.persistence

import com.cadence.cadence.habit.domain.Habit
import com.cadence.cadence.habit.domain.ResetMode
import com.cadence.cadence.habit.domain.WindowType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("habit")
data class HabitEntity(
  @Id val id: UUID? = null,
  val playerId: UUID,
  val name: String,
  val description: String? = null,
  val windowType: String,
  val resetMode: String,
  val isActive: Boolean = true,
  val createdAt: Instant = Instant.now(),
  @MappedCollection(idColumn = "habit_id")
  val steps: Set<StepEntity> = emptySet(),
  @MappedCollection(idColumn = "habit_id")
  val policies: Set<HabitPolicyEntity> = emptySet()
) {
  fun toDomain(): Habit = Habit(
    id = id ?: throw IllegalStateException("Habit id is null"),
    playerId = playerId,
    name = name,
    description = description,
    windowType = WindowType.valueOf(windowType),
    resetMode = ResetMode.valueOf(resetMode),
    isActive = isActive,
    createdAt = createdAt,
    steps = steps.map { it.toDomain() },
    policies = policies.map { it.toDomain() }
  )

  companion object {
    fun fromDomain(habit: Habit): HabitEntity = HabitEntity(
      id = habit.id,
      playerId = habit.playerId,
      name = habit.name,
      description = habit.description,
      windowType = habit.windowType.name,
      resetMode = habit.resetMode.name,
      isActive = habit.isActive,
      createdAt = habit.createdAt,
      steps = habit.steps.map { StepEntity.fromDomain(it) }.toSet(),
      policies = habit.policies.map { HabitPolicyEntity.fromDomain(it) }.toSet()
    )
  }
}
