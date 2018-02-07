package com.example.miniprinter;

import android.app.Activity;
import android.os.Bundle;
import android.posapi.Conversion;
import android.posapi.PosApi;
import android.posapi.PosApi.OnIcPasmEventListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jizankj.miniprinter.R;

public class PsamActivity extends Activity {
	private TextView mTv = null;
	private Button mBtnP1On = null;
	private Button mBtnP2On = null;
	private Button mBtnClean = null;
	private Button mBtnClose = null;
	private Button mBtnCmd   = null;
	private EditText mEtCmd  = null; 
	private PosApi  mApi  = null;
	private int mPsamSlotTemp = 1;
	private int mCurPsamSlot = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pasm);
		
		mApi = App.getInstance().getPosApi();
		mApi.setOnIcPasmEventListener(listener);
		initViews();
	}



	private void initViews() {
		mTv = (TextView)this.findViewById(R.id.tv_text);
		mBtnP1On = (Button)this.findViewById(R.id.btn_p1init);
		mBtnP2On = (Button)this.findViewById(R.id.btn_p2init);
		mBtnClose = (Button)this.findViewById(R.id.btn_close);
		mBtnClean = (Button)this.findViewById(R.id.btn_clear);
		mBtnCmd= (Button)this.findViewById(R.id.btnSend);
		mEtCmd= (EditText)this.findViewById(R.id.etContent);

		//reset  psam1
		mBtnP1On.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mApi.resetPsam(500, 1);
				mPsamSlotTemp = 1;
			}
		});

		//reset  psam2
		mBtnP2On.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mApi.resetPsam(500, 2);
				mPsamSlotTemp = 2;
			}
		});

		mBtnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mApi.closePsam(mCurPsamSlot);
			}
		});
		
		mBtnCmd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String str  = mEtCmd.getText().toString().trim();
				if(TextUtils.isEmpty(str)) return;
				byte[] mCmd = Conversion.HexString2Bytes(str);
				mApi.psamCmd(mCurPsamSlot, mCmd, mCmd.length);
			}
		});
		
		mBtnClean.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTv.setText("");
			}
		});
	}


	OnIcPasmEventListener listener = new OnIcPasmEventListener() {
		@Override
		public void onReset(int state, int slot, byte[] atr, int length) {
			
			// TODO Auto-generated method stub
			if(state == PosApi.COMM_STATUS_SUCCESS){
				mCurPsamSlot = mPsamSlotTemp;
				mTv.append("PSAM Card reset successful\n");
				mTv.append("Reset the data:"+Conversion.Bytes2HexString(atr)+"\n");
			}else {
				mTv.append("PSAM Card reset failed\n");
			}
		}
		
		@Override
		public void onClose(int state, int slot) {
			if(state == PosApi.COMM_STATUS_SUCCESS){
				mTv.append("PSAM card closed successfully\n");
			} else {
				mTv.append("PSAM Card closed failed\n");
			}
		}
		
		@Override
		public void onApdu(int state, int slot, byte[] reApdu, int length) {
			if(state == PosApi.COMM_STATUS_SUCCESS){
				if(reApdu != null){
					mTv.append("Data reply:"+Conversion.Bytes2HexString(reApdu)+"\n");
				}

			} else {
				mTv.append("PSAM Command execution failed\n");
				
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mApi != null){
			mApi.removeEventListener(listener);
		}
	}
}
