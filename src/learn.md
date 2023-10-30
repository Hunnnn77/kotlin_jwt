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