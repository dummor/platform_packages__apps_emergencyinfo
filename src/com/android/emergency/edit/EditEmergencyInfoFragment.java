/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.emergency.edit;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.emergency.PreferenceKeys;
import com.android.emergency.R;
import com.android.emergency.ReloadablePreferenceInterface;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.MetricsProto.MetricsEvent;

/**
 * Fragment that displays personal and medical information.
 */
public class EditEmergencyInfoFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.edit_emergency_info);

        for (int i = 0; i < PreferenceKeys.KEYS_EDIT_EMERGENCY_INFO.length; i++) {
            final int index = i;
            String preferenceKey = PreferenceKeys.KEYS_EDIT_EMERGENCY_INFO[i];
            Preference preference = findPreference(preferenceKey);
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    boolean notSet = TextUtils.isEmpty((String) value);
                    // 0 is the default subtype. In DP1 and DP2 we had no explicit subtype.
                    // Start at 30 to differentiate between before and after.
                    MetricsLogger.action(
                            preference.getContext(),
                            MetricsEvent.ACTION_EDIT_EMERGENCY_INFO_FIELD,
                            30 + index * 2 + (notSet ? 0 : 1));
                    return true;
                }
            });
        }

        Preference preference = findPreference(PreferenceKeys.KEY_SOS_CALL_NUMBER);
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String number = (String) newValue;
                String value = TextUtils.isEmpty(number) ? "" : number;
                    Settings.Global.putString(getContext().getContentResolver(),
                            "sos_call_number", value);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadFromPreference();
    }

    /** Reloads all the preferences by reading the value from the shared preferences. */
    public void reloadFromPreference() {
        for (String preferenceKey : PreferenceKeys.KEYS_EDIT_EMERGENCY_INFO) {
            ReloadablePreferenceInterface preference = (ReloadablePreferenceInterface)
                    findPreference(preferenceKey);
            if (preference != null) {
                preference.reloadFromPreference();
            }
        }
    }

    public static Fragment newInstance() {
        return new EditEmergencyInfoFragment();
    }
}
