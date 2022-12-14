package com.example.mytaste.utils

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.mytaste.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityLoginBinding

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
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //config ActionBar
        actionBar = supportActionBar!!
        actionBar.title= "Connexion"

        //config ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Veuillez attendre")
        progressDialog.setMessage("Connexion en cours")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //click login
        binding.NoAccountTv.setOnClickListener {
            //before login  check data
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        binding.LoginBtn.setOnClickListener{
            validateData()
        }

    }

    private fun validateData() {
        //get data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid format
            binding.emailEt.error = "Format de l'email invalide!"
        }
        else if(TextUtils.isEmpty(password)){
            //empty password
            binding.passwordEt.error = "Veuillez entrer un mot de passe!"
        }
        else {
            //valid data begin login
            firebaseLogin()
        }

    }

    private fun firebaseLogin() {
        //show progress
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                //get user info
                val firebaseUser = firebaseAuth.currentUser
                val emal = firebaseUser!!.email
                Toast.makeText(this, "Connect?? en tant que $email", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ListActivity::class.java)
                intent.putExtras(Bundle())
                startActivity(intent)
                //finish()
            }
            .addOnFailureListener { e->
                //login fail
                progressDialog.dismiss()
                Toast.makeText(this, "Echec de la connexion d?? ?? ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        //if already logged in go to list
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //user already logged in
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtras(Bundle())
            startActivity(intent)
            //finish()
        }
    }

    override fun onResume() {
        super.onResume()
        email = ""
        password = ""
        binding.emailEt.text.clear()
        binding.passwordEt.text.clear()
        binding.emailEt.clearFocus()
        binding.passwordEt.clearFocus()
    }
}