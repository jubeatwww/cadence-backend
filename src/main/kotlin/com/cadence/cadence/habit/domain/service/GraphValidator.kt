package com.cadence.cadence.habit.domain.service

import com.cadence.cadence.habit.domain.Step
import org.springframework.stereotype.Component
import java.util.UUID

sealed class GraphValidationResult {
  data object Valid : GraphValidationResult()
  data class CycleDetected(val cycle: List<UUID>) : GraphValidationResult()
  data class InvalidDependency(val stepId: UUID, val missingDependency: UUID) : GraphValidationResult()
}

@Component
class GraphValidator {

  fun validate(steps: List<Step>): GraphValidationResult {
    val stepIds = steps.map { it.id }.toSet()

    // Check for invalid dependencies (referencing non-existent steps)
    for (step in steps) {
      for (dep in step.dependsOn) {
        if (dep !in stepIds) {
          return GraphValidationResult.InvalidDependency(step.id, dep)
        }
      }
    }

    // Check for cycles using DFS
    val cycle = detectCycle(steps)
    if (cycle != null) {
      return GraphValidationResult.CycleDetected(cycle)
    }

    return GraphValidationResult.Valid
  }

  private fun detectCycle(steps: List<Step>): List<UUID>? {
    val adjacency = steps.associate { it.id to it.dependsOn.toList() }
    val visited = mutableSetOf<UUID>()
    val recursionStack = mutableSetOf<UUID>()
    val path = mutableListOf<UUID>()

    fun dfs(nodeId: UUID): List<UUID>? {
      visited.add(nodeId)
      recursionStack.add(nodeId)
      path.add(nodeId)

      for (neighbor in adjacency[nodeId] ?: emptyList()) {
        if (neighbor !in visited) {
          val cycle = dfs(neighbor)
          if (cycle != null) return cycle
        } else if (neighbor in recursionStack) {
          val cycleStart = path.indexOf(neighbor)
          return path.subList(cycleStart, path.size) + neighbor
        }
      }

      recursionStack.remove(nodeId)
      path.removeAt(path.lastIndex)
      return null
    }

    for (step in steps) {
      if (step.id !in visited) {
        val cycle = dfs(step.id)
        if (cycle != null) return cycle
      }
    }

    return null
  }
}
