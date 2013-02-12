
package com.ushahidi.android.app.ui.phone;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentMapActivity;
import android.text.TextUtils;
import android.util.Log;
import android.content.Intent;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.helpers.ReportViewPager;
import com.ushahidi.android.app.helpers.TabsAdapter;
import com.ushahidi.android.app.ui.tablet.ListReportFragment;
import com.ushahidi.android.app.ui.tablet.MapFragment;
import com.ushahidi.android.app.ui.phone.AddReportActivity;
import android.view.KeyEvent;
import android.widget.TextView;

public class ReportTabActivity extends FragmentMapActivity {

    private ReportViewPager mViewPager;

    private TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report_tab);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle();
        ActionBar.Tab reportsTab = getSupportActionBar().newTab().setText(
                getString(R.string.reports));
        ActionBar.Tab mapTab = getSupportActionBar().newTab().setText(getString(R.string.map));

        mViewPager = (ReportViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);

        mTabsAdapter.addTab(reportsTab, ListReportFragment.class);
        mTabsAdapter.addTab(mapTab, MapFragment.class);

        if (savedInstanceState != null) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("index"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", getSupportActionBar().getSelectedNavigationIndex());
    }
    
    public void setTitle() {
		Preferences.loadSettings(this);
		if ((Preferences.activeMapName != null)
				&& (!TextUtils.isEmpty(Preferences.activeMapName))) {

			getSupportActionBar().setTitle(Preferences.activeMapName);
		}
	}

    // disable volume graphic
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN  
            /*keyCode == KeyEvent.KEYCODE_VOLUME_UP*/) { 
            return true;
        } else {
            return super.onKeyDown(keyCode, event); 
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (//keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
            keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
            ) {
            Intent intent = new Intent(this,AddReportActivity.class);            
            startActivityForResult(intent, 2);
            overridePendingTransition(R.anim.home_enter,
                                      R.anim.home_exit);
            return true;
        }
        else {
            return super.onKeyUp(keyCode, event); 
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

}
