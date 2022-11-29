package com.gta.presentation.ui.mypage

import android.content.res.AssetManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMypageTermsBinding
import com.gta.presentation.ui.base.BaseFragment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class MyPageTermsFragment :
    BaseFragment<FragmentMypageTermsBinding>(R.layout.fragment_mypage_terms) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDescriptionServices.text = getAssetData("terms_of_service.txt")
        binding.tvDescriptionPrivacy.text = getAssetData("terms_of_privacy.txt")

        binding.tvDescriptionServices.movementMethod = ScrollingMovementMethod()
        binding.tvDescriptionPrivacy.movementMethod = ScrollingMovementMethod()
    }

    private fun getAssetData(fileName: String): String {
        val inputStream: InputStream?
        var result: String
        try {
            inputStream = resources.assets.open(fileName, AssetManager.ACCESS_BUFFER)
            val reader = BufferedReader(InputStreamReader(inputStream))
            result = reader.readLines().joinToString("\n")
            inputStream.close()
        } catch (e: IOException) {
            result = ""
        }

        return result
    }
}
