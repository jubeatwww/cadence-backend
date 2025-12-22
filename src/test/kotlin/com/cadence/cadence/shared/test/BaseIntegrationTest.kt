package com.cadence.cadence.shared.test

import com.cadence.cadence.shared.test.util.annotation.DisableCleanup
import com.cadence.cadence.shared.test.util.annotation.DisableSeeding
import com.cadence.cadence.shared.test.util.annotation.EnableAllTestContainers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.ScriptUtils
import org.springframework.test.context.ActiveProfiles
import java.sql.Connection

@SpringBootTest
@ActiveProfiles("test")
@EnableAllTestContainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseIntegrationTest {
  @Autowired
  protected lateinit var jdbcTemplate: JdbcTemplate

  @Autowired
  private lateinit var redisTemplate: StringRedisTemplate

  @Autowired
  private lateinit var applicationContext: ApplicationContext

  fun flushRedis() {
    redisTemplate.execute { connection ->
      connection.serverCommands().flushAll()
      null
    }
  }

  private fun doCleanup() {
    val resource = applicationContext.getResource("classpath:sql/cleanup.sql")
    jdbcTemplate.execute { connection: Connection ->
      ScriptUtils.executeSqlScript(connection, resource)
    }
  }

  private fun doSeeding() {
    val seeding = applicationContext.getResource("classpath:sql/seeding.sql")
    jdbcTemplate.execute { connection: Connection ->
      ScriptUtils.executeSqlScript(connection, seeding)
    }
  }

  @BeforeAll
  fun initSeeding() {
    doSeeding()
  }

  @AfterEach
  fun cleanup(testInfo: TestInfo) {
    flushRedis()

    val testClass = testInfo.testClass.get()
    val testMethod = testInfo.testMethod.get()
    if (testClass.isAnnotationPresent(DisableCleanup::class.java) ||
      testMethod.isAnnotationPresent(DisableCleanup::class.java)
    ) {
      return
    }

    doCleanup()

    if (testMethod.isAnnotationPresent(DisableSeeding::class.java) || testClass.isAnnotationPresent(DisableSeeding::class.java)) {
      return
    }

    doSeeding()
  }

  @AfterAll
  fun finalCleanup() {
    flushRedis()
    doCleanup()
    doSeeding()
  }
}
