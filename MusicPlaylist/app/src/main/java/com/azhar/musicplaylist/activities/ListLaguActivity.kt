package com.azhar.musicplaylist.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.azhar.musicplaylist.R
import com.azhar.musicplaylist.adapter.ListLaguAdapter
import com.azhar.musicplaylist.adapter.ListLaguAdapter.onSelectData
import com.azhar.musicplaylist.model.ModelListLagu
import com.azhar.musicplaylist.networking.Api
import kotlinx.android.synthetic.main.activity_list_lagu.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class ListLaguActivity : AppCompatActivity(), onSelectData {

    var listLaguAdapter: ListLaguAdapter? = null
    var progressDialog: ProgressDialog? = null
    var modelListLagu: MutableList<ModelListLagu> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_lagu)

        //set Transparent Statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Mohon Tunggu")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Sedang menampilkan data...")

        rvListMusic!!.setHasFixedSize(true)
        rvListMusic!!.setLayoutManager(LinearLayoutManager(this))

        //get data Music
        listMusic
    }

    private val listMusic: Unit
        private get() {
            progressDialog!!.show()
            AndroidNetworking.get(Api.ListMusic)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            try {
                                progressDialog!!.dismiss()
                                val playerArray = response.getJSONArray("post")
                                for (i in 0 until playerArray.length()) {
                                    if (i > 3) {
                                        val temp = playerArray.getJSONObject(i)
                                        val dataApi = ModelListLagu()
                                        dataApi.strId = temp.getString("id")
                                        dataApi.strCoverLagu = temp.getString("coverartikel")
                                        dataApi.strNamaBand = temp.getString("namaband")
                                        dataApi.strJudulMusic = temp.getString("judulmusic")
                                        modelListLagu.add(dataApi)
                                        showListMusic()
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Toast.makeText(this@ListLaguActivity,
                                        "Gagal menampilkan data!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(anError: ANError) {
                            progressDialog!!.dismiss()
                            Toast.makeText(this@ListLaguActivity,
                                    "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show()
                        }
                    })
        }

    private fun showListMusic() {
        listLaguAdapter = ListLaguAdapter(this@ListLaguActivity, modelListLagu, this)
        rvListMusic!!.adapter = listLaguAdapter
    }

    //send data to activity Detail Lagu
    override fun onSelected(modelListLagu: ModelListLagu) {
        val intent = Intent(this@ListLaguActivity, DetailLaguActivity::class.java)
        intent.putExtra("detailLagu", modelListLagu)
        startActivity(intent)
    }

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }
}