package com.cadence.cadence.habit.infrastructure.persistence

import com.cadence.cadence.habit.domain.Policy
import com.google.gson.Gson
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("habit_policy")
data class HabitPolicyEntity(
  @Id val id: UUID? = null,
  val policyType: String,
  val configJson: String
) {
  fun toDomain(): Policy = when (policyType) {
    "QUOTA" -> {
      val config = gson.fromJson(configJson, QuotaConfig::class.java)
      Policy.Quota(config.maxCycles)
    }

    "COOLDOWN" -> {
      val config = gson.fromJson(configJson, CooldownConfig::class.java)
      Policy.Cooldown(config.minutes)
    }

    "REWARD" -> {
      val config = gson.fromJson(configJson, RewardConfig::class.java)
      Policy.Reward(config.multiplier)
    }

    else -> throw IllegalArgumentException("Unknown policy type: $policyType")
  }

  companion object {
    private val gson = Gson()

    fun fromDomain(policy: Policy): HabitPolicyEntity = when (policy) {
      is Policy.Quota -> HabitPolicyEntity(
        policyType = "QUOTA",
        configJson = gson.toJson(QuotaConfig(policy.maxCycles))
      )

      is Policy.Cooldown -> HabitPolicyEntity(
        policyType = "COOLDOWN",
        configJson = gson.toJson(CooldownConfig(policy.minutes))
      )

      is Policy.Reward -> HabitPolicyEntity(
        policyType = "REWARD",
        configJson = gson.toJson(RewardConfig(policy.multiplier))
      )
    }
  }
}

private data class QuotaConfig(val maxCycles: Int)
private data class CooldownConfig(val minutes: Int)
private data class RewardConfig(val multiplier: Double)
