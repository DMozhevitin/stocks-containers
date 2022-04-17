package client

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ExchangeClient(baseUrl: String): BaseHttpClient(baseUrl) {

    fun addCompany(companyName: String, count: Int, price: Double) {
        req("/company/add", mutableMapOf(
            "company" to companyName,
            "price" to price.toString(),
            "count" to count.toString()
        ))
    }

    fun getPrice(companyName: String): Double {
        return req(
            "/stock/price", mutableMapOf(
                "company" to companyName
            )
        ).toDouble()
    }

    fun buy(companyName: String, count: Int): String {
        return req(
            "/stock/buy", mutableMapOf(
                "company" to companyName,
                "count" to count.toString()
            )
        )
    }

    fun sell(companyName: String, count: Int): String {
        return req(
            "/stock/sell", mutableMapOf(
                "company" to companyName,
                "count" to count.toString()
            )
        )
    }

    fun clear() {
        req("/stock/clear", mutableMapOf())
    }
}