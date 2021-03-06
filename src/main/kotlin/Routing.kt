package app.persofin.backend

import app.persofin.backend.routing.transactionRoute
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.RootRoute() {
    route("/") {
        get {
            call.respondText("I'm an API")
        }

        transactionRoute()
    }
}
