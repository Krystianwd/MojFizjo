package com.example.mojfizjo.Fragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
public class TestAccountFragment {

    Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void beforeTest(){
        context =  ApplicationProvider.getApplicationContext();
    }

    @Test
    public void givenNotFreshlyLoggedInAccount_whenClickedChangePasswordButton_thenShowsLoginRefreshNeededErrors() {
        onView(withId(R.id.account)).perform(click());
        onView(withId(R.id.nav_change_password)).perform(click());
        String errorText1 = context.getResources().getString(R.string.walidacja_brak_maila);
        String errorText2 = context.getResources().getString(R.string.walidacja_brak_hasla);
        onView(withId(R.id.input_email)).check(matches(hasErrorText(errorText1)));
        onView(withId(R.id.input_password)).check(matches(hasErrorText(errorText2)));
    }

    @Test
    public void givenWrongEmail_whenClickedChangePasswordButton_thenShowsWrongEmailError() throws InterruptedException {
        onView(withId(R.id.account)).perform(click());
        onView(withId(R.id.nav_change_password)).perform(click());
        onView(withId(R.id.input_email)).perform(typeText("wrongEmail@mail.com"));
        onView(withId(R.id.input_password)).perform(typeText("oldPassword"));
        onView(withId(R.id.input_new_password)).perform(typeText("newPassword"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.confirm_button)).perform(click());
        Thread.sleep(1000);
        String errorText = context.getResources().getString(R.string.bledny_email);
        onView(withId(R.id.input_email)).check(matches(hasErrorText(errorText)));
    }

    @Test
    public void givenNotFreshlyLoggedInAccount_whenClickedDeleteAccountButton_thenShowsLoginRefreshNeededErrors() {
        onView(withId(R.id.account)).perform(click());
        onView(withId(R.id.nav_delete_acc)).perform(click());
        String errorText1 = context.getResources().getString(R.string.walidacja_brak_maila);
        String errorText2 = context.getResources().getString(R.string.walidacja_brak_hasla);
        onView(withId(R.id.input_email)).check(matches(hasErrorText(errorText1)));
        onView(withId(R.id.input_password)).check(matches(hasErrorText(errorText2)));
    }

    @Test
    public void givenWrongEmail_whenClickedDeleteAccountButton_thenShowsWrongEmailError() throws InterruptedException {
        onView(withId(R.id.account)).perform(click());
        onView(withId(R.id.nav_delete_acc)).perform(click());
        onView(withId(R.id.input_email)).perform(typeText("wrongEmail@mail.com"));
        onView(withId(R.id.input_password)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.confirm_button)).perform(click());
        Thread.sleep(1000);
        String errorText = context.getResources().getString(R.string.bledny_email);
        onView(withId(R.id.input_email)).check(matches(hasErrorText(errorText)));
    }

    @Test
    public void givenLoggedInUser_whenClickedLogOutButton_thenLogsOut() {
        onView(withId(R.id.account)).perform(click());
        onView(withId(R.id.nav_logout)).perform(click());
        onView(withId(R.id.login_header)).check(matches(isDisplayed()));
    }

}
