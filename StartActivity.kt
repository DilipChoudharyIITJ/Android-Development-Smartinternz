package com.tenalis.mart

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class StartActivity : AppCompatActivity() {

    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var phone: TextInputLayout
    private lateinit var progress: ProgressBar
    private lateinit var phoneNumber: String
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val sendOTP = findViewById<Button>(R.id.signUpBtn)
        val signUp = findViewById<Button>(R.id.verifyBtn)
        val resendOTP = findViewById<TextView>(R.id.resendOTP)
        phone = findViewById(R.id.phone)
        progress = findViewById(R.id.progress)

        sendOTP.setOnClickListener {
            sendVerificationCode()
            Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show()
            sendOTP.visibility = View.GONE
            signUp.visibility = View.VISIBLE
        }

        signUp.setOnClickListener {
            verifyOTP()
            progress.visibility = View.VISIBLE
        }

        resendOTP.setOnClickListener {
            if (resendToken.toString() != "null" && phoneNumber.isNotEmpty()) {
                Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show()
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber("+91$phoneNumber")
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(callbacks)
                    .setForceResendingToken(resendToken)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
    }

    private fun sendVerificationCode() {

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
                Toast.makeText(this@StartActivity, "Success", Toast.LENGTH_SHORT).show()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(
                    this@StartActivity,
                    "Failed" + e.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("error", e.message.toString())
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        phoneNumber = phone.editText?.text.toString()

        if (phoneNumber.length != 10) {
            phone.error = "Enter a Valid Phone Number"
            return
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val auth = Firebase.auth
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun verifyOTP() {
        val otpView = findViewById<TextInputLayout>(R.id.otp)
        val otp = otpView.editText?.text.toString()
        if (otp.isNotEmpty()) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
            signInWithPhoneAuthCredential(credential)
        } else {
            otpView.error = "Enter a Valid OTP"
        }
    }

    override fun onStart() {
        super.onStart()
        val auth = Firebase.auth
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Disclaimer")
                .setMessage("This app is for people in Tenali and Surrounding areas who wants to order from Tenali's Mart")
                .setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }.setNegativeButton("Not Ok") { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    finish()
                }.show()
        }
    }
}
