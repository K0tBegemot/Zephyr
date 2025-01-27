package dev.kotbegemot.wind

import dev.kotbegemot.wind.service.remote.StartService
import io.grpc.ServerBuilder
import java.util.concurrent.Executors

fun main() {
    ServerBuilder
        .forPort(9090)
        .executor(Executors.newFixedThreadPool(50))
        .addService(StartService())
}