package com.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.LogLevel;
import com.tendcloud.tenddata.TCAgent;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by ntop on 15/7/8.
 */
public class LuckyBuyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 开启debug会影响性能

        //Talking data analytics
        TCAgent.LOG_ON=true;
        TCAgent.init(this, "F032EB9619F23CD34D2670DAD0FCFFDA", "play.google.com");
        TCAgent.setReportUncaughtExceptions(true);

        //Adjust data analytics
        String appToken = "p6l3e3hqx0cg";
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(this, appToken, environment);
        Adjust.onCreate(config);

        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());

        config.setLogLevel(LogLevel.VERBOSE);   // enable all logging
        config.setLogLevel(LogLevel.DEBUG);     // enable more logging
        config.setLogLevel(LogLevel.INFO);      // the default
        config.setLogLevel(LogLevel.WARN);      // disable info logging
        config.setLogLevel(LogLevel.ERROR);     // disable warnings as well
        config.setLogLevel(LogLevel.ASSERT);    // disable errors as well
        config.setLogLevel(LogLevel.SUPRESS);   // disable all log output
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        //Twitter
        //PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
    }

    private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
