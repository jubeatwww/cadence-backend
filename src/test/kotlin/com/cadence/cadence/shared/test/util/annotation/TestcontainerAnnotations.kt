package com.cadence.cadence.shared.test.util.annotation

import com.cadence.cadence.shared.test.util.testcontainers.PostgresInitializer
import com.cadence.cadence.shared.test.util.testcontainers.RedisInitializer
import org.springframework.test.context.ContextConfiguration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContextConfiguration(initializers = [PostgresInitializer::class])
annotation class EnableTestPostgres

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContextConfiguration(initializers = [RedisInitializer::class])
annotation class EnableTestRedis

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContextConfiguration(
  initializers = [
    PostgresInitializer::class,
    RedisInitializer::class,
  ]
)
annotation class EnableAllTestContainers
