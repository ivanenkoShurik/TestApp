package com.ai.testApp.interactor

import com.ai.testApp.TestApp
import com.ai.testApp.ui.model.LoginData

interface ILoginInteractor {
    fun saveLoginData(loginData: LoginData)
    fun isLoginDataValid(loginData: LoginData): Boolean
}

class LoginInteractor : ILoginInteractor {
    val LOGIN_DATA_KEY = "loginDataKey"

    override fun saveLoginData(loginData: LoginData) {
        TestApp.putStringToSharedPref(
            LOGIN_DATA_KEY,
            loginData.toString()
        )
    }

    override fun isLoginDataValid(comparisonLoginData: LoginData): Boolean {
        val savedLoginData =
            TestApp.getStringFromSharedPref(
                LOGIN_DATA_KEY
            )
        return comparisonLoginData.toString() == savedLoginData
    }
}