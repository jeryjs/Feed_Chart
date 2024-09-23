package com.jery.feedchart.ui.details

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.jery.feedchart.data.model.FeedDetails
import com.jery.feedchart.data.model.FeedRecommendation
import com.jery.feedchart.data.model.FodderAvailability
import com.jery.feedchart.data.repository.FeedRepository
import com.jery.feedchart.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var feedRecommendations: Map<Int, List<FeedRecommendation>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animalId = intent.getIntExtra("ANIMAL_ID", -1)
        feedRecommendations = FeedRepository(this).getRecommendations()

        setupUI(animalId)
    }

    private fun setupUI(animalId: Int) {
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.milkYieldSpinner.adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item,
            feedRecommendations[animalId]?.map { it.milkYield.toString() } ?: emptyList()
        )

        binding.milkYieldSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    updateRecommendations(animalId)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.fodderAvailabilityGroup.setOnCheckedChangeListener { _, _ ->
            updateRecommendations(animalId)
        }
    }

    private fun getRecommendation(
        animalId: Int,
        milkYield: Float,
        fodderAvailability: FodderAvailability
    ): FeedDetails? {
        val recommendations = feedRecommendations[animalId] ?: emptyList()
        return recommendations.find { it.milkYield == milkYield }?.greenFodderAvailability?.get(
            fodderAvailability
        )
    }

    private fun displayRecommendation(recommendation: FeedDetails?) {
        recommendation?.let {
            binding.concentrateTextView.text = "Concentrate: ${it.concentrateString} Kg"
            binding.greenFodderTextView.text = "Green Fodder: ${it.greenFodderString} Kg"
            binding.dryRoughageTextView.text = "Dry Roughage: ${it.dryRoughageString} Kg"
        }
    }

    private fun updateRecommendations(animalId: Int) {
        val milkYield = binding.milkYieldSpinner.selectedItem?.toString()?.toFloatOrNull() ?: return
        val fodderAvailability = when (binding.fodderAvailabilityGroup.checkedRadioButtonId) {
            binding.highGreenRadio.id -> FodderAvailability.HIGH
            binding.moderateGreenRadio.id -> FodderAvailability.MODERATE
            binding.lowGreenRadio.id -> FodderAvailability.LOW
            else -> FodderAvailability.HIGH
        }

        val recommendation = getRecommendation(animalId, milkYield, fodderAvailability)
        displayRecommendation(recommendation)
    }
}