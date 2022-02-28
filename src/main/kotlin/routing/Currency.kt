package app.persofin.backend.routing

import app.persofin.backend.model.Currency
import app.persofin.backend.model.CurrencyData
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.currencyRoute() {
    route("currency") {
        get {
            call.respond(transaction { Currency.all().map { it.toData() } })
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, Unit)
            val idNum = id.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, Unit)
            try {
                call.respond(transaction { Currency[idNum].toData() })
            } catch (_: EntityNotFoundException) {
                call.respond(HttpStatusCode.NotFound, Unit)
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, Unit)
            val idNum = id.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, Unit)
            try {
                call.respond(transaction { Currency[idNum].transactions })
            } catch (_: EntityNotFoundException) {
                call.respond(HttpStatusCode.NotFound, Unit)
            }
        }
        put {
            val currency = call.receive<CurrencyData>()
            transaction { currency.create() }
            call.respond(HttpStatusCode.OK, Unit)
        }
        patch {
            val currency = call.receive<CurrencyData>()
            try {
                transaction { currency.applyUpdate() }
            } catch (_: EntityNotFoundException) {
                call.respond(HttpStatusCode.NotFound, Unit)
            }
            call.respond(HttpStatusCode.OK, Unit)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, Unit)
            val idNum = id.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest, Unit)
            try {
                transaction { Currency[idNum].delete() }
            } catch (_: EntityNotFoundException) {
                return@delete call.respond(HttpStatusCode.NotFound, Unit)
            }
            call.respond(HttpStatusCode.OK, Unit)
        }
    }
}