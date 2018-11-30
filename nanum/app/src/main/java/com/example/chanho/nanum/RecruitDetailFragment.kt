package com.example.chanho.nanum

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chanho.nanum.model.ApplicantDTO
import com.example.chanho.nanum.model.ProfileDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_recruitdetail.*
import kotlinx.android.synthetic.main.fragment_recruitdetail.view.*
import kotlinx.android.synthetic.main.item_applicant.view.*

class RecruitDetailFragment : Fragment(){

    var firestore : FirebaseFirestore? = null
    var auth : FirebaseAuth? = null
    var kind : Int? = 0
    var name : String? = null
    var field : String? = null
    var recycleNum : String? = null
    var myapplicant : ApplicantDTO? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(inflater.context).inflate(R.layout.fragment_recruitdetail,container,false)




        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        recycleNum = arguments!!.getString("recycleNum")

        var mainActivity = (activity as MainActivity)

        mainActivity.toolbar_title_image.visibility = View.GONE
        mainActivity.toolbar_btn_back.visibility = View.VISIBLE
        mainActivity.toolbar_username.visibility = View.VISIBLE

        mainActivity.toolbar_btn_back.setOnClickListener {
            mainActivity.toolbar_title_image.visibility = View.VISIBLE
            mainActivity.toolbar_btn_back.visibility = View.GONE
            mainActivity.toolbar_username.visibility = View.GONE
            var fragment = RecruitFragment()

            activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_content,fragment).commit()

        }

        view.text_view_title.text.toString()
        view.text_view_field.text.toString()
        view.text_view_date.text.toString()
        view.text_view_name.text.toString()
        view.text_view_name.setText("기관명: "+arguments!!.getString("recruitName"))
        view.text_view_title.setText("제목: "+arguments!!.getString("recruitTitle"))
        view.text_view_field.setText("카테고리: "+arguments!!.getString("recruitField"))
        view.text_view_date.setText("기간: "+arguments!!.getString("recruitDate"))
        view.text_view_body.text = arguments!!.getString("recruitExplain")


        firestore?.collection("profiles")?.whereEqualTo("uid", auth?.currentUser?.uid)?.get()?.addOnCompleteListener { Task->
            var profileDTO = Task.result.documents.get(0).toObject(ProfileDTO::class.java)

            kind = profileDTO.kind
            name = profileDTO.name
            field = profileDTO.field
            if(Task.isSuccessful){
                if(kind == 1){
                    view.btn_submit.visibility = View.VISIBLE
                }else{
                    view.applicant_recyclerView.visibility = View.VISIBLE
                }
            }

        }



        firestore?.collection("recruits")?.document(recycleNum!!)?.collection("applicants")?.whereEqualTo("uid",auth?.currentUser?.uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            if(querySnapshot == null) return@addSnapshotListener

            if(querySnapshot.documents.toString() == "[]"){
                view.btn_submit.setText("신청하기")
            }else{
                myapplicant = querySnapshot.documents.get(0).toObject(ApplicantDTO::class.java)

                if(myapplicant!!.state == 1){
                    view.btn_submit.setText("접수 중")
                }else if(myapplicant!!.state == 2){
                    view.btn_submit.setText("선발 완료")
                }
            }




        }

        view.btn_submit.setOnClickListener {
            var applicant = ApplicantDTO()

            applicant.name = name
            applicant.field = field
            applicant.uid = auth?.currentUser?.uid
            Log.d("state",myapplicant?.state.toString())
            if(myapplicant?.state == 1){
                myapplicant?.state = null
                firestore?.collection("recruits")?.document(recycleNum!!)?.collection("applicants")?.document(applicant.uid!!)?.delete()
            }else if(myapplicant?.state == null){
                applicant.state = 1
                firestore?.collection("recruits")?.document(recycleNum!!)?.collection("applicants")?.document(applicant.uid!!)?.set(applicant)
            }

        }

        view.applicant_recyclerView.adapter = RecruitdetailRecyclerViewAdapter()
        view.applicant_recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }


    inner  class RecruitdetailRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        val applicants : ArrayList<ApplicantDTO>

        init{
            applicants = ArrayList()

            firestore?.collection("recruits")?.document(recycleNum!!)?.collection("applicants")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                applicants.clear()
                if(querySnapshot == null) return@addSnapshotListener
                Log.d("no????",querySnapshot.documents.toString() )
                if(kind == 2){
                    if(querySnapshot.documents.toString() == "[]"){
                        text_noapplicant.visibility = View.VISIBLE
                    }
                }


                for(snapshot in querySnapshot.documents){
                    applicants.add(snapshot.toObject(ApplicantDTO::class.java))
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_applicant,parent,false)
            return CustomViewHolder(view)
        }
        private inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)

        override fun getItemCount(): Int {
            return applicants.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder.itemView
            var startfragment = "recruitdetail"

            view.text_applicant_name.setText("이름: "+ applicants[position].name)
            view.text_applicant_field.setText("관심분야:" + applicants[position].field)

            firestore?.collection("profileImages")?.document(applicants[position].uid!!)?.get()?.addOnCompleteListener {
                task ->
                if(task.isSuccessful){
                    var url = task.result["image"]
                    Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(view.img_applicant_profile)
                }
            }


            view.btn_applicant_submit.setOnClickListener {
                if(applicants[position].state == 1){
                    applicants[position].state = 2
                }else{
                    applicants[position].state = 1
                }

                firestore?.collection("recruits")?.document(recycleNum!!)?.collection("applicants")?.document(applicants[position].uid.toString())?.set(applicants[position])
            }


            if(applicants[position].state == 1){
                view.btn_applicant_submit.setText("모집")
            }else{
                view.btn_applicant_submit.setText("모집됨")
            }

            view.img_applicant_profile.setOnClickListener { v->
                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid", applicants[position].uid)
                bundle.putString("userId", applicants[position].name)
                bundle.putString("startfragment",startfragment)

                bundle.putString("recruitName",arguments!!.getString("recruitName"))
                bundle.putString("recruitTitle",arguments!!.getString("recruitTitle"))
                bundle.putString("recruitField",arguments!!.getString("recruitField"))
                bundle.putString("recruitDate",arguments!!.getString("recruitDate"))
                bundle.putString("recycleNum",arguments!!.getString("recycleNum"))
                bundle.putString("recruitExplain",arguments!!.getString("recruitExplain"))



                fragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit()

            }

        }

    }
}