# Quickstart: Habit Context

## 1. Create a Linear Habit (Daily Workout)

**Concept**: A simple sequence: Warmup -> Run -> Cool Down. Window-bound to Daily (resets every day).

```bash
POST /habits
{
  "name": "Morning Run",
  "windowType": "DAILY",
  "resetMode": "WINDOW_BOUND",
  "steps": [
    { "id": "uuid-1", "name": "Warmup", "difficulty": "EASY", "baseXp": 10 },
    { "id": "uuid-2", "name": "Run 5km", "difficulty": "HARD", "baseXp": 50, "dependsOnStepIds": ["uuid-1"] },
    { "id": "uuid-3", "name": "Cool Down", "difficulty": "EASY", "baseXp": 10, "dependsOnStepIds": ["uuid-2"] }
  ]
}
```

## 2. Check Available Tasks

Call the Task Board API. If it's the start of the day:

```bash
GET /tasks/daily
```

**Response**:
```json
[
  {
    "habitName": "Morning Run",
    "stepName": "Warmup",
    "status": "AVAILABLE"
  }
]
```
*Note: "Run 5km" is LOCKED until Warmup is done.*

## 3. Complete a Step

```bash
POST /completions
{
  "habitId": "...",
  "stepId": "uuid-1",
  "occurredAt": "2025-12-21T08:00:00Z"
}
```

**Result**: 
- XP added to player.
- "Warmup" marked complete.
- "Run 5km" becomes AVAILABLE on next Task Board refresh.

## 4. Infinite Mode (Piano Practice)

**Concept**: Scales C Major -> Arpeggios -> Repertoire. Infinite loop (completing Repertoire restarts C Major with cycle_index=2).

```bash
POST /habits
{
  "name": "Piano Practice",
  "windowType": "WEEKLY",
  "resetMode": "INFINITE", 
  "steps": [ ... (Circle graph: C -> A -> R -> C) ... ]
}
```

In `INFINITE` mode, completing the last step immediately unlocks the first step again, incrementing the cycle count for bonus XP multipliers.
