package com.example.miniprinter;

import android.app.Application;
import android.posapi.PosApi;
import android.util.Log;

public class App extends Application{

	private String mCurDev = "";
	public String getCurDevice() {
		return mCurDev;
	}
	public void setCurDevice(String mCurDev) {
		this.mCurDev = mCurDev;
	}

	static App instance = null;
	public static  App getInstance() {
		return instance != null ? instance : (instance = new App());
	}

	/*
	 * Printer API
	 */
	public PosApi getPosApi(){
		return mPosApi;
	}
	PosApi mPosApi = null;

	public App() {
		 super.onCreate();
		instance = this;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("hello", "APP onCreate~~");
		mPosApi = PosApi.getInstance(this);
	}







	
	
	
}
