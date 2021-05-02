package com.iab.GJobs.util

import com.iab.GJobs.data_model.*
import org.json.JSONArray
import org.json.JSONException

import org.json.JSONObject




fun LatestJobStringToJson(jsonString: String): LatestJobDataModel?{
    if(jsonString.equals("")){
        return null
    }else{
        try {
            val jSONObject = JSONObject(jsonString)
            if (jSONObject.getString("statuscode") == "1") {
                val baseDataClasses: ArrayList<BaseDataClass> = ArrayList()
                val data: JSONArray = jSONObject.getJSONArray("data")
                for(i in 0 until data.length()){
                    val jsonObject2:JSONObject = data.getJSONObject(i)
                    val baseDataClass:BaseDataClass = BaseDataClass(jsonObject2.getString("id"),
                    jsonObject2.getString("name"),
                    jsonObject2.getString("last_date"))
                    baseDataClasses.add(baseDataClass)
                }
                return LatestJobDataModel(baseDataClasses, jSONObject.getString("statuscode"), jSONObject.getString("statusmessage"))
            }else{
                return null
            }
        } catch (jSONException: JSONException) {
            return null
        }
    }
}


fun resultStringToJson(jsonString: String): ResultDataModel?{
    if(jsonString.equals("")){
        return null
    }else{
        try {
            val jSONObject = JSONObject(jsonString)
            if (jSONObject.getString("statuscode") == "1") {
                val baseDataClasses: ArrayList<BaseDataClass> = ArrayList()
                val data: JSONArray = jSONObject.getJSONArray("data")
                for(i in 0 until data.length()){
                    val jsonObject2:JSONObject = data.getJSONObject(i)
                    val baseDataClass:BaseDataClass = BaseDataClass(jsonObject2.getString("id"),
                        jsonObject2.getString("name"),
                        jsonObject2.getString("last_date"))
                    baseDataClasses.add(baseDataClass)
                }
                return ResultDataModel(baseDataClasses, jSONObject.getString("statuscode"), jSONObject.getString("statusmessage"))
            }else{
                return null
            }
        } catch (jSONException: JSONException) {
            return null
        }
    }
}

fun admitCardStringToJson(jsonString: String): AdmitCardDataModel?{
    if(jsonString.equals("")){
        return null
    }else{
        try {
            val jSONObject = JSONObject(jsonString)
            if (jSONObject.getString("statuscode") == "1") {
                val baseDataClasses: ArrayList<BaseDataClass> = ArrayList()
                val data: JSONArray = jSONObject.getJSONArray("data")
                for(i in 0 until data.length()){
                    val jsonObject2:JSONObject = data.getJSONObject(i)
                    val baseDataClass:BaseDataClass = BaseDataClass(jsonObject2.getString("id"),
                        jsonObject2.getString("name"),
                        jsonObject2.getString("last_date"))
                    baseDataClasses.add(baseDataClass)
                }
                return AdmitCardDataModel(baseDataClasses, jSONObject.getString("statuscode"), jSONObject.getString("statusmessage"))
            }else{
                return null
            }
        } catch (jSONException: JSONException) {
            return null
        }
    }
}

fun getDetailDataFromServer(jsonString: String): DetailResponseDataModel?{
    if(jsonString == ""){
        return null
    }else{
        return try {
            val jSONObject = JSONObject(jsonString)
            if (jSONObject.getString("statuscode") == "1") {
                val data: JSONObject = jSONObject.getJSONObject("data")
                DetailResponseDataModel(
                    data.getString("title"),
                    data.getString("description"),
                    jSONObject.getString("statuscode"),
                    jSONObject.getString("statusmessage"))
            }else{
                null
            }
        } catch (jSONException: JSONException) {
            null
        }
    }
}
