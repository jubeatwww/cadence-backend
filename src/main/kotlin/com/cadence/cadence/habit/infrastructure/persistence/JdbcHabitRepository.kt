package com.cadence.cadence.habit.infrastructure.persistence

import com.cadence.cadence.habit.domain.Habit
import com.cadence.cadence.habit.domain.HabitRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

interface SpringDataHabitRepository : CrudRepository<HabitEntity, UUID> {
  fun findAllByPlayerId(playerId: UUID): List<HabitEntity>
}

@Repository
class JdbcHabitRepository(
  private val springDataRepo: SpringDataHabitRepository
) : HabitRepository {

  override fun save(habit: Habit): Habit {
    val entity = HabitEntity.fromDomain(habit)
    return springDataRepo.save(entity).toDomain()
  }

  override fun findById(id: UUID): Habit? {
    return springDataRepo.findById(id).orElse(null)?.toDomain()
  }

  override fun findAllByPlayerId(playerId: UUID): List<Habit> {
    return springDataRepo.findAllByPlayerId(playerId).map { it.toDomain() }
  }

  override fun delete(id: UUID) {
    springDataRepo.deleteById(id)
  }
}
