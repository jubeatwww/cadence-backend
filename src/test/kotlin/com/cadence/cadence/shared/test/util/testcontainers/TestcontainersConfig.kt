package com.cadence.cadence.shared.test.util.testcontainers

import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

private const val SHARED_NETWORK_NAME = "testcontainers-shared-network"

private class ExistingNetwork(private val id: String) : Network {
  override fun getId(): String = id
  override fun close() {}
}

private fun ensureSharedNetwork(): Network {
  val dockerClient = DockerClientFactory.instance().client()

  val existing = dockerClient.listNetworksCmd()
    .withNameFilter(SHARED_NETWORK_NAME)
    .exec()
    .firstOrNull()

  if (existing != null) {
    return ExistingNetwork(existing.id)
  }

  return Network.builder()
    .createNetworkCmdModifier { cmd -> cmd.withName(SHARED_NETWORK_NAME) }
    .build()
}

object TestcontainersConfig {
  private val sharedNetwork = ensureSharedNetwork()

  val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:17.3")).apply {
    withNetwork(sharedNetwork)
    withNetworkAliases("postgres")
    withDatabaseName("sunnymiucatapi")
    withUsername("sunnymiucat")
    withPassword("password")
    withCommand(
      "postgres",
      "-c", "wal_level=logical",
      "-c", "max_wal_senders=10",
      "-c", "max_replication_slots=10"
    )
  }
  val redisContainer = GenericContainer<Nothing>("redis:8.2.1").apply {
    withNetwork(sharedNetwork)
    withNetworkAliases("redis")
    withExposedPorts(6379)
  }
}
