package com.example.monkibox

import android.app.Application
import com.example.monkibox.dataclass.Product
import com.example.monkibox.storage.CartStorage
import com.example.monkibox.viewmodels.CartViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    // Regla para simular el Hilo Principal en los tests
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Creamos una aplicación falsa (Mock) porque el ViewModel la pide
    private val application = mockk<Application>(relaxed = true)

    private lateinit var viewModel: CartViewModel

    @Before
    fun setup() {
        // MOCKK: Simulamos el almacenamiento local
        // Esto evita que el test intente leer la memoria real del teléfono (que no existe aquí)
        mockkObject(CartStorage)

        // Entrenamos al simulacro: "Cuando te pidan items, devuelve lista vacía"
        every { CartStorage.getAllCartItems(any()) } returns emptyList()
        // "Cuando te pidan guardar, no hagas nada"
        every { CartStorage.saveCartItems(any(), any()) } returns Unit

        // Iniciamos el ViewModel con la App falsa
        viewModel = CartViewModel(application)
    }

    @After
    fun tearDown() {
        unmockkAll() // Limpieza al terminar
    }

    @Test
    fun `addItem agrega producto y calcula subtotales correctamente`() = runTest {

        // --- ACTIVAMOS EL STATEFLOW ---
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.cartTotals.collect()
        }

        // 1. GIVEN (Dado un producto de precio 1000)
        // Usamos IDs numéricos tipo Long como definimos en el cambio a Spring Boot
        val product = Product(
            id = 1L,
            name = "Producto Test",
            price = 1000.0,
            stock = 10,
            description = "Desc",
            imageUrl = "url"
        )

        // 2. WHEN (Cuando agrego 2 unidades de ese producto)
        viewModel.addItem(product, 2)

        // 3. THEN (Entonces el total debe ser correcto)
        val cartItems = viewModel.cartItems.value
        val totals = viewModel.cartTotals.value

        // Verificaciones (Asserts)
        assertEquals("Debería haber 1 ítem en la lista", 1, cartItems.size)
        assertEquals("La cantidad debería ser 2", 2, cartItems[0].quantity)

        // Validación matemática:
        // Subtotal: 1000 * 2 = 2000
        assertEquals(2000.0, totals.subtotal, 0.0)

        // Envío: 50.0 (según tu lógica en el ViewModel)
        assertEquals(50.0, totals.shipping, 0.0)

        // Impuestos: 19% de 2000 = 380
        assertEquals(380.0, totals.taxes, 0.0)

        // Total Final: 2000 + 50 + 380 = 2430
        assertEquals(2430.0, totals.total, 0.0)
    }
}