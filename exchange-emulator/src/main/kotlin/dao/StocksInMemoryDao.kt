package dao

import domain.Company

class StocksInMemoryDao : StocksDao {
    private val data: MutableMap<String, Company> = mutableMapOf()

    override fun addCompany(company: Company) {
        data[company.name] = company
    }

    override fun getCompanyByName(name: String): Company? = data[name]

    override fun getAllCompanies(): List<Company> = data.values.toList()

    override fun updateCompany(company: Company) {
        data[company.name] = company
    }

    override fun clear() = data.clear()
}