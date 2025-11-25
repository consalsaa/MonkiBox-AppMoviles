package com.example.monkibox

import android.app.Application
import com.example.monkibox.dataclass.Product
import com.example.monkibox.network.ProductApiService
import com.example.monkibox.network.RetrofitClient
import com.example.monkibox.viewmodels.ProductViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private lateinit var viewModel: ProductViewModel
    private val mockApi = mockk<ProductApiService>()

    @Before
    fun setup() {
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0

        mockkObject(RetrofitClient)
        every { RetrofitClient.productApi } returns mockApi
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `loadProducts carga lista desde la API correctamente`() = runTest {
        // GIVEN
        val fakeList = listOf(
            Product(id = 1L, name = "P1", price = 100.0, stock = 5),
            Product(id = 2L, name = "P2", price = 200.0, stock = 10)
        )
        coEvery { mockApi.getAllProducts() } returns fakeList

        // WHEN
        viewModel = ProductViewModel(application)

        // THEN
        // Esperamos a que la lista tenga elementos (no esté vacía)
        val products = viewModel.productList.filter { it.isNotEmpty() }.first()

        assertEquals(2, products.size)
        assertEquals("P1", products[0].name)
    }
}