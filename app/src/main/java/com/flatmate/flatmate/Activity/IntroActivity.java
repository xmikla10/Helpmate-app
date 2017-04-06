package com.flatmate.flatmate.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.flatmate.flatmate.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.flatmate.flatmate.Other.SampleSlide;


public class IntroActivity extends AppIntro2
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //addSlide(AppIntroFragment.newInstance("Slide titel", "Description", R.drawable.demo_slide_two, Color.parseColor("#0091EA")));

        //addSlide(AppIntroFragment.newInstance("Slide titel", "Description", R.drawable.demo_slide_first, Color.parseColor("#0091EA")));

        addSlide(SampleSlide.newInstance(R.layout.demo_slide_1));
        addSlide(SampleSlide.newInstance(R.layout.demo_slide_2));
        addSlide(SampleSlide.newInstance(R.layout.demo_slide_3));
        addSlide(SampleSlide.newInstance(R.layout.demo_slide_4));

        showSkipButton(false);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent i = new Intent(IntroActivity.this, SignInActivity.class);
        startActivity(i);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent i = new Intent(IntroActivity.this, SignInActivity.class);
        startActivity(i);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}