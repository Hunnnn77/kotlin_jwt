package com.example.db

import com.example.model.AuthBody
import com.example.routing.Config
import com.example.util.toUpperFirst
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull

class Mongo private constructor(config: Config) {
    companion object {
        fun getInstance(config: Config): Mongo {
            return Mongo(config)
        }
    }

    private val mongoClient = MongoClient.create(config.uri)
    private val database = mongoClient.getDatabase(config.db)
    private val collection = database.getCollection<AuthBody>(config.coll)

    private suspend fun findUser(authBody: AuthBody): Boolean {
        return collection.find(Filters.eq(AuthBody::email.name, authBody.email)).firstOrNull() != null
    }

    suspend fun insertOne(authBody: AuthBody): Result<Unit> = coroutineScope {
        return@coroutineScope try {
            if (!findUser(authBody)) {
                collection.insertOne(authBody)
                Result.success(Unit)
            } else {
                Result.failure(Throwable("existing user".toUpperFirst()))
            }
        } catch (e: Exception) {
            Result.failure(Throwable(e.message))
        }
    }

    suspend fun updateRt(authBody: AuthBody, rt: String): Result<Unit> {
        val res = collection.findOneAndUpdate(
            filter = Filters.eq(AuthBody::email.name, authBody.email),
            update = Updates.set(AuthBody::rt.name, rt)
        )
        return if (res != null) Result.success(Unit) else Result.failure(Throwable("not updated refresh token".toUpperFirst()))
    }

    suspend fun removeRt(email: String): Result<Unit> {
        val res = collection.findOneAndUpdate(
            filter = Filters.eq(AuthBody::email.name, email),
            update = Updates.set(AuthBody::rt.name, null)
        )
        return if (res != null) Result.success(Unit) else Result.failure(Throwable("not updated refresh token".toUpperFirst()))
    }

    fun close() {
        mongoClient.close()
    }
}