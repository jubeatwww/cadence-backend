# Cadence - A Habit Tracker Game — DDD 設計文件

> 目的：以 DDD 方式定義「遊戲化 Habit Tracker」的核心領域模型。
>
> 核心需求：
> 1. **時間窗綁定任務**：進入新 window 時重置回起點，可設配額限制輪次。
> 2. **無限循環任務**：完成末端後立即回到起點，持續累積輪次。
> 3. **冷卻機制**：完成後計算下次可執行時間，跨 window 生效。
>
> 設計原則：
> - 收斂 Aggregate 邊界，確保事務一致性
> - 採用 CQRS 分離寫入模型與查詢模型
> - 時間統一使用 UTC

---

## 1. 範疇與假設

### 1.1 產品範疇
- 支援 iOS + Windows 桌面
- 每日產生當天可執行任務清單，從無 dependency 的 MVA 開始

### 1.2 時間模型
- 所有時間欄位統一使用 **UTC**
- 時間窗（Window）類型：Daily / Weekly / Biweekly / Monthly
- 冷卻（Cooldown）為**絕對時間限制**，不受 window 邊界影響

### 1.3 每日任務產生流程
- 系統每日執行一次任務產生
- 驗證時機：任務產生時自動驗證 + 可手動觸發驗證
- 列出所有「無未完成 dependency」的任務供執行

---

## 2. Cycle 模式設計

### 2.1 圖結構規則

Step 的 dependency 結構支援兩種形式：

**A) 單一路徑**：可以是循環
- 每個 step 入度 ≤ 1、出度 ≤ 1
- 允許形成循環（A → B → C → A）

**B) 有分支/匯聚**：必須是 DAG（無環）
- 允許一個 step 有多個後繼（分支）
- 允許一個 step 有多個前置（匯聚）
- 不允許存在任何環

```
✓ A → B → C           （單一路徑，線性）
✓ A → B → C → A       （單一路徑，循環）
✓ A → B, A → C        （分支，DAG）
✓ A → C, B → C        （匯聚，DAG）
✓ A → B → D, A → C → D（分支+匯聚，DAG）
✗ A → B → A, A → C    （有分支又有環）
```

**共同要求**：
- 至少一個入度為 0 的 step（MVA/起點）
- DAG 可有多個起點，皆可並行執行

### 2.2 兩種 Reset Mode

| 模式 | 說明 | 重置觸發 | Quota |
|------|------|----------|-------|
| **window_bound** | 時間窗綁定 | 進入新 window | 適用 |
| **infinite** | 無限循環 | 完成一輪後 | 不適用 |

### 2.3 Window-bound 模式
- 任務進度與特定 window 綁定
- Window 結束時，無論完成到哪一步，下個 window 從起點重新開始
- 適用場景：每週固定流程（週一規劃 → 週五回顧）
- 可設定 quota 限制同一 window 內的輪次
- 完成一輪後可繼續下一輪（受 quota 限制）

### 2.4 Infinite 模式
- 任務進度跨 window 延續
- 完成一輪後自動回到起點，cycle_index++
- 適用場景：持續累積的練習（每次練琴：暖身 → 練習曲 → 新曲目）
- 不適用 quota

### 2.5 Cycle 完成判定

| 圖結構 | 完成一輪的條件 |
|--------|----------------|
| 單一路徑（循環） | 回到起點 step |
| 單一路徑（線性） | 完成末端 step（出度為 0） |
| DAG（有分支） | 完成所有末端 step（所有出度為 0 的 step） |

### 2.6 Cooldown 組合

| Reset Mode | Cooldown | 行為 |
|------------|----------|------|
| window_bound | 無 | Window 內可連續完成，新 window 重置進度 |
| window_bound | 有 | Window 內受 CD 限制，新 window 重置進度但 CD 延續 |
| infinite | 無 | 可連續完成，跨 window 延續進度 |
| infinite | 有 | 受 CD 限制，跨 window 延續進度與 CD |

---

## 3. Bounded Context（界限上下文）

### 3.1 Habit Catalog（定義/配置）
- 管理：Habit、Step、Dependency、Policies（Quota/Cooldown/Reward）

### 3.2 Execution Tracking（執行/進度）
- 管理：Window、Completion
- 查詢模型：TaskBoard、HabitProgress

### 3.3 Player Economy（玩家經濟）
- 管理：Player、XPLedger
- 查詢模型：PlayerStat

---

## 4. Ubiquitous Language（統一語言）

| 術語 | 定義 |
|------|------|
| **Player** | 玩家/使用者 |
| **Habit** | 習慣主題（寫作、運動、洗碗） |
| **Step** | Habit 的一個執行單位/關卡 |
| **Dependency** | Step 間的前置關係（A 完成後才能做 B） |
| **Cycle** | 從起點到終點完成一輪 |
| **Window** | 統計與配額的時間窗（日/週/雙週/月） |
| **Completion** | 一次執行記錄（Done/Skipped），不可變 |
| **Quota** | Window-bound 模式下，時間窗內允許的最大輪次 |
| **Cooldown** | 完成後的最小間隔，以絕對時間計算 |
| **MVA** | Minimum Viable Action，無 dependency 的起始任務 |

---

## 5. Aggregate 設計

### 5.1 設計原則
- Aggregate 是一致性邊界，一次事務只修改一個 Aggregate
- 跨 Aggregate 的協調由 Domain Events + Eventual Consistency 處理

### 5.2 Aggregate 定義

#### A) Habit Aggregate（主聚合）

**Root**：Habit

**包含 Entities**：
- Step
- QuotaPolicy（僅 window_bound 模式）
- CooldownPolicy
- RewardPolicy

**責任**：
- Step dependency 的結構一致性
- 規則定義的完整性約束

**Invariants**：
- 每個 Habit 至少有一個 Step
- 至少一個入度為 0 的 step（起點/MVA）
- 若存在分支或匯聚（任一 step 入度 > 1 或出度 > 1），則不可有環
- 若為單一路徑（所有 step 入度 ≤ 1 且出度 ≤ 1），允許循環

#### B) Player Aggregate

**Root**：Player

**包含 Entities**：
- XPLedger

**責任**：
- XP 入帳的原子性

**Invariants**：
- XP 總額 = SUM(XPLedger.amount)
- Ledger 記錄不可變

---

## 6. 非 Aggregate 的 Entities

### 6.1 Window
- 代表一個時間窗實例
- 由系統根據當前時間自動產生或查詢

### 6.2 Completion
- 執行歷史記錄，寫入後不可變
- 作為 Event Log 存在

---

## 7. Write Model Schema

> 統一規範：snake_case、單數名詞、主鍵 uuid（TEXT）、外鍵 <ref>_id、時間 *_at（UTC）

### 7.1 Player Economy

#### player
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| name | text | |
| created_at | datetime | |

#### xp_ledger
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| player_id | uuid, FK → player | |
| source_type | text | enum: completion / bonus / admin |
| source_id | uuid | |
| amount | int | |
| created_at | datetime | |

---

### 7.2 Habit Catalog

#### habit
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| player_id | uuid, FK → player | |
| name | text | |
| category | text | |
| is_active | bool | |
| window_type | text | enum: daily / weekly / biweekly / monthly |
| reset_mode | text | enum: window_bound / infinite |
| created_at | datetime | |

#### step
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| habit_id | uuid, FK → habit | |
| name | text | |
| description | text | |
| difficulty | text | enum: easy / medium / hard |
| base_xp | int | |
| sort_order | int | UI 排序用 |

#### step_dependency
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| step_id | uuid, FK → step | 當前 step |
| depends_on_step_id | uuid, FK → step | 前置 step |

**約束**：
- unique(step_id, depends_on_step_id)
- 兩個 step 必須屬於同一 habit

---

### 7.3 Policies

#### quota_policy
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| habit_id | uuid, FK → habit | |
| max_cycles | int | Window 內最大輪次 |

> 僅適用於 window_bound 模式。infinite 模式不應建立此 policy。

#### cooldown_policy
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| habit_id | uuid, FK → habit | |
| cooldown_seconds | int | |

#### reward_policy
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| habit_id | uuid, FK → habit | |
| cycle_growth_type | text | enum: linear / exponential |
| cycle_growth_value | float | linear: +N%; exponential: 底數 |
| cycle_multiplier_cap | float | 倍率上限 |

---

### 7.4 Execution Tracking

#### window
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| player_id | uuid, FK → player | |
| window_type | text | enum: daily / weekly / biweekly / monthly |
| start_at | datetime | UTC |
| end_at | datetime | UTC |

**約束**：unique(player_id, window_type, start_at)

#### completion
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| player_id | uuid, FK → player | |
| habit_id | uuid, FK → habit | |
| window_id | uuid, FK → window | |
| step_id | uuid, FK → step | |
| cycle_index | int | 第幾輪（1 起算） |
| status | text | enum: done / skipped |
| xp_gained | int | |
| occurred_at | datetime | UTC |
| note | text, nullable | |

#### habit_cooldown_state
| 欄位 | 類型 | 說明 |
|------|------|------|
| **habit_id** | uuid, PK/FK → habit | |
| next_available_at | datetime, nullable | UTC |
| updated_at | datetime | |

#### habit_progress
| 欄位 | 類型 | 說明 |
|------|------|------|
| **habit_id** | uuid, PK/FK → habit | |
| current_cycle | int | 當前輪次 |
| updated_at | datetime | |

> 用於 infinite 模式跨 window 追蹤進度。window_bound 模式的 cycle 由 completion 查詢推算。

---

## 8. Read Model Schema（CQRS Query Side）

> 由 Domain Events 驅動更新，可重建。

### 8.1 player_stat
| 欄位 | 類型 | 說明 |
|------|------|------|
| **player_id** | uuid, PK | |
| xp_total | int | |
| level | int | |
| updated_at | datetime | |

### 8.2 window_habit_summary
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| habit_id | uuid, FK | |
| window_id | uuid, FK | |
| cycles_completed | int | |
| xp_earned | int | |
| updated_at | datetime | |

**約束**：unique(habit_id, window_id)

### 8.3 step_completion_cache
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| step_id | uuid, FK | |
| window_id | uuid, FK | |
| cycle_index | int | |
| is_completed | bool | |
| completed_at | datetime, nullable | |

**約束**：unique(step_id, window_id, cycle_index)

---

## 9. Domain Events

### 9.1 事件規格
所有事件包含：event_id, occurred_at, aggregate_type, aggregate_id, correlation_id, payload

### 9.2 事件清單

| 事件 | 觸發時機 | 主要 Payload |
|------|----------|--------------|
| **StepCompleted** | completion(done) 寫入後 | habit_id, step_id, window_id, cycle_index, xp_gained |
| **StepSkipped** | completion(skipped) 寫入後 | habit_id, step_id, window_id, cycle_index |
| **CycleCompleted** | 一輪所有 step 完成 | habit_id, window_id, cycle_index |
| **QuotaReached** | cycles_completed >= max_cycles | habit_id, window_id, max_cycles |
| **CooldownStarted** | 有 cooldown 的 habit 完成 step | habit_id, next_available_at |
| **XPGranted** | xp_ledger 寫入後 | player_id, amount, source_type, source_id |

---

## 10. Invariants（不變條件）

### 10.1 Dependency 圖結構

**規則**：
- 至少一個入度為 0 的 step（MVA）
- 單一路徑（所有 step 入度 ≤ 1 且出度 ≤ 1）：允許循環
- 有分支/匯聚（任一 step 入度 > 1 或出度 > 1）：必須無環（DAG）

**驗證時機**：每日任務產生時 + 手動觸發

**驗證方式**：
1. 計算每個 step 的入度與出度
2. 確認至少一個入度為 0 的 step 存在
3. 判斷是否為單一路徑（所有 step 入度 ≤ 1 且出度 ≤ 1）
4. 若非單一路徑，執行拓撲排序檢測環

### 10.2 Cooldown 約束
- 若 next_available_at > now()，該 habit 不可完成任何 step
- 完成後：next_available_at = occurred_at + cooldown_seconds

### 10.3 Quota 約束（僅 window_bound）
- cycles_completed >= max_cycles 時，該 habit 在此 window 不可再完成

### 10.4 XP 計算
- xp_gained = base_xp × difficulty_modifier × cycle_multiplier
- cycle_multiplier = min(cap, growth_function(cycle_index))

| difficulty | modifier |
|------------|----------|
| easy | 1.0 |
| medium | 1.5 |
| hard | 2.0 |

### 10.5 Completion 不可變
- 寫入後不可修改
- 更正以 xp_ledger(source_type=admin) 補償

---

## 11. Application Use Cases

### 11.1 GenerateDailyTasks
**觸發**：每日定時 / App 啟動

**流程**：
1. 確保當日各類型 window 存在
2. 驗證所有 active habit 的 dependency 圖（依 reset_mode 套用不同規則）
3. 對每個 habit 計算可執行的 step

**Window-bound 模式**：
- 取當前 window 的 completion，識別當前 cycle
- 新 window 自動從 cycle=1 開始

**Infinite 模式**：
- 從 habit_progress 取得 current_cycle
- 跨 window 延續進度

### 11.2 CompleteStep
**輸入**：player_id, habit_id, step_id, occurred_at, note?

**前置檢查**：
1. step 屬於該 habit
2. step 的所有 dependency 在當前 cycle 已完成
3. cooldown：now() >= next_available_at
4. quota（window_bound）：cycles_completed < max_cycles

**執行**：
1. 計算 xp_gained
2. 寫入 completion
3. 寫入 xp_ledger
4. 更新 habit_cooldown_state（若有）
5. 發出 StepCompleted

**Cycle 完成判定**：
- 單一路徑（循環）：當前 step 的後繼是起點
- 單一路徑（線性）：當前 step 出度為 0
- DAG：所有出度為 0 的 step 都已完成

完成一輪時：
- 發出 CycleCompleted
- infinite 模式：更新 habit_progress.current_cycle++
- window_bound 模式：下一輪從頭開始（若未達 quota）

### 11.3 SkipStep
- 寫入 completion(status=skipped, xp_gained=0)
- 發出 StepSkipped
- 不解鎖後續 step

### 11.4 ValidateHabitGraph
**執行**：
1. 計算每個 step 的入度與出度
2. 確認至少一個入度為 0 的 step 存在（MVA）
3. 判斷是否為單一路徑：所有 step 入度 ≤ 1 且出度 ≤ 1
4. 若非單一路徑（有分支/匯聚），執行拓撲排序檢測環
5. 回傳驗證結果：pass / 無起點 / 有分支且有環

---

## 12. Queries

### 12.1 GetTaskBoard
**輸入**：player_id, date

**輸出**：每個 habit 的可執行 step、cooldown 狀態、quota 狀態

### 12.2 GetHabitDetail
**輸入**：habit_id

**輸出**：基本資訊、steps（含 dependency）、policies、近期 completion、當前進度

### 12.3 GetPlayerStats
**輸入**：player_id

**輸出**：xp_total、level、各 window 統計

---

## 13. 索引建議

**Write Model**：
- completion(player_id, occurred_at DESC)
- completion(window_id, habit_id)
- step_dependency(step_id)
- step_dependency(depends_on_step_id)
- habit_cooldown_state(next_available_at)

**Read Model**：
- window_habit_summary(window_id)
- step_completion_cache(window_id, cycle_index)

---

## 14. 同步考量

### 14.1 Idempotency Key
completion 使用 (player_id, habit_id, step_id, window_id, cycle_index, occurred_at) 判斷重複

### 14.2 Event Outbox

#### event_outbox
| 欄位 | 類型 | 說明 |
|------|------|------|
| **id** | uuid, PK | |
| event_type | text | |
| aggregate_type | text | |
| aggregate_id | uuid | |
| payload | text (JSON) | |
| created_at | datetime | |
| published_at | datetime, nullable | |

---

## 15. 附錄：與 Notion 既有概念的對應

| Notion 概念 | 本設計對應 |
|-------------|-----------|
| Habits | habit |
| Units | step |
| Dependency (self-relation) | step_dependency |
| Loops | window |
| HabitLoopProgress (rollup) | window_habit_summary (Read Model) |
| Sessions | completion |
| Profile | player + player_stat |
| Next Unit (rollup) | 移除，由查詢計算 |
| XP (rollup) | 移除，由 xp_ledger 計算 |
