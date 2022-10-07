package com.example.litechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.litechat.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SIgnInActivity : AppCompatActivity() {
    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    authFirbaseWithGoogle(account.idToken.toString())
                }
            } catch (e: ApiException) {
                Toast.makeText(
                    applicationContext, "ApiException",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.btnSignIn.setOnClickListener { signInWithGoogle() }
        checkAuthState()
    }

    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        launcher.launch(getClient().signInIntent)
    }

    private fun authFirbaseWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    applicationContext, "Successfully",
                    Toast.LENGTH_LONG
                ).show()
                checkAuthState()
            } else {
                Toast.makeText(
                    applicationContext, "Error,try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkAuthState() {
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
