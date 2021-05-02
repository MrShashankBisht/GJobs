package com.iab.GJobs.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.iab.GJobs.DetailActivity
import com.iab.GJobs.data_model.AdmitCardDataModel
import com.iab.GJobs.data_model.LatestJobDataModel
import com.iab.GJobs.data_model.ResultDataModel
import com.iab.GJobs.databinding.MainRecyclerItemBinding
import java.lang.ref.WeakReference
import java.util.regex.Pattern

class MainActivityRecyclerAdapter(context: Context): RecyclerView.Adapter<MainActivityRecyclerAdapter.MainActivityRecyclerItemViewHolder>(){
    private var contextWeakReference: WeakReference<Context> = WeakReference(context)
    //this setter and getter is for just tutorial purpose
    var latestJobsArrayList: LatestJobDataModel? = null
    var resultArrayList: ResultDataModel? = null
    var admitCardArrayList: AdmitCardDataModel? = null

    enum class MainRecyclerViewType {NONE, LATEST_JOB, RESULT, ADMIT_CARD }
    var thisMainRecyclerType: MainRecyclerViewType = MainRecyclerViewType.NONE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivityRecyclerItemViewHolder {
        val layoutInflater = LayoutInflater.from(this.contextWeakReference.get())
        val itemBinding = MainRecyclerItemBinding.inflate(layoutInflater)
        val itemLayoutParam: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        itemBinding.root.layoutParams = itemLayoutParam
        return MainActivityRecyclerItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MainActivityRecyclerItemViewHolder, position: Int) {
        when (thisMainRecyclerType) {
            MainRecyclerViewType.ADMIT_CARD -> {
                admitCardArrayList?.let {
                    val name:String = it.dataArrayList[position].name
                    holder.itemViewBinding.mainRecyclerItemTitleText.text = name
                    val pattern: Pattern = Pattern.compile(" ")
                    val subName = name.split(pattern, 2)
                    holder.itemViewBinding.mainRecyclerItemSubTitle.text = subName[0]
                    if(it.dataArrayList[position].last_date != "" && it.dataArrayList[position].last_date != " "){
                        holder.itemViewBinding.mainRecyclerItemLastDateTextView.text = it.dataArrayList.get(position).last_date
                    }else{
                        holder.itemViewBinding.mainRecyclerItemLastDateTextView.text = "Date Not Available"
                    }
                }
            }
            MainRecyclerViewType.LATEST_JOB -> {
                latestJobsArrayList?.let {
                    val name:String = it.dataArrayList[position].name
                    holder.itemViewBinding.mainRecyclerItemTitleText.text = name
                    val pattern: Pattern = Pattern.compile(" ")
                    val subName = name.split(pattern, 2)
                    holder.itemViewBinding.mainRecyclerItemSubTitle.text = subName[0]
                    if(it.dataArrayList[position].last_date != "" && it.dataArrayList[position].last_date != " "){
                        holder.itemViewBinding.mainRecyclerItemLastDateTextView.text = it.dataArrayList.get(position).last_date
                    }else{
                        holder.itemViewBinding.mainRecyclerItemLastDateTextView.text = "Date Not Available"
                    }
                }
            }
            MainRecyclerViewType.RESULT -> {
                resultArrayList?.let {
                    val name:String = it.dataArrayList[position].name
                    holder.itemViewBinding.mainRecyclerItemTitleText.text = name
                    val pattern: Pattern = Pattern.compile(" ")
                    val subName = name.split(pattern, 2)
                    holder.itemViewBinding.mainRecyclerItemSubTitle.text = subName[0]
                    if(it.dataArrayList[position].last_date != "" && it.dataArrayList[position].last_date != " "){
                        holder.itemViewBinding.mainRecyclerItemLastDateTextView.text = it.dataArrayList.get(position).last_date
                    }else{
                        holder.itemViewBinding.mainRecyclerItemLastDateTextView.text = "Date Not Available"
                    }
                }
            }
            else -> {}
        }

        holder.itemViewBinding.root.setOnClickListener {
            when(thisMainRecyclerType){
                MainRecyclerViewType.LATEST_JOB -> {
                    latestJobsArrayList?.let {
                        val action:String = "cat_list_detail"
                        val id:String = "1"
                        val job_id:String = it.dataArrayList.get(position).id_string
                        val job_title = it.dataArrayList[position].name
                        callToDetail(action, id, job_id, job_title);
                    }
                }
                MainRecyclerViewType.RESULT -> {
                    resultArrayList?.let {
                        val action:String = "cat_list_detail"
                        val id:String = "2"
                        val job_id:String = it.dataArrayList.get(position).id_string
                        val job_title = it.dataArrayList[position].name
                        callToDetail(action, id, job_id, job_title);
                    }
                }
                MainRecyclerViewType.ADMIT_CARD -> {
                    admitCardArrayList?.let {
                        val action:String = "cat_list_detail"
                        val id:String = "3"
                        val job_id:String = it.dataArrayList.get(position).id_string
                        val job_title = it.dataArrayList[position].name
                        callToDetail(action, id, job_id, job_title);
                    }
                }
                else -> {}
            }
        }
    }

    override fun getItemCount(): Int {
        when (thisMainRecyclerType) {
            MainRecyclerViewType.ADMIT_CARD -> {
                return if(admitCardArrayList != null){
                    admitCardArrayList!!.dataArrayList.size
                }else{
                    0
                }
            }
            MainRecyclerViewType.LATEST_JOB -> {
                return if(latestJobsArrayList != null){
                    latestJobsArrayList!!.dataArrayList.size
                }else{
                    0
                }
            }
            MainRecyclerViewType.RESULT -> {
                return if(resultArrayList != null){
                    resultArrayList!!.dataArrayList.size
                }else{
                    0
                }
            }
            else -> {
                return 0;
            }
        }
    }

    class MainActivityRecyclerItemViewHolder(var itemViewBinding: MainRecyclerItemBinding): RecyclerView.ViewHolder(itemViewBinding.root){}


    fun callToDetail(action:String, id:String, job_id:String, job_title:String){
        contextWeakReference.get()?.let{
            val detailIntent = Intent(it, DetailActivity::class.java)
            detailIntent.putExtra("action", action)
            detailIntent.putExtra("id", id)
            detailIntent.putExtra("cat_id", job_id)
            detailIntent.putExtra("Job_title", job_title)
            it.startActivity(detailIntent)
        }
    }

}