# mongo
```
implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.11.0")
implementation("org.mongodb:bson-kotlinx:4.11.0")

@Serializable
data class Body(
    val title: String,
    val content: String,

    @SerialName("content_value")
    val contentVal: String,
)
```

# custom err
```kotlin
data class AuthorizationException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Throwable(message, cause)

fun Application.statusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is AuthorizationException -> call.respond(
                    HttpStatusCode.Unauthorized, ExceptionResponse(
                        AuthorizationException(
                            message = ""
                        )
                    )
                )
            }
        }
    }
}
```