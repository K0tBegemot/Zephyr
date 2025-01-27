package dev.kotbegemot.wind.config.property

data class ApplicationPropertyConfig(
    val common: CommonPropertyConfig,
    val database: DatabasePropertyConfig,
    val kafka: KafkaPropertyConfig,
    val grpc: GrpcPropertyConfig,
)
data class CommonPropertyConfig(
    val applicationName: String,
    val tag: String,
    val previousTag: String,
)
data class DatabasePropertyConfig(
    val driver: String,
    val host: String,
    val port: Int,
    val database: String,
    val user: String,
    val password: String,
    val options: Map<String, String>,
)
data class KafkaPropertyConfig(
    val bootstrapServers: Set<KafkaBootstrapServerPropertyConfig>,
)
data class KafkaBootstrapServerPropertyConfig(
    val hostname: String,
    val port: Int,
)
data class GrpcPropertyConfig(
    val serverPort: Int,
)