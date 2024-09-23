package com.jery.feedchart.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jery.feedchart.R
import com.jery.feedchart.databinding.ActivityMainBinding
import com.jery.feedchart.databinding.ItemAnimalBinding
import com.jery.feedchart.ui.details.DetailsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val animalTypes = resources.getStringArray(R.array.animal_types)
        binding.rvAnimals.layoutManager = LinearLayoutManager(this)
        binding.rvAnimals.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private lateinit var itemBinding: ItemAnimalBinding

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                itemBinding = ItemAnimalBinding.inflate(layoutInflater, parent, false)
                return object : RecyclerView.ViewHolder(itemBinding.root) {
                    init {
                        itemBinding.root.setOnClickListener {
                            val position = binding.rvAnimals.getChildAdapterPosition(it)
                            val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                            intent.putExtra("ANIMAL_ID", position)
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val animalIcons = resources.obtainTypedArray(R.array.animal_icons)
                val animalDesc = resources.getStringArray(R.array.animal_desc)[position]
                itemBinding.imageAnimal.setImageResource(animalIcons.getResourceId(position, 0))
                itemBinding.textAnimalName.text = animalTypes[position]
                itemBinding.textAnimalDesc.text = animalDesc
                animalIcons.recycle()
            }

            override fun getItemCount(): Int = animalTypes.size
        }
    }
}