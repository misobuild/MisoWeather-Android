package com.miso.misoweather.common

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.miso.misoweather.R

open class MisoActivity :AppCompatActivity() {
    companion object
    {
        val MISOWEATHER_BASE_URL:String = "http://3.35.55.100/"
    }
    lateinit var prefs: SharedPreferences
    lateinit var pairList: ArrayList<Pair<String,String>>
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("misoweather", Context.MODE_PRIVATE)
        pairList = ArrayList()

    }
    open fun doBack()
    {

    }

    override fun onBackPressed() {
        doBack()
    }
    fun getBigShortScale(bigScale:String):String
    {
        try {
            val regionList = resources.getStringArray(R.array.regions_full)
            val index = regionList.indexOf(bigScale)
            val regionSmallList = resources.getStringArray(R.array.regions)

            return regionSmallList.get(index)
        }catch (e:Exception)
        {
            return ""
        }
    }

    fun transferToBack(){
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }

    fun transferToNext(){
        overridePendingTransition(R.anim.slide_right_exit,R.anim.slide_right_enter)
    }

    fun overFromUnder()
    {
        overridePendingTransition(R.anim.slide_bottom_exit,R.anim.stay)
    }

    fun sinkFromTop()
    {
        overridePendingTransition(0,R.anim.slide_bottom_enter)
    }

    fun addPreferencePair(first:String,second:String)
    {
        val pair = Pair(first,second)
        pairList.add(pair)
    }

    fun removePreference(pref:String)
    {
        val pair = Pair(pref,"")
        pairList.add(pair)
    }

    fun removePreference(vararg pref:String)
    {
       for(i in 0..pref.size-1)
       {
           val pair = Pair(pref[i],"")
           pairList.add(pair)
       }
    }
    fun getPreference(pref:String):String?
    {
        return prefs!!.getString(pref,"")
    }
    fun savePreferences()
    {
       var edit = prefs!!.edit()
        for(i in 0..pairList.size-1)
        {
            val pair = pairList.get(i)
            edit.putString(pair.first,pair.second)
        }
        edit.apply()
        pairList.clear()
    }
}