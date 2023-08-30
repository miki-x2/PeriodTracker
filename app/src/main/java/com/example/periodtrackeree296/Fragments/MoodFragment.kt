package com.example.periodtrackeree296.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.periodtrackeree296.R
import com.example.periodtrackeree296.databinding.FragmentMoodBinding

/**
 * A simple [Fragment] subclass.
 * Use the [MoodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoodFragment : Fragment() {

    private var _binding:FragmentMoodBinding?=null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}