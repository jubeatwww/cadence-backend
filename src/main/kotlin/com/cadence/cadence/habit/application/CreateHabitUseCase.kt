package com.cadence.cadence.habit.application

import com.cadence.cadence.habit.domain.*
import com.cadence.cadence.habit.domain.service.GraphValidationResult
import com.cadence.cadence.habit.domain.service.GraphValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

data class CreateHabitCommand(
  val playerId: UUID,
  val name: String,
  val description: String? = null,
  val windowType: WindowType,
  val resetMode: ResetMode,
  val steps: List<StepInput>,
  val policies: List<Policy> = emptyList()
)

data class StepInput(
  val id: UUID = UUID.randomUUID(),
  val name: String,
  val sortOrder: Int,
  val difficulty: Difficulty,
  val baseXp: Int,
  val dependsOn: Set<UUID> = emptySet()
)

sealed class CreateHabitResult {
  data class Success(val habit: Habit) : CreateHabitResult()
  data class ValidationError(val message: String) : CreateHabitResult()
}

@Service
class CreateHabitUseCase(
  private val habitRepository: HabitRepository,
  private val graphValidator: GraphValidator
) {

  @Transactional
  fun execute(command: CreateHabitCommand): CreateHabitResult {
    if (command.steps.isEmpty()) {
      return CreateHabitResult.ValidationError("Habit must have at least one step")
    }

    if (command.resetMode == ResetMode.INFINITE && command.policies.any { it is Policy.Quota }) {
      return CreateHabitResult.ValidationError("Quota policy is not allowed for INFINITE reset mode")
    }

    val steps = command.steps.map { input ->
      Step(
        id = input.id,
        name = input.name,
        sortOrder = input.sortOrder,
        difficulty = input.difficulty,
        baseXp = input.baseXp,
        dependsOn = input.dependsOn
      )
    }

    // Validate graph structure
    when (val validationResult = graphValidator.validate(steps)) {
      is GraphValidationResult.Valid -> {}
      is GraphValidationResult.CycleDetected ->
        return CreateHabitResult.ValidationError(
          "Cycle detected in step dependencies: ${validationResult.cycle}"
        )

      is GraphValidationResult.InvalidDependency ->
        return CreateHabitResult.ValidationError(
          "Step ${validationResult.stepId} references non-existent dependency ${validationResult.missingDependency}"
        )
    }

    val habit = Habit(
      id = UUID.randomUUID(),
      playerId = command.playerId,
      name = command.name,
      description = command.description,
      windowType = command.windowType,
      resetMode = command.resetMode,
      steps = steps,
      policies = command.policies
    )

    val saved = habitRepository.save(habit)
    return CreateHabitResult.Success(saved)
  }
}
