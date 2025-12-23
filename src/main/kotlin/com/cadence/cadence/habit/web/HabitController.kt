package com.cadence.cadence.habit.web

import com.cadence.cadence.habit.application.*
import com.cadence.cadence.habit.domain.*
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

data class CreateHabitRequest(
  val name: String,
  @NotBlank
  val description: String? = null,
  val windowType: String,
  val resetMode: String,
  val steps: List<StepRequest>,
  val policies: List<PolicyRequest> = emptyList()
)

data class StepRequest(
  val id: UUID? = null,
  val name: String,
  val sortOrder: Int,
  val difficulty: String,
  val baseXp: Int,
  val dependsOnStepIds: List<UUID> = emptyList()
)

data class PolicyRequest(
  val type: String,
  val config: Map<String, Any>
)

data class HabitResponse(
  val id: UUID,
  val playerId: UUID,
  val name: String,
  val description: String?,
  val windowType: String,
  val resetMode: String,
  val isActive: Boolean,
  val steps: List<StepResponse>,
  val policies: List<PolicyResponse>
)

data class StepResponse(
  val id: UUID,
  val name: String,
  val sortOrder: Int,
  val difficulty: String,
  val baseXp: Int,
  val dependsOn: List<UUID>
)

data class PolicyResponse(
  val type: String,
  val config: Map<String, Any>
)

data class ErrorResponse(val message: String)

@RestController
@RequestMapping("/habits")
class HabitController(
  private val createHabitUseCase: CreateHabitUseCase,
  private val habitRepository: HabitRepository
) {

  @PostMapping
  fun createHabit(
    @RequestHeader("X-Player-Id") playerId: UUID,
    @RequestBody request: CreateHabitRequest
  ): ResponseEntity<Any> {
    val windowType = parseEnum<WindowType>(request.windowType, "windowType")
      ?: return ResponseEntity.badRequest().body(
        ErrorResponse("Invalid windowType: ${request.windowType}. Valid values: ${WindowType.entries.map { it.name }}")
      )

    val resetMode = parseEnum<ResetMode>(request.resetMode, "resetMode")
      ?: return ResponseEntity.badRequest().body(
        ErrorResponse("Invalid resetMode: ${request.resetMode}. Valid values: ${ResetMode.entries.map { it.name }}")
      )

    val steps = request.steps.mapIndexed { index, step ->
      val difficulty = parseEnum<Difficulty>(step.difficulty, "steps[$index].difficulty")
        ?: return ResponseEntity.badRequest().body(
          ErrorResponse("Invalid difficulty: ${step.difficulty}. Valid values: ${Difficulty.entries.map { it.name }}")
        )
      StepInput(
        id = step.id ?: UUID.randomUUID(),
        name = step.name,
        sortOrder = step.sortOrder,
        difficulty = difficulty,
        baseXp = step.baseXp,
        dependsOn = step.dependsOnStepIds.toSet()
      )
    }

    val policies = try {
      request.policies.map { it.toDomain() }
    } catch (e: IllegalArgumentException) {
      return ResponseEntity.badRequest().body(ErrorResponse(e.message ?: "Invalid policy configuration"))
    }

    val command = CreateHabitCommand(
      playerId = playerId,
      name = request.name,
      description = request.description,
      windowType = windowType,
      resetMode = resetMode,
      steps = steps,
      policies = policies
    )

    return when (val result = createHabitUseCase.execute(command)) {
      is CreateHabitResult.Success ->
        ResponseEntity.status(HttpStatus.CREATED).body(result.habit.toResponse())

      is CreateHabitResult.ValidationError ->
        ResponseEntity.badRequest().body(ErrorResponse(result.message))
    }
  }

  private inline fun <reified T : Enum<T>> parseEnum(value: String, fieldName: String): T? {
    return try {
      enumValueOf<T>(value)
    } catch (e: IllegalArgumentException) {
      null
    }
  }

  @GetMapping
  fun listHabits(@RequestHeader("X-Player-Id") playerId: UUID): ResponseEntity<List<HabitResponse>> {
    val habits = habitRepository.findAllByPlayerId(playerId)
    return ResponseEntity.ok(habits.map { it.toResponse() })
  }

  @GetMapping("/{habitId}")
  fun getHabit(
    @RequestHeader("X-Player-Id") playerId: UUID,
    @PathVariable habitId: UUID
  ): ResponseEntity<Any> {
    val habit = habitRepository.findById(habitId)
      ?: return ResponseEntity.notFound().build()

    if (habit.playerId != playerId) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ErrorResponse("Access denied"))
    }

    return ResponseEntity.ok(habit.toResponse())
  }

  private fun Habit.toResponse() = HabitResponse(
    id = id,
    playerId = playerId,
    name = name,
    description = description,
    windowType = windowType.name,
    resetMode = resetMode.name,
    isActive = isActive,
    steps = steps.map { it.toResponse() },
    policies = policies.map { it.toResponse() }
  )

  private fun Step.toResponse() = StepResponse(
    id = id,
    name = name,
    sortOrder = sortOrder,
    difficulty = difficulty.name,
    baseXp = baseXp,
    dependsOn = dependsOn.toList()
  )

  private fun Policy.toResponse(): PolicyResponse = when (this) {
    is Policy.Quota -> PolicyResponse("QUOTA", mapOf("maxCycles" to maxCycles))
    is Policy.Cooldown -> PolicyResponse("COOLDOWN", mapOf("minutes" to minutes))
    is Policy.Reward -> PolicyResponse("REWARD", mapOf("multiplier" to multiplier))
  }

  private fun PolicyRequest.toDomain(): Policy = when (type) {
    "QUOTA" -> Policy.Quota(config.requireInt("maxCycles", type))
    "COOLDOWN" -> Policy.Cooldown(config.requireInt("minutes", type))
    "REWARD" -> Policy.Reward(config.requireDouble("multiplier", type))
    else -> throw IllegalArgumentException("Unknown policy type: $type. Valid types: QUOTA, COOLDOWN, REWARD")
  }

  private fun Map<String, Any>.requireInt(key: String, policyType: String): Int {
    val value = this[key]
      ?: throw IllegalArgumentException("Missing required '$key' for policy type '$policyType'")
    return (value as? Number)?.toInt()
      ?: throw IllegalArgumentException("Invalid '$key' for policy type '$policyType': expected number")
  }

  private fun Map<String, Any>.requireDouble(key: String, policyType: String): Double {
    val value = this[key]
      ?: throw IllegalArgumentException("Missing required '$key' for policy type '$policyType'")
    return (value as? Number)?.toDouble()
      ?: throw IllegalArgumentException("Invalid '$key' for policy type '$policyType': expected number")
  }
}
