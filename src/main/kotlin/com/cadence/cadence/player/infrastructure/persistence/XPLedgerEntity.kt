package com.cadence.cadence.player.infrastructure.persistence

import com.cadence.cadence.player.domain.XPLedgerEntry
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("xp_ledger")
data class XPLedgerEntity(
  @Id val id: UUID? = null,
  val playerId: UUID,
  val amount: Int,
  val sourceRef: String? = null,
  val createdAt: Instant = Instant.now()
) {
  fun toDomain(): XPLedgerEntry = XPLedgerEntry(
    id = id ?: throw IllegalStateException("XPLedger id is null"),
    playerId = playerId,
    amount = amount,
    sourceRef = sourceRef,
    createdAt = createdAt
  )

  companion object {
    fun fromDomain(entry: XPLedgerEntry): XPLedgerEntity = XPLedgerEntity(
      id = entry.id,
      playerId = entry.playerId,
      amount = entry.amount,
      sourceRef = entry.sourceRef,
      createdAt = entry.createdAt
    )
  }
}
