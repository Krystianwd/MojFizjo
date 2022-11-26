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
public class TestRegistrationFragment {

    Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void beforeTest(){
        context =  ApplicationProvider.getApplicationContext();
        onView(withId(R.id.registration_page_button)).perform(click());
    }

    @Test
    public void givenEmptyEmail_whenClickedRegisterButton_thenShowsEmptyEmailError() {
        onView(withId(R.id.input_email)).perform(typeText(""));
        onView(withId(R.id.register_button)).perform(click());
        String errorText = context.getResources().getString(R.string.walidacja_brak_maila);
        onView(withId(R.id.input_email)).check(matches(hasErrorText(errorText)));
    }

    @Test
    public void givenInvalidEmail_whenClickedRegisterButton_thenShowsInvalidEmailError() {
        onView(withId(R.id.input_email)).perform(typeText("invalidEmail"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_button)).perform(click());
        String errorText = context.getResources().getString(R.string.walidacja_zly_mail);
        onView(withId(R.id.input_email)).check(matches(hasErrorText(errorText)));
    }

    @Test
    public void givenPasswordWithLessThan6Chars_whenClickedRegisterButton_thenShowsTooShortPasswordError() {
        onView(withId(R.id.input_email)).perform(typeText("validEmail@mail.com"));
        onView(withId(R.id.input_password)).perform(typeText("12345"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_button)).perform(click());
        String errorText = context.getResources().getString(R.string.walidacja_za_krotkie_haslo);
        onView(withId(R.id.input_password)).check(matches(hasErrorText(errorText)));
    }

    @Test
    public void givenNotMatchingPasswords_whenClickedRegisterButton_thenShowsNotMatchingPasswordsError() {
        onView(withId(R.id.input_email)).perform(typeText("validEmail@mail.com"));
        onView(withId(R.id.input_password)).perform(typeText("123456"));
        onView(withId(R.id.input_password_repeat)).perform(typeText("654321"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_button)).perform(click());
        String errorText = context.getResources().getString(R.string.walidacja_rozne_hasla);
        onView(withId(R.id.input_password_repeat)).check(matches(hasErrorText(errorText)));
    }


}
