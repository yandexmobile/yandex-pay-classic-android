package com.yandex.pay.core.ui.fragments

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import com.yandex.pay.core.R
import com.yandex.pay.core.databinding.YandexpayConfirm3dsFragmentBinding
import com.yandex.pay.core.ui.views.interfaces.IHeaderView
import com.yandex.pay.core.utils.visible
import com.yandex.pay.core.viewmodels.Confirm3DSViewModel

internal class Confirm3DSFragment :
    BaseFragment<Confirm3DSViewModel>(R.layout.yandexpay_confirm_3ds_fragment) {

    private var binding: YandexpayConfirm3dsFragmentBinding? = null
    private val requireBinding: YandexpayConfirm3dsFragmentBinding
        get() = requireNotNull(binding)

    override val viewModel: Confirm3DSViewModel by viewModels {
        Confirm3DSViewModel.Factory(requireActivity().application, parentViewModel)
    }

    override fun onDestroy() {
        binding?.yandexpayWebview?.destroy()
        binding = null
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = YandexpayConfirm3dsFragmentBinding.bind(view)

        viewModel.loading.distinctUntilChanged().observe(viewLifecycleOwner, ::showContent)

        setupHeader(requireBinding.yandexpayHeaderView)
        setupWebView(requireBinding.yandexpayWebview)
        if (savedInstanceState == null) {
            loadUrl()
        } else {
            requireBinding.yandexpayWebview.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        requireBinding.yandexpayWebview.saveState(outState)
    }

    override fun onBackPressed(): Boolean {
        viewModel.onBackPressed()
        return false
    }

    private fun showContent(loading: Boolean) {
        requireBinding.yandexpayProgressBar.visible = loading
        requireBinding.yandexpayWebview.visible = !loading
    }

    private fun loadUrl() {
        val uri = getUrl(arguments)
        if (uri != null) {
            viewModel.startLoading()
            requireBinding.yandexpayWebview.loadUrl(uri.toString())
        }
    }

    private fun setupHeader(header: IHeaderView) {
        viewModel.bind(header)
    }

    private fun setupWebView(webView: WebView) {
        with(webView) {
            with(settings) {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                allowFileAccess = false
                allowContentAccess = false
                cacheMode = WebSettings.LOAD_NO_CACHE
            }
            webViewClient = object : WebViewClient() {
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    onSslError(handler, error)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) = Unit

                override fun onPageFinished(view: WebView?, url: String?) {
                    viewModel.doneLoading()
                }

                @TargetApi(Build.VERSION_CODES.M)
                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    if (request?.isForMainFrame == true && errorResponse != null) {
                        //TODO:
                    }
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    if (consoleMessage != null && consoleMessage.messageLevel() >= ConsoleMessage.MessageLevel.ERROR) {
                        //TODO:
                    }
                    return false
                }
            }
        }
    }

    private fun onSslError(handler: SslErrorHandler?, error: SslError?) {
        if (getDebug(arguments)) {
            handler?.proceed()
        } else {
            handler?.cancel()
        }
    }

    internal companion object {
        private const val EXTRA_URL = "url"
        private const val IS_DEBUG = "is_debug"

        fun newInstance(
            url: Uri,
            debug: Boolean
        ): Confirm3DSFragment = Confirm3DSFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_URL, url)
                putBoolean(IS_DEBUG, debug)
            }
        }

        fun getUrl(extras: Bundle?): Uri? = extras?.getParcelable(EXTRA_URL)
        fun getDebug(extras: Bundle?): Boolean = extras?.getBoolean(IS_DEBUG) == true
    }
}
