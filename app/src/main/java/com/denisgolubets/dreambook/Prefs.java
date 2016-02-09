package com.denisgolubets.dreambook;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.denisgolubets.dreambook.R;


public class Prefs extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}