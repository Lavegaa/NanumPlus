package com.example.chanho.nanum

import android.util.Log
import com.example.chanho.nanum.model.ProfileDTO
import com.google.firebase.firestore.FirebaseFirestore

class getProfile{

    var firestore : FirebaseFirestore?= null

    var profileDTO : ProfileDTO? = null

    fun getprofile(uid : String, outputProfileDTO : ProfileDTO):ProfileDTO{
        firestore = FirebaseFirestore.getInstance()

        firestore?.collection("profiles")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

            if(documentSnapshot == null) return@addSnapshotListener

            profileDTO = ProfileDTO()

            profileDTO = documentSnapshot.toObject(ProfileDTO::class.java)

            outputProfileDTO.name = profileDTO!!.name

            outputProfileDTO.area = profileDTO!!.area

            outputProfileDTO.field = profileDTO!!.field

            outputProfileDTO.expalain = profileDTO!!.expalain

            outputProfileDTO.kind = profileDTO!!.kind

            Log.d("profileDTO",profileDTO.toString())
            Log.d("outputDTO",outputProfileDTO.toString())


        }
        return outputProfileDTO


    }

}