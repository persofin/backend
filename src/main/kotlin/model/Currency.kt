package app.persofin.backend.model

import app.persofin.backend.persistence.db.InitializableTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

enum class SymbolPosition {
    START, END, DECIMAL_SEPARATOR,
    START_WITH_SPACE, END_WITH_SPACE,
}

object Currencies : LongIdTable(), InitializableTable {
    val code = varchar("code", 5).uniqueIndex()
    val decimalPlaces = byte("decimal_places")
    val symbol = varchar("symbol", 4).uniqueIndex()
    val symbolPosition = enumeration("symbol_pos", SymbolPosition::class)

    private val includedCurrencies = listOf(
        // Fiat
        CurrencyData(code = "EUR", decimalPlaces = 2, symbol = "€", symbolPosition = SymbolPosition.END_WITH_SPACE),
        CurrencyData(code = "USD", decimalPlaces = 2, symbol = "$", symbolPosition = SymbolPosition.START),
        CurrencyData(code = "JPY", decimalPlaces = 0, symbol = "¥", symbolPosition = SymbolPosition.START),
        CurrencyData(code = "GBP", decimalPlaces = 2, symbol = "£", symbolPosition = SymbolPosition.START),

        // Crypto
        CurrencyData(code = "BTC", decimalPlaces = 8, symbol = "₿", symbolPosition = SymbolPosition.START),
        CurrencyData(code = "LTC", decimalPlaces = 8, symbol = "LTC", symbolPosition = SymbolPosition.END_WITH_SPACE),
        CurrencyData(code = "ETH", decimalPlaces = 18, symbol = "ETH", symbolPosition = SymbolPosition.END_WITH_SPACE),
        CurrencyData(code = "USDC", decimalPlaces = 6, symbol = "USDC", symbolPosition = SymbolPosition.END_WITH_SPACE),
    )

    override fun initialize() {
        includedCurrencies.forEach { includedCurrency ->
            val finding = Currency.find { code eq includedCurrency.code }.limit(1)
            if (finding.empty()) {
                includedCurrency.create()
            } else {
                includedCurrency.apply { id = finding.first().id.value }.applyUpdate()
            }
        }
    }
}

class Currency(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Currency>(Currencies)

    var code by Currencies.code
    var decimalPlaces by Currencies.decimalPlaces
    var symbol by Currencies.symbol
    var symbolPosition by Currencies.symbolPosition
    val transactions by Transaction referrersOn Transactions.currency

    fun toData() = CurrencyData(id.value, code, decimalPlaces, symbol, symbolPosition)
}

@Serializable
data class CurrencyData(
    var id: Long = 0,
    var code: String,
    var decimalPlaces: Byte,
    var symbol: String,
    var symbolPosition: SymbolPosition,
) {
    private fun Currency.fill() {
        code = this@CurrencyData.code
        decimalPlaces = this@CurrencyData.decimalPlaces
        symbol = this@CurrencyData.symbol
        symbolPosition = this@CurrencyData.symbolPosition
    }

    fun create() {
        Currency.new { fill() }
    }

    fun applyUpdate() {
        Currency[this@CurrencyData.id].apply { fill() }
    }
}
