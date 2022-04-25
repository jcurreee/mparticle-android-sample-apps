package com.mparticle.example.higgsshopsampleapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.mparticle.testing.BaseTest


class TestApplication: HiggsShopSampleApplication() {
    override fun onCreate() {
        BaseTest().initializeTestServer()
        super.onCreate()
    }
}

class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}