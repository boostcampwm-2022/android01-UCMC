package com.gta.presentation.ui.mypage

import android.content.res.AssetManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMypageTermsBinding
import com.gta.presentation.ui.base.BaseFragment
import java.io.BufferedReader
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
        return try {
            inputStream = resources.assets.open(fileName, AssetManager.ACCESS_BUFFER)

            val reader = BufferedReader(InputStreamReader(inputStream))

            var strResult = ""
            var line: String?
            while (true) {
                line = reader.readLine()
                if (line == null) break
                strResult += line + "\n"
            }
            strResult
        } catch (e: Exception) {
            ""
        }
    }
}
