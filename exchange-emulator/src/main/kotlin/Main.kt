import dao.StocksInMemoryDao
import domain.Company
import service.PriceGeneratorService
import service.StockExchangeService

fun main(args: Array<String>) {
    val stocksDao = StocksInMemoryDao()
    StockExchangeService(stocksDao, PriceGeneratorService(stocksDao))
        .start(args[0].toInt())
}