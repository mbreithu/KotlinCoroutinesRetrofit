package com.chase.kotlincoroutines

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.chase.kotlincoroutines.adapters.PostAdapter
import com.chase.kotlincoroutines.model.Post
import com.chase.kotlincoroutines.network.RetrofitFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.toast
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = RetrofitFactory.makeRetrofitService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getPosts()
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { initRecyclerView(it) }
                    } else {
                        toast("Error network operation failed with ${response.code()}")
                    }
                }
            } catch (e: HttpException) {
                Log.e("REQUEST", "Exception ${e.message}")
            } catch (e: Throwable) {
                Log.e("REQUEST", "Ooops: Something else went wrong")
            }
        }
    }

    private fun initRecyclerView(list: List<Post>) {
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = PostAdapter(list, this)
    }
}
