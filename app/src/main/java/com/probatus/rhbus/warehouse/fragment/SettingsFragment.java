package com.probatus.rhbus.warehouse.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ListView;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.probatus.rhbus.warehouse.Interface.INavigationFragment;
import com.probatus.rhbus.warehouse.R;

/**
 * Created by ganapathi on 23/1/20.
 */

public class SettingsFragment extends PreferenceFragment
        implements INavigationFragment {

    protected FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        Preference prefCheckForUpdates = findPreference("myKey");

        prefCheckForUpdates.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AppUpdater(getActivity())
                        .setUpdateFrom(UpdateFrom.GITHUB)
                        .setGitHubUserAndRepo("Ganeshbhatpk34","Warehouse-Managment")
                        .setDisplay(Display.DIALOG)
                        .showAppUpdated(true)
                        .start();
                return true;
            }
        });

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.titleSettings));

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        if (listView != null) listView.setVerticalScrollBarEnabled(false);
        prepareFloatingActionButton();

    }

    protected void prepareFloatingActionButton(){
        if(fab == null){
            fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        }
        fab.hide();
    }
}
