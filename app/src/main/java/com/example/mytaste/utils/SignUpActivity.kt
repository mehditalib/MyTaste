package com.example.mytaste.utils

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.mytaste.R
import com.example.mytaste.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivitySignUpBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //config ActionBar , enable back button
        actionBar = supportActionBar!!
        actionBar.title = "Inscription"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //config ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Veuillez attendre")
        progressDialog.setMessage("Création du compte")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        //click begin signup
        binding.SignUpBtn.setOnClickListener {
            //validate data
            validateDate()

        }
    }

    private fun validateDate() {
        //get data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid format
            binding.emailEt.error = "Format de l'email invalide!"
        }
        else if (TextUtils.isEmpty(password)){
            //empty password
            binding.passwordEt.error = "Veuillez entrer un mot de passe!"
        }
        else if(password.length < 8) {
            binding.passwordEt.error = "Le mot de passe doit contenir au moins 8 caractères!"
        }
        else {
            //valid data
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        //show progress
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //signup success
                progressDialog.dismiss()
                //get current user
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Compte crée avec succès!", Toast.LENGTH_SHORT).show()

                //open list
                val intent = Intent(this, ListActivity::class.java)
                intent.putExtras(Bundle())
                startActivity(intent)
                finish()

            }
            .addOnFailureListener { e->
                //signup fail
                progressDialog.dismiss()
                Log.d(this.javaClass.name, "Echec : ${e.message}")
                Toast.makeText(this, "Echec de l'inscription à cause de ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}