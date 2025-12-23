package com.cadence.cadence.habit.domain.service

import com.cadence.cadence.habit.domain.Difficulty
import com.cadence.cadence.habit.domain.Step
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertIs

class GraphValidatorTest {

  private val validator = GraphValidator()

  private fun step(id: UUID, vararg dependsOn: UUID) = Step(
    id = id,
    name = "Step $id",
    sortOrder = 0,
    difficulty = Difficulty.EASY,
    baseXp = 10,
    dependsOn = dependsOn.toSet()
  )

  @Test
  fun `valid linear DAG`() {
    val a = UUID.randomUUID()
    val b = UUID.randomUUID()
    val c = UUID.randomUUID()

    val steps = listOf(
      step(a),
      step(b, a),
      step(c, b)
    )

    val result = validator.validate(steps)
    assertIs<GraphValidationResult.Valid>(result)
  }

  @Test
  fun `valid DAG with multiple dependencies`() {
    val a = UUID.randomUUID()
    val b = UUID.randomUUID()
    val c = UUID.randomUUID()

    val steps = listOf(
      step(a),
      step(b),
      step(c, a, b)
    )

    val result = validator.validate(steps)
    assertIs<GraphValidationResult.Valid>(result)
  }

  @Test
  fun `detects simple cycle`() {
    val a = UUID.randomUUID()
    val b = UUID.randomUUID()

    val steps = listOf(
      step(a, b),
      step(b, a)
    )

    val result = validator.validate(steps)
    assertIs<GraphValidationResult.CycleDetected>(result)
  }

  @Test
  fun `detects longer cycle`() {
    val a = UUID.randomUUID()
    val b = UUID.randomUUID()
    val c = UUID.randomUUID()

    val steps = listOf(
      step(a, c),
      step(b, a),
      step(c, b)
    )

    val result = validator.validate(steps)
    assertIs<GraphValidationResult.CycleDetected>(result)
  }

  @Test
  fun `detects self-reference`() {
    val a = UUID.randomUUID()

    val steps = listOf(step(a, a))

    val result = validator.validate(steps)
    assertIs<GraphValidationResult.CycleDetected>(result)
  }

  @Test
  fun `detects invalid dependency`() {
    val a = UUID.randomUUID()
    val missing = UUID.randomUUID()

    val steps = listOf(step(a, missing))

    val result = validator.validate(steps)
    assertIs<GraphValidationResult.InvalidDependency>(result)
  }

  @Test
  fun `empty steps list is valid`() {
    val result = validator.validate(emptyList())
    assertIs<GraphValidationResult.Valid>(result)
  }

  @Test
  fun `single step without dependencies is valid`() {
    val a = UUID.randomUUID()
    val result = validator.validate(listOf(step(a)))
    assertIs<GraphValidationResult.Valid>(result)
  }
}
