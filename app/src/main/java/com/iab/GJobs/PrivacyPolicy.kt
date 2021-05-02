package com.iab.GJobs

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class PrivacyPolicy : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        supportActionBar!!.show()
        val adView = findViewById<AdView>(R.id.pp_ad_view)
        adView.loadAd(AdRequest.Builder().build())
        val webView: WebView = findViewById(R.id.webView_privacy_policy)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/"+"privacy.html")

    }
}