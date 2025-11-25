package com.example.monkibox

import android.app.Application
import com.example.monkibox.dataclass.Purchase
import com.example.monkibox.storage.HistoryStorage
import com.example.monkibox.viewmodels.HistoryViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
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
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setup() {
        mockkObject(HistoryStorage)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `loadPurchaseHistory carga la lista de compras correctamente`() = runTest {
        // 1. GIVEN
        val mockPurchase = Purchase(
            id = "boleta-123",
            items = emptyList(),
            subtotal = 1000.0,
            shipping = 0.0,
            taxes = 190.0,
            total = 1190.0
        )
        val fakeHistory = listOf(mockPurchase)

        every { HistoryStorage.getAllPurchases(any()) } returns fakeHistory

        // 2. WHEN
        viewModel = HistoryViewModel(application)

        // 3. THEN
        val historyList = viewModel.purchaseList.filter { it.isNotEmpty() }.first()

        // Verificaciones
        assertEquals("Debería haber 1 boleta en el historial", 1, historyList.size)
        assertEquals("El ID de la boleta debe coincidir", "boleta-123", historyList[0].id)
        assertEquals("El total debe coincidir", 1190.0, historyList[0].total, 0.0)
    }
}