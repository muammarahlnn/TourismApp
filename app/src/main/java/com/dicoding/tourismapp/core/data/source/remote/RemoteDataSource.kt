package com.dicoding.tourismapp.core.data.source.remote

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.tourismapp.core.data.source.remote.network.ApiResponse
import com.dicoding.tourismapp.core.data.source.remote.response.TourismResponse
import com.dicoding.tourismapp.core.utils.JsonHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.json.JSONException
<<<<<<< HEAD
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSource private constructor(private val apiService: ApiService) {

    fun getAllTourism(): Flowable<ApiResponse<List<TourismResponse>>> {
        val resultData = PublishSubject.create<ApiResponse<List<TourismResponse>>>()

        //get data from remote api
        val client = apiService.getList()
        client
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .subscribe({ response ->
                val dataArray = response.places
                resultData.onNext(
                    if (dataArray.isNotEmpty()) ApiResponse.Success(dataArray)
                    else ApiResponse.Empty
                )
            }, { error ->
                resultData.onNext(ApiResponse.Error(error.message.toString()))
                Log.e("RemoteDataSource", error.toString())
            })

        return resultData.toFlowable(BackpressureStrategy.BUFFER)
    }
=======
>>>>>>> parent of 1941414 (implement retrofit)

class RemoteDataSource private constructor(private val jsonHelper: JsonHelper) {
    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(helper: JsonHelper): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource(helper)
            }
    }

    fun getAllTourism(): LiveData<ApiResponse<List<TourismResponse>>> {
        val resultData = MutableLiveData<ApiResponse<List<TourismResponse>>>()

        //get data from local json
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            try {
                val dataArray = jsonHelper.loadData()
                if (dataArray.isNotEmpty()) {
                    resultData.value = ApiResponse.Success(dataArray)
                } else {
                    resultData.value = ApiResponse.Empty
                }
            } catch (e: JSONException){
                resultData.value = ApiResponse.Error(e.toString())
                Log.e("RemoteDataSource", e.toString())
            }
        }, 2000)

        return resultData
    }
}

