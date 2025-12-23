package com.cadence.cadence.player.domain

import java.time.Instant
import java.util.UUID

data class XPLedgerEntry(
  val id: UUID,
  val playerId: UUID,
  val amount: Int,
  val sourceRef: String? = null,
  val createdAt: Instant = Instant.now()
)
