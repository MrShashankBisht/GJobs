package com.iab.GJobs

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.iab.GJobs.adapter.MainActivityRecyclerAdapter
import com.iab.GJobs.data_model.AdmitCardDataModel
import com.iab.GJobs.data_model.LatestJobDataModel
import com.iab.GJobs.data_model.ResultDataModel
import com.iab.GJobs.databinding.ActivityMainBinding
import com.iab.GJobs.databinding.ExitDialogBinding
import com.iab.GJobs.util.LatestJobStringToJson
import com.iab.GJobs.util.NativeClass
import com.iab.GJobs.util.admitCardStringToJson
import com.iab.GJobs.util.resultStringToJson
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), View.OnClickListener {

//    binding
    lateinit var mainBinding: ActivityMainBinding;

    lateinit var latestJobRecyclerAdapter:MainActivityRecyclerAdapter
    lateinit var resultRecyclerAdapter:MainActivityRecyclerAdapter
    lateinit var admitCardRecyclerAdapter:MainActivityRecyclerAdapter
    lateinit var exitDialogAdRequest: AdRequest
    lateinit var webApi:String;

    private var mInterstitialAd: InterstitialAd? = null
    private var mAdIsLoading: Boolean = false
    private var isInterstitialAddVisibale = false
    val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

//    lateinit var mainActivityViewModel: MainActivityViewModel

    lateinit var sharePreferences:SharedPreferences
    lateinit var editor: Editor
    var latestJobCount = 1
    var admitCardCount = 1
    var resultCount = 1
//    MutableLiveData

    var latestJobDataModels: MutableLiveData<LatestJobDataModel> = MutableLiveData()
    var resultDataModels: MutableLiveData<ResultDataModel> = MutableLiveData()
    var admitCardDataModels: MutableLiveData<AdmitCardDataModel> = MutableLiveData()

    var activeRecyclerView:MainActivityRecyclerAdapter.MainRecyclerViewType = MainActivityRecyclerAdapter.MainRecyclerViewType.LATEST_JOB;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        mainBinding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(mainBinding.root)
        MobileAds.initialize(this) {}
//        hide tittle bar
        supportActionBar?.hide();
        sidePanelTransitionStart(!isSidePanelOpen())



        val adRequest = AdRequest.Builder().build()
        exitDialogAdRequest = AdRequest.Builder().build()
        mainBinding.mainAdView.loadAd(adRequest)

        sharePreferences = getSharedPreferences("GJob", Context.MODE_PRIVATE)
        editor = sharePreferences.edit()

        webApi = NativeClass.getDecodedString()

        loadAd()
        sheduleInterstitialAds()



//        setting click listener
        mainBinding.mainSidePanel.latestJobLinearLayout.setOnClickListener(this)
        mainBinding.mainSidePanel.resultLinearLayout.setOnClickListener(this)
        mainBinding.mainSidePanel.admitCardLinearLayout.setOnClickListener(this)
        mainBinding.mainSidePanel.privayPolicyConstraintLayout.setOnClickListener(this)
        mainBinding.mainSidePanel.feedBackConstraintLayout.setOnClickListener(this)
        mainBinding.mainSidePanel.rattingConstraintLayout.setOnClickListener(this)
        mainBinding.mainSidePanel.shareConstraintLayout.setOnClickListener(this)
        mainBinding.latestJobLoadMore.setOnClickListener(this)
        mainBinding.resultLoadMore.setOnClickListener(this)
        mainBinding.admitCardLoadMore.setOnClickListener(this)
        mainBinding.backButton.setOnClickListener(this)

        mainBinding.mainProgressBar.visibility = View.VISIBLE

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("MainActivity", "getInstanceId failed",
                                task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result!!.token
                })


        initRecyclerView()
        latestJobDataModels.observe(this, {
            latestJobRecyclerAdapter.notifyDataSetChanged()
        })

        resultDataModels.observe(this, {
            resultRecyclerAdapter.notifyDataSetChanged()
        })

        admitCardDataModels.observe(this, {
            admitCardRecyclerAdapter.notifyDataSetChanged()
        })

        getLatestJobsFromServer("cat_list_data", 1, latestJobCount)
        getResultFromServer("cat_list_data", 2, resultCount)
        getAdmitCardFromServer("cat_list_data", 3, admitCardCount)

    }


    private fun isSidePanelOpen(): Boolean{
        val sidePanelMotionLayout: MotionLayout = mainBinding.mainSidePanel.sideExpendLayout
        return sidePanelMotionLayout.progress != 0.0f
    }

    private fun sidePanelTransitionStart(isTranslated: Boolean){
        val sidePanelMotionLayout: MotionLayout = mainBinding.mainSidePanel.sideExpendLayout
        if(isTranslated){
            sidePanelMotionLayout.transitionToStart()
        }else{
            sidePanelMotionLayout.transitionToEnd()
        }
    }

    private fun initRecyclerView(){
        latestJobRecyclerAdapter = MainActivityRecyclerAdapter(this)
        latestJobRecyclerAdapter.thisMainRecyclerType = MainActivityRecyclerAdapter.MainRecyclerViewType.LATEST_JOB;
        latestJobRecyclerAdapter.latestJobsArrayList = latestJobDataModels.value

        resultRecyclerAdapter = MainActivityRecyclerAdapter(this)
        resultRecyclerAdapter.thisMainRecyclerType = MainActivityRecyclerAdapter.MainRecyclerViewType.RESULT;
        resultRecyclerAdapter.resultArrayList = resultDataModels.value

        admitCardRecyclerAdapter = MainActivityRecyclerAdapter(this)
        admitCardRecyclerAdapter.thisMainRecyclerType = MainActivityRecyclerAdapter.MainRecyclerViewType.LATEST_JOB;
        admitCardRecyclerAdapter.admitCardArrayList = admitCardDataModels.value

//        set Layout Manager
        mainBinding.mainActivityLatestJobRecyclerView.layoutManager = LinearLayoutManager(this)
        mainBinding.mainActivityAdmitCardRecyclerView.layoutManager = LinearLayoutManager(this)
        mainBinding.mainActivityResultRecyclerView.layoutManager = LinearLayoutManager(this)
//        creating adapter for latest job, result, admit card
        mainBinding.mainActivityLatestJobRecyclerView.adapter = latestJobRecyclerAdapter
        mainBinding.mainActivityAdmitCardRecyclerView.adapter = admitCardRecyclerAdapter
        mainBinding.mainActivityResultRecyclerView.adapter = resultRecyclerAdapter

        changeActiveRecyclerVisibility()

        val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider)!!)
        mainBinding.mainActivityLatestJobRecyclerView.addItemDecoration(itemDecorator)
        mainBinding.mainActivityAdmitCardRecyclerView.addItemDecoration(itemDecorator)
        mainBinding.mainActivityResultRecyclerView.addItemDecoration(itemDecorator)
    }

    private fun changeActiveRecyclerVisibility(){
        if(activeRecyclerView == MainActivityRecyclerAdapter.MainRecyclerViewType.LATEST_JOB){
            mainBinding.latestLatestJobContainer.visibility = View.VISIBLE
            mainBinding.resultContainer.visibility = View.GONE
            mainBinding.admitCardContainer.visibility = View.GONE

//            changing text color
            mainBinding.mainSidePanel.resultContainerTextView.setTextColor(resources.getColor(R.color.white))
            mainBinding.mainSidePanel.latestJobContainerTextView.setTextColor(resources.getColor(R.color.background))
            mainBinding.mainSidePanel.admitCardContainerTextView.setTextColor(resources.getColor(R.color.white))

        }
        if(activeRecyclerView == MainActivityRecyclerAdapter.MainRecyclerViewType.RESULT){
            mainBinding.resultContainer.visibility = View.VISIBLE
            mainBinding.latestLatestJobContainer.visibility = View.GONE
            mainBinding.admitCardContainer.visibility = View.GONE

            //            changing text color
            mainBinding.mainSidePanel.resultContainerTextView.setTextColor(resources.getColor(R.color.background))
            mainBinding.mainSidePanel.latestJobContainerTextView.setTextColor(resources.getColor(R.color.white))
            mainBinding.mainSidePanel.admitCardContainerTextView.setTextColor(resources.getColor(R.color.white))
        }
        if(activeRecyclerView == MainActivityRecyclerAdapter.MainRecyclerViewType.ADMIT_CARD){
            mainBinding.admitCardContainer.visibility = View.VISIBLE
            mainBinding.resultContainer.visibility = View.GONE
            mainBinding.latestLatestJobContainer.visibility = View.GONE

            //            changing text color
            mainBinding.mainSidePanel.resultContainerTextView.setTextColor(resources.getColor(R.color.white))
            mainBinding.mainSidePanel.latestJobContainerTextView.setTextColor(resources.getColor(R.color.white))
            mainBinding.mainSidePanel.admitCardContainerTextView.setTextColor(resources.getColor(R.color.background))
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it){
                mainBinding.mainSidePanel.latestJobLinearLayout -> {
                    activeRecyclerView = MainActivityRecyclerAdapter.MainRecyclerViewType.LATEST_JOB
                }
                mainBinding.mainSidePanel.resultLinearLayout -> {
                    activeRecyclerView = MainActivityRecyclerAdapter.MainRecyclerViewType.RESULT
                }
                mainBinding.mainSidePanel.admitCardLinearLayout -> {
                    activeRecyclerView = MainActivityRecyclerAdapter.MainRecyclerViewType.ADMIT_CARD
                }
                mainBinding.mainSidePanel.privayPolicyConstraintLayout -> {
                    val privacyPolicyIntent: Intent = Intent(this, PrivacyPolicy::class.java)
                    startActivity(privacyPolicyIntent)
                }
                mainBinding.mainSidePanel.feedBackConstraintLayout -> {
                    val email = Intent(Intent.ACTION_SEND)
                    email.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("itandbisht@gmail.com"))
                    email.putExtra(Intent.EXTRA_SUBJECT, "My Feedback !!")
                    email.putExtra(Intent.EXTRA_TEXT, "Type your feedback here ")
                    email.type = "message/rfc822"
                    startActivity(Intent.createChooser(email, "Choose an Email client :"))
                }
                mainBinding.mainSidePanel.rattingConstraintLayout -> {
                    try {
                        startActivity(
                                Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=$packageName")
                                )
                        )
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                                Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                )
                        )
                    }
                }
                mainBinding.mainSidePanel.shareConstraintLayout -> {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "*/*"
//                        val imageUri = Uri.parse("android.resource://$packageName/drawable/share_app_image")

                        var imageUri: Uri? = null
                        try {
                            imageUri = Uri.parse(MediaStore.Images.Media.insertImage(this.contentResolver,
                                    BitmapFactory.decodeResource(resources, R.drawable.share_app_image), null, null))
                        } catch (e: NullPointerException) {
                        }

                        shareIntent.putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.app_name))
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        var shareMessage = "\n${resources.getString(R.string.app_name)}\n" +
                                "Hi Download this App to get the latest job, Result and admitCard " +
                                "I have already download this app and I love it !!  \n"
                        shareMessage = """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            
                            
                            """.trimIndent()
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "Send App"))
                    } catch (e: Exception) {
                        //e.toString();
                    }
                }
                mainBinding.latestJobLoadMore -> {
                    latestJobCount += 1
                    getLatestJobsFromServer(loadCount = latestJobCount)
                }
                mainBinding.resultLoadMore -> {
                    resultCount += 1
                    getResultFromServer(loadCount = resultCount)
                }
                mainBinding.admitCardLoadMore -> {
                    admitCardCount += 1
                    getAdmitCardFromServer(loadCount = admitCardCount)
                }

                mainBinding.backButton -> {
                    showExitDialog()
                }
            }
            changeActiveRecyclerVisibility()
        }
    }

    private fun showExitDialog(){
        val exitDialog: Dialog = Dialog(this)
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setCancelable(false);
        val exitDialogBinding: ExitDialogBinding = ExitDialogBinding.inflate(
                LayoutInflater.from(this)
        )
        exitDialog.setContentView(exitDialogBinding.root);
        exitDialogBinding.exitDialogOkTextViewBtn.setOnClickListener {
            exitDialog.dismiss()
            finish()
        }
        exitDialogBinding.exitDialogCancelTextViewBtn.setOnClickListener {
            exitDialog.dismiss()
        }
//        exitDialogBinding.exitDialogAdView.loadAd(exitDialogAdRequest)
        exitDialog.show()
    }

    fun getLatestJobsFromServer(
            category_type: String = "cat_list_data",
            cat_id: Int = 1,
            loadCount: Int = 1
    ) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        if(activeRecyclerView == MainActivityRecyclerAdapter.MainRecyclerViewType.LATEST_JOB){
            mainBinding.mainProgressBar.visibility = View.VISIBLE
        }

        // Request a string response from the provided URL.
        val stringRequestLatestJob = object : StringRequest(Request.Method.POST, webApi,
                Response.Listener<String> { it2 ->
                    val latestJobDataModel: LatestJobDataModel? = LatestJobStringToJson(it2)
                    latestJobDataModel?.let {
                        if (latestJobDataModels.value == null) {
                            latestJobDataModels.value = it
                        } else {
                            latestJobDataModels.value?.dataArrayList?.addAll(it.dataArrayList)
                        }
                        latestJobRecyclerAdapter.thisMainRecyclerType =
                                MainActivityRecyclerAdapter.MainRecyclerViewType.LATEST_JOB
                        latestJobRecyclerAdapter.latestJobsArrayList = latestJobDataModels.value
                        latestJobRecyclerAdapter.notifyDataSetChanged()
                        mainBinding.mainProgressBar.visibility = View.GONE
                    }
                }, Response.ErrorListener {
            Log.d("LatestJobsError", it.message.toString())
            mainBinding.mainProgressBar.visibility = View.GONE

        }) {
            override fun getParams(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap["action"] = category_type
                hashMap["cat_id"] = cat_id.toString()
                hashMap["position"] = loadCount.toString()
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

    fun getResultFromServer(
            category_type: String = "cat_list_data",
            cat_id: Int = 2,
            loadCount: Int = 1
    ) {
        // Instantiate the RequestQueue.
        val queueResult = Volley.newRequestQueue(this)

        if(activeRecyclerView == MainActivityRecyclerAdapter.MainRecyclerViewType.RESULT){
            mainBinding.mainProgressBar.visibility = View.VISIBLE
        }

        // Request a string response from the provided URL.
        val stringRequestResult = object : StringRequest(Request.Method.POST, webApi,
                Response.Listener<String> { it2 ->
                    val resultDataModel: ResultDataModel? = resultStringToJson(it2)
                    resultDataModel?.let {
                        if (resultDataModels.value == null) {
                            resultDataModels.value = it
                        } else {
                            resultDataModels.value?.dataArrayList?.addAll(it.dataArrayList)
                        }
                        resultRecyclerAdapter.thisMainRecyclerType =
                                MainActivityRecyclerAdapter.MainRecyclerViewType.RESULT
                        resultRecyclerAdapter.resultArrayList = resultDataModels.value
                        resultRecyclerAdapter.notifyDataSetChanged()
                        mainBinding.mainProgressBar.visibility = View.GONE

                    }
                }, Response.ErrorListener {
            Log.d("LatestJobsError", it.message.toString())
            mainBinding.mainProgressBar.visibility = View.GONE
        }) {
            override fun getParams(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap["action"] = category_type
                hashMap["cat_id"] = cat_id.toString()
                hashMap["position"] = loadCount.toString()
                return hashMap

            }

            override fun getHeaders(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap["Content-Type"] = "application/x-www-form-urlencoded"
                return hashMap
            }
        }
        queueResult.cache.clear()
        queueResult.add(stringRequestResult)
    }

    fun getAdmitCardFromServer(
            category_type: String = "cat_list_data",
            cat_id: Int = 3,
            loadCount: Int = 1
    ) {
        // Instantiate the RequestQueue.
        val queueAdmitCard = Volley.newRequestQueue(this)

        if(activeRecyclerView == MainActivityRecyclerAdapter.MainRecyclerViewType.ADMIT_CARD){
            mainBinding.mainProgressBar.visibility = View.VISIBLE
        }
        // Request a string response from the provided URL.
        val stringRequest = object : StringRequest(Request.Method.POST, webApi,
                Response.Listener<String> { it2 ->
                    val admitCardDataModel: AdmitCardDataModel? = admitCardStringToJson(it2)
                    admitCardDataModel?.let {
                        if (admitCardDataModels.value == null) {
                            admitCardDataModels.value = it
                        } else {
                            admitCardDataModels.value?.dataArrayList?.addAll(it.dataArrayList)
                        }
                        admitCardRecyclerAdapter.thisMainRecyclerType =
                                MainActivityRecyclerAdapter.MainRecyclerViewType.ADMIT_CARD
                        admitCardRecyclerAdapter.admitCardArrayList = admitCardDataModels.value
                        admitCardRecyclerAdapter.notifyDataSetChanged()
                        mainBinding.mainProgressBar.visibility = View.GONE
                    }
                }, Response.ErrorListener {
            Log.d("LatestJobsError", it.message.toString())
            mainBinding.mainProgressBar.visibility = View.GONE
        }) {
            override fun getParams(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap["action"] = category_type
                hashMap["cat_id"] = cat_id.toString()
                hashMap["position"] = loadCount.toString()
                return hashMap

            }

            override fun getHeaders(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap["Content-Type"] = "application/x-www-form-urlencoded"
                return hashMap
            }
        }

        queueAdmitCard.cache.clear()
        queueAdmitCard.add(stringRequest)
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    private fun sheduleInterstitialAds(){
        scheduler.scheduleAtFixedRate(Runnable {
            runOnUiThread {
                if (!isInterstitialAddVisibale) {
                    showInterstitial()
                }
            }
        }, 20, 60, TimeUnit.SECONDS)
    }

    private fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
                this, resources.getString(R.string.interstitial_unit_id), adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mInterstitialAd = null
                        mAdIsLoading = false
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                        mAdIsLoading = false
                    }
                }
        )
    }

    // Show the ad if it's ready. Otherwise toast and restart the game.
    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    isInterstitialAddVisibale = false
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is dismissed.
                    mInterstitialAd = null
                    isInterstitialAddVisibale = true
                }

            }
            mInterstitialAd?.show(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if(scheduler.isShutdown){
            sheduleInterstitialAds()
        }
    }

    override fun onPause() {
        super.onPause()
//        if(!scheduler.isShutdown){
//            scheduler.shutdown()
//        }
    }
}