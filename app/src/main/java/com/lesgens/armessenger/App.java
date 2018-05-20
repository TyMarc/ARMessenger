package com.lesgens.armessenger;

import android.app.Application;

import com.lesgens.armessenger.controller.AppController;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppController.getInstance().init(this);
    }
}
