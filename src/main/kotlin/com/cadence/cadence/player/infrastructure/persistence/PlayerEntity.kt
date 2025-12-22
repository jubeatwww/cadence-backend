package com.cadence.cadence.player.infrastructure.persistence

import com.cadence.cadence.player.domain.Player
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("player")
data class PlayerEntity(
  @Id val id: UUID? = null,
  val username: String,
  val createdAt: Instant = Instant.now()
) {
  fun toDomain(): Player = Player(
    id = id ?: throw IllegalStateException("Player id is null"),
    username = username,
    createdAt = createdAt
  )

  companion object {
    fun fromDomain(player: Player): PlayerEntity = PlayerEntity(
      id = player.id,
      username = player.username,
      createdAt = player.createdAt
    )
  }
}
