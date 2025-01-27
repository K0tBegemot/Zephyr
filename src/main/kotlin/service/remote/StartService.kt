package dev.kotbegemot.wind.service.remote

import dev.kotbegemot.wind.grpc.StartServiceGrpcKt
import dev.kotbegemot.wind.grpc.StartServiceOuterClass
import dev.kotbegemot.wind.grpc.entityUpdateEvent

class StartService: StartServiceGrpcKt.StartServiceCoroutineImplBase() {
    override suspend fun createEntity(request: StartServiceOuterClass.CreateCommand): StartServiceOuterClass.EntityUpdateEvent {
        return entityUpdateEvent {
            id = request.id
        }
    }
    override suspend fun updateEntity(request: StartServiceOuterClass.UpdateCommand): StartServiceOuterClass.EntityUpdateEvent {
        return entityUpdateEvent {
            id = request.id
        }
    }
}