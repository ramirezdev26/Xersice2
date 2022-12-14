package com.example.xersice2

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.xersice2.LoginActivity2.Companion.usermail
import com.example.xersice2.Utility.animateViewFloat
import com.example.xersice2.Utility.animateViewInt
import com.example.xersice2.Utility.getSecFromWatch
import com.example.xersice2.Utility.setHeightLinearLayout
import com.google.android.material.navigation.NavigationView
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import me.tankery.lib.circularseekbar.CircularSeekBar
import me.tankery.lib.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout

    private lateinit var swIntervalMode: Switch
    private lateinit var swChallenges: Switch
    private lateinit var swVolumes: Switch

    private lateinit var npChallengeDistance: NumberPicker
    private lateinit var npChallengeDurationHH: NumberPicker
    private lateinit var npChallengeDurationMM: NumberPicker
    private lateinit var npChallengeDurationSS: NumberPicker

    private var challengeDistance: Float = 0f
    private var challengeDuration: Int = 0

    private lateinit var tvChrono: TextView

    private lateinit var csbRunWalk: CircularSeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolBar()
        initObjects()
        initNavigationView()

    }

    override fun onBackPressed() {
        // super.onBackPressed()

        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)
        else
            signOut()
    }

    private fun initToolBar(){
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val toogle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title,
            R.string.navigation_drawer_close)

        drawer.addDrawerListener(toogle)

        toogle.syncState()


    }

    private fun initNavigationView(){
        var navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        var headerView: View = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false)
        navigationView.removeHeaderView(headerView)
        navigationView.addHeaderView(headerView)

        var tvUser: TextView = headerView.findViewById(R.id.tvUser)
        tvUser.text = usermail
    }

    private fun initStopWatch() {
        tvChrono.text = getString(R.string.init_stop_watch_value)
    }

    private fun initObjects(){
        tvChrono = findViewById(R.id.tvChrono)
        tvChrono.setTextColor(ContextCompat.getColor(this, R.color.white))

        val lyMap = findViewById<LinearLayout>(R.id.lyMap)
        val lyFragmentMap = findViewById<LinearLayout>(R.id.lyFragmentMap)
        val lyIntervalModeSpace = findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
        val lyIntervalMode = findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyChallengesSpace = findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChallenges = findViewById<LinearLayout>(R.id.lyChallenges)
        val lySettingsVolumesSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
        val lySettingsVolumes = findViewById<LinearLayout>(R.id.lySettingsVolumes)
        val lySoftTrack = findViewById<LinearLayout>(R.id.lySoftTrack)
        val lySoftVolume = findViewById<LinearLayout>(R.id.lySoftVolume)

        setHeightLinearLayout(lyMap, 0)
        setHeightLinearLayout(lyIntervalModeSpace, 0)
        setHeightLinearLayout(lyChallengesSpace, 0)
        setHeightLinearLayout(lySettingsVolumesSpace, 0)
        setHeightLinearLayout(lySoftTrack, 0)
        setHeightLinearLayout(lySoftVolume, 0)

        lyFragmentMap.translationY = -300f
        lyIntervalMode.translationY = -300f
        lyChallenges.translationY = -300f
        lySettingsVolumes.translationY = -300f

        swIntervalMode = findViewById(R.id.swIntervalMode)
        swChallenges = findViewById(R.id.swChallenges)
        swVolumes = findViewById(R.id.swVolumes)

        csbRunWalk = findViewById(R.id.csbRunWalk)

        npChallengeDistance = findViewById(R.id.npChallengeDistance)
        npChallengeDurationHH = findViewById(R.id.npChallengeDurationHH)
        npChallengeDurationMM = findViewById(R.id.npChallengeDurationMM)
        npChallengeDurationSS = findViewById(R.id.npChallengeDurationSS)

        csbRunWalk.setOnSeekBarChangeListener(object : OnCircularSeekBarChangeListener {
            override fun onProgressChanged(circularSeekBar: CircularSeekBar?, progress: Float, fromUser: Boolean) {

                var STEPS_UX: Int = 15
                var set: Int = 0
                var p = progress.toInt()

                if (p%STEPS_UX != 0){
                    while (p >= 60) p -= 60
                    while (p >= STEPS_UX) p -= STEPS_UX
                    if (STEPS_UX - p > STEPS_UX/2) set = -1 * p
                    else set = STEPS_UX - p

                    csbRunWalk.progress = csbRunWalk.progress + set
                }
            }
            override fun onStopTrackingTouch(seekBar: CircularSeekBar) {
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar) {
            }
        })
    }

    fun callSignOut (view: View) {
        signOut()
    }
    private fun signOut(){
        usermail = ""

        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity2::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.nav_item_record -> callRecordActivity()
            R.id.nav_item_signout -> signOut()
        }

        drawer.closeDrawer((GravityCompat.START))

        return true
    }
    private fun callRecordActivity(){
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }

    fun inflateIntervalMode(v: View){
        val lyIntervalMode = findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyIntervalModeSpace = findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
        val lySoftTrack = findViewById<LinearLayout>(R.id.lySoftTrack)
        val lySoftVolume = findViewById<LinearLayout>(R.id.lySoftVolume)
        val tvRounds = findViewById<TextView>(R.id.tvRounds)

        if (swIntervalMode.isChecked){
            animateViewInt(swIntervalMode, "textColor", ContextCompat.getColor(this, R.color.orange), 500)
            setHeightLinearLayout(lyIntervalModeSpace, 600)
            animateViewFloat(lyIntervalMode, "translationY", 0f, 500)
            animateViewFloat(tvChrono, "translationX", -110f, 500)
            tvRounds.setText(R.string.rounds)
            animateViewInt(tvRounds, "textColor", ContextCompat.getColor(this, R.color.white), 500)

            setHeightLinearLayout(lySoftTrack, 120)
            setHeightLinearLayout(lySoftVolume, 120)
            if (swVolumes.isChecked){
                var lySettingsVolumesSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
                setHeightLinearLayout(lySettingsVolumesSpace, 600)
            }
        }else{
            swIntervalMode.setTextColor(ContextCompat.getColor(this, R.color.white))
            setHeightLinearLayout(lyIntervalModeSpace, 0)
            lyIntervalMode.translationY = -200f
            animateViewFloat(tvChrono, "trnaslationX", 0f, 500)
            tvRounds.text = ""
            setHeightLinearLayout(lySoftTrack, 0)
            setHeightLinearLayout(lySoftVolume, 0)
            if (swVolumes.isChecked){
                var lySettingsVolumesSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
                setHeightLinearLayout(lySettingsVolumesSpace, 400)
            }

        }
    }

    fun inflateChallenges(v: View){
        val lyChallengesSpace = findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChalleges = findViewById<LinearLayout>(R.id.lyChallenges)
        if(swChallenges.isChecked){
            animateViewInt(swChallenges, "textColor", ContextCompat.getColor(this, R.color.orange), 500)
            setHeightLinearLayout(lyChallengesSpace, 750)
            animateViewFloat(lyChalleges, "translationY", 0f, 500)
        }
        else{
            swChallenges.setTextColor(ContextCompat.getColor(this, R.color.white))
            setHeightLinearLayout(lyChallengesSpace, 0)
            lyChalleges.translationY = -300f

            challengeDistance = 0f
            challengeDuration = 0
        }
    }

    fun showDuration(v: View){
        showChallenge("duration")
    }
    fun showDistance(v:View){
        showChallenge("distance")
    }
    private fun showChallenge(option: String){
        var lyChallenfeDuration = findViewById<LinearLayout>(R.id.lyChallengeDuration)
        var lyChallenfeDistance = findViewById<LinearLayout>(R.id.lyChallengeDistance)
        var tvChallengeDuration = findViewById<TextView>(R.id.tvChallengeDuration)
        var tvChallengeDistance = findViewById<TextView>(R.id.tvChallengeDistance)

        when (option){
            "duration" -> {
                lyChallenfeDuration.translationZ = 5f
                lyChallenfeDistance.translationZ = 0f

                tvChallengeDuration.setTextColor(ContextCompat.getColor(this, R.color.orange))
                tvChallengeDuration.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_dark))

                tvChallengeDistance.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvChallengeDistance.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_medium))

                challengeDistance = 0f
                getChallengeDuration(npChallengeDurationHH.value, npChallengeDurationMM.value, npChallengeDurationSS.value)
            }
            "distance" -> {
                lyChallenfeDuration.translationZ = 0f
                lyChallenfeDistance.translationZ = 5f

                tvChallengeDuration.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvChallengeDuration.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_medium))

                tvChallengeDistance.setTextColor(ContextCompat.getColor(this, R.color.orange))
                tvChallengeDistance.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_dark))

                challengeDuration = 0
                challengeDistance = npChallengeDistance.value.toFloat()
            }
        }
    }

    private fun getChallengeDuration(hh: Int, mm: Int, ss:Int){
        var hours: String = hh.toString()
        if (hh<10) hours = "0"+hours
        var minutes: String = mm.toString()
        if (mm<10) minutes = "0"+minutes
        var seconds: String = ss.toString()
        if (ss<10) seconds = "0"+seconds

        challengeDuration = getSecFromWatch("${hours}:${minutes}:${seconds}")
    }

    fun inflateVolumes(v: View){
        val lySettingsVolumesSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
        val lySettingsVolumes = findViewById<LinearLayout>(R.id.lySettingsVolumes)

        if(swVolumes.isChecked){
            animateViewInt(swVolumes, "textColor", ContextCompat.getColor(this, R.color.orange), 500)

            var value = 400
            if(swIntervalMode.isChecked) value = 600

            setHeightLinearLayout(lySettingsVolumesSpace, value)
            animateViewFloat(lySettingsVolumes, "translationY", 0f, 500)
        }
        else{
            swVolumes.setTextColor(ContextCompat.getColor(this, R.color.white))
            setHeightLinearLayout(lySettingsVolumesSpace, 0)
            lySettingsVolumes.translationY = -300f
        }
    }
}