package com.mayank.webrtcvideochat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import java.security.Permission

class MainActivity : AppCompatActivity() {

    var permission = arrayOf(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO)
    var permission_code = 8429
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!isPermissionGranted()){
            ActivityCompat.requestPermissions(this,permission,permission_code)
        }
        findViewById<Button>(R.id.materialButton).setOnClickListener {
            val text = findViewById<EditText>(R.id.username).text.toString()
            if(text.isNotEmpty()){
                instance.firebaseRDB.setValue(text)
                startActivity(Intent(this,videoActivity::class.java).putExtra("username",text))
            }
            else Toast.makeText(this,"Please Enter The Group Name",Toast.LENGTH_LONG).show()
        }

    }

    private fun isPermissionGranted(): Boolean {
        permission.forEach {
            if(ActivityCompat.checkSelfPermission(this,it) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }

}