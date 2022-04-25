package com.mparticle.example.higgsshopsampleapp;

import android.app.Application
import com.mparticle.BaseEvent
import com.mparticle.MParticle
import com.mparticle.MParticleOptions
import com.mparticle.SdkListener
import com.mparticle.internal.listeners.InternalListener
import com.mparticle.internal.listeners.InternalListenerManager
import com.mparticle.messages.events.BatchMessage
import com.mparticle.messages.events.MPEventMessage
import com.squareup.moshi.Json
import org.json.JSONObject

open class HiggsShopSampleApplication: Application() {
    val TAG = "HiggsShopSampleApplication"
    override fun onCreate() {
        super.onCreate()
        val options: MParticleOptions = MParticleOptions.builder(this)
            .credentials(
                BuildConfig.HIGGS_SHOP_SAMPLE_APP_KEY,
                BuildConfig.HIGGS_SHOP_SAMPLE_APP_SECRET
            )
            .environment(MParticle.Environment.Development)
            // logLevel can be 'NONE', 'ERROR', 'WARNING', 'DEBUG', 'VERBOSE', or 'INFO
            // (the default is 'DEBUG').
            // This logLevel provides context into the inner workings of mParticle.
            // It can be updated after MP has been initialized using mParticle.setLogLevel().
            // and passing.  Logs will be available in the inspector.
            // More can be found at https://docs.mparticle.com/developers/sdk/android/logger/
            .logLevel(MParticle.LogLevel.VERBOSE)
            .build()

        MParticle.start(options)
    }
        abstract class MParticleRequestListener: SdkListener() {
            var lastRequest: JSONObject? = null

            override fun onNetworkRequestStarted(type: Endpoint, url: String, body: JSONObject) {
                if (type == SdkListener.Endpoint.EVENTS) {
                    lastRequest = body
                }
//                if (type == SdkListener.Endpoint.EVENTS) {
//                    BatchMessage.fromString(body.toString())
//                        .messages
//                        .filterIsInstance<MPEventMessage>()
//                        .filter {
//                            it.name == "my eventName"
//                        }
//                        .isNotEmpty()
//                        .let {
//                            if (it) {
//                                messageSent = true
//                            }
//                        }
//                }
            }

            override fun onNetworkRequestFinished(
                type: Endpoint,
                url: String,
                response: JSONObject?,
                responseCode: Int
            ) {
                if (type == SdkListener.Endpoint.EVENTS) {
                    val last = lastRequest
                    if (last != null) {
                        lastRequest = null
                        onEventRequestFinished(
                            type,
                            url,
                            BatchMessage.fromString(last.toString()),
                            response,
                            responseCode
                        )
                    }
                }
            }

            abstract fun onEventRequestFinished(
                type: SdkListener.Endpoint?,
                url: String?,
                request: BatchMessage,
                response: JSONObject?,
                responseCode: Int
            )
        }
}