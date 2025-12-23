package com.cadence.cadence.habit.infrastructure.persistence

import com.cadence.cadence.habit.domain.Difficulty
import com.cadence.cadence.habit.domain.Step
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("habit_step")
data class StepEntity(
  @Id val id: UUID? = null,
  val name: String,
  val sortOrder: Int,
  val difficulty: String,
  val baseXp: Int,
  @MappedCollection(idColumn = "step_id")
  val dependencies: Set<StepDependencyEntity> = emptySet()
) {
  fun toDomain(): Step = Step(
    id = id ?: throw IllegalStateException("Step id is null"),
    name = name,
    sortOrder = sortOrder,
    difficulty = Difficulty.valueOf(difficulty),
    baseXp = baseXp,
    dependsOn = dependencies.map { it.dependsOnStepId }.toSet()
  )

  companion object {
    fun fromDomain(step: Step): StepEntity = StepEntity(
      id = step.id,
      name = step.name,
      sortOrder = step.sortOrder,
      difficulty = step.difficulty.name,
      baseXp = step.baseXp,
      dependencies = step.dependsOn.map { StepDependencyEntity(it) }.toSet()
    )
  }
}

@Table("step_dependency")
data class StepDependencyEntity(
  val dependsOnStepId: UUID
)
