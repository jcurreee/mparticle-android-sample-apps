package com.mparticle.example.higgsshopsampleapp.activities

import android.app.ActionBar
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.mparticle.MPEvent
import com.mparticle.MParticle
import com.mparticle.MParticleOptions
import com.mparticle.SdkListener
import com.mparticle.example.higgsshopsampleapp.BuildConfig
import com.mparticle.example.higgsshopsampleapp.HiggsShopSampleApplication
import com.mparticle.example.higgsshopsampleapp.R
import com.mparticle.example.higgsshopsampleapp.utils.Constants
import com.mparticle.internal.listeners.InternalListenerManager
import com.mparticle.messages.events.BatchMessage
import com.mparticle.messages.events.MPEventMessage
import org.json.JSONObject

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        MParticle.getInstance()?.logScreen("Landing")

        val btnCTA = findViewById(R.id.landing_cta) as Button
        if(hasApiKey()) {
            btnCTA.isClickable = true
            btnCTA.alpha = 1.0F
            btnCTA.setOnClickListener {
                val event = MPEvent.Builder("Landing Button Click", MParticle.EventType.Other)
                    .build()
                MParticle.getInstance()?.logEvent(event)
                val intent = Intent(this, MainActivity::class.java)

                val manager = InternalListenerManager.start(this)
                manager?.addListener(object:
                    HiggsShopSampleApplication.MParticleRequestListener() {
                    override fun onEventRequestFinished(
                        type: SdkListener.Endpoint?,
                        url: String?,
                        request: BatchMessage,
                        response: JSONObject?,
                        responseCode: Int
                    ) {
                        request.messages
                            .filterIsInstance<MPEventMessage>()
                            .any { it.name == "Landing Button Click" }
                            .let {
                                if (it) {
                                    if (responseCode >= 200 && responseCode < 300) {
                                        showNetworkResult("Landing Button Click event uploaded!")
                                    } else {
                                        showNetworkResult("ERROR: Landing Button Click event failed to upload")
                                    }
                                    manager.removeListener(this)
                                }
                            }
                    }

                })
                //startActivity(intent)
            }
        } else {
            btnCTA.isClickable = false
            btnCTA.alpha = 0.3F
            showBlankAPIKeyAlert()
        }
//        findViewById<AppCompatButton>(R.id.logEvent).setOnClickListener {
//            val event = MPEvent.Builder("Launch Event")
//                .customAttributes(mapOf("initialized" to true.toString()))
//                .build()
//            MParticle.getInstance()!!.logEvent(event)
//            MParticle.getInstance()!!.upload()
//        }
    }

    fun hasApiKey(): Boolean {
        return !(BuildConfig.HIGGS_SHOP_SAMPLE_APP_KEY.isNullOrBlank()
                || BuildConfig.HIGGS_SHOP_SAMPLE_APP_SECRET.isNullOrBlank())
    }

    fun showBlankAPIKeyAlert() {
        val parentLayout: View = findViewById(android.R.id.content)
        val snackbar = Snackbar.make(parentLayout, getString(R.string.landing_apikey_alert), Snackbar.LENGTH_INDEFINITE)
        val layoutParams = ActionBar.LayoutParams(snackbar.view.layoutParams)

        val tv = (snackbar.view.findViewById<TextView>(R.id.snackbar_text))
        tv?.maxLines = 5
        tv?.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

        snackbar.setBackgroundTint(getColor(R.color.white))
        snackbar.setTextColor(getColor(R.color.black))
        snackbar.view.layoutParams = layoutParams
        snackbar.view.setPadding(0, 10, 0, 0)
        snackbar.setActionTextColor(getColor(R.color.blue_4079FE))

        val snackbarActionTextView =
            snackbar.view.findViewById<View>(com.google.android.material.R.id.snackbar_action) as TextView
        snackbarActionTextView.setAllCaps(false)
        snackbarActionTextView.setTypeface(snackbarActionTextView.getTypeface(), Typeface.BOLD);
        snackbar.setAction("Go to docs") {
            val url = Constants.URL_DOCS_API_KEY
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
            finish()
        }
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.show()
    }

    fun showNetworkResult(message: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        val snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
        val layoutParams = ActionBar.LayoutParams(snackbar.view.layoutParams)

        val tv = (snackbar.view.findViewById<TextView>(R.id.snackbar_text))
        tv?.textAlignment = View.TEXT_ALIGNMENT_CENTER

        snackbar.setBackgroundTint(getColor(R.color.white))
        snackbar.setTextColor(getColor(R.color.black))
        snackbar.setActionTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.blue_4079FE
            )
        )
        snackbar.view.layoutParams = layoutParams
        snackbar.view.setPadding(0, 10, 0, 0)
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.show()
    }

}