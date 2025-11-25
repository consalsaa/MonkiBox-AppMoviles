package com.example.monkibox

import android.app.Application
import com.example.monkibox.dataclass.User
import com.example.monkibox.network.RetrofitClient
import com.example.monkibox.network.UserApiService
import com.example.monkibox.viewmodels.UserViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private lateinit var viewModel: UserViewModel
    private val mockApi = mockk<UserApiService>()

    @Before
    fun setup() {
        mockkObject(RetrofitClient)
        every { RetrofitClient.userApi } returns mockApi
        viewModel = UserViewModel(application)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `login exitoso actualiza authStatus a Success`() = runTest {
        // GIVEN
        val email = "test@monkibox.com"
        val password = "123"
        val mockUser = User(id = 1, email = email, password = password, role = "USER")
        coEvery { mockApi.login(any()) } returns Response.success(mockUser)

        // WHEN
        viewModel.login(email, password)

        // THEN (Esperamos el primer valor que NO sea null)
        val status = viewModel.authStatus.filterNotNull().first()

        assertTrue("El estado debería ser Success", status is UserViewModel.AuthResult.Success)
    }

    @Test
    fun `login fallido actualiza authStatus a Error`() = runTest {
        // GIVEN
        coEvery { mockApi.login(any()) } returns Response.error(401, okhttp3.ResponseBody.create(null, ""))

        // WHEN
        viewModel.login("error@mail.com", "malapass")

        // THEN (Esperamos el primer valor que NO sea null)
        val status = viewModel.authStatus.filterNotNull().first()

        assertTrue("El estado debería ser Error", status is UserViewModel.AuthResult.Error)
    }
}