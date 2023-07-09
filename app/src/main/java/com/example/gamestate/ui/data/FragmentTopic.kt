package com.example.gamestate.ui.data

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.util.TimeUtils.formatDuration
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestate.R
import com.example.gamestate.ui.RecyclerViewUpdateListener
import com.example.gamestate.ui.TopicActivity
import com.example.gamestate.ui.data.Library.RecViewLibraryAdapter
import com.example.gamestate.ui.data.Topic.RecViewTopicAdapter
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentTopic.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentTopic : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var recyclerViewUpdateListener: RecyclerViewUpdateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RecyclerViewUpdateListener) {
            recyclerViewUpdateListener = context
        } else {
            throw RuntimeException("$context must implement RecyclerViewUpdateListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        recyclerViewUpdateListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_topic, container, false)
        // Inflate the layout for this fragment
        val activity: Activity? = activity
        val sharedPreferences =requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("userid","")
        val token = sharedPreferences.getString("token","")
        val userIDTopic = arguments?.getString("userID")
        val topicid = arguments?.getString("topicid")
        val username = arguments?.getString("username")
        val gameID = arguments?.getInt("gameID")
        val commentsList= arguments?.getSerializable("arraylistcomment")as? ArrayList<Comment>
        val commentbutton: ImageButton = view.findViewById(R.id.commentbutton)
        val comment: EditText = view.findViewById(R.id.editcomment)
        val serverIP = resources.getString(R.string.server_ip)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverIP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RetroFitService::class.java)

        val requestBody = JsonObject()
        fun commentinit(){
            val commentText = comment.text.toString()
            requestBody.addProperty("text", commentText)
            requestBody.addProperty("topic_id", topicid)
            requestBody.addProperty("user_id",userid)


            val call = service.createcomment(token!!, requestBody)
            val r1 = Runnable {
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            val msm = responseJson.getString("message")
                            if (responseJson.getInt("status") == 200)
                            {
                                commentsList?.add(Comment(
                                    commentText,username!!,"Just Now")
                                )
                                updateMainActivityRecyclerView(commentsList!!)

                                Toast.makeText(activity, msm, Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Toast.makeText(activity, msm, Toast.LENGTH_SHORT).show()
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

        fun sendPushNotification(pushToken: String) {
            val server = "https://fcm.googleapis.com/"
            val retrofit = Retrofit.Builder()
                .baseUrl(server)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetroFitService::class.java)

            val requestBody = JsonObject()
            requestBody.addProperty("to", pushToken)

            val notificationObject = JsonObject()
            notificationObject.addProperty("body", "$username commented on your topic")
            notificationObject.addProperty("title", "Comment")

            requestBody.add("notification", notificationObject)

            val dataObject = JsonObject()
            dataObject.addProperty("body", "$username commented on your topic")
            dataObject.addProperty("title", "Comment")
            dataObject.addProperty("topicID", topicid)
            dataObject.addProperty("gameID", gameID)

            requestBody.add("data", dataObject)

            val call = service.sendPushNotification(requestBody, "application/json", resources.getString(R.string.pushNotificationKey))
            val r = Runnable {call.execute()}
            val t = Thread(r)
            t.start()
        }

        fun getPushToken() {
            val server = resources.getString(R.string.server_ip)
            val retrofit = Retrofit.Builder()
                .baseUrl(server)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetroFitService::class.java)
            val token = sharedPreferences.getString("token","")
            val call = service.searchUserByID(token!!, userIDTopic!!)
            val r = Runnable {
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val res = response.body()?.string()
                            val responseJson = JSONObject(res!!)
                            val msm = responseJson.getString("message")
                            if (responseJson.getInt("status") == 200) {
                                val pushToken =
                                    responseJson.getJSONObject("message").getJSONObject("user")
                                        .getString("pushToken")
                                sendPushNotification(pushToken)
                            } else {
                                Toast.makeText(activity, msm, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(activity, "Network Failure", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            val t = Thread(r)
            t.start()
        }

        commentbutton.setOnClickListener {
            commentinit()
            getPushToken()
            comment.text.clear()
        }
        return view
    }
    private fun updateMainActivityRecyclerView(dataList: ArrayList<Comment>) {
        recyclerViewUpdateListener?.updateRecyclerView(dataList)
    }
    fun formatDuration(durationMillis: Long): String {
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / (1000 * 60)) % 60
        val hours = (durationMillis / (1000 * 60 * 60)) % 24
        val days = durationMillis / (1000 * 60 * 60 * 24)

        return when {
            days > 0 -> "${days}d"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            seconds > 0 -> "${seconds}s"
            else -> "Just now"
        }
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