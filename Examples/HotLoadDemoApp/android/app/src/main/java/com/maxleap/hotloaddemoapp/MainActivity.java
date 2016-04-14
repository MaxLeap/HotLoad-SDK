package com.maxleap.hotloaddemoapp;

import android.os.Bundle;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.maxleap.hotload.react.HotLoad;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends ReactActivity {

    private HotLoad hotLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hotLoad = new HotLoad("deployment-key-here", this, BuildConfig.DEBUG);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getJSBundleFile() {
        return this.hotLoad.getBundleUrl("index.android.bundle");
    }

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "HotLoadDemoApp";
    }

    /**
     * Returns whether dev mode should be enabled.
     * This enables e.g. the dev menu.
     */
    @Override
    protected boolean getUseDeveloperSupport() {
        return BuildConfig.DEBUG;
    }

    /**
     * A list of packages used by the app. If the app uses additional views
     * or modules besides the default ones, add more packages here.
     */
    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
                new MainReactPackage(),
                this.hotLoad.getReactPackage()
        );
    }
}
