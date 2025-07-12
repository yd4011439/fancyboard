package fancyboard.fonts.keyboard

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import java.util.Date

private const val APP_REVIEW_ASKED_DATE = "appReviewAskedDate";
private const val HAS_ASKED = "hasAskedReviewFirstTime";
fun askReview(context: Context) {
    val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val date = prefs.getLong(APP_REVIEW_ASKED_DATE, -1L)

    // If the preference value is not set the set it
    if (date == -1L) {
        prefs.edit().putLong(APP_REVIEW_ASKED_DATE, Date().time).apply()
        return
    }

    val hasAskedFirstTime = prefs.getBoolean(HAS_ASKED, false)
    val daysPassed = (Date().time - date) / (86400 * 1000)
    if ((daysPassed > 5 && !hasAskedFirstTime) || daysPassed > 25) {
        val manager = ReviewManagerFactory.create(context)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(context as Activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    prefs.edit().apply {
                        putLong(APP_REVIEW_ASKED_DATE, Date().time)
                        putBoolean(HAS_ASKED, true)
                        apply()
                    }
                }
            }
        }
    }
}