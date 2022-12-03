package com.gta.presentation.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }

    fun isBindingNotNull(): Boolean = _binding != null

    fun sendSnackBar(
        message: String,
        @androidx.annotation.IntRange(from = -2) length: Int = Snackbar.LENGTH_SHORT
    ) {
        Snackbar.make(binding.root, message, length).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
