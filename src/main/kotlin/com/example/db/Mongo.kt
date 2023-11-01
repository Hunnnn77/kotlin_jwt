package com.example.db

import com.example.model.Registration
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
    private val collection = database.getCollection<Registration>(config.coll)

    suspend fun findUser(email: String): Registration? {
        return collection.find(Filters.eq(Registration::email.name, email)).firstOrNull()
    }

    suspend fun insertOne(registration: Registration): Result<Unit> {
        return try {
            if (findUser(registration.email) != null) {
                Result.failure(MongoImplException(message = "existing user".toUpperFirst()))
            } else {
                collection.insertOne(registration)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(MongoImplException(message = e.message))
        }
    }

    suspend fun updateRt(email: String, rt: String): Result<Unit> {
        val res = collection.findOneAndUpdate(
            filter = Filters.eq(Registration::email.name, email),
            update = Updates.set(Registration::rt.name, rt)
        )
        return if (res != null) Result.success(Unit) else Result.failure(MongoImplException(message = "not updated refresh token".toUpperFirst()))
    }

    suspend fun removeRt(email: String): Result<Unit> {
        val res = collection.findOneAndUpdate(
            filter = Filters.eq(Registration::email.name, email),
            update = Updates.set(Registration::rt.name, null)
        )
        return if (res != null) Result.success(Unit) else Result.failure(MongoImplException(message = "not updated refresh token".toUpperFirst()))
    }

    suspend fun fetchRt(email: String): Result<Registration> {
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