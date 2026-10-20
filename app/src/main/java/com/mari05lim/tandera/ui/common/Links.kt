package com.mari05lim.tandera.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.mari05lim.tandera.BuildConfig
import com.mari05lim.tandera.R
import com.mari05lim.tandera.data.MediaType

const val CONTACT_EMAIL = "mariodev@outlook.com.br"

private fun Context.storeUrl() = "https://play.google.com/store/apps/details?id=$packageName"

private fun Context.startSafely(intent: Intent) {
    try {
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show()
    }
}

/** Abre no app correspondente (Netflix, YouTube...) quando instalado; senão, no navegador. */
fun Context.openUrl(url: String) = startSafely(Intent(Intent.ACTION_VIEW, url.toUri()))

fun Context.shareText(text: String) {
    val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
    startSafely(Intent.createChooser(send, getString(R.string.action_share)))
}

fun Context.shareApp() = shareText("${getString(R.string.share_app_text)}\n${storeUrl()}")

fun Context.openStore() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()))
    } catch (_: ActivityNotFoundException) {
        openUrl(storeUrl())
    }
}

fun Context.sendFeedback() = startSafely(
    Intent(Intent.ACTION_SENDTO, "mailto:".toUri())
        .putExtra(Intent.EXTRA_EMAIL, arrayOf(CONTACT_EMAIL))
        .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject, BuildConfig.VERSION_NAME)),
)

/**
 * Link curto do título no site: I{id}T{F|S} (ex.: …/tandera/I1058424TF).
 * O .htaccess do TanderaWEB encaminha esse código para share.php.
 */
fun siteTitleUrl(type: MediaType, id: Int): String =
    "${BuildConfig.SITE_URL}I${id}T${if (type == MediaType.Movie) "F" else "S"}"
