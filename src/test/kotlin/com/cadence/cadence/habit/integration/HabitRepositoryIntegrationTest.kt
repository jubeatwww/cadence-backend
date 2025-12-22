package com.cadence.cadence.habit.integration

import com.cadence.cadence.habit.domain.Difficulty
import com.cadence.cadence.habit.domain.HabitRepository
import com.cadence.cadence.habit.domain.ResetMode
import com.cadence.cadence.habit.domain.WindowType
import com.cadence.cadence.shared.test.BaseIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HabitRepositoryIntegrationTest : BaseIntegrationTest() {

  @Autowired
  private lateinit var habitRepository: HabitRepository

  companion object {
    val TEST_PLAYER_ID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
    val HABIT_DRINK_WATER_ID: UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
    val HABIT_DAILY_ID: UUID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
  }

  @Test
  fun `should find all habits for player from seed data`() {
    val habits = habitRepository.findAllByPlayerId(TEST_PLAYER_ID)

    assertEquals(4, habits.size)
    assertTrue(habits.any { it.name == "喝水" })
    assertTrue(habits.any { it.name == "日常" })
    assertTrue(habits.any { it.name == "有氧" })
    assertTrue(habits.any { it.name == "重訓" })
  }

  @Test
  fun `should find habit by id with steps and dependencies`() {
    val habit = habitRepository.findById(HABIT_DRINK_WATER_ID)

    assertNotNull(habit)
    assertEquals("喝水", habit.name)
    assertEquals(WindowType.DAILY, habit.windowType)
    assertEquals(ResetMode.WINDOW_BOUND, habit.resetMode)
    assertEquals(10, habit.steps.size)

    // Verify steps are loaded with correct data
    val firstStep = habit.steps.find { it.name == "喝水 200ml" }
    assertNotNull(firstStep)
    assertEquals(Difficulty.EASY, firstStep.difficulty)
    assertEquals(3, firstStep.baseXp)
    assertTrue(firstStep.dependsOn.isEmpty())

    // Verify dependency chain
    val secondStep = habit.steps.find { it.name == "喝水 400ml" }
    assertNotNull(secondStep)
    assertEquals(1, secondStep.dependsOn.size)
    assertTrue(secondStep.dependsOn.contains(firstStep.id))
  }

  @Test
  fun `should find habit with no dependencies (日常)`() {
    val habit = habitRepository.findById(HABIT_DAILY_ID)

    assertNotNull(habit)
    assertEquals("日常", habit.name)
    assertEquals(5, habit.steps.size)

    // All steps should have no dependencies
    habit.steps.forEach { step ->
      assertTrue(step.dependsOn.isEmpty(), "Step ${step.name} should have no dependencies")
    }
  }

  @Test
  fun `should verify weekly habit properties`() {
    val habits = habitRepository.findAllByPlayerId(TEST_PLAYER_ID)
    val weeklyHabits = habits.filter { it.windowType == WindowType.WEEKLY }

    assertEquals(2, weeklyHabits.size)
    assertTrue(weeklyHabits.any { it.name == "有氧" })
    assertTrue(weeklyHabits.any { it.name == "重訓" })
  }
}