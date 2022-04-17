package service

import dao.StocksDao
import domain.Company
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*

class StockExchangeService(
    private val stocksDao: StocksDao,
    private val priceGeneratorService: PriceGeneratorService
) {
    fun start(port: Int) {
        embeddedServer(Netty, port) {
            routing {
                get("/company/add") {
                    val name = getParam("company")!!
                    val price = getParam("price")!!.toDouble()
                    val mbStocksCount = getParam("count")?.toInt()

                    if (stocksDao.getCompanyByName(name) != null) {
                        badRequest("Company $name already exists")
                    } else {
                        val stocksCount = mbStocksCount ?: 0
                        stocksDao.addCompany(Company(name, price, stocksCount))
                        call.response.status(HttpStatusCode.Created)
                    }
                }

                get("/stock/add") {
                    val name = getParam("company")!!
                    val count = getParam("count")!!.toInt()

                    val company = stocksDao.getCompanyByName(name)
                    if (company == null) {
                        badRequest("Company $name does not exist")
                    } else {
                        stocksDao.updateCompany(
                            company.copy(
                                stockCount = company.stockCount + count
                            )
                        )
                        call.response.status(HttpStatusCode.OK)
                    }
                }

                get("/stock/price") {
                    val name = getParam("company")!!
                    val company = stocksDao.getCompanyByName(name)
                    if (company == null) {
                        badRequest("Company $name does not exist")
                    } else {
                        call.respondText { company.stockPrice.toString() }
                    }
                }

                get("/stock/count") {
                    val name = getParam("company")!!
                    val company = stocksDao.getCompanyByName(name)
                    if (company == null) {
                        badRequest("Company $name does not exist")
                    } else {
                        call.respondText { company.stockCount.toString() }
                    }
                }

                get("/stock/buy") {
                    val name = getParam("company")!!
                    val count = getParam("count")!!.toInt()

                    val company = stocksDao.getCompanyByName(name)
                    if (company == null) {
                        badRequest("Company $name does not exist")
                    } else if (company.stockCount < count) {
                        badRequest("Company $name does not have enough stocks")
                    } else {
                        stocksDao.updateCompany(
                            company.copy(stockCount = company.stockCount - count)
                        )
                        call.response.status(HttpStatusCode.OK)
                        priceGeneratorService.generateNewPrice(company)
                        call.respondText("OK")
                    }
                }

                get("/stock/sell") {
                    val name = getParam("company")!!
                    val count = getParam("count")!!.toInt()

                    val company = stocksDao.getCompanyByName(name)
                    if (company == null) {
                        badRequest("Company $name does not exist")
                    } else {
                        stocksDao.updateCompany(
                            company.copy(stockCount = company.stockCount + count)
                        )
                        call.response.status(HttpStatusCode.OK)
                        priceGeneratorService.generateNewPrice(company)
                        call.respondText("OK")
                    }
                }

                // Needed only for test purposes
                get("/stock/clear") {
                    stocksDao.clear()
                }
            }
        }.start(wait = true)
    }

    private fun PipelineContext<*, ApplicationCall>.getParam(key: String) = context.request.queryParameters[key]

    private suspend fun PipelineContext<*, ApplicationCall>.badRequest(msg: String) {
        call.response.status(HttpStatusCode.BadRequest)
        call.respondText { msg }
    }
}