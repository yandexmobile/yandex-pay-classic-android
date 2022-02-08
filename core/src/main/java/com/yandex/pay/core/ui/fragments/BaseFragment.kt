package com.yandex.pay.core.ui.fragments

import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yandex.pay.core.viewmodels.BaseViewModel
import com.yandex.pay.core.viewmodels.MainViewModel

internal abstract class BaseFragment<VM : BaseViewModel> : Fragment {
    constructor() : super()

    @ContentView
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    protected abstract val viewModel: VM
    protected val parentViewModel: MainViewModel by activityViewModels()

    open fun onBackPressed(): Boolean = false
}
