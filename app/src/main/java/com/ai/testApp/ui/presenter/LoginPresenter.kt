package com.ai.testApp.ui.presenter

import com.ai.testApp.interactor.ILoginInteractor
import com.ai.testApp.ui.model.LoginData
import com.ai.testApp.ui.view.ILoginView

interface ILoginPresenter {
    fun onDestroy()
    fun onSaveButtClicked(loginData: LoginData)
    fun onCheckButtClicked(comparedLoginData: LoginData)
    fun onTouchIdButtClicked()
}

class LoginPresenter(var loginView: ILoginView?, var interactor: ILoginInteractor?) :
    ILoginPresenter {

    override fun onDestroy() {
        loginView = null
        interactor = null
    }

    override fun onSaveButtClicked(loginData: LoginData) {
        interactor?.saveLoginData(loginData)
    }

    override fun onCheckButtClicked(comparedLoginData: LoginData) {
        if (interactor?.isLoginDataValid(comparedLoginData)!!) {
            loginView?.showLoginSuccessfulNotification()
            loginView?.showLoginSuccessfulToast()
        } else {
            loginView?.showLoginFailureNotification()
            loginView?.showLoginFailureToast()
        }
    }

    override fun onTouchIdButtClicked() {
        loginView?.checkTouchIdApi()
    }
}