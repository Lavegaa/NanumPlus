package com.example.chanho.nanum


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.chanho.nanum.model.RecruitDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_recruit.*

class AddRecruitActivity : AppCompatActivity() {

    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recruit)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btn_submit.setOnClickListener {
            recruitUpload()
        }

    }



    fun recruitUpload(){

        var recruitDTO = RecruitDTO()

        recruitDTO.uid = auth?.currentUser?.uid

        recruitDTO.userId = auth?.currentUser?.email

        recruitDTO.title = text_edit_title.text.toString()

        recruitDTO.date = text_edit_date.text.toString()

        recruitDTO.field = text_edit_field.text.toString()

        recruitDTO.explain = text_edit_body.text.toString()

        firestore?.collection("recruits")?.document()?.set(recruitDTO)

        finish()



    }

}
