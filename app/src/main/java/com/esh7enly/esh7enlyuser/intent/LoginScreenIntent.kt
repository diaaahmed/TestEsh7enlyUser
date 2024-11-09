package com.esh7enly.esh7enlyuser.intent

sealed class LoginScreenIntent
{
    object LoginClick: LoginScreenIntent()
    object SignupClick: LoginScreenIntent()
    object ForgetPasswordClick: LoginScreenIntent()
}

sealed class LoginScreenState{
    object Idle: LoginScreenState()
    object Loading: LoginScreenState()
    object Success: LoginScreenState()
    object Error: LoginScreenState()
}

sealed class LoginScreenOutput{

    object NavigateToHomeScreen: LoginScreenOutput()
    object NavigateToRegister: LoginScreenOutput()
    object NavigateToForget: LoginScreenOutput()
}