package com.example.chanho.nanum

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import com.example.chanho.nanum.model.ProfileDTO
import com.example.chanho.nanum.model.RecruitDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_recruit.*
import kotlinx.android.synthetic.main.fragment_recruit.view.*
import kotlinx.android.synthetic.main.item_recruit.view.*

class RecruitFragment : Fragment(){

    var firestore : FirebaseFirestore? = null
    var auth : FirebaseAuth? = null
    var myProfile : ProfileDTO? = null
    var Uid : String? = null
    var name : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(inflater.context).inflate(R.layout.fragment_recruit, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        myProfile = ProfileDTO()
        Uid = FirebaseAuth.getInstance().currentUser?.uid


        firestore?.collection("profiles")?.whereEqualTo("uid", Uid)?.get()?.addOnCompleteListener { Task->
            var profileDTO = Task.result.documents.get(0).toObject(ProfileDTO::class.java)

            name = profileDTO.name

            if(profileDTO.kind == 2){
                view.btn_addrecruit.visibility = View.VISIBLE
            }
        }

        view.recruitviewfragment_recyclerview.adapter = RecruitRecyclerviewAdapter()
        view.recruitviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)


        view.btn_addrecruit.setOnClickListener { v ->
            var intent = Intent(v.context,AddRecruitActivity::class.java)
            startActivity(intent)
        }

        return view

    }

    inner class RecruitRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        val recruitDTOs : ArrayList<RecruitDTO>
        val recruitUidList : ArrayList<String>

        init{
            recruitDTOs = ArrayList()
            recruitUidList = ArrayList()

            var uid = FirebaseAuth.getInstance().currentUser?.uid

            firestore?.collection("recruits")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(querySnapshot == null) return@addSnapshotListener
                recruitDTOs.clear()
                recruitUidList.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(RecruitDTO::class.java)
                    recruitDTOs.add(item)
                    recruitUidList.add(snapshot.id)
                }
                notifyDataSetChanged()

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_recruit,parent,false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)

        override fun getItemCount(): Int {
            return recruitDTOs.size
        }

        override fun onBindViewHolder(holder : RecyclerView.ViewHolder, position: Int) {

            val viewHolder = (holder as CustomViewHolder).itemView

            viewHolder.text_name.text = recruitDTOs!![position].userId

            viewHolder.text_title.text = recruitDTOs!![position].title

            viewHolder.text_date.text = recruitDTOs!![position].date

            viewHolder.text_field.text = recruitDTOs!![position].field

            viewHolder.recruit_recyclerview.setOnClickListener{


                var fragment = RecruitDetailFragment()
                var bundle = Bundle()
                bundle.putString("recruitTitle",recruitDTOs[position].title)
                bundle.putString("recruitDate",recruitDTOs[position].date)
                bundle.putString("recruitExplain",recruitDTOs[position].explain)
                bundle.putString("recruitField",recruitDTOs[position].field)
                bundle.putString("recruitName",recruitDTOs[position].userId)
                bundle.putString("recruitUid",Uid)
                bundle.putString("recycleNum",recruitUidList[position])
                fragment.arguments = bundle

                activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_content,fragment).commit()

//
            }


        }

    }


}