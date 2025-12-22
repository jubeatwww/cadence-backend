# Units Mock Data for PostgreSQL Seeding

## 目的

這份文件是給 AI 閱讀的，用來產生 PostgreSQL 的 seeding 腳本。Schema 可能會調整，但資料結構與範例可做為參考。

---

## 資料表關係概覽

```
Habits (1) ──< Units (N)
                │
                └── Dependency (self-reference, 1:1)
                └── Next (self-reference, 1:N)
```

- 每個 **Unit** 屬於一個 **Habit**
- Unit 可有一個 **Dependency**（前置任務，指向另一個 Unit）
- Unit 可有多個 **Next**（後續任務）
- Dependency 和 Next 形成一個 **技能樹/任務鏈** 的結構

---

## Habits 資料表

### Schema

| Column | Type | Description | Options |
| --- | --- | --- | --- |
| id | UUID/SERIAL | Primary Key | - |
| name | TEXT | 習慣名稱 | - |
| description | TEXT | 習慣描述 | - |
| category | ENUM | 分類 | `身體`, `生活`, `學習`, `創作` |
| default_loop | ENUM | 預設週期 | `Daily`, `Weekly`, `Monthly` |
| active | BOOLEAN | 是否啟用 | - |

### Mock Data (10 筆)

```json
[
  {"name": "重訓", "description": "希望每週三練來每個部位都有練到", "category": "身體", "default_loop": "Weekly", "active": true},
  {"name": "System Design 閱讀", "description": "面試練習，盡可能看完很多Awesome Scalability的文章跟把DDIA、極客時間的課程讀完", "category": "學習", "default_loop": "Daily", "active": true},
  {"name": "Day Trade", "description": "想要穩定從小那獲利", "category": "學習", "default_loop": "Daily", "active": true},
  {"name": "有氧", "description": "跑步機在客廳，啟動成本低希望每天有氧來增加減脂效率", "category": "身體", "default_loop": "Weekly", "active": true},
  {"name": "寫作", "description": "將念書跟刷題的內容寫成技術部落格，偶爾也想嘗試不同題材", "category": "創作", "default_loop": "Monthly", "active": true},
  {"name": "喝水", "description": "每天喝水滿2000c.c.", "category": "身體", "default_loop": "Daily", "active": true},
  {"name": "日常", "description": null, "category": "生活", "default_loop": "Daily", "active": true},
  {"name": "刷題", "description": "每天刷Hard跟練習面試直到進Google", "category": "學習", "default_loop": "Daily", "active": true},
  {"name": "英文", "description": "讓英文口說成為日常，達到流利對答可以面試全英文職缺的程度", "category": "學習", "default_loop": "Daily", "active": true},
  {"name": "吉他", "description": "中期目標是學會彈鄭成和的Gravity", "category": "創作", "default_loop": "Daily", "active": true}
]
```

---

## Units 資料表

### Schema

| Column | Type | Description | Options |
| --- | --- | --- | --- |
| id | UUID/SERIAL | Primary Key | - |
| name | TEXT | 任務名稱 | - |
| description | TEXT | 任務描述 | - |
| difficulty | ENUM | 難度 | `Easy`, `Medium`, `Hard` |
| loop_type | ENUM | 週期類型 | `Daily`, `Weekly`, `Monthly` |
| xp | INTEGER | 經驗值 | - |
| habit_id | FK → Habits | 所屬習慣 | - |
| dependency_id | FK → Units (nullable) | 前置任務（自己關聯） | - |

### Mock Data (73 筆)

以下資料按 Habit 分組，`dependency_name` 欄位表示該 Unit 的前置任務名稱（用於建立關聯）。

### 喝水 (Daily, 身體)

```json
[
  {"name": "喝水 200ml", "description": "200ml", "difficulty": "Easy", "loop_type": "Daily", "xp": 3, "dependency_name": null},
  {"name": "喝水 400ml", "description": "400ml", "difficulty": "Easy", "loop_type": "Daily", "xp": 4, "dependency_name": "喝水 200ml"},
  {"name": "喝水 600ml", "description": "600ml", "difficulty": "Easy", "loop_type": "Daily", "xp": 5, "dependency_name": "喝水 400ml"},
  {"name": "喝水 800ml", "description": "800ml", "difficulty": "Easy", "loop_type": "Daily", "xp": 6, "dependency_name": "喝水 600ml"},
  {"name": "喝水 1000ml", "description": "1000ml", "difficulty": "Medium", "loop_type": "Daily", "xp": 7, "dependency_name": "喝水 800ml"},
  {"name": "喝水 1200ml", "description": "1200ml", "difficulty": "Medium", "loop_type": "Daily", "xp": 8, "dependency_name": "喝水 1000ml"},
  {"name": "喝水 1400ml", "description": "1400ml", "difficulty": "Medium", "loop_type": "Daily", "xp": 9, "dependency_name": "喝水 1200ml"},
  {"name": "喝水 1600ml", "description": "1600ml", "difficulty": "Medium", "loop_type": "Daily", "xp": 10, "dependency_name": "喝水 1400ml"},
  {"name": "喝水 1800ml", "description": "1800ml", "difficulty": "Hard", "loop_type": "Daily", "xp": 12, "dependency_name": "喝水 1600ml"},
  {"name": "喝水 2000ml", "description": "2000ml - 達標！", "difficulty": "Hard", "loop_type": "Daily", "xp": 15, "dependency_name": "喝水 1800ml"}
]
```

### 有氧 (Weekly, 身體)

```json
[
  {"name": "有氧 #1", "description": "本週第 1 次", "difficulty": "Easy", "loop_type": "Weekly", "xp": 30, "dependency_name": null},
  {"name": "有氧 #2", "description": "本週第 2 次", "difficulty": "Easy", "loop_type": "Weekly", "xp": 35, "dependency_name": "有氧 #1"},
  {"name": "有氧 #3", "description": "本週第 3 次", "difficulty": "Easy", "loop_type": "Weekly", "xp": 40, "dependency_name": "有氧 #2"},
  {"name": "有氧 #4", "description": "本週第 4 次", "difficulty": "Easy", "loop_type": "Weekly", "xp": 50, "dependency_name": "有氧 #3"},
  {"name": "有氧 #5", "description": "本週第 5 次", "difficulty": "Medium", "loop_type": "Weekly", "xp": 60, "dependency_name": "有氧 #4"},
  {"name": "有氧 #6", "description": "本週第 6 次 - 達標！", "difficulty": "Medium", "loop_type": "Weekly", "xp": 75, "dependency_name": "有氧 #5"}
]
```

### 重訓 (Weekly, 身體)

```json
[
  {"name": "重訓 #1", "description": "本週第 1 次", "difficulty": "Easy", "loop_type": "Weekly", "xp": 30, "dependency_name": null},
  {"name": "重訓 #2", "description": "本週第 2 次", "difficulty": "Easy", "loop_type": "Weekly", "xp": 35, "dependency_name": "重訓 #1"},
  {"name": "重訓 #3", "description": "本週第 3 次", "difficulty": "Medium", "loop_type": "Weekly", "xp": 45, "dependency_name": "重訓 #2"},
  {"name": "重訓 #4", "description": "本週第 4 次 - 達標！", "difficulty": "Hard", "loop_type": "Weekly", "xp": 60, "dependency_name": "重訓 #3"}
]
```

### 日常 (Daily/Weekly, 生活)

```json
[
  {"name": "日常: 冥想", "description": "心靈平靜", "difficulty": "Easy", "loop_type": "Daily", "xp": 15, "dependency_name": null},
  {"name": "日常: 吃維他命", "description": "快速完成", "difficulty": "Easy", "loop_type": "Daily", "xp": 5, "dependency_name": null},
  {"name": "日常: 陪貓玩", "description": "貓咪互動", "difficulty": "Easy", "loop_type": "Daily", "xp": 10, "dependency_name": null},
  {"name": "日常: 貓梳毛", "description": "貓咪保養", "difficulty": "Easy", "loop_type": "Daily", "xp": 10, "dependency_name": null},
  {"name": "日常: 滾健腹輪", "description": "核心訓練", "difficulty": "Medium", "loop_type": "Daily", "xp": 15, "dependency_name": null},
  {"name": "日常: 貓洗水碗", "description": "清洗貓咪水碗", "difficulty": "Easy", "loop_type": "Weekly", "xp": 10, "dependency_name": null},
  {"name": "日常: 收拾家裡", "description": "整理環境", "difficulty": "Easy", "loop_type": "Weekly", "xp": 10, "dependency_name": null},
  {"name": "日常: 吸地板", "description": "清潔地板", "difficulty": "Easy", "loop_type": "Weekly", "xp": 10, "dependency_name": null}
]
```

### System Design 閱讀 (Daily, 學習)

```json
[
  {"name": "SD: 選文章交給 AI", "description": "MVA 起點：選定文章並貼給 AI", "difficulty": "Easy", "loop_type": "Daily", "xp": 10, "dependency_name": null},
  {"name": "SD: AI 問答", "description": "Route A：蘇格拉底式問答學習", "difficulty": "Easy", "loop_type": "Daily", "xp": 30, "dependency_name": "SD: 選文章交給 AI"},
  {"name": "SD: 回想問答", "description": "MVA：回想 AI 問答的內容", "difficulty": "Easy", "loop_type": "Daily", "xp": 10, "dependency_name": null},
  {"name": "SD: 閱讀文章", "description": "開始閱讀文章", "difficulty": "Medium", "loop_type": "Daily", "xp": 40, "dependency_name": "SD: 回想問答"},
  {"name": "SD: 草稿心智圖", "description": "隨便畫個粗略心智圖", "difficulty": "Easy", "loop_type": "Daily", "xp": 20, "dependency_name": "SD: 閱讀文章"},
  {"name": "SD: 閱讀完成", "description": "讀完整篇文章", "difficulty": "Hard", "loop_type": "Daily", "xp": 50, "dependency_name": "SD: 草稿心智圖"},
  {"name": "SD: 輸出心智圖", "description": "輸出完整心智圖（或給 Gemini 生後 review）", "difficulty": "Hard", "loop_type": "Daily", "xp": 70, "dependency_name": "SD: 閱讀完成"}
]
```

### Day Trade (Daily/Weekly, 學習)

```json
[
  {"name": "DT: 打開圖表", "description": "Route A MVA：打開 TradingView 看圖", "difficulty": "Easy", "loop_type": "Daily", "xp": 5, "dependency_name": null},
  {"name": "DT: 找 setup", "description": "辨識 setup（不執行）", "difficulty": "Medium", "loop_type": "Daily", "xp": 20, "dependency_name": "DT: 打開圖表"},
  {"name": "DT: 標記關鍵價位", "description": "標記支撐/壓力/結構", "difficulty": "Easy", "loop_type": "Daily", "xp": 15, "dependency_name": "DT: 找 setup"},
  {"name": "DT: 截圖紀錄", "description": "截圖保存觀察結果", "difficulty": "Easy", "loop_type": "Daily", "xp": 10, "dependency_name": "DT: 標記關鍵價位"},
  {"name": "DT: 選歷史日期", "description": "Route B MVA：選一段歷史行情", "difficulty": "Easy", "loop_type": "Daily", "xp": 5, "dependency_name": null},
  {"name": "DT: 對照走勢", "description": "對照實際走勢驗證判斷", "difficulty": "Easy", "loop_type": "Daily", "xp": 15, "dependency_name": "DT: 選歷史日期"},
  {"name": "DT: 標記判斷", "description": "標記當時你會怎麼做", "difficulty": "Medium", "loop_type": "Daily", "xp": 20, "dependency_name": "DT: 對照走勢"},
  {"name": "DT: 紀錄學習", "description": "紀錄學到什麼", "difficulty": "Easy", "loop_type": "Daily", "xp": 10, "dependency_name": "DT: 標記判斷"},
  {"name": "DT: 打開模擬盤", "description": "Route C MVA：打開 Tradovate 模擬盤", "difficulty": "Easy", "loop_type": "Weekly", "xp": 5, "dependency_name": null},
  {"name": "DT: 等待 setup", "description": "耐心等待 setup 出現", "difficulty": "Medium", "loop_type": "Weekly", "xp": 20, "dependency_name": "DT: 打開模擬盤"},
  {"name": "DT: 執行交易", "description": "照規則執行交易", "difficulty": "Hard", "loop_type": "Weekly", "xp": 30, "dependency_name": "DT: 等待 setup"},
  {"name": "DT: 檢討過程", "description": "檢討「有沒有照規則做」不看盈虧", "difficulty": "Medium", "loop_type": "Weekly", "xp": 25, "dependency_name": "DT: 執行交易"}
]
```

### 刷題 (Daily, 學習)

```json
[
  {"name": "刷題: Medium 餵 Gemini", "description": "MVA：選題並餵給 Gemini GEM", "difficulty": "Easy", "loop_type": "Daily", "xp": 10, "dependency_name": null},
  {"name": "刷題: Medium 解題", "description": "解不出來才實際解題", "difficulty": "Medium", "loop_type": "Daily", "xp": 30, "dependency_name": "刷題: Medium 餵 Gemini"},
  {"name": "刷題: Medium EMPIRE", "description": "EMPIRE 面試模擬", "difficulty": "Medium", "loop_type": "Daily", "xp": 50, "dependency_name": "刷題: Medium 解題"},
  {"name": "刷題: Hard 餵 Gemini", "description": "MVA：選題並餵給 Gemini GEM", "difficulty": "Easy", "loop_type": "Daily", "xp": 10, "dependency_name": null},
  {"name": "刷題: Hard 解題", "description": "解不出來才實際解題", "difficulty": "Hard", "loop_type": "Daily", "xp": 50, "dependency_name": "刷題: Hard 餵 Gemini"},
  {"name": "刷題: Hard EMPIRE", "description": "EMPIRE 面試模擬", "difficulty": "Hard", "loop_type": "Daily", "xp": 70, "dependency_name": "刷題: Hard 解題"}
]
```

### 英文 (Daily, 學習)

```json
[
  {"name": "英文: 選素材", "description": "MVA：打開 YouTube/Podcast 選好片段", "difficulty": "Easy", "loop_type": "Daily", "xp": 5, "dependency_name": null},
  {"name": "英文: 裸聽", "description": "不開字幕先聽一遍", "difficulty": "Easy", "loop_type": "Daily", "xp": 10, "dependency_name": "英文: 選素材"},
  {"name": "英文: 字幕+語塊", "description": "開字幕再看，紀錄語塊", "difficulty": "Easy", "loop_type": "Daily", "xp": 15, "dependency_name": "英文: 裸聽"},
  {"name": "英文: AI擴展", "description": "語塊丟給 AI 生例句/解釋用法", "difficulty": "Easy", "loop_type": "Daily", "xp": 15, "dependency_name": "英文: 字幕+語塊"},
  {"name": "英文: 默寫", "description": "默寫語塊/句子", "difficulty": "Medium", "loop_type": "Daily", "xp": 20, "dependency_name": "英文: AI擴展"},
  {"name": "英文: 小聲跟讀", "description": "嘴巴動但幾乎沒聲音，降低羞恥感", "difficulty": "Medium", "loop_type": "Daily", "xp": 25, "dependency_name": "英文: 默寫"},
  {"name": "英文: 正常跟讀", "description": "開口正常音量跟讀", "difficulty": "Hard", "loop_type": "Daily", "xp": 35, "dependency_name": "英文: 小聲跟讀"},
  {"name": "英文: 再聽驗收", "description": "最後裸聽驗收成果", "difficulty": "Easy", "loop_type": "Daily", "xp": 15, "dependency_name": "英文: 正常跟讀"}
]
```

### 寫作 (Monthly, 創作)

有三條路徑（Route A/B/C），共用部分步驟：

```json
[
  {"name": "寫作: SD 選題開頁", "description": "Route A MVA：選 System Design 題目，開 Notion 頁面", "difficulty": "Easy", "loop_type": "Monthly", "xp": 10, "dependency_name": null},
  {"name": "寫作: SD 初稿", "description": "撰寫完整草稿", "difficulty": "Medium", "loop_type": "Monthly", "xp": 60, "dependency_name": "寫作: SD 選題開頁"},
  {"name": "寫作: SD 大綱", "description": "擬定文章架構", "difficulty": "Easy", "loop_type": "Monthly", "xp": 30, "dependency_name": "寫作: SD 初稿"},
  {"name": "寫作: SD 發佈", "description": "潤稿發佈（最高獎勵）", "difficulty": "Hard", "loop_type": "Monthly", "xp": 100, "dependency_name": "寫作: SD 大綱"},
  {"name": "寫作: 刷題選題開頁", "description": "Route B MVA：選刷題題目，開 Notion 頁面", "difficulty": "Easy", "loop_type": "Monthly", "xp": 10, "dependency_name": null},
  {"name": "寫作: 刷題初稿", "description": "撰寫完整草稿", "difficulty": "Medium", "loop_type": "Monthly", "xp": 40, "dependency_name": "寫作: 刷題選題開頁"},
  {"name": "寫作: 刷題大綱", "description": "擬定文章架構", "difficulty": "Easy", "loop_type": "Monthly", "xp": 20, "dependency_name": "寫作: 刷題初稿"},
  {"name": "寫作: 刷題發佈", "description": "潤稿發佈", "difficulty": "Hard", "loop_type": "Monthly", "xp": 60, "dependency_name": "寫作: 刷題大綱"},
  {"name": "寫作: 一般選題開頁", "description": "Route C MVA：選其他題材，開 Notion 頁面", "difficulty": "Easy", "loop_type": "Monthly", "xp": 10, "dependency_name": null},
  {"name": "寫作: 一般初稿", "description": "撰寫完整草稿", "difficulty": "Medium", "loop_type": "Monthly", "xp": 30, "dependency_name": "寫作: 一般選題開頁"},
  {"name": "寫作: 一般大綱", "description": "擬定文章架構", "difficulty": "Easy", "loop_type": "Monthly", "xp": 15, "dependency_name": "寫作: 一般初稿"},
  {"name": "寫作: 一般發佈", "description": "潤稿發佈（部落格多樣化）", "difficulty": "Hard", "loop_type": "Monthly", "xp": 45, "dependency_name": "寫作: 一般大綱"}
]
```

---

## 給 AI 的 Seeding 指引

### 建議步驟

1. **先建立 Habits**：共 10 筆，產生各自的 ID
2. **建立 Units（不含 dependency）**：共 73 筆，先建立基本資料，`dependency_id` 暫時為 `NULL`
3. **更新 Units 的 dependency_id**：用 `dependency_name` 查找對應的 Unit ID 來建立關聯

### ENUM 定義

```sql
CREATE TYPE category_enum AS ENUM ('身體', '生活', '學習', '創作');
CREATE TYPE loop_type_enum AS ENUM ('Daily', 'Weekly', 'Monthly');
CREATE TYPE difficulty_enum AS ENUM ('Easy', 'Medium', 'Hard');
```

### 特性說明

- **XP 設計規則**：同一個 Habit 中，越後面的步驟 XP 越高
- **Difficulty 設計規則**：Easy → Medium → Hard 是漸進的難度曲線
- **Dependency Chain**：形成線性任務鏈，完成前置任務才能解鎖下一個
- **多路徑設計**：某些 Habit（如寫作、Day Trade）有多條獨立路徑，各自有 `dependency_name: null` 的起點