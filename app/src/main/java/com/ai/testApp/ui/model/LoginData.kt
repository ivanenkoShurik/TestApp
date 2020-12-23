package com.ai.testApp.ui.model

data class LoginData(val login: String, val password: String) {
    override fun toString(): String {
        return login + password
    }
}
