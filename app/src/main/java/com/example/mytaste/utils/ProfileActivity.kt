package com.example.mytaste.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.mytaste.R
import com.example.mytaste.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityProfileBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //config ActionBar
        actionBar = supportActionBar!!
        actionBar.title = "Profile"

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //click logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }
    }

    private fun checkUser() {
        //check user login status
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //user logged in
            val email = firebaseUser.email
            binding.emailTv.text = email
        }
        else{
            //user not logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
