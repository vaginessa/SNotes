package in.snotes.snotes.settings;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;

import in.snotes.snotes.R;
import in.snotes.snotes.utils.AppConstants;
import in.snotes.snotes.utils.SharedPrefsHelper;
import timber.log.Timber;


public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);

        Preference isPinSetPref = findPreference(AppConstants.PREFS_IS_PIN_SET);
        isPinSetPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(AppConstants.PREFS_IS_PIN_SET)) {

            boolean pref = (boolean) newValue;

            Timber.i(String.valueOf(pref));

            if (pref) {
                new MaterialDialog.Builder(getActivity())
                        .title("Enter Password")
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input("0000", null, (dialog, input) -> {
                            // preference is changed from false to true
                            Timber.i("Changing false to true");
                            SharedPrefsHelper.setPin(Integer.parseInt(input.toString()));

                        }).show();
            }
            return true;
        }
        return false;
    }

}
