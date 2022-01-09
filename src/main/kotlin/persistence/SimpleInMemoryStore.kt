package app.persofin.backend.persistence

import app.persofin.backend.model.Transaction

object SimpleInMemoryStore {
    val transactions = mutableListOf<Transaction>()
}