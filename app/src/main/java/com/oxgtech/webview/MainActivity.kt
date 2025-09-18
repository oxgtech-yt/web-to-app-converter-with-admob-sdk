package com.oxgtech.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.progressindicator.LinearProgressIndicator
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var loadingScreen: ConstraintLayout
    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var loadingText: TextView
    
    // AdMob variables
    private lateinit var bannerAd: AdView
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    
    private val homeUrl = "https://www.google.com"
    private var isFirstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize AdMob
        MobileAds.initialize(this) {}
        
        initViews()
        setupWebView()
        setupToolbar()
        setupBottomNavigation()
        setupSwipeRefresh()
        setupAds()
        
        // Setup modern back press handling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
        
        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Load home page
        webView.loadUrl(homeUrl)
    }
    
    private fun initViews() {
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        toolbar = findViewById(R.id.toolbar)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        loadingScreen = findViewById(R.id.loadingScreen)
        loadingAnimation = findViewById(R.id.loadingAnimation)
        loadingText = findViewById(R.id.loadingText)
        bannerAd = findViewById(R.id.bannerAd)
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                progressBar.progress = 0
                swipeRefreshLayout.isRefreshing = false
                
                if (isFirstLoad) {
                    showLoadingScreen()
                }
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
                
                if (isFirstLoad) {
                    hideLoadingScreen()
                    isFirstLoad = false
                }
                
                // Update toolbar title
                toolbar.title = view?.title ?: "WebView"
                
                // Update navigation buttons state
                updateNavigationButtons()
            }
            
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                progressBar.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
                
                if (isFirstLoad) {
                    hideLoadingScreen()
                    isFirstLoad = false
                }
                
                Toast.makeText(this@MainActivity, "Error loading page", Toast.LENGTH_SHORT).show()
            }
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                }
            }
            
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                toolbar.title = title ?: "WebView"
            }
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        toolbar.title = "WebView"
    }
    
    private fun setupBottomNavigation() {
        // Bottom navigation removed - no buttons to setup
    }
    
    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            webView.reload()
        }
        
        swipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorSecondary,
            R.color.colorAccent
        )
    }
    
    private fun updateNavigationButtons() {
        // No navigation buttons to update
    }
    
    private fun showLoadingScreen() {
        loadingScreen.visibility = View.VISIBLE
        loadingAnimation.playAnimation()
    }
    
    private fun hideLoadingScreen() {
        loadingScreen.visibility = View.GONE
        loadingAnimation.pauseAnimation()
    }
    
    private fun setupAds() {
        // Setup Banner Ad
        val bannerAdRequest = AdRequest.Builder().build()
        bannerAd.loadAd(bannerAdRequest)
        
        // Load Interstitial Ad
        loadInterstitialAd()
        
        // Load Rewarded Ad
        loadRewardedAd()
    }
    
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
                
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    setupInterstitialCallbacks()
                }
            })
    }
    
    private fun setupInterstitialCallbacks() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadInterstitialAd() // Load next ad
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
            }
        }
    }
    
    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                }
                
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    setupRewardedCallbacks()
                }
            })
    }
    
    private fun setupRewardedCallbacks() {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                loadRewardedAd() // Load next ad
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
            }
        }
    }
    
    fun showInterstitialAd() {
        interstitialAd?.show(this) ?: run {
            Toast.makeText(this, "Interstitial ad not ready", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun showRewardedAd() {
        rewardedAd?.show(this) { rewardItem ->
            // User earned reward
            Toast.makeText(this, "You earned ${rewardItem.amount} ${rewardItem.type}!", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "Rewarded ad not ready", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}