package com.ai.testApp.ui.view

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ai.testApp.ui.presenter.ILoginPresenter
import com.ai.testApp.interactor.LoginInteractor
import com.ai.testApp.ui.presenter.LoginPresenter
import com.ai.testApp.R
import com.ai.testApp.ui.model.LoginData
import java.util.concurrent.Executor

interface ILoginView {
    fun showLoginSuccessfulNotification()
    fun showLoginFailureNotification()
    fun showLoginSuccessfulToast()
    fun showLoginFailureToast()
    fun checkTouchIdApi()
}

class LoginAct : AppCompatActivity(), ILoginView {
    private lateinit var activityContext: Context

    private lateinit var executor: Executor
    private lateinit var biometricManager: BiometricManager

    private lateinit var loginEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var saveButt: Button
    private lateinit var checkButt: Button
    private lateinit var touchIdButt: Button

    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationManager: NotificationManager
    lateinit var builder: Notification.Builder
    private val channelId = "1"
    private val notifyId = 1
    private val description = "TestApp notification"
    private val notificationTitle = "TestApp"
    private var presenter: ILoginPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        activityContext = this

        loginEt = findViewById(R.id.act_main__login_et)
        passwordEt = findViewById(R.id.act_main__password_et)
        saveButt = findViewById(R.id.act_main__save_butt)
        saveButt.setOnClickListener {
            presenter?.onSaveButtClicked(
                LoginData(
                    loginEt.text.toString(),
                    passwordEt.text.toString()
                )
            )
        }
        checkButt = findViewById(R.id.act_main__check_butt)
        checkButt.setOnClickListener {
            presenter?.onCheckButtClicked(
                LoginData(
                    loginEt.text.toString(),
                    passwordEt.text.toString()
                )
            )
        }
        touchIdButt = findViewById(R.id.act_main__touch_id_butt)
        touchIdButt.setOnClickListener { presenter?.onTouchIdButtClicked() }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        executor = ContextCompat.getMainExecutor(activityContext)
        biometricManager = BiometricManager.from(activityContext)

        presenter = LoginPresenter(
            this,
            LoginInteractor()
        )
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        presenter = null
        super.onDestroy()
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(activityContext, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    override fun showLoginSuccessfulNotification() {
        showNotification(resources.getString(R.string.msg_login_successful))
    }

    override fun showLoginFailureNotification() {
        showNotification(resources.getString(R.string.error_msg_login_failure))
    }

    private fun showNotification(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNewestNotify(message)
        } else {
            sendOlderNotify(message)
        }
    }

    @SuppressLint("NewApi")
    private fun sendNewestNotify(message: String) {
        val intent = Intent(activityContext, LoginAct()::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                activityContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        notificationChannel =
            NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableVibration(true)
        notificationManager.createNotificationChannel(notificationChannel)
        builder = Notification.Builder(activityContext, channelId)
            .setContentTitle(notificationTitle)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_small_notif_24_fff)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    activityContext.resources,
                    R.drawable.ic_launcher_background
                )
            )
            .setContentIntent(pendingIntent)
        notificationManager.notify(notifyId, builder.build())
    }

    private fun sendOlderNotify(message: String) {
        val builder = NotificationCompat.Builder(activityContext)
            .setSmallIcon(R.drawable.ic_small_notif_24_fff)
            .setContentTitle(notificationTitle)
            .setContentText(message)
            .setAutoCancel(true)
        val intent = Intent(activityContext, LoginAct::class.java)
        val pendingIntent = PendingIntent.getActivity(
            activityContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)
        notificationManager.notify(notifyId, builder.build())
    }

    override fun showLoginSuccessfulToast() {
        showToast(resources.getString(R.string.msg_login_successful))
    }

    override fun showLoginFailureToast() {
        showToast(resources.getString(R.string.error_msg_login_failure))
    }

    override fun checkTouchIdApi() {
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> loginWithTouchIdApi()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> showToast(
                resources.getString(
                    R.string.error_msg_no_biometric_hardware
                )
            )
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> showToast(
                resources.getString(
                    R.string.error_msg_biometric_hw_unavailable
                )
            )
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> showToast(
                resources.getString(
                    R.string.error_msg_no_biometric_record
                )
            )
        }
    }

    private fun loginWithTouchIdApi() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.fingerprint_auth_title))
            .setSubtitle(getString(R.string.fingerprint_auth_subtitle))
            .setDescription(getString(R.string.fingerprint_auth_description))
            .setDeviceCredentialAllowed(true)
            .setConfirmationRequired(false)
            .build()
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showLoginSuccessfulToast()
                    showLoginSuccessfulNotification()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showToast(resources.getString(R.string.error_msg_login_error))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast(resources.getString(R.string.error_msg_login_failure))
                }
            })
        biometricPrompt.authenticate(promptInfo)
    }
}
