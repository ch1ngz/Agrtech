package com.agrtech.farms

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrtech.R
import com.agrtech.maps.MapsActivity
import kotlinx.android.synthetic.main.activity_farms.farmsRecyclerView
import org.koin.android.ext.android.inject

class FarmsActivity : AppCompatActivity() {

    private val viewModel: FarmsViewModel by inject()

    private val farmsAdapter: FarmsAdapter by lazy {
        FarmsAdapter { MapsActivity.start(this, it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farms)

        with(farmsRecyclerView) {
            layoutManager = LinearLayoutManager(this@FarmsActivity)
            adapter = farmsAdapter
        }

        viewModel.farmsLiveData.observe(this) { farms ->
            farmsAdapter.setItems(farms)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFarms()
    }
}