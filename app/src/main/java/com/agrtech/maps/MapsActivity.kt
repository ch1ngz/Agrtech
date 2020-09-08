package com.agrtech.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.agrtech.R
import com.agrtech.farms.Farm
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_maps.actionTextView
import kotlinx.android.synthetic.main.activity_maps.overlayView
import kotlinx.android.synthetic.main.activity_maps.seekGroup
import kotlinx.android.synthetic.main.activity_maps.toolbar
import kotlinx.android.synthetic.main.activity_maps.wateringSeekBar
import kotlinx.android.synthetic.main.activity_maps.wateringTextView
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val EXTRA_FARM = "EXTRA_FARM"
        private const val DEFAULT_ZOOM_VALUE = 16.5F
        private const val POLYLINE_WIDTH = 8F
        private const val MINIMAL_POLYGON_POINTS = 3

        fun start(context: Context, farm: Farm) {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra(EXTRA_FARM, farm)
            context.startActivity(intent)
        }
    }

    private lateinit var googleMap: GoogleMap

    private val farm: Farm by lazy {
        intent.getParcelableExtra(EXTRA_FARM) as Farm
    }

    private val viewModel: MapsViewModel by viewModel()

    private val farmBounds = mutableListOf<LatLng>()
    private var farmPolygon: Polygon? = null
    private var farmPolyline: Polyline? = null

    private var level: WateringLevel by Delegates.observable(WateringLevel.LOW) { _, _, newValue ->
        wateringSeekBar.progress = newValue.intValue
        wateringTextView.text = getString(R.string.maps_watering_level_format, newValue.intValue)

        val color = WateringLevel.generateColor(newValue)
        farmPolygon?.fillColor = ContextCompat.getColor(this, color)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        toolbar.title = farm.name
        toolbar.inflateMenu(R.menu.maps)
        toolbar.setOnMenuItemClickListener { onMenuClicked(it) }
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.menu.findItem(R.id.action_finish).isVisible = false

        wateringTextView.text = getString(R.string.maps_watering_level_format, WateringLevel.LOW.intValue)
        wateringSeekBar.setOnSeekBarChangeListener(getSeekBarChangeListener())

        actionTextView.clipToOutline = true
        actionTextView.setOnClickListener {
            overlayView.isVisible = !overlayView.isVisible
            val isActive = overlayView.isVisible

            setActiveMode(isActive)
        }
        overlayView.setOnTouchListener { _, event -> drawPoints(event) }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

        val target = CameraUpdateFactory.newLatLngZoom(farm.location, DEFAULT_ZOOM_VALUE)
        googleMap.moveCamera(target)

        farm.areas.forEach { area ->
            val rectOptions = PolygonOptions()
            rectOptions.addAll(area.points)
            rectOptions.strokeWidth(0F)
            val color = WateringLevel.generateColor(area.wateringLevel)
            rectOptions.fillColor(ContextCompat.getColor(this, color))
            googleMap.addPolygon(rectOptions)
        }
    }

    private fun setActiveMode(isActive: Boolean) {
        if (isActive) {
            actionTextView.setText(R.string.maps_finish)
            toolbar.setTitle(R.string.maps_drawing_mode)
            clearMap()
        } else {
            actionTextView.setText(R.string.maps_draw)
            toolbar.title = farm.name
            drawPolygon()
        }
    }

    private fun onMenuClicked(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_finish -> {
            viewModel.saveArea(farm.farmId, farmBounds, level)
            finish()
            true
        }
        else -> false
    }

    private fun getSeekBarChangeListener() = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            level = WateringLevel.parseProgress(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    private fun toggleViews(isFinishing: Boolean = true) {
        toolbar.menu.findItem(R.id.action_finish).isVisible = isFinishing
        seekGroup.isVisible = isFinishing
        actionTextView.isVisible = !isFinishing
    }

    private fun drawPoints(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val xCoordinate = x.roundToInt()
        val yCoordinate = y.roundToInt()
        val point = Point(xCoordinate, yCoordinate)
        val latLng = googleMap.projection.fromScreenLocation(point)
        val latitude = latLng.latitude
        val longitude = latLng.longitude

        if (event.action == MotionEvent.ACTION_DOWN) {
            val farmPoint = LatLng(latitude, longitude)
            farmBounds.add(farmPoint)
            drawPolyline()
        }

        return true
    }

    private fun drawPolyline() {
        if (farmPolyline == null) {
            val color = ContextCompat.getColor(this, R.color.colorBlue)
            val options = PolylineOptions().width(POLYLINE_WIDTH).color(color)
            options.addAll(farmBounds)
            farmPolyline = googleMap.addPolyline(options)
            return
        }

        farmPolyline!!.points = farmBounds
    }

    private fun drawPolygon() {
        if (farmBounds.size <= MINIMAL_POLYGON_POINTS) {
            Toast.makeText(this, R.string.maps_incorrect_bounds, Toast.LENGTH_SHORT).show()
            clearMap()
            return
        }

        val rectOptions = PolygonOptions()
        rectOptions.addAll(farmBounds)
        rectOptions.strokeWidth(0F)
        rectOptions.fillColor(ContextCompat.getColor(this, R.color.colorBlue))

        farmPolyline?.remove()
        farmPolyline = null

        farmPolygon?.remove()
        farmPolygon = googleMap.addPolygon(rectOptions)

        toggleViews()
    }

    private fun clearMap() {
        farmPolyline?.remove()
        farmPolyline = null

        farmPolygon?.remove()
        farmPolygon = null
    }
}