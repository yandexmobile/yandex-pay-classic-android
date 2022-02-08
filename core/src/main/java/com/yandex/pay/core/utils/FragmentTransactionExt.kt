package com.yandex.pay.core.utils

import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.yandex.pay.core.R
import com.yandex.pay.core.ui.fragments.BaseFragment
import com.yandex.pay.core.ui.fragments.OnAppearance
import com.yandex.pay.core.ui.transitions.BottomSheetTransition

internal fun FragmentTransaction.setUpFragmentAnimation(
    fragment: BaseFragment<*>,
    currentFragmentRoot: View?,
    animated: Boolean,
) {
    val transitionName = currentFragmentRoot?.transitionName
    if (animated && transitionName != null) {
        addSharedElement(currentFragmentRoot, transitionName)
        val enterTransition = BottomSheetTransition(
            R.animator.yandexpay_slide_in_to_left,
            R.animator.yandexpay_slide_out_to_left,
        )
        fragment.sharedElementEnterTransition = enterTransition
        fragment.sharedElementReturnTransition = BottomSheetTransition(
            R.animator.yandexpay_slide_in_to_right,
            R.animator.yandexpay_slide_out_to_right,
        )
        if (fragment is OnAppearance) {
            enterTransition.onAnimationCompleteListener = {
                fragment.appearanceAnimationCompleted()
            }
        }
    } else {
        fragment.sharedElementEnterTransition = null
        fragment.sharedElementReturnTransition = null
    }
}
