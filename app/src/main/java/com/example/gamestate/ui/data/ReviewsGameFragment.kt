package com.example.gamestate.ui.data

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gamestate.R
import com.example.gamestate.ui.data.Game.RecViewReviewsGameAdapter
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

/**
 * A fragment representing a list of Items.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class ReviewsGameFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: RecViewReviewsGameAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reviews_game, container, false)
        // Inflate the layout for this fragment
        val activity: Activity? = activity
        val gameID = arguments?.getInt("gameid")
        val token = arguments?.getString("token")
        val recyclerView = view.findViewById<RecyclerView>(R.id.game_reviews_recyclerview)
        val serverIP = resources.getString(R.string.server_ip)
        val linearLayoutManager = object : LinearLayoutManager(activity) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(serverIP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RetroFitService::class.java)
        val mainHandler = Handler(Looper.getMainLooper())
        fun reviewsinit(){
            val callReviews = service.searchReviewsbyGameID(token!!,gameID!!)
            val r1 = Runnable {
                callReviews.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            if (responseJson.getInt("status") == 200)
                            {
                                val reviewsArray = responseJson.getJSONArray("reviewsgame")
                                val reviewstitleList = ArrayList<String>()
                                //val reviewsImageList = ArrayList<String>()
                                val reviewsratingList = ArrayList<Int>()
                                for(i in 0 until reviewsArray.length())
                                {
                                    reviewstitleList.add(reviewsArray.getJSONObject(i).getString("title"))
                                    reviewsratingList.add(reviewsArray.getJSONObject(i).getInt("rating"))

                                }
                                mainHandler.post {
                                    adapter = RecViewReviewsGameAdapter(reviewstitleList,reviewsratingList)
                                    recyclerView.adapter = adapter
                                    recyclerView.layoutManager = linearLayoutManager
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(activity, "Network Failure", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            val t1 = Thread(r1)
            t1.start()
        }
        reviewsinit()

        return view
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentTopic.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentTopic().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}