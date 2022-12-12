package com.example.mojfizjo;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestNavigationView {

    Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
    @Before
    public void beforeTest() throws InterruptedException {
        context =  ApplicationProvider.getApplicationContext();
        onView(withId(R.id.account)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void whenClickedRemindersButton_thenOpenDialog(){
        onView(withId(R.id.nav_notification)).perform(click());
        onView(withText("Mój Fizjo")).check(matches(isDisplayed()));
    }
    @Test
    public void givenRemindHour_whenClickedApply_thenCloseDialog(){
        onView(withId(R.id.nav_notification)).perform(click());
        onView(withId(R.id.remind_hour_before_training)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("00:00"))).perform(click());
        onView(withId(R.id.remind_hour_before_training)).check(matches(withSpinnerText(containsString("00:00"))));
        onView(withText("Zatwierdź")).perform(click());
        onView(withText("Mój Fizjo")).check(matches(not(isDisplayed())));
    }

}
