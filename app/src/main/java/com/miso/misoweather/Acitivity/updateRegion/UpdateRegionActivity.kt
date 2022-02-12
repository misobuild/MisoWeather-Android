package com.miso.misoweather.Acitivity.updateRegion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.Acitivity.selectRegion.RecyclerRegionsAdapter
import com.miso.misoweather.Acitivity.selectRegion.RegionItem
import com.miso.misoweather.Acitivity.selectTown.SelectTownActivity
import com.miso.misoweather.databinding.ActivityUpdateRegionBinding
import java.lang.Exception

class UpdateRegionActivity : MisoActivity() {
    lateinit var binding: ActivityUpdateRegionBinding
    lateinit var gridAdapter: RecyclerRegionsAdapter
    lateinit var grid_region: RecyclerView
    lateinit var btn_back: ImageButton
    lateinit var btn_next: Button
    lateinit var currentRegion:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        setRecyclerRegions()
    }

    fun initializeViews() {
        currentRegion = intent.getStringExtra("region")?:getBigShortScale("bigScale")!!
        grid_region = binding.gridRegions
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        btn_back.setOnClickListener() {
            startActivity(Intent(this, ChatMainActivity::class.java))
            transferToBack()
            finish()
        }
        btn_next.setOnClickListener()
        {
            try {
                var bigScaleRegion = gridAdapter.getSelectedItemShortName()
                var intent =  Intent(this, ChatMainActivity::class.java)
                intent.putExtra("region", bigScaleRegion)
                startActivity(intent)
                transferToBack()
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setRecyclerRegions() {
        var regionItems = getRegionItems()
        var regionList = resources.getStringArray(R.array.regions)
        var index = regionList.indexOf(currentRegion)
        gridAdapter = RecyclerRegionsAdapter(this@UpdateRegionActivity, regionItems,index)
        grid_region.adapter = gridAdapter
        grid_region.layoutManager = GridLayoutManager(this, 4)
        val spaceDecoration = VerticalSpaceItemDecoration(30)
        grid_region.addItemDecoration(spaceDecoration)

    }

    fun getRegionItems(): ArrayList<RegionItem> {
        var regions = resources.getStringArray(R.array.regions)
        var regions_full = resources.getStringArray(R.array.regions_full)
        var regionItems: ArrayList<RegionItem> = ArrayList<RegionItem>()
        for (i: Int in 0..regions.size - 1) {
            var item: RegionItem = RegionItem()
            item.shortName = regions.get(i)
            item.longName = regions_full.get(i)
            regionItems.add(item)
        }

        return regionItems
    }
}