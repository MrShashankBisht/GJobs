package com.iab.GJobs.data_model

data class BaseDataClass(
        val id_string: String,
        val name: String,
        val last_date:String
)

data class LatestJobDataModel (
        val dataArrayList: ArrayList<BaseDataClass>,
        val statusCode : String,
        val statusMessage : String
    )


data class ResultDataModel (
    val dataArrayList: ArrayList<BaseDataClass>,
    val statusCode : String,
    val statusMessage : String
)

data class AdmitCardDataModel (
    val dataArrayList: ArrayList<BaseDataClass>,
    val statusCode : String,
    val statusMessage : String
)

data class DetailResponseDataModel(
    val title:String,
    val description:String,
    val statuscode:String,
    val statusmessage:String
)
