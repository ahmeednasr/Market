package com.example.market.data.repo

import com.example.market.MainCoroutineRule
import com.example.market.data.pojo.*
import com.example.market.data.remote.ApiService
import com.example.market.data.remote.CurrencyApi
import com.example.market.data.remote.GovernmentAPI
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyLong
import org.mockito.MockitoAnnotations
import retrofit2.Response
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryImplTest {

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var currencyApi: CurrencyApi

    @Mock
    private lateinit var governmentAPI: GovernmentAPI

    private lateinit var repoImpl: Repository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        repoImpl = RepositoryImpl(apiService, currencyApi, governmentAPI)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `getBrands should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val brand = BrandResponse(listOf(SmartCollection(id = 1, title = "Adidas")))

            `when`(apiService.getBrands()).thenReturn(Response.success(brand))

            // Invoke the function under test
            val result = repoImpl.getBrands()

            assertEquals(result.body(), brand)
            assertEquals(
                result.body()?.smart_collections?.size,
                brand.smart_collections?.size
            )
            assertEquals(
                result.body()?.smart_collections?.get(0)?.id,
                brand.smart_collections?.get(0)?.id
            )
            assertEquals(
                result.body()?.smart_collections?.get(0)?.title,
                brand.smart_collections?.get(0)?.title
            )
        }

    @Test
    fun `getProducts should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val product = ProductResponse(listOf(Product(id = 1, title = "Adidas")))

            `when`(apiService.getProducts()).thenReturn(Response.success(product))

            // Invoke the function under test
            val result = repoImpl.getProducts()

            assertEquals(result.body(), product)
            assertEquals(
                result.body()?.products?.size,
                product.products?.size
            )
            assertEquals(
                result.body()?.products?.get(0)?.id,
                product.products?.get(0)?.id
            )
            assertEquals(
                result.body()?.products?.get(0)?.title,
                product.products?.get(0)?.title
            )
        }

    @Test
    fun `getSingleProduct should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val product = ProductResponse(product = Product(id = 1, title = "Adidas"))

            `when`(apiService.getSingleProduct(0L)).thenReturn(Response.success(product))

            // Invoke the function under test
            val result = repoImpl.getSingleProduct(0L)

            assertEquals(result.body(), product)
            assertEquals(
                result.body()?.product?.id,
                product.product?.id
            )
            assertEquals(
                result.body()?.product?.title,
                product.product?.title
            )
        }

    @Test
    fun `getBrandProducts should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val product = ProductResponse(product = Product(id = 1, title = "Adidas"))

            `when`(apiService.getBrandProducts("Adidas")).thenReturn(Response.success(product))

            // Invoke the function under test
            val result = repoImpl.getBrandProducts("Adidas")

            assertEquals(result.body(), product)
            assertEquals(
                result.body()?.product?.id,
                product.product?.id
            )
            assertEquals(
                result.body()?.product?.title,
                product.product?.title
            )
        }

    @Test
    fun `getCurrencies should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val currency = Currencies(mutableListOf(Currency("EGP", true, "25/7/2023")))

            `when`(apiService.getCurrencies()).thenReturn(Response.success(currency))

            // Invoke the function under test
            val result = repoImpl.getCurrencies()

            assertEquals(result.body(), currency)
            assertEquals(
                result.body()?.currencies?.get(0)?.currency,
                currency.currencies[0].currency
            )
        }
}