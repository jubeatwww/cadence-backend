package com.cadence.cadence.player.domain

import java.time.Instant
import java.util.UUID

data class Player(
  val id: UUID,
  val username: String,
  val createdAt: Instant = Instant.now()
)
