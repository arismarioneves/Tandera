package com.mari05lim.tandera.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.mari05lim.tandera.BuildConfig
import com.mari05lim.tandera.R
import com.mari05lim.tandera.data.MediaType

/** Abre no app correspondente (Netflix, YouTube...) quando instalado; senão, no navegador. */
fun Context.openUrl(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show()
    }
}

fun Context.shareText(text: String) {
    val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
    startActivity(Intent.createChooser(send, null))
}

/** Página do título no site, que mostra a mesma disponibilidade. */
fun siteTitleUrl(type: MediaType, id: Int): String = "${BuildConfig.SITE_URL}?id=$id&tipo=${type.apiValue}"
