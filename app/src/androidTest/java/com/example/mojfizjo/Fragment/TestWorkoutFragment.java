package com.example.mojfizjo.Fragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.endsWith;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
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
public class TestWorkoutFragment {

    Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void beforeTest() throws InterruptedException {
        context =  ApplicationProvider.getApplicationContext();
        onView(withId(R.id.workout)).perform(click());
        onView(withText("PlanTest2")).perform(click());
        onView(withText("Ćwiczenie 1")).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void givenExercisesInPlan_whenClickedExerciseName_thenShowsCorrectExercise() {
        onView(withId(R.id.exerciseNameText)).check(matches(withText("Ćwiczenie 1")));
        Espresso.pressBack();
        onView(withText("Ćwiczenie 2")).perform(click());
        onView(withId(R.id.exerciseNameText)).check(matches(withText("Ćwiczenie 2")));
    }

    @Test
    public void givenExerciseInPlan_whenClickedPreviewButton_thenShowsCorrectExercisePreview() {
        onView(withId(R.id.previewExerciseButton)).perform(click());
        onView(withId(R.id.registration_header)).check(matches(withText("Ćwiczenie 1")));
        onView(withId(R.id.submitAddingExerciseToPlan)).perform(click());
        onView(withId(R.id.exerciseNameText)).check(matches(withText("Ćwiczenie 1")));
    }

    @Test
    public void testPausePlayButton() throws InterruptedException {
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("3"))));
        onView(withId(R.id.pauseButton)).perform(click()); //play
        Thread.sleep(1000);
        onView(withId(R.id.pauseButton)).perform(click()); //pause
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("2"))));
        Thread.sleep(1000);
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("2"))));
        onView(withId(R.id.pauseButton)).perform(click()); //play
        Thread.sleep(1000);
        onView(withId(R.id.pauseButton)).perform(click()); //pause
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("1"))));
    }

    @Test
    public void testResetButton() throws InterruptedException {
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("3"))));
        onView(withId(R.id.pauseButton)).perform(click()); //play
        Thread.sleep(1000);
        onView(withId(R.id.pauseButton)).perform(click()); //pause
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("2"))));
        onView(withId(R.id.resetButton)).perform(click());
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("3"))));
    }

    @Test
    public void testStopButton() throws InterruptedException {
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("3"))));
        onView(withId(R.id.setCounterText)).check(matches(withText(endsWith("1/3"))));
        onView(withId(R.id.pauseButton)).perform(click()); //play
        Thread.sleep(1000);
        onView(withId(R.id.pauseButton)).perform(click()); //pause
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("2"))));
        onView(withId(R.id.stopButton)).perform(click());
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("3"))));
        onView(withId(R.id.setCounterText)).check(matches(withText(endsWith("2/3"))));
        onView(withId(R.id.stopButton)).perform(click());
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("3"))));
        onView(withId(R.id.setCounterText)).check(matches(withText(endsWith("3/3"))));
    }

    @Test
    public void testCountdown() throws InterruptedException {
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("3"))));
        onView(withId(R.id.setCounterText)).check(matches(withText(endsWith("1/3"))));
        onView(withId(R.id.pauseButton)).perform(click()); //play
        Thread.sleep(3000);
        onView(withId(R.id.setCounterText)).check(matches(withText(endsWith("2/3"))));
        Thread.sleep(1000);
        onView(withId(R.id.countdownText)).check(matches(withText(endsWith("2"))));
        Thread.sleep(2000);
        onView(withId(R.id.setCounterText)).check(matches(withText(endsWith("3/3"))));
        Thread.sleep(3000);
        onView(withId(R.id.confirmExerciseFinishedButton)).check(matches(isDisplayed()));
    }

}
