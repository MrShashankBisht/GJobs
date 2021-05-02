package com.iab.GJobs

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.AdRequest
import com.iab.GJobs.data_model.DetailResponseDataModel
import com.iab.GJobs.databinding.ActivityDetailBinding
import com.iab.GJobs.util.NativeClass
import com.iab.GJobs.util.getDetailDataFromServer
import com.iab.GJobs.util.utils.getFinalString
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class DetailActivity : AppCompatActivity() {

    lateinit var detailActivityBinding: ActivityDetailBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        detailActivityBinding = ActivityDetailBinding.inflate(LayoutInflater.from(this))
        setContentView(detailActivityBinding.root)

        supportActionBar?.hide()

        detailActivityBinding.detailAdView.loadAd(AdRequest.Builder().build())
        detailActivityBinding.jobShareConstraintLayout.visibility = View.GONE
        detailActivityBinding.webView.settings.javaScriptEnabled = true
        detailActivityBinding.detailProgressBar.visibility = View.VISIBLE

        val webSettings: WebSettings = detailActivityBinding.webView.getSettings()
        webSettings.minimumFontSize = 50
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        val action: String = intent.getStringExtra("action")!!
        val id: String = intent.getStringExtra("id")!!
        val job_id: String = intent.getStringExtra("cat_id")!!
        val job_title: String = intent.getStringExtra("Job_title")!!
        loadDataFromServer(action, id, job_id, job_title)

        detailActivityBinding.jobShareConstraintLayout.setOnClickListener {
            //Get the picture object
            val picture= detailActivityBinding.webView.capturePicture();
            //Get the width and height of the image (without reflecting the image content)
            val width=picture.width
            val height=picture.height
            if (width>0&&height>0) {
                //Create a bitmap
                val bitmap:Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                val canvas = Canvas(bitmap);
                //Draw (the native method will be called to complete the graphics drawing)
                picture.draw(canvas);
                shareWebViewBitmap(bitmap)
            }
        }

    }

//    load data from server
    fun loadDataFromServer(action: String, id: String, job_id: String, job_title:String){
    val queue = Volley.newRequestQueue(this)
    val url = NativeClass.getDecodedString()

    // Request a string response from the provided URL.
    val stringRequestLatestJob = object : StringRequest(Request.Method.POST, url,
            Response.Listener<String> { it2 ->
                val detailResponseDataModel: DetailResponseDataModel? = getDetailDataFromServer(it2)
                detailResponseDataModel?.let {
                    val filterString: String = filterDetailData(it.description, job_title)
                    detailActivityBinding.webView.loadDataWithBaseURL(
                            null,
                            filterString,
                            "text/html",
                            "UTF-8",
                            null
                    );
                }
                detailActivityBinding.detailProgressBar.visibility = View.GONE
                detailActivityBinding.jobShareConstraintLayout.visibility = View.VISIBLE
            }, Response.ErrorListener {
        Log.d("LatestJobsError", it.message.toString())
        detailActivityBinding.detailProgressBar.visibility = View.GONE
            detailActivityBinding.jobShareConstraintLayout.visibility = View.GONE
        }) {
        override fun getParams(): MutableMap<String, String> {
            val hashMap = HashMap<String, String>()
            hashMap["action"] = action
            hashMap["id"] = job_id
            hashMap["cat_id"] = id
            return hashMap

        }

        override fun getHeaders(): MutableMap<String, String> {
            val hashMap = HashMap<String, String>()
            hashMap["Content-Type"] = "application/x-www-form-urlencoded"
            return hashMap
        }
    }
    queue.cache.clear()
    queue.add(stringRequestLatestJob)
    }

    fun filterDetailData(detail: String, jobTitle:String):String{
        val constHeadOfTheBody = "<html>\n" +
                "<head>\n" +
                "   <meta name=\\\"viewport\\\" content=\\\"width=device-width, user-scalable=yes\\\" />\n" +
                "   <style>\n" +
                "      body {background-color: #1F232A;}\n" +
                "      h2   {color: #ffc491;}\n" +
                "      b   {color: #ffc491;}\n" +
                "      span   {color: #ffc491;}\n" +
                "      p    {color: rgb(255, 255, 255);}\n" +
                "      li    {color: rgb(255, 255, 255);}\n" +
                "      td { color: #ffc491;}\n" +
                "      a { color: #91c4ff;}\n" +
                "      </style>\n" +
                "</head>\n" +
                "<body> \n" +
                "<h2 align=\"center\"><b>" + jobTitle + "</b></h2>\n"

        val constFootOfTheBody:String = "</body>\n" +
                "</html>"

        val dataFinalHtmlString:String = getFinalString(detail)

        return (constHeadOfTheBody + dataFinalHtmlString + constFootOfTheBody)
    }

    private fun shareWebViewBitmap(bitmap: Bitmap){

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imagePath = MediaStore.Images.Media.insertImage(
            this.contentResolver,
            bitmap,
            "img_$timeStamp",
            null
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath))
            putExtra(Intent.EXTRA_TEXT,"Hey please check this application " + "https://play.google.com/store/apps/details?id=" +getPackageName());
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

}