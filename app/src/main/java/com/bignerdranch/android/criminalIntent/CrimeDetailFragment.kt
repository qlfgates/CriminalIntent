package com.bignerdranch.android.criminalIntent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bignerdranch.android.criminalIntent.databinding.FragmentCrimeDetailBinding
import java.util.*

class CrimeDetailFragment : Fragment(){

    private lateinit var binding: FragmentCrimeDetailBinding

    private var _binding: FragmentCrimeDetailBinding? = null

    private lateinit var crime: Crime

    private val args: CrimeDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime(
            id = UUID.randomUUID(),
            title = "",
            date = Date(),
            isSolved = false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            crimeTitle.doOnTextChanged{ text, _, _, _ -> crime = crime.copy(title = text.toString())}
            crimeDate.apply {
                text = crime.date.toString()
                isEnabled = false
            }
            crimeSolved.setOnCheckedChangeListener{ _, isChecked ->
                crime = crime.copy(isSolved = isChecked)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        binding = null
        _binding = null
    }


}