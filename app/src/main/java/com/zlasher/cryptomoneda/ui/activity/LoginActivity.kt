package com.zlasher.cryptomoneda.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.zlasher.cryptomoneda.R
import com.zlasher.cryptomoneda.databinding.ActivityLoginBinding
import com.zlasher.cryptomoneda.network.Callback
import com.zlasher.cryptomoneda.network.FirestoreService
import com.zlasher.cryptomoneda.network.USERS_COLLECTION_NAME
import com.zlasher.cryptomoneda.model.User
import java.lang.Exception


const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    //private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var auth: FirebaseAuth
    lateinit var firestoreService: FirestoreService
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())
    }

    fun onStartClicked(view: View) {
        view.isEnabled = false
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val username = binding.username.text.toString()
                    firestoreService.findUserById(username, object : Callback<User> {
                        override fun onSuccess(result: User?) {
                            if (result == null) {
                                val user = User()
                                user.username = username
                                saveUserAndStartMainActivity(user, view)
                            } else startMainActivity(username)
                        }

                        override fun onFailed(exception: Exception) {
                            showErrorMessage(view)
                        }

                    })
                } else {
                    view.isEnabled = true
                    showErrorMessage(view)

                }
            }
    }

    private fun saveUserAndStartMainActivity(user: User, view: View) {
        firestoreService.setDocument(
            user,
            USERS_COLLECTION_NAME,
            user.username,
            object : Callback<Void> {
                override fun onSuccess(result: Void?) {
                    startMainActivity(user.username)
                }

                override fun onFailed(exception: Exception) {
                    view.isEnabled = true
                    showErrorMessage(view)
                    Log.e(TAG, "error", exception)

                }
            })
    }

    private fun showErrorMessage(view: View) {
        view.isEnabled = true
        Snackbar.make(
            view, getString(R.string.error_while_connecting_to_the_server),
            Snackbar.LENGTH_LONG
        )
            .setAction("info", null).show()
    }

    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }

}