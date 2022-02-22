package app.persofin.backend

import app.persofin.backend.model.Transactions
import app.persofin.backend.persistence.db.DB
import app.persofin.backend.persistence.db.DB.migrate
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    DB.connect()
    transaction {
        migrate(
            Transactions
        )
    }
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }

        routing {
           RootRoute()
        }
    }.start(wait = true)
}
