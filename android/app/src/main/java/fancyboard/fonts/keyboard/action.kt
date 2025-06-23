package fancyboard.fonts.keyboard

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast


fun sendEmail(context: Context, recipient: String, subject: String, message: String) {
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.data = Uri.parse("mailto:")

    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))

    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)

    emailIntent.putExtra(Intent.EXTRA_TEXT, message)
    emailIntent.type = "text/plain"
    try{
        context.startActivity(emailIntent)
    }catch (e:Exception){
        Toast.makeText(context, "Error while opening email app", Toast.LENGTH_LONG).show()
    }
}

fun rateApp(context: Context) {
    val uri: Uri = Uri.parse("market://details?id=${context.packageName}")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    )

    try {
        context.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=${context.packageName}")
            )
        )
    }
}

fun shareApp(context: Context) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "नेपाली किबोर्ड")
        val shareMessage = "https://play.google.com/store/apps/details?id=${context.packageName}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        context.startActivity(Intent.createChooser(shareIntent, "choose one"))
    } catch (e: Exception) {
    }
}