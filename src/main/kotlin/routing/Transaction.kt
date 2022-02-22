package app.persofin.backend.routing

import app.persofin.backend.model.Transaction
import app.persofin.backend.model.TransactionData
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.transactionRoute() {
    route("transaction") {
        get {
            call.respond(transaction { Transaction.all().map { it.toData() } })
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, Unit)
            val idNum = id.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, Unit)
            try {
                call.respond(transaction { Transaction[idNum].toData() })
            } catch (_: EntityNotFoundException) {
                call.respond(HttpStatusCode.NotFound, Unit)
            }
        }
        put {
            val transaction = call.receive<TransactionData>()
            transaction { transaction.create() }
            call.respond(HttpStatusCode.OK, Unit)
        }
        patch {
            val transaction = call.receive<TransactionData>()
            try {
                transaction { transaction.applyUpdate() }
            } catch (_: EntityNotFoundException) {
                call.respond(HttpStatusCode.NotFound, Unit)
            }
            call.respond(HttpStatusCode.OK, Unit)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, Unit)
            val idNum = id.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest, Unit)
            try {
                transaction { Transaction[idNum].delete() }
            } catch (_: EntityNotFoundException) {
                return@delete call.respond(HttpStatusCode.NotFound, Unit)
            }
            call.respond(HttpStatusCode.OK, Unit)
        }
    }
}