package com.example.litechat.presenter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.litechat.contracts.LoginContact
import com.example.litechat.listeners.OnAccountSearchListenerObject
import com.example.litechat.listeners.OnLoginListenerObject
import com.example.litechat.model.UserDataModel
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit


class LoginActivityPresenter : LoginContact.Presenter
{
    var onLoginListenerObject : OnLoginListenerObject? = null
    override fun checkAccountExists (number : String, listener1 : OnAccountSearchListenerObject)
    {
        var flag = false
        val database = FirebaseFirestore.getInstance()
        database.collection("Users").get().addOnSuccessListener { result ->
            for (document in result) {
                if(document.id == number)
                {
                    flag = true
                    listener1.listener!!.onUserAccountMatch()
                }
            }
            if(flag == false)
            {
                listener1.listener!!.onUserAccountNotFound()
            }
        }
    }

    override fun addUserToFirebase(number : String, id : String, name : String)
    {
        val database = FirebaseFirestore.getInstance()
        val user : UserDataModel = UserDataModel(Id = id , Name = name , Number = number)
        database.collection("Users").document(number.toString()).set(user)
    }

    override fun verifyNumber(number : String, activity : Activity, context: Context, dialog: ProgressBar , loginListener : OnLoginListenerObject)
    {
        onLoginListenerObject = loginListener
        var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
                    Log.d("Verification", "SMS Verification Sucessful\n$p0")
                    dialog.visibility = View.INVISIBLE
                    signInWithPhoneAuthCredential(p0!! , context)
                    Toast.makeText(context, "Verification Sucessfull", Toast.LENGTH_LONG).show()
                }

                override fun onVerificationFailed(p0: FirebaseException?) {
                    Log.d("Verification", "SMS Verification UnSucessful\n$p0")
                    Toast.makeText(context, "Verification UnSucessfull ${p0.toString()}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                    super.onCodeSent(p0, p1)
                    Log.d("Verification", "SMS Verification Sucessfully Sent")
                }

            }

        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber("+91$number", 60, TimeUnit.SECONDS, activity, mCallbacks)
    }

    override fun verifyNumber(number : String, activity : Activity, context: Context, dialog: ProgressBar, name: String , loginListener : OnLoginListenerObject)
    {
        onLoginListenerObject = loginListener
        var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
                    Log.d("Verification", "SMS Verification Sucessful\n$p0")
                    dialog.visibility = View.INVISIBLE
                    addUserToFirebase(number , p0.toString() , name)
                    signInWithPhoneAuthCredential(p0!! , context)
                    Toast.makeText(context, "Verification Sucessfull", Toast.LENGTH_LONG).show()
                }

                override fun onVerificationFailed(p0: FirebaseException?) {
                    Log.d("Verification", "SMS Verification UnSucessful\n$p0")
                    Toast.makeText(context, "Verification UnSucessfull ${p0.toString()}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                    super.onCodeSent(p0, p1)
                    Log.d("Verification", "SMS Verification Sucessfully Sent")
                }

            }

        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber("+91$number", 60, TimeUnit.SECONDS, activity, mCallbacks)
    }


    override fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, context: Context) {
        var mAuth : FirebaseAuth = FirebaseAuth(FirebaseApp.getInstance())
        mAuth.signInWithCredential(credential).addOnSuccessListener {result ->
            Toast.makeText(context , result.user.toString() , Toast.LENGTH_SHORT).show()
            onLoginListenerObject!!.listener!!.onScucess()
        }
            .addOnFailureListener { exception ->
                Toast.makeText(context , exception.toString() , Toast.LENGTH_SHORT).show()
                onLoginListenerObject!!.listener!!.onFailure()
            }
    }

}