package com.example.netadminmarketplace.data

import com.example.netadminmarketplace.model.Review
import com.example.netadminmarketplace.model.UserProfile
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/** Пагинация отзывов + атомарное добавление оценки и пересчет рейтинга. */
class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()

    data class Page(val reviews: List<Review>, val nextCursor: DocumentSnapshot?, val hasNext: Boolean)

    fun loadPage(adminId: String, after: DocumentSnapshot?, callback: (Result<Page>) -> Unit) {
        var query: Query = db.collection("sysadmins").document(adminId).collection("reviews")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(PAGE_SIZE + 1L)
        if (after != null) query = query.startAfter(after)
        query.get().addOnSuccessListener { snapshot ->
            val docs = snapshot.documents
            val visible = docs.take(PAGE_SIZE)
            callback(Result.success(Page(visible.mapNotNull { it.toObject(Review::class.java) }, visible.lastOrNull(), docs.size > PAGE_SIZE)))
        }.addOnFailureListener { callback(Result.failure(it)) }
    }

    fun hasReview(adminId: String, reviewerUid: String, callback: (Result<Boolean>) -> Unit) {
        db.collection("sysadmins").document(adminId).collection("reviews").document(reviewerUid).get()
            .addOnSuccessListener { callback(Result.success(it.exists())) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    fun submitReview(adminId: String, author: UserProfile, rating: Int, text: String, callback: (Result<Unit>) -> Unit) {
        if (adminId == author.uid) return callback(Result.failure(IllegalArgumentException("Нельзя оставить отзыв самому себе")))
        val adminRef = db.collection("sysadmins").document(adminId)
        val reviewRef = adminRef.collection("reviews").document(author.uid)
        db.runTransaction { tx ->
            val adminDoc = tx.get(adminRef)
            if (!adminDoc.exists()) error("Карточка специалиста не найдена")
            val oldReview = tx.get(reviewRef)
            if (oldReview.exists()) error("Вы уже оставили отзыв этому специалисту")
            val oldSum = adminDoc.getLong("ratingSum") ?: 0L
            val oldCount = adminDoc.getLong("reviewsCount") ?: 0L
            val newSum = oldSum + rating
            val newCount = oldCount + 1L
            val newRating = newSum.toDouble() / newCount.toDouble()
            tx.set(reviewRef, mapOf(
                "authorUid" to author.uid,
                "authorName" to author.displayName,
                "authorAvatarKey" to author.avatarKey,
                "rating" to rating.toLong(),
                "text" to text.trim(),
                "createdAt" to FieldValue.serverTimestamp()
            ))
            tx.update(adminRef, mapOf(
                "ratingSum" to newSum,
                "reviewsCount" to newCount,
                "rating" to newRating,
                "latestReviewText" to text.trim(),
                "latestReviewerName" to author.displayName
            ))
            Unit
        }.addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    companion object { const val PAGE_SIZE = 5 }
}
