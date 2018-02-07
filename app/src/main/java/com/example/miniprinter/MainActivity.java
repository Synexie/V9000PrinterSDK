package com.example.miniprinter;

import java.io.File;
import java.io.FileWriter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.posapi.PosApi;
import android.posapi.PosApi.OnCommEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.jizankj.miniprinter.R;

public class MainActivity extends Activity {

	private PosApi mApi = null;
	private Button mBtnPsam = null;
	private Button mBtnPrinter =null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();

		//1  Microcontroller power
		try {
			FileWriter localFileWriterOn = new FileWriter(new File("/proc/gpiocontrol/set_sam"));
			localFileWriterOn.write("1");
			localFileWriterOn.close();
		} catch (Exception e) { 
			e.printStackTrace(); 
		}

		//2 Interface initialization
		mApi = App.getInstance().getPosApi();
		// Set initialization callback
		mApi.setOnComEventListener(mCommEventListener);
		// Use an extension to initialize the interface
		mApi.initDeviceEx("/dev/ttyMT2");
	}

	private void initViews(){
		mBtnPrinter = (Button)this.findViewById(R.id.btn_printer);
		mBtnPsam = (Button)this.findViewById(R.id.btn_psam);
		mBtnPrinter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, PrintBarcodeActivity.class));
			}
		});

		mBtnPsam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, PsamActivity.class));
			}
		});
	}

	OnCommEventListener mCommEventListener = new OnCommEventListener() {

		@Override
		public void onCommState(int cmdFlag, int state, byte[] resp, int respLen) {
			switch(cmdFlag){
			case PosApi.POS_INIT:
				if(state==PosApi.COMM_STATUS_SUCCESS){
					Toast.makeText(getApplicationContext(), "Initialization successful", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Initialization failed", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Destroy the interface
		if(mApi!=null) {
			mApi.closeDev();
		}

		try {
			FileWriter localFileWriterOn = new FileWriter(new File("/proc/gpiocontrol/set_sam"));
			localFileWriterOn.write("0");
			localFileWriterOn.close();
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
}
