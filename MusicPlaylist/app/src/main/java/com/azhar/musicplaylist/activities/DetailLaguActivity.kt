package com.azhar.musicplaylist.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.azhar.musicplaylist.R
import com.azhar.musicplaylist.model.ModelListLagu
import com.azhar.musicplaylist.networking.Api
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_detail_lagu.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class DetailLaguActivity : AppCompatActivity() {

    var idLagu: String? = null
    var modelListLagu: ModelListLagu? = null
    var progressDialog: ProgressDialog? = null
    var mHandler: Handler? = null
    var mRunnable: Runnable? = null
    var rotate: RotateAnimation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_lagu)

        //set Transparent Statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        toolbar_detail.setTitle(null)
        setSupportActionBar(toolbar_detail)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Mohon Tunggu")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Sedang menampilkan data...")

        mHandler = Handler()

        modelListLagu = intent.getSerializableExtra("detailLagu") as ModelListLagu
        if (modelListLagu != null) {
            idLagu = modelListLagu!!.strId

            //Get image source
            Glide.with(this)
                    .load(modelListLagu!!.strCoverLagu)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgCover)

            imgPause.setVisibility(View.GONE)
            imgPlay.setVisibility(View.VISIBLE)

            //Method get data
            detailLagu
        }
    }

    private val detailLagu: Unit
        private get() {
            progressDialog!!.show()
            AndroidNetworking.get(Api.DetailMusic)
                    .addPathParameter("id", idLagu)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            try {
                                progressDialog!!.dismiss()
                                val playerArray = response.getJSONArray("data")
                                for (i in 0 until playerArray.length()) {

                                    val temp = playerArray.getJSONObject(i)

                                    val judulMusic = temp.getString("judulmusic")
                                    tvTitleMusic!!.text = judulMusic

                                    val namaBand = temp.getString("namaband")
                                    tvBand!!.text = namaBand

                                    val urlMusic = temp.getString("linkmp3")
                                    val mediaPlayer = MediaPlayer()

                                    imgPlay!!.setOnClickListener {

                                        //set Rotate Cover Album
                                        rotate = RotateAnimation(0F, 360F,
                                                Animation.RELATIVE_TO_SELF, 0.5f,
                                                Animation.RELATIVE_TO_SELF, 0.5f)
                                        rotate!!.duration = 15000
                                        rotate!!.interpolator = LinearInterpolator()
                                        rotate!!.repeatCount = Animation.INFINITE

                                        imgCover!!.startAnimation(rotate)

                                        try {
                                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                                            mediaPlayer.setDataSource(urlMusic)
                                            mediaPlayer.prepare()
                                            mediaPlayer.start()
                                        } catch (e: IOException) {
                                            e.printStackTrace()
                                        }

                                        imgPlay!!.visibility = View.GONE
                                        imgPause!!.visibility = View.VISIBLE
                                        seekBar!!.maxProgress = mediaPlayer.duration / 1000

                                        mRunnable = Runnable {
                                            val mCurrentPosition = mediaPlayer.currentPosition / 1000
                                            val duration = mediaPlayer.duration
                                            @SuppressLint("DefaultLocale") val time = String.format("%02d min, %02d sec",
                                                    TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
                                                    TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
                                            )
                                            seekBar!!.progress = mCurrentPosition
                                            mHandler!!.postDelayed(mRunnable, 1000)
                                            tvTime!!.text = time
                                        }
                                        mHandler!!.postDelayed(mRunnable, 1000)
                                    }
                                    imgPause!!.setOnClickListener {
                                        rotate!!.cancel()
                                        mediaPlayer.stop()
                                        mediaPlayer.reset()
                                        imgPlay!!.visibility = View.VISIBLE
                                        imgPause!!.visibility = View.GONE
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Toast.makeText(this@DetailLaguActivity,
                                        "Gagal menampilkan data!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(anError: ANError) {
                            progressDialog!!.dismiss()
                            Toast.makeText(this@DetailLaguActivity,
                                    "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show()
                        }
                    })
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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