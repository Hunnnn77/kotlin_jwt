package com.example.db

import com.example.model.AuthBody
import com.example.config.Config
import com.example.model.MongoImplException
import com.example.util.toUpperFirst
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
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

    suspend fun findUser(email: String): AuthBody? {
        return collection.find(Filters.eq(AuthBody::email.name, email)).firstOrNull()
    }

    suspend fun insertOne(authBody: AuthBody): Result<Unit> {
        return try {
            if (findUser(authBody.email) != null) {
                Result.failure(MongoImplException(message = "existing user".toUpperFirst()))
            } else {
                collection.insertOne(authBody)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(MongoImplException(message = e.message))
        }
    }

    suspend fun updateRt(authBody: AuthBody, rt: String): Result<Unit> {
        val res = collection.findOneAndUpdate(
            filter = Filters.eq(AuthBody::email.name, authBody.email),
            update = Updates.set(AuthBody::rt.name, rt)
        )
        return if (res != null) Result.success(Unit) else Result.failure(MongoImplException(message = "not updated refresh token".toUpperFirst()))
    }

    suspend fun removeRt(email: String): Result<Unit> {
        val res = collection.findOneAndUpdate(
            filter = Filters.eq(AuthBody::email.name, email),
            update = Updates.set(AuthBody::rt.name, null)
        )
        return if (res != null) Result.success(Unit) else Result.failure(MongoImplException(message = "not updated refresh token".toUpperFirst()))
    }

    suspend fun fetchRt(email: String): Result<AuthBody> {
        return try {
            findUser(email).let {
                if (it == null) {
                    Result.failure(MongoImplException(message = "no user"))
                } else {
                    Result.success(it)
                }
            }
        } catch (e: Exception) {
            Result.failure(MongoImplException(message = "failed to fetch rt"))
        }
    }

    fun close() {
        mongoClient.close()
    }
}