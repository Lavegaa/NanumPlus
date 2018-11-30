package com.example.chanho.nanum

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import com.example.chanho.nanum.R.color.colorEmailSignIn
import com.example.chanho.nanum.model.ProfileDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_editprofile.*
import kotlinx.android.synthetic.main.item_recruit.*
import com.example.chanho.nanum.getProfile.*

class EditprofileActivity : AppCompatActivity() {

    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var kind : Int? = 0
    var uid : String? = null
    var profileDTO : ProfileDTO? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        kind = intent.getIntExtra("kind",0)
        uid = auth?.currentUser?.uid
        profileDTO = ProfileDTO()
        if(kind == 0){
            btn_check_student.setOnClickListener {
                if(kind == 0){
                    btn_check_student.setBackgroundColor(resources.getColor(R.color.colorEmailSignInPressed))
                }else{
                    btn_check_center.setBackgroundColor(resources.getColor(R.color.colorEmailSignIn))
                    btn_check_student.setBackgroundColor(resources.getColor(R.color.colorEmailSignInPressed))
                }
                edit_profile_name.setHint("이름")
                edit_profile_field.setHint("분야")

                kind = 1
            }

            btn_check_center.setOnClickListener {
                if(kind == 0){
                    btn_check_student.setBackgroundColor(resources.getColor(R.color.colorEmailSignInPressed))
                }else{
                    btn_check_center.setBackgroundColor(resources.getColor(R.color.colorEmailSignInPressed))
                    btn_check_student.setBackgroundColor(resources.getColor(R.color.colorEmailSignIn))
                }
                edit_profile_name.setHint("기관명")
                edit_profile_field.setHint("기관을 설명해주세요.")

                kind = 2
            }
        }else if(kind == 1){
            text_profile_title.setText("프로필")
            btn_check_center.visibility = GONE
            btn_check_student.visibility = GONE
            edit_profile_name.setText(intent.getStringExtra("name"))
            edit_profile_field.setText(intent.getStringExtra("field"))
            edit_profile_area.setText(intent.getStringExtra("area"))
            edit_profile_name.setHint("이름")
            edit_profile_field.setHint("분야")
        }else{
            text_profile_title.setText("프로필")
            btn_check_center.visibility = GONE
            btn_check_student.visibility = GONE
            edit_profile_name.setText(intent.getStringExtra("name"))
            edit_profile_field.setText(intent.getStringExtra("explain"))
            edit_profile_area.setText(intent.getStringExtra("area"))
            edit_profile_name.setHint("기관명")
            edit_profile_field.setHint("기관을 설명해주세요.")

        }


        btn_profile_submit.setOnClickListener {
            profileUpload(kind!!)
            text_profile_title.setText("꺼짐")
        }

    }




    fun profileUpload(kind : Int){

            profileDTO?.uid = auth?.currentUser?.uid

            profileDTO?.name = edit_profile_name.text.toString()

            profileDTO?.area = edit_profile_area.text.toString()

            if(kind == 1){
                profileDTO?.field = edit_profile_field.text.toString()
            }else{
                profileDTO?.expalain = edit_profile_field.text.toString()
            }

            profileDTO?.kind = kind

            firestore?.collection("profiles")?.document(uid!!)?.set(profileDTO!!)?.addOnSuccessListener {
                Log.d("onSuccess",kind.toString())
                finish()
            }




    }

}
