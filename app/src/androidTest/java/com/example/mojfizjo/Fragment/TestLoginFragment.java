package com.example.mojfizjo.Fragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
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
public class TestLoginFragment {
    Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void beforeTest(){
        context =  ApplicationProvider.getApplicationContext();
    }

    @Test
    public void givenEmptyEmail_whenClickedLoginButton_thenShowsEmptyEmailError() {
        onView(withId(R.id.input_email)).perform(typeText(""));
        onView(withId(R.id.login_button)).perform(click());
        String errorText = context.getResources().getString(R.string.walidacja_brak_maila);
        onView(withId(R.id.input_email)).check(matches(hasErrorText(errorText)));
    }

    @Test
    public void givenEmptyEmail_whenClickedChangePasswordButton_thenShowsEmptyEmailError() {
        onView(withId(R.id.input_email)).perform(typeText(""));
        onView(withId(R.id.forgot_password_button)).perform(click());
        String errorText = context.getResources().getString(R.string.walidacja_brak_maila);
        onView(withId(R.id.input_email)).check(matches(hasErrorText(errorText)));
    }

    @Test
    public void givenInvalidEmail_whenClickedLoginButton_thenShowsInvalidEmailError() {
        onView(withId(R.id.input_email)).perform(typeText("invalidEmail"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
        String errorText = context.getResources().getString(R.string.walidacja_zly_mail);
        onView(withId(R.id.input_email)).check(matches(hasErrorText(errorText)));
    }

    @Test
    public void givenNotMatchingEmailAndPassword_whenClickedLoginButton_thenShowsWrongEmailOrPasswordError() throws InterruptedException {
        onView(withId(R.id.input_email)).perform(typeText("user1email@mail.com"));
        onView(withId(R.id.input_password)).perform(typeText("user2password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
        Thread.sleep(1000);
        String errorText = context.getResources().getString(R.string.zle_haslo);
        onView(withId(R.id.input_password)).check(matches(hasErrorText(errorText)));
    }

    @Test
    public void givenUnregisteredEmail_whenClickedChangePasswordButton_thenShowsUnregisteredEmailError() throws InterruptedException {
        onView(withId(R.id.input_email)).perform(typeText("unregisteredUser@mail.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.forgot_password_button)).perform(click());
        Thread.sleep(1000);
        String errorText = context.getResources().getString(R.string.nieznany_mail);
        onView(withId(R.id.input_email)).check(matches(hasErrorText(errorText)));
    }
}
