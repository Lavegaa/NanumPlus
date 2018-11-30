package com.example.chanho.nanum

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.example.chanho.nanum.model.FollowDTO
import com.example.chanho.nanum.model.ProfileDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_editprofile.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    var PICK_PROFILE_FROM_ALBUM = 10
    var firestore : FirebaseFirestore? = null
    var user : FirebaseAuth? = null
    var uid : String? = null

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setToolbarDefault()
        when(item.itemId){
            R.id.action_home ->{
                var detailviewFragment = DetailviewFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,detailviewFragment).commit()
                return true
            }

            R.id.action_search ->{
                var recruitFragment = RecruitFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,recruitFragment).commit()
                return true
            }

            R.id.action_add_photo ->{
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    startActivity(Intent(this,AddPhotoActivity::class.java))
                }
                return true
            }

            R.id.action_favorite_alarm ->{
                var alertFragment = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,alertFragment).commit()
                return true
            }

            R.id.action_account ->{
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                var userFragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid",uid)
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestore = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance()

        uid = user?.currentUser?.uid


        firestore?.collection("profiles")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

            if(documentSnapshot == null) return@addSnapshotListener

            var profileDTO = documentSnapshot.toObject(ProfileDTO::class.java)
            if(profileDTO == null){
                profileDTO = ProfileDTO()
                profileDTO?.uid = uid
                firestore?.collection("profiles")?.document(uid!!)?.set(profileDTO)
            }

            if(profileDTO.kind == 0){
                var followDTO = FollowDTO()
                firestore!!.collection("users").document(uid!!).set(followDTO)
                startActivity(Intent(this, EditprofileActivity::class.java))

            }

        }

        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.selectedItemId = R.id.action_home

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
//        registerPushToken()
    }

    fun registerPushToken(){
        var pushToken = FirebaseInstanceId.getInstance().token
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var map = mutableMapOf<String,Any>()

        map["pushToken"] = pushToken!!
        FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)

    }

    fun setToolbarDefault(){
        toolbar_btn_back.visibility = View.GONE
        toolbar_username.visibility = View.GONE
        toolbar_title_image.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK){
            var imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid).putFile(imageUri!!).addOnCompleteListener {
                task ->
                var url = task.result.downloadUrl.toString()
                var map = HashMap<String,Any>()
                map["image"] = url
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)
            }
        }

    }
}
