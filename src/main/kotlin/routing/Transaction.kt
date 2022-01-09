package app.persofin.backend.routing

import app.persofin.backend.model.Transaction
import app.persofin.backend.persistence.SimpleInMemoryStore
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.TransactionRoute() {
    route("transaction") {
        get {
            call.respond(SimpleInMemoryStore.transactions)
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, Unit)
            call.respond(
                SimpleInMemoryStore.transactions.find { it.id == id.toIntOrNull() } ?: return@get call.respond(
                    HttpStatusCode.NotFound, Unit
                )
            )
        }
        put {
            SimpleInMemoryStore.transactions.run {
                add(call.receive<Transaction>().apply { this.id = this@run.size+1 })
            }
            call.respond(HttpStatusCode.OK, Unit)
        }
        patch {
            val transaction = call.receive<Transaction>()
            SimpleInMemoryStore.transactions.run {
                this[indexOfFirst { it.id == transaction.id }] = transaction
            }
            call.respond(HttpStatusCode.OK, Unit)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, Unit)
            SimpleInMemoryStore.transactions.run {
                removeAt(indexOfFirst { it.id == id.toIntOrNull() })
            }
            call.respond(HttpStatusCode.OK, Unit)
        }
    }
}