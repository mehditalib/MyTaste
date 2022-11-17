package com.example.mytaste.utils

//import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mytaste.R
import com.example.mytaste.fragments.RestaurantFragmentRecyclerView
import com.example.mytaste.interfaces.RestaurantListener
import com.example.mytaste.pojo.Restaurant


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ////////supportFragmentManager.beginTransaction().add(R.id.containerList, RestaurantFragmentRecyclerView(this)).commit()
        /*Call map activity
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)*/
        /*Call list activity*/
        val intent = Intent(this, ListActivity::class.java)
        intent.putExtras(Bundle())
        startActivity(intent)

        /*var et_user_name = findViewById(R.id.et_user_name) as EditText
        var et_password = findViewById(R.id.et_password) as EditText
        var btn_reset = findViewById(R.id.btn_reset) as Button
        var btn_submit = findViewById(R.id.btn_submit) as Button

        btn_reset.setOnClickListener {
            et_user_name.setText("")
            et_password.setText("")
        }

        btn_submit.setOnClickListener {
            val user_name = et_user_name.text;
            val password = et_password.text;


        }*/


    }

}