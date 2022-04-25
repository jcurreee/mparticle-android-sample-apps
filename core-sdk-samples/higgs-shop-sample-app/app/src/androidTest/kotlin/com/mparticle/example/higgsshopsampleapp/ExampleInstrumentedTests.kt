package com.mparticle.example.higgsshopsampleapp

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.mparticle.api.MParticle
import com.mparticle.example.higgsshopsampleapp.activities.LandingActivity
import com.mparticle.example.higgsshopsampleapp.activities.MainActivity
import com.mparticle.internal.Logger
import com.mparticle.messages.events.MPEventMessage
import com.mparticle.messages.events.PushRegistrationMessage
import com.mparticle.testing.BaseTest
import com.mparticle.testing.testserver.EndpointType
import com.mparticle.testing.testserver.Server
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class ExampleInstrumentedTests: BaseTest(true) {

    val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)

    @get:Rule
    var landingActivityScenarioRule = activityScenarioRule<LandingActivity>()

    @get:Rule
    var mainActivityScenarioRule = activityScenarioRule<MainActivity>(intent)

    @Test
    fun testUseAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.mparticle.example.higgsshopsampleapp", appContext.packageName)
    }

    @Test
    fun testShowLandingCTA() {
        onView(withId(R.id.landing_cta)).check(matches(withText(R.string.landing_cta)))
    }

    @Test
    fun testLaunchEventLoggedToMParticle() {
        Server
            .endpoint(EndpointType.Events)
            .assertWillReceive { request ->
                request.body.messages
                    .filterIsInstance<MPEventMessage>()
                    .any { event ->
                        event.name == "Landing Button Click"
                    }
            }
            .after {
                onView(withId(R.id.landing_cta)).perform(click())
                MParticle.getInstance()?.upload()
            }
            .blockUntilFinished()
    }
}