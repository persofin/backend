package app.persofin.backend.model

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import kotlinx.serialization.Serializable

object Transactions : LongIdTable() {
    val amount = long("amount")
    val type = short("transaction_type")
}

class Transaction(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Transaction>(Transactions)

    var amount by Transactions.amount
    var type by Transactions.type

    fun toData() = TransactionData(id.value, amount, type)
}

@Serializable
data class TransactionData(
    var id: Long = 0,
    var amount: Long,
    var type: Short,
) {
    fun create() {
        Transaction.new {
            amount = this@TransactionData.amount
            type = this@TransactionData.type
        }
    }

    fun applyUpdate() {
        Transaction[this@TransactionData.id].apply {
            amount = this@TransactionData.amount
            type = this@TransactionData.type
        }
    }
}
