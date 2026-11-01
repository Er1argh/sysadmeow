package com.example.netadminmarketplace.data

import com.example.netadminmarketplace.model.Specialist
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

/** Публичный каталог sysadmin-котов в Cloud Firestore /sysadmins. */
class AdminRepository {
    private val db = FirebaseFirestore.getInstance()

    fun observePublished(onChanged: (List<Pair<String, Specialist>>) -> Unit, onError: (Throwable) -> Unit): ListenerRegistration {
        return db.collection("sysadmins")
            .whereEqualTo("published", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener onError(error)
                val list = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    doc.toObject(Specialist::class.java)?.let { doc.id to it }
                }.sortedWith(compareByDescending<Pair<String, Specialist>> { it.second.rating }.thenByDescending { it.second.reviewsCount })
                onChanged(list)
            }
    }

    fun getAdmin(adminId: String, callback: (Result<Specialist>) -> Unit) {
        db.collection("sysadmins").document(adminId).get()
            .addOnSuccessListener { doc ->
                val item = doc.toObject(Specialist::class.java)
                if (item != null) callback(Result.success(item))
                else callback(Result.failure(IllegalStateException("Карточка sysadmin-кота не найдена")))
            }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    fun getMyListing(uid: String, callback: (Result<Specialist?>) -> Unit) {
        db.collection("sysadmins").document(uid).get()
            .addOnSuccessListener { callback(Result.success(it.toObject(Specialist::class.java))) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    fun saveListing(uid: String, data: Map<String, Any>, callback: (Result<Unit>) -> Unit) {
        db.collection("sysadmins").document(uid).set(data, SetOptions.merge())
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }
}
