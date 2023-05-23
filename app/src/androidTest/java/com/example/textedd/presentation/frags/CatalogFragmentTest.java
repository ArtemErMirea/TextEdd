package com.example.textedd.presentation.frags;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.textedd.R;
import com.example.textedd.presentation.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CatalogFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        onView(withId(R.id.username)).perform(typeText("Ur"));
        onView(withId(R.id.password)).perform(typeText("friend"));
        onView(withId(R.id.login)).perform(click());
    }

    @Test
    public void updateRV() {
    }

    @Test
    public void openFileAsNote() {
        onView(withId(R.id.action_open)).perform(click());
        onView(withId(R.id.input_text)).perform(typeText("my"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.noteFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void openFileAsTag() {
        onView(withId(R.id.action_open)).perform(click());
        onView(withId(R.id.input_text)).perform(typeText("Dootl"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.tag_frag)).check(matches(isDisplayed()));
    }

    @Test
    public void onNoteClick() {
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.noteFragment)).check(matches(isDisplayed()));
    }
}