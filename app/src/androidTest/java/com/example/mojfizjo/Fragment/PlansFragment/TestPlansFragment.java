package com.example.mojfizjo.Fragment.PlansFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
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
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPlansFragment {

    Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void beforeTest() throws InterruptedException {
        context =  ApplicationProvider.getApplicationContext();
        onView(withId(R.id.plans)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.plans)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void A_PlansFragmentIntegrationTest_createPlan() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.addPlan)).perform(click());
        onView(withId(R.id.planNameToAdd)).perform(typeText("a"));
        onView(withId(R.id.addExerciseToPlan)).perform(click());
        Espresso.closeSoftKeyboard();
        onView(withContentDescription("Ćwiczenia na ból kolana")).perform(click());
        Thread.sleep(2000);
        onView(withContentDescription("Ćwiczenie 1")).perform(click());
        onView(withId(R.id.submitAddingExerciseToPlan)).perform(click());
        onView(withId(R.id.dialog_sets)).perform(typeText("1"));
        onView(withId(R.id.dialog_time)).perform(typeText("1"));
        Espresso.closeSoftKeyboard();
        String buttonText = context.getResources().getString(R.string.zatwierdz);
        onView(withText(buttonText)).perform(click());
        onView(withId(R.id.planNameToAdd)).perform(typeText("newPlanTest"));
        onView(withId(R.id.submitPlan)).perform(click());
        Thread.sleep(2000);
        onView(withText("poniedziałek")).perform(click());
        onView(withText("sobota")).perform(scrollTo(), click());
        onView(withText(buttonText)).perform(click());
        Espresso.closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(R.id.workout)).perform(click());
        onView(withText("anewPlanTest")).check(matches(isDisplayed()));
        onView(withId(R.id.home)).perform(click());
        onView(withId(R.id.home_textView_plans)).check(matches(withText(endsWith("anewPlanTest "))));
    }

    @Test
    public void B_PlansFragmentIntegrationTest_editPlan() throws InterruptedException {
        Thread.sleep(1000);
        onView(withText("anewPlanTest")).perform(longClick());
        String text = context.getResources().getString(R.string.edytuj_plan);
        onView(withText(text)).perform(click());
        onView(withText("anewPlanTest")).perform(click());
        onView(withId(R.id.addExerciseToPlan)).perform(click());
        Espresso.closeSoftKeyboard();
        onView(withContentDescription("Ćwiczenia na ból kolana")).perform(click());
        Thread.sleep(2000);
        onView(withContentDescription("Ćwiczenie 2")).perform(scrollTo(), click());
        onView(withId(R.id.submitAddingExerciseToPlan)).perform(click());
        onView(withId(R.id.dialog_sets)).perform(typeText("1"));
        onView(withId(R.id.dialog_time)).perform(typeText("1"));
        Espresso.closeSoftKeyboard();
        String buttonText = context.getResources().getString(R.string.zatwierdz);
        onView(withText(buttonText)).perform(click());
        onView(withId(R.id.planNameToAdd)).perform(click());
        onView(withId(R.id.submitPlan)).perform(click());
        Espresso.closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(R.id.workout)).perform(click());
        onView(withText("anewPlanTest")).perform(click());
        onView(withText("Ćwiczenie 2")).perform(click());
        onView(withId(R.id.exerciseNameText)).check(matches(withText("Ćwiczenie 2")));
    }

    @Test
    public void C_PlansFragmentIntegrationTest_deletePlan() {
        onView(withText("anewPlanTest")).perform(longClick());
        String text = context.getResources().getString(R.string.usun_plan);
        onView(withText(text)).perform(click());
        onView(withId(R.id.workout)).perform(click());
        onView(withText("anewPlanTest")).check(doesNotExist());
    }

}
