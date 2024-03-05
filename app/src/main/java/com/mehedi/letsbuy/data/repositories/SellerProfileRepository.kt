package com.mehedi.letsbuy.data.repositories

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.mehedi.letsbuy.core.Nodes
import com.mehedi.letsbuy.views.dashboard.seller.profile.SellerProfile
import com.mehedi.letsbuy.views.dashboard.seller.profile.toMap
import javax.inject.Inject

class SellerProfileRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val storageRef: StorageReference
) {
    fun uploadImage(productImageUri: Uri): UploadTask {

        val storage: StorageReference =
            storageRef.child("profile").child("USER_${System.currentTimeMillis()}")

        return storage.putFile(productImageUri)


    }

    fun updateUser(user: SellerProfile): Task<Void> {


        return db.collection(Nodes.USER).document(user.userID).update(user.toMap())


    }

     fun getUserByUserID(userID: String): Task<QuerySnapshot> {

        return db.collection(Nodes.USER).whereEqualTo("userID", userID).get()


    }


}