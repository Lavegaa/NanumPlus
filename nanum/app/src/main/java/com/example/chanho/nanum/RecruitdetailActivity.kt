package com.example.chanho.nanum

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.chanho.nanum.model.RecruitDTO
import kotlinx.android.synthetic.main.activity_recruitdetail.*

class RecruitdetailActivity : AppCompatActivity() {

    var recruitDTO : RecruitDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recruitdetail)

        text_view_title.text.toString()
        text_view_field.text.toString()
        text_view_date.text.toString()
        text_view_title.setText("제목: "+intent.getStringExtra("recruitTitle"))
        text_view_field.setText("카테고리: "+intent.getStringExtra("recruitField"))
        text_view_date.setText("기간: "+intent.getStringExtra("recruitDate"))
        text_view_body.text = intent.getStringExtra("recruitExplain")



    }


}
