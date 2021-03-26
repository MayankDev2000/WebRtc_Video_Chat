package com.mayank.webrtcvideochat

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class videoActivity : AppCompatActivity() {

    lateinit var username:String
    lateinit var webView: WebView
    lateinit var callLayout: ConstraintLayout
    lateinit var frdname:String
    var isPeerConnected:Boolean = false
    lateinit var UUID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
         username = intent.getStringExtra("username")
        webView = findViewById(R.id.webvideo)
        instance.firebaseuserRDB =  instance.firebaseRDB.child(username)
       
        setUpWeb()
        callLayout = findViewById(R.id.callLayout)
        findViewById<Button>(R.id.callButton).setOnClickListener {
            frdname = findViewById<EditText>(R.id.friendUsername).text.toString()
            if(!frdname.isEmpty() && isPeerConnected){
                callToFriend()
            }else{
                Toast.makeText(this,"Error check Internet or username is empty,${isPeerConnected}",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun callToFriend() {
        instance.firebaseRDB.child(frdname).child("incoming").setValue(username)
        instance.firebaseRDB.child(frdname).child("conId").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value != null){
                    callLayout.visibility = View.GONE
                    Toast.makeText(baseContext,"${snapshot.value.toString()}",Toast.LENGTH_SHORT).show()
                    JavascriptHandler("javascript:startCall(\"${snapshot.value}\")")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun setUpWeb(){
        webView.webChromeClient = object :WebChromeClient(){
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false

        webView.addJavascriptInterface(JavascriptInterface(this),"WebRtcVideo")
        setUpVideo()
    }

    private fun setUpVideo() {
       val HtmlFile = "file:android_asset/call.html"
        webView.loadUrl(HtmlFile)
        UUID = "test"
        JavascriptHandler("javascript:init(\"$UUID\")")
        init()
    }

    private fun init() {
        instance.firebaseuserRDB.child("incoming").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value != null)
                AlertDialog.Builder(this@videoActivity)
                    .setMessage("call Incoming from ${snapshot.value.toString()}")
                    .setPositiveButton("accept") { dialogInterface: DialogInterface, i: Int ->
                        oncallaccept(snapshot.value as String)
                        dialogInterface.cancel()
                    }
                        .setNegativeButton("cancel") { d: DialogInterface, i: Int ->
                            instance.firebaseuserRDB.child("incoming").setValue(null)
                            d.cancel()
                        }.setOnDismissListener { d: DialogInterface ->
                            d.dismiss()
                        }
                        .create()
                    .show()

            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    override fun onDestroy() {
        instance.firebaseRDB.child(username).setValue(null)
        webView.loadUrl("about:blank")
        super.onDestroy()
    }

    private fun oncallaccept(s: String) {
        instance.firebaseuserRDB.child("conId").setValue(UUID)
        
    }



    private fun JavascriptHandler(functionName:String){
        webView.post { webView.evaluateJavascript(functionName,null) }
    }
    fun tost(text:String) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
        UUID = text
    }

    fun onPeerConnected() {
        isPeerConnected = true
        Toast.makeText(this,"Peer Connected",Toast.LENGTH_SHORT).show()
    }
}