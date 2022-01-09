package app.persofin.backend.model

import app.persofin.backend.types.CurrencyID
import app.persofin.backend.types.ID
import app.persofin.backend.types.MonetaryAmount
import app.persofin.backend.types.TransactionType
import kotlinx.serialization.Required
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    var id: ID = 0,
    val amount: MonetaryAmount,
    val currency: CurrencyID,
    val type: TransactionType,
)
