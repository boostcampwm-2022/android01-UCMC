package com.gta.presentation.ui.reservation

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentPaymentBinding
import com.gta.presentation.ui.base.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentFragment : BaseFragment<FragmentPaymentBinding>(R.layout.fragment_payment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            delay(2000)
            findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToMapFragment())
        }
    }
}
