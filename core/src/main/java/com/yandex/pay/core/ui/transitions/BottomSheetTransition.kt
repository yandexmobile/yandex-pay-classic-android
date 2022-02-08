package com.yandex.pay.core.ui.transitions

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Point
import android.transition.Transition
import android.transition.TransitionValues
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.AnimatorRes
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams

// Taken from here: https://tech-en.netlify.app/articles/en510066/index.html
internal class BottomSheetTransition(
    @AnimatorRes
    private val newFragmentEntering: Int,
    @AnimatorRes
    private val oldFragmentExiting: Int,
) : Transition() {
    companion object {
        private const val PROP_HEIGHT = "heightTransition:height"

        // the property PROP_VIEW_TYPE is workaround that allows to run transition always
        // even if height was not changed. It's required as we should set container height
        // to WRAP_CONTENT after animation complete
        private const val PROP_VIEW_TYPE = "heightTransition:viewType"
        private const val ANIMATION_DURATION = 400L

        private val TransitionProperties = arrayOf(PROP_HEIGHT, PROP_VIEW_TYPE)
    }

    var onAnimationCompleteListener: () -> Unit = {}

    override fun getTransitionProperties(): Array<String> = TransitionProperties

    override fun captureStartValues(transitionValues: TransitionValues) {
        transitionValues.values[PROP_HEIGHT] = transitionValues.view.height
        transitionValues.values[PROP_VIEW_TYPE] = "start"

        val parentView = transitionValues.view.parent as View
        parentView.updateLayoutParams<ViewGroup.LayoutParams> {
            height = parentView.height
        }
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        transitionValues.values[PROP_HEIGHT] = getViewHeight(transitionValues.view.parent as View)
        transitionValues.values[PROP_VIEW_TYPE] = "end"
    }

    override fun createAnimator(
        sceneRoot: ViewGroup?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }

        val animators = listOf<Animator>(
            prepareHeightAnimator(
                startValues.values[PROP_HEIGHT] as Int,
                endValues.values[PROP_HEIGHT] as Int,
                endValues.view,
            ),
        ) + prepareSlideOutAnimator(startValues.view) + prepareSlideInAnimator(endValues.view)

        return AnimatorSet()
            .apply {
                duration = ANIMATION_DURATION
                playTogether(animators)
                doOnEnd {
                    onAnimationCompleteListener()
                }
            }
    }

    private fun prepareSlideInAnimator(view: View): List<Animator> =
        (AnimatorInflater.loadAnimator(view.context, newFragmentEntering) as AnimatorSet)
            .childAnimations
            .onEach { it.setTarget(view) }

    private fun prepareSlideOutAnimator(view: View): List<Animator> =
        (AnimatorInflater.loadAnimator(view.context, oldFragmentExiting) as AnimatorSet)
            .childAnimations
            .onEach { it.setTarget(view) }

    private fun prepareHeightAnimator(
        startHeight: Int,
        endHeight: Int,
        view: View
    ) = ValueAnimator.ofInt(startHeight, endHeight)
        .apply {
            val container = view.parent as View

            addUpdateListener { animation ->
                container.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = animation.animatedValue as Int
                }
            }

            doOnEnd {
                container.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
        }

    private fun getViewHeight(view: View): Int {
        val widthMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(getScreenWidth(view), View.MeasureSpec.EXACTLY)
        val heightMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(getScreenHeight(view), View.MeasureSpec.AT_MOST)

        return view.apply { measure(widthMeasureSpec, heightMeasureSpec) }.measuredHeight
    }

    private fun getScreenHeight(view: View) =
        getDisplaySize(view).y - getStatusBarHeight(view.context)

    private fun getScreenWidth(view: View) =
        getDisplaySize(view).x

    private fun getDisplaySize(view: View): Point {
        val point = Point()
        @Suppress("DEPRECATION")
        (view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(
            point
        )
        return point
    }

    private fun getStatusBarHeight(context: Context): Int {
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resId > 0) context.resources.getDimensionPixelSize(resId) else 0
    }
}
