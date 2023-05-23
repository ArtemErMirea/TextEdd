package com.example.textedd.presentation.frags;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.textedd.R;
import com.example.textedd.presentation.MainActivity;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class LoginFragmentTest {

    private View decorView;

    @Rule public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> decorView =
                activity.getWindow().getDecorView());
    }

    @Test
    public void onLoginSuccess() {
        onView(withId(R.id.username)).perform(typeText("Ur"));
        onView(withId(R.id.password)).perform(typeText("friend"));
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.catalogFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void onLoginFailed() {

        onView(withId(R.id.username)).perform(typeText(""));
        onView(withId(R.id.password)).perform(typeText(""));
        onView(withId(R.id.login)).perform(click());
        onView(withText("Неверный пароль")).
                inRoot(withDecorView(Matchers.not(decorView))).
                check(matches(isDisplayed()));
    }

    @Test
    public void onRegistrationFailed() {
        onView(withId(R.id.username)).perform(typeText("Ur"));
        onView(withId(R.id.password)).perform(typeText("invalid_password"));
        onView(withId(R.id.register)).perform(click());
        onView(withText("Регистрация не удалась.")).
                inRoot(withDecorView(Matchers.not(decorView))).
                check(matches(isDisplayed()));
    }
}