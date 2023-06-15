package com.example.nq.authFirebase

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    // DESPUÉS de intentar Iniciar Sesión, modificamos el estado del usuario
    fun onSignInResult(result: SignInResult) {

        _state.update { it.copy(
            isSignInSuccessful = result.data !=  null,
            signInError = result.errorMessage
        ) }

    }

    fun resetState() {
        _state.update { SignInState() }
    }
}