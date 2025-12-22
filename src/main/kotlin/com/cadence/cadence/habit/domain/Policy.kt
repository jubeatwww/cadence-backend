package com.cadence.cadence.habit.domain

sealed class Policy {
  data class Quota(val maxCycles: Int) : Policy()
  data class Cooldown(val minutes: Int) : Policy()
  data class Reward(val multiplier: Double) : Policy()
}
