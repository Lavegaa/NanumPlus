package com.example.chanho.nanum

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chanho.nanum.model.RecruitDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_recruit.*
import kotlinx.android.synthetic.main.fragment_recruit.view.*
import kotlinx.android.synthetic.main.item_recruit.view.*

class RecruitFragment : Fragment(){

    var firestore : FirebaseFirestore? = null
    var user : FirebaseAuth? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(inflater.context).inflate(R.layout.fragment_recruit, container, false)
        firestore = FirebaseFirestore.getInstance()

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
                Log.d("querysnapshot : ",querySnapshot!!.documents.toString())
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(RecruitDTO::class.java)
                    recruitDTOs.add(item)
                    recruitUidList.add(snapshot.id)
                    Log.d("RecruitDTO snapshot : ",recruitDTOs.toString())
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

            viewHolder.recruit_recyclerview.setOnClickListener{ v ->


                var intent = Intent(v.context,RecruitdetailActivity::class.java)
                intent.putExtra("recruitTitle",recruitDTOs[position].title)
                intent.putExtra("recruitDate",recruitDTOs[position].date)
                intent.putExtra("recruitExplain",recruitDTOs[position].explain)
                intent.putExtra("recruitField",recruitDTOs[position].field)


                startActivity(intent)
            }


        }

    }


}