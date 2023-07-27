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

    @Test
    fun `createUser should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val user = NewUser(User(email = "mf502308@gmail.com"))
            val userRes = CustomerResponse(Customer(email = "mf502308@gmail.com"))

            `when`(apiService.postCustomer(customer = user)).thenReturn(Response.success(userRes))

            // Invoke the function under test
            val result = repoImpl.createUser(user)

            assertEquals(result.body()?.customer, userRes.customer)
            assertEquals(
                result.body()?.customer?.email,
                user.customer.email
            )
        }

    @Test
    fun `getAllCustomers should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val customer = CustomersResponse(listOf(Customer(id = 1, email = "mf502308@gmail.com")))

            `when`(apiService.getAllCustomers()).thenReturn(Response.success(customer))

            // Invoke the function under test
            val result = repoImpl.getAllCustomers()

            assertEquals(result.body(), customer)
            assertEquals(
                result.body()?.customers?.get(0)?.id,
                customer.customers[0].id
            )
            assertEquals(
                result.body()?.customers?.get(0)?.email,
                customer.customers[0].email
            )
        }

    @Test
    fun `getCustomer should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val customer = CustomerResponse(Customer(id = 1, email = "mf502308@gmail.com"))

            `when`(apiService.getSingleCustomer(1)).thenReturn(Response.success(customer))

            // Invoke the function under test
            val result = repoImpl.getCustomer(1)

            assertEquals(result.body(), customer)
            assertEquals(
                result.body()?.customer?.id,
                customer.customer.id
            )
            assertEquals(
                result.body()?.customer?.email,
                customer.customer.email
            )
        }

    @Test
    fun `getFavourites should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val favorites = DraftOrderResponse(
                DraftOrder(
                    lineItems = listOf(
                        LineItemsItem(
                            title = "shoes",
                            quantity = 1,
                            price = "100.00"
                        )
                    )
                )
            )

            `when`(apiService.getFavourites(1)).thenReturn(Response.success(favorites))

            // Invoke the function under test
            val result = repoImpl.getFavourites(1)

            assertEquals(result.body(), favorites)
            assertEquals(
                result.body()?.draftOrder?.lineItems,
                favorites.draftOrder?.lineItems
            )
            assertEquals(
                result.body()?.draftOrder?.lineItems?.get(0)?.title,
                favorites.draftOrder?.lineItems?.get(0)?.title
            )
        }

    @Test
    fun `getGovernment should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val government = GovernmentPojo(
                data = Data(
                    states = listOf(
                        StatesItem(
                            name = "Cairo"
                        )
                    )
                )
            )

            `when`(governmentAPI.getGovernment(Country("Cairo")))
                .thenReturn(Response.success(government))

            // Invoke the function under test
            val result = repoImpl.getGovernment("Cairo")

            assertEquals(result.body(), government)
            assertEquals(
                result.body()?.data?.states?.get(0)?.name,
                government.data?.states?.get(0)?.name
            )
        }

    @Test
    fun `getCities should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val city = CitiesPojo(
                data = listOf(
                    "Cairo"
                )
            )

            `when`(governmentAPI.getCities(requestBody = CitiesRequest("Cairo", "")))
                .thenReturn(Response.success(city))

            // Invoke the function under test
            val result = repoImpl.getCities("Cairo", "")

            assertEquals(result.body(), city)
            assertEquals(
                result.body()?.data?.get(0),
                city.data?.get(0)
            )
        }

    @Test
    fun `getDiscountCodes should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val discountRes = DiscountResponse(
                price_rules = listOf(
                    PriceRule(id = 1)
                )
            )

            `when`(apiService.getDiscountCodes()).thenReturn(Response.success(discountRes))

            // Invoke the function under test
            val result = repoImpl.getDiscountCodes()

            assertEquals(result.body(), discountRes)
            assertEquals(
                result.body()?.price_rules?.get(0)?.id,
                discountRes.price_rules?.get(0)?.id
            )
        }

    @Test
    fun `getCustomerOrders should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val orderRes = OrderResponse(
                orders = listOf(
                    Order(
                        id = 1
                    )
                )
            )

            `when`(apiService.getCustomerOrders(1)).thenReturn(Response.success(orderRes))

            // Invoke the function under test
            val result = repoImpl.getCustomerOrders(1)

            assertEquals(result.body(), orderRes)
            assertEquals(
                result.body()?.orders?.get(0)?.id,
                orderRes.orders?.get(0)?.id
            )
        }

    @Test
    fun `getSingleOrder should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val orderRes = OrderResponse(
                order = Order(
                    id = 1
                )
            )

            `when`(apiService.getSingleOrder(1)).thenReturn(Response.success(orderRes))

            // Invoke the function under test
            val result = repoImpl.getSingleOrder(1)

            assertEquals(result.body(), orderRes)
            assertEquals(
                result.body()?.order?.id,
                orderRes.order?.id
            )
        }

    @Test
    fun `getCart should return success state when API response is successful`() =
        runBlocking {
            // Mock the API response
            val cart = DraftOrderResponse(
                DraftOrder(
                    lineItems = listOf(
                        LineItemsItem(
                            title = "shoes",
                            quantity = 1,
                            price = "100.00"
                        )
                    )
                )
            )

            `when`(apiService.getCart(1)).thenReturn(Response.success(cart))

            // Invoke the function under test
            val result = repoImpl.getCart(1)

            assertEquals(result.body(), cart)
            assertEquals(
                result.body()?.draftOrder?.lineItems,
                cart.draftOrder?.lineItems
            )
            assertEquals(
                result.body()?.draftOrder?.lineItems?.get(0)?.title,
                cart.draftOrder?.lineItems?.get(0)?.title
            )
        }
}