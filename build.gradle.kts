import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Property
import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.26"
    id("nu.studer.jooq") version "9.0"
    application
    id("com.google.protobuf") version "0.9.4"
}

group = "dev.kotbegemot.wind"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

val jooqVersion = "3.19.11"
val kotlinCoroutinesVersion = "1.10.1"
val kotestVersion = "5.9.1"
val mockkVersion = "1.13.14"
val reactorVersion = "1.2.3"
val jooqYdbDialectVersion = "1.0.1"
val jooqYdbR2dbcDriverVersion = "0.9.0-SNAPSHOT"
val vertxVersion = "4.5.11"
val protobufDepVersion = "4.29.2"
val grpcJavaVersion = "1.69.0"
val grpcKotlinVersion = "1.4.1"
val protobufVersion = "3.25.3"
val arrowVersion = "2.0.0"
val postgresDriverVersion = "1.0.7.RELEASE"
val hopliteVersion = "2.9.0"

dependencies {
    //DEPS
    //Kotlin
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:${reactorVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    //Functional
    implementation(platform("io.arrow-kt:arrow-stack:${arrowVersion}"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-optics")
    ksp("io.arrow-kt:arrow-optics-ksp-plugin:${arrowVersion}")
    //Config
    implementation("com.sksamuel.hoplite:hoplite-core:${hopliteVersion}")
    implementation("com.sksamuel.hoplite:hoplite-hocon:${hopliteVersion}")
    implementation("com.sksamuel.hoplite:hoplite-arrow:${hopliteVersion}")
    implementation("com.sksamuel.hoplite:hoplite-datetime:${hopliteVersion}")
    //Database
    implementation("org.jooq:jooq:${jooqVersion}")
    implementation("org.jooq:jooq-kotlin:${jooqVersion}")
    runtimeOnly("org.postgresql:r2dbc-postgresql:${postgresDriverVersion}")
    jooqGenerator("org.jooq:jooq-meta-extensions:${jooqVersion}")
    jooqGenerator("org.jooq:jooq-meta-kotlin:${jooqVersion}")
    //gRPC
    implementation("com.google.protobuf:protobuf-java:$protobufDepVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufDepVersion")
    implementation("com.google.protobuf:protobuf-java-util:$protobufDepVersion")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("io.grpc:grpc-stub:$grpcJavaVersion")
    implementation("io.grpc:grpc-protobuf:$grpcJavaVersion")
    //TEST
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${kotlinCoroutinesVersion}")
    testImplementation("io.kotest:kotest-assertions-core-jvm:${kotestVersion}")
    testImplementation("io.kotest:kotest-framework-concurrency-jvm:${kotestVersion}")
    testImplementation("io.mockk:mockk-jvm:${mockkVersion}")
    testImplementation(kotlin("test"))
    testImplementation("io.grpc:grpc-testing:$grpcJavaVersion")
}

tasks.test {
    useJUnitPlatform()
}
tasks.withType<KotlinCompile> {
    dependsOn("generateJooq")
    dependsOn("generateProto")
}
kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-Xjsr305=strict",
                "-opt-in=kotlin.RequiresOptIn"
            )
        )
    }
}
jooq {
    version.set("$jooqVersion")
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)
            jooqConfiguration.apply {
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        inputSchema = "public"
                        properties = listOf(
                            Property().apply {
                                key = "scripts"
                                value = "src/main/resources/schema.sql"
                            },
                            Property().apply {
                                key = "sort"
                                value = "none"
                            },
                            Property().apply {
                                key = "unqualifiedSchema"
                                value = "public"
                            },
                            Property().apply {
                                key = "defaultNameCase"
                                value = "lower"
                            }
                        )
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isPojosAsKotlinDataClasses = true
                        isImplicitJoinPathsToMany = false
                    }
                    target.apply {
                        packageName = "dev.kotbegemot.wind.jooq"
                        directory = "${projectDir}/build/generated/jooq/main"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}
protobuf{
    protoc {
        artifact = "com.google.protobuf:protoc:${protobufVersion}"
    }
    plugins {
        id("grpc"){
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcJavaVersion}"
        }
        id("grpckt"){
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${grpcKotlinVersion}:jdk8@jar"
        }
    }
    generateProtoTasks{
        ofSourceSet("main").forEach {
            it.plugins{
                id("grpc"){
                    option("@generated=omit")
                }
                id("grpckt"){}
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}