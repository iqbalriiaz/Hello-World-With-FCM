package com.iqbalriiaz.helloworldwithfcm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iqbalriiaz.helloworldwithfcm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChangeText.setOnClickListener {
            binding.dynamicTextView.setText(R.string.changed_text)
        }
    }
}