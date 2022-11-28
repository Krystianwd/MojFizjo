package com.example.mojfizjo.Fragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestExercisesFragment {
    Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void beforeTest(){
        context =  ApplicationProvider.getApplicationContext();
    }

    @Test
    public void givenExerciseInCategory_whenClickedPreviewButton_thenShowsCorrectPreview() throws InterruptedException {
        onView(withId(R.id.exercises)).perform(click());
        onView(withContentDescription("Ćwiczenia na ból kolana")).perform(click());
        Thread.sleep(1000);
        onView(withContentDescription("Ćwiczenie 1")).perform(click());
        onView(withId(R.id.registration_header)).check(matches(withText("Ćwiczenie 1")));
    }
}
