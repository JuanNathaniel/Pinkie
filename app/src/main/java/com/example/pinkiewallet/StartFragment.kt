package com.example.pinkiewallet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.pinkiewallet.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menggunakan binding yang sesuai untuk button
        binding.lanjutkanButton.setOnClickListener {
            val register1Fragment = Register1()
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, register1Fragment)
            transaction.addToBackStack(null) // Untuk menambahkan ke back stack, jika diperlukan
            transaction.commit()
        }

//        val videoView = binding.root.findViewById<VideoView>(R.id.background_video)
//        val videoUri = Uri.parse("android.resource://" + requireActivity().packageName + "/" + R.raw.ic_pinkie_animation)
//
//        videoView.setVideoURI(videoUri)
//        videoView.setOnPreparedListener { mediaPlayer ->
//            mediaPlayer.isLooping = true
//        }
//
//        videoView.start()
    }
}
