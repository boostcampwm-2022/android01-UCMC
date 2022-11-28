package com.gta.presentation.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gta.presentation.databinding.FragmentTosDialogBinding

class TosDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTosDialogBinding? = null
    val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTosDialogBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.let { binding ->
            binding.btnAccept.setOnClickListener {
                binding.vm?.checkTermsAccepted(true)
            }

            binding.btnCancel.setOnClickListener {
                binding.vm?.checkTermsAccepted(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "TosDialogFragment"
    }
}
