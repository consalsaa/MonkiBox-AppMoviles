package com.example.monkibox

import com.example.monkibox.dataclass.RandomUserResponse
import com.example.monkibox.dataclass.UserLocation
import com.example.monkibox.dataclass.UserName
import com.example.monkibox.dataclass.UserPicture
import com.example.monkibox.dataclass.UserResult
import com.example.monkibox.network.RandomUserApiService
import com.example.monkibox.network.RetrofitClient
import com.example.monkibox.viewmodels.AboutViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AboutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AboutViewModel
    private val mockApi = mockk<RandomUserApiService>()

    @Before
    fun setup() {
        mockkObject(RetrofitClient)
        every { RetrofitClient.randomUserApi } returns mockApi
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `loadReviews transforma datos de la API correctamente`() = runTest {
        // GIVEN (Simulamos la respuesta compleja de RandomUser API)
        val fakeUser = UserResult(
            name = UserName("Juan", "Perez"),
            picture = UserPicture("http://foto.jpg"),
            location = UserLocation("Chile")
        )
        val fakeResponse = RandomUserResponse(results = listOf(fakeUser))

        // Entrenamos al mock para devolver 1 usuario
        coEvery { mockApi.getRandomUsers(any()) } returns fakeResponse

        // WHEN
        viewModel = AboutViewModel() // Esto dispara loadReviews en el init

        // THEN
        val reviews = viewModel.reviews.value

        // Verificamos que se creó 1 reseña
        assertEquals(1, reviews.size)

        // Verificamos que transformó bien el nombre (First + Last)
        assertEquals("Juan Perez", reviews[0].name)

        // Verificamos que se inventó un comentario (no debe estar vacío)
        assertTrue(reviews[0].comment.isNotEmpty())
    }
}