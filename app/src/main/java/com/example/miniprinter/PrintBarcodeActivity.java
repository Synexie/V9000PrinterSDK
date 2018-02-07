package com.example.miniprinter;


import java.io.UnsupportedEncodingException;

import com.example.miniprinter.util.BarcodeCreater;
import com.example.miniprinter.util.BitmapTools;
import com.jizankj.miniprinter.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.posapi.PosApi;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.zyapi.PrintQueue;
import android.zyapi.PrintQueue.OnPrintListener;

public class PrintBarcodeActivity extends Activity {
	private Button btnCreat1D;
	private Button btnCreat2D;
	private Button btnCreatPic;
	private Button btnPrint;
	private Button btnPrintText;
	private Button btnPrintMix;
	private ImageView iv;
	private EditText etContent;


	private EditText etImgHeight;
	private EditText etImgWidth;
	private EditText etImgMarginLeft;
	private EditText etConcentration;

	private Bitmap mBitmap =null;

	private PosApi mPosApi;


	private PrintQueue mPrintQueue = null;

	// 	Black mark detection related
	private Button btnBlack;
	private AlertDialog.Builder builder;
	private boolean isShowAlertDialog = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_barcode);
		initViews();

		mPosApi = App.getInstance().getPosApi();
		mPrintQueue = new PrintQueue(this,mPosApi);
		mPrintQueue.init();
		mPrintQueue.setOnPrintListener(new OnPrintListener() {

			@Override
			public void onGetState(int state) {
				switch(state){
					case 0:
						// There is paper
						Toast.makeText(PrintBarcodeActivity.this, "Paper ok", Toast.LENGTH_SHORT).show();
						break;
					case 1:
						// Out of paper
						Toast.makeText(PrintBarcodeActivity.this, "No paper", Toast.LENGTH_SHORT).show();
						break;
				}
			}

			@Override
			public void onPrinterSetting(int state) {
				switch(state){
					case 0:
						Toast.makeText(PrintBarcodeActivity.this, "Continue to have paper", Toast.LENGTH_SHORT).show();
						break;
					case 1:
						Toast.makeText(PrintBarcodeActivity.this, "Out of paper", Toast.LENGTH_SHORT).show();
						break;
					case 2:
						Toast.makeText(PrintBarcodeActivity.this, "Black mark was detected", Toast.LENGTH_SHORT).show();
						break;
				}
			}

			@Override
			public void onFinish() {
				// mPosApi.gpioControl((byte)0x23,2,0);
				Toast.makeText(PrintBarcodeActivity.this, "Print completed", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailed(int state) {
				// mPosApi.gpioControl((byte)0x23,2,0);
				switch(state){

					case PosApi.ERR_POS_PRINT_NO_PAPER:
						// Print out of paper
						showTip(getString(R.string.print_no_paper));
						break;
					case PosApi.ERR_POS_PRINT_FAILED:
						// Printing failed
						showTip(getString(R.string.print_failed));
						break;
					case PosApi.ERR_POS_PRINT_VOLTAGE_LOW:
						// The voltage is too low
						showTip(getString(R.string.print_voltate_low));
						break;
					case PosApi.ERR_POS_PRINT_VOLTAGE_HIGH:
						// The voltage is too high (?)
						showTip(getString(R.string.print_voltate_high));
						break;
				}
				Toast
					.makeText(PrintBarcodeActivity.this, "Print failed error code:" + state, Toast.LENGTH_SHORT)
					.show();
			}
		});
	}




	private void initViews() {
		btnCreat1D     = (Button)this.findViewById(R.id.btnCreat1d);
		btnCreat2D     = (Button)this.findViewById(R.id.btnCreat2d);
		btnCreatPic    = (Button)this.findViewById(R.id.btnCreatPic);
		btnBlack       = (Button)this.findViewById(R.id.btnBlack);
		btnPrint       = (Button)this.findViewById(R.id.btnPrint);
		btnPrintMix    = (Button)this.findViewById(R.id.btnPrintMix);
		btnPrintText   = (Button)this.findViewById(R.id.btnPrintText);

		iv = (ImageView)this.findViewById(R.id.iv2d);

		etContent = (EditText)this.findViewById(R.id.etContent);
		etImgHeight = (EditText)this.findViewById(R.id.etContentHeight);
		etImgWidth = (EditText)this.findViewById(R.id.etContentWidth);
		etImgMarginLeft = (EditText)this.findViewById(R.id.etMarginLeft);
		etConcentration = (EditText)this.findViewById(R.id.etConcentration);


		etContent.setText("1234567890");
		etImgHeight.setText("150");
		etImgWidth.setText("150");
		etImgMarginLeft.setText("0");
		etConcentration.setText("60");


		btnBlack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//1-->Get the paper status
				//mPosApi.printerGetState(1);
				showAlertDialog("Whether to carry out black label paper detection");

			}
		});

		btnCreat1D.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(etContent.getText().toString())
						||TextUtils.isEmpty(etImgHeight.getText().toString())
						||TextUtils.isEmpty(etImgWidth.getText().toString())){
					Toast.makeText(PrintBarcodeActivity.this, "Please check the image parameters", Toast.LENGTH_SHORT).show();
					return ;
				}

				int mWidth = Integer.valueOf(etImgWidth.getText().toString().trim());
				int mHeight = Integer.valueOf(etImgHeight.getText().toString().trim());
				mBitmap=BarcodeCreater.creatBarcode(PrintBarcodeActivity.this,etContent.getText().toString(), mWidth, mHeight,true, 1);
				iv.setImageBitmap(mBitmap);

			}
		});

		btnCreat2D.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(etContent.getText().toString())
						||TextUtils.isEmpty(etImgHeight.getText().toString())
						||TextUtils.isEmpty(etImgWidth.getText().toString())){
					Toast.makeText(PrintBarcodeActivity.this, "Please check the image parameters", Toast.LENGTH_SHORT).show();
					return ;
				}
				int mWidth = Integer.valueOf(etImgWidth.getText().toString().trim());
				int mHeight = Integer.valueOf(etImgHeight.getText().toString().trim());

				mBitmap=BarcodeCreater.encode2dAsBitmap(etContent.getText().toString(), mWidth, mHeight, 2);
				iv.setImageBitmap(mBitmap);

			}
		});

		btnCreatPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vv);
				mBitmap = BitmapTools.gray2Binary(mBitmap);
				iv.setImageBitmap(mBitmap);

			}
		});


		btnPrintText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				printTextWithInput();
			}
		});

		btnPrint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mBitmap == null) return;
				int mLeft = Integer.valueOf(etImgMarginLeft.getText().toString().trim());
				byte[] printData =BitmapTools.bitmap2PrinterBytes(mBitmap);
				int concentration = Integer.valueOf(etConcentration.getText().toString().trim());

				mPrintQueue.addBmp(concentration, mLeft, mBitmap.getWidth(), mBitmap.getHeight(), printData);
				mPrintQueue.printStart();
			}
		});

		btnPrintMix.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//mPosApi.gpioControl((byte)0x23,2,1);
				byte [] test = new byte[]{0x33,0x34,0x31,0x33,0x34,0x31,0x33,0x34,0x31,0x33,0x34,0x31,0x00};
				String string ="WeChat payment WeChat payment\n";

				try {
					test = string.getBytes("GBK");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				printMix();
			}
		});
	}

	/*
	 * Print text   size 1 --times the size    2--2 times the size
	 */
	private void addPrintTextWithSize(int size ,int concentration,  byte[] data){
		if(data == null) return ;
		//2 Times font size
		byte[] _2x = new byte[]{0x1b,0x57,0x02};
		//1 Times font size
		byte[] _1x = new byte[]{0x1b,0x57,0x01};
		byte[] mData = null;
		if(size == 1){
			mData = new byte[3+data.length];
			// 1 Times font size by default
			System.arraycopy(_1x, 0, mData, 0, _1x.length);
			System.arraycopy(data, 0, mData, _1x.length, data.length);
			mPrintQueue.addText(concentration, mData);
		} else if(size == 2){
			mData = new byte[3+data.length];
			// 1 Times font size by default
			System.arraycopy(_2x, 0, mData, 0, _2x.length);
			System.arraycopy(data, 0, mData, _2x.length, data.length);
			mPrintQueue.addText(concentration, mData);
		}
	}

	private void printTextWithInput(){
		try {
			int  concentration = Integer.valueOf(etConcentration.getText().toString().trim());
			if(TextUtils.isEmpty(etContent.getText().toString())){
				return;
			}

			StringBuilder sb =new StringBuilder();
			sb.append(etContent.getText());
			sb.append("\n");
			byte[]text = null;
			text =sb.toString().getBytes("GBK");

			addPrintTextWithSize(1, concentration, text);


			mPrintQueue.printStart();



		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	private void printText(){
		try {
			int concentration = Integer.valueOf(etConcentration.getText().toString().trim());
			StringBuilder sb = new StringBuilder();
			sb.append("        Cash receipt                 ");
			sb.append("\n");
			sb.append("date   : ");
			sb.append("2016-11-15     16:00");
			sb.append("\n");
			sb.append("operator:admin");
			sb.append("\n");
			sb.append("Receipt number：1234567890");
			sb.append("\n");
			sb.append("Number Quantity Price discount subtotals");
			sb.append("\n");

            sb.append("Underline——————————————————\n");

            sb.append("Horizontal line--------------------\n");
            sb.append("Horizontal line---------Underline————————\n");
            sb.append("------------------------\n");
            sb.append("——————————————————————————\n");
            sb.append("=== === === = = =   =======\n");
            sb.append("+++++++----————............\n");
            sb.append(".........................\n");
            sb.append(".|||||//////||||||\n");
            /*
			sb.append("-----------------------------");
			sb.append("\n");
			sb.append("AM126   1  1200  0   1200");
			sb.append("\n");
			sb.append("AM127   1  1300  0   1300");
			sb.append("\n");
			sb.append("AM128   1  1400  0   1400");
			sb.append("\n");
			*/
			sb.append("-----------------------------");
			sb.append("\n");
			sb.append("Total sales: 3 ");
			sb.append("\n");
			sb.append("Total price (RMB): 3900");
			sb.append("\n");
			sb.append("Reciprocal amount (RMB): 3900");
			sb.append("\n");
			sb.append("Change the amount (RMB): 0");
			sb.append("\n");


			sb.append("-----------------------------");
			sb.append("\n");
			sb.append("payment method: WeChat payment ");
			sb.append("\n");
			sb.append("See you next time     Please keep a small ticket！");
			sb.append("\n");
			sb.append("-----------------------------");
			sb.append("\n");
			byte[]text = null;
			text = sb.toString().getBytes("GBK");

			addPrintTextWithSize(1, concentration, text);

			sb = new StringBuilder();
			sb.append("   Thank you patronage");
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");

			text = sb.toString().getBytes("GBK");
			addPrintTextWithSize(2, concentration, text);

			mPrintQueue.printStart();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
	private void printMix() {
		try {
			int  concentration = Integer.valueOf(etConcentration.getText().toString().trim());
			StringBuilder sb =new StringBuilder();
			sb.append("        Cash receipt                 ");
			sb.append("\n");
			sb.append("date   : ");
			sb.append("2017-06-28     9:00");
			sb.append("\n");
			sb.append("operator:admin");
			sb.append("\n");
			sb.append("Receipt number：1234567890");
			sb.append("\n");
			sb.append("Number Quantity Price discount subtotals");
			sb.append("\n");
			sb.append("-----------------------------");
			sb.append("\n");
			sb.append("AM126   1  1200  0   1200");
			sb.append("\n");
			sb.append("AM127   1  1300  0   1300");
			sb.append("\n");
			sb.append("AM128   1  1400  0   1400");
			sb.append("\n");
			sb.append("-----------------------------");
			sb.append("\n");
			sb.append("Total sales: 3 ");
			sb.append("\n");
			sb.append("Total price (RMB): 3900");
			sb.append("\n");
			sb.append("Reciprocal amount (RMB): 3900");
			sb.append("\n");
			sb.append("Change the amount (RMB): 0");
			sb.append("\n");


			sb.append("-----------------------------");
			sb.append("\n");
			sb.append("payment method: WeChat payment");
			sb.append("\n");
			sb.append("Welcome to visit please keep a good ticket!");
			sb.append("\n");
			sb.append("-----------------------------");
			sb.append("\n");
			byte[]text = null;
			text = sb.toString().getBytes("GBK");

			addPrintTextWithSize(1, concentration, text);

			sb =new StringBuilder();
			sb.append("   Thank you patronage");
			sb.append("\n");

			text = sb.toString().getBytes("GBK");
			addPrintTextWithSize(2, concentration, text);

			sb = new StringBuilder();
			sb.append("\n");
			text = sb.toString().getBytes("GBK");
			addPrintTextWithSize(1, concentration, text);

			int  mWidth = 300;
			int  mHeight = 60;
			mBitmap=BarcodeCreater.creatBarcode(PrintBarcodeActivity.this,"1234567890", mWidth, mHeight,true, 1);
			byte[] printData =BitmapTools.bitmap2PrinterBytes(mBitmap);
			mPrintQueue.addBmp(concentration, 30, mBitmap.getWidth(), mBitmap.getHeight(), printData);

			sb =new StringBuilder();
			sb.append("\n");
			sb.append("     Sweep download APP more offers");
			sb.append("\n");
			sb.append("\n");
			text = sb.toString().getBytes("GBK");
			addPrintTextWithSize(1, concentration, text);

			mWidth =150;
			mHeight = 150;

			mBitmap=BarcodeCreater.encode2dAsBitmap("1234567890", mWidth, mHeight, 2);
			printData =BitmapTools.bitmap2PrinterBytes(mBitmap);
			mPrintQueue.addBmp(concentration, 100, mBitmap.getWidth(), mBitmap.getHeight(), printData);

			sb =new StringBuilder();
			sb.append("Within 1 month by ticket to the service desk to invoice!");
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");
			text =sb.toString().getBytes("GBK");
			addPrintTextWithSize(1, concentration, text);


			mPrintQueue.printStart();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void showTip(String msg){
		new AlertDialog.Builder(this).setTitle(getString(R.string.tips))
				.setMessage(msg)
				.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
	}

	public void showAlertDialog(String msg) {

		isShowAlertDialog = true;

		builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);

		builder.setTitle(getString(R.string.tips));
		builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//1-> Take the paper test   mWidth Black mark width
				mPosApi.printerSetting(1, 0);
			}
		});
		builder.setNegativeButton(getString(R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						isShowAlertDialog = false;
					}
				});

		builder.create().show();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mBitmap!=null){
			mBitmap.recycle();
		}

		if(mPrintQueue!=null){
			mPrintQueue.close();
		}
	}
}
