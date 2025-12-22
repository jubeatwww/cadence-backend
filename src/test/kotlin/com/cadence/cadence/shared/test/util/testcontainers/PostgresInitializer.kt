package com.cadence.cadence.shared.test.util.testcontainers

import org.flywaydb.core.Flyway
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class PostgresInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
  override fun initialize(applicationContext: ConfigurableApplicationContext) {
    val container = TestcontainersConfig.postgresContainer
    container.start()

    TestPropertyValues
      .of(
        "spring.datasource.url=${container.jdbcUrl}",
        "spring.datasource.username=${container.username}",
        "spring.datasource.password=${container.password}",
        "spring.flyway.enabled=true"
      )
      .applyTo(applicationContext.environment)
    val flyway = Flyway
      .configure()
      .cleanDisabled(false)
      .dataSource(container.jdbcUrl, container.username, container.password)
      .load()
    flyway.clean()
    flyway.migrate()
  }
}
