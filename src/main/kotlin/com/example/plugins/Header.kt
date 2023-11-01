import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.configureHeader() {
    install(DefaultHeaders) {
    }
}