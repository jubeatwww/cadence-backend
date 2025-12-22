package com.cadence.cadence.habit.web

import com.cadence.cadence.habit.application.*
import com.cadence.cadence.habit.domain.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

data class CreateHabitRequest(
  val name: String,
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
    val command = CreateHabitCommand(
      playerId = playerId,
      name = request.name,
      description = request.description,
      windowType = WindowType.valueOf(request.windowType),
      resetMode = ResetMode.valueOf(request.resetMode),
      steps = request.steps.map { step ->
        StepInput(
          id = step.id ?: UUID.randomUUID(),
          name = step.name,
          sortOrder = step.sortOrder,
          difficulty = Difficulty.valueOf(step.difficulty),
          baseXp = step.baseXp,
          dependsOn = step.dependsOnStepIds.toSet()
        )
      },
      policies = request.policies.map { it.toDomain() }
    )

    return when (val result = createHabitUseCase.execute(command)) {
      is CreateHabitResult.Success ->
        ResponseEntity.status(HttpStatus.CREATED).body(result.habit.toResponse())

      is CreateHabitResult.ValidationError ->
        ResponseEntity.badRequest().body(ErrorResponse(result.message))
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
    "QUOTA" -> Policy.Quota((config["maxCycles"] as Number).toInt())
    "COOLDOWN" -> Policy.Cooldown((config["minutes"] as Number).toInt())
    "REWARD" -> Policy.Reward((config["multiplier"] as Number).toDouble())
    else -> throw IllegalArgumentException("Unknown policy type: $type")
  }
}
