package app.persofin.backend.persistence.db

import app.persofin.backend.model.Transactions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction

object DB {

    fun connect() {
        Database.connect(
            "jdbc:${System.getenv("DATABASE_URL") ?: "postgresql://localhost/db"}",
            "org.postgresql.Driver",
        )
    }

    fun <T : Table> Transaction.migrate(vararg tables: T) {
        SchemaUtils.create(*tables)
        execInBatch(SchemaUtils.addMissingColumnsStatements(*tables))
    }

}