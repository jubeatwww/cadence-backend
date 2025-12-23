package com.cadence.cadence.shared.test.util.testcontainers

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class RedisInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
  override fun initialize(applicationContext: ConfigurableApplicationContext) {
    val container = TestcontainersConfig.redisContainer
    container.start()

    TestPropertyValues
      .of(
        "spring.data.redis.host=${container.host}",
        "spring.data.redis.port=${container.getMappedPort(6379)}"
      )
      .applyTo(applicationContext.environment)
  }
}
