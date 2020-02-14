package com.probatus.rhbus.warehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.valdesekamdem.library.mdtoast.MDToast;
import com.webianks.easy_feedback.components.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import spencerstudios.com.bungeelib.Bungee;

/**
 * Activity - That appears when your application crashes.
 * @author Ajay
 *
 */
public class CrashActivity extends Activity {

	String directoryPath = Environment.getExternalStorageDirectory()+ "/RHBUS/";
	String filePath = directoryPath + "Logs/ErrorLog.txt";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crash_activity);

		saveAsFile(getIntent().getStringExtra("STACKTRACE"),CrashActivity.this);

		final TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setText("Unexpected Error occurred. \nPlease Send LOG to RHBUS.\n" +
				"TO RESTART CLEAR THE CACHE AND LOGIN IT AGAIN!!");

		findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// so it will first save the error trace in vm folder of parent directory of SD card
				sendErrorMail(CrashActivity.this, filePath);
			}
		});

	}

	/**
	 * This list a set of application which can send email.
	 * Here user have to pick one apps via email will be send to developer email id.
	 //* @param _context
	 * @param filePath
	 */
	private void sendErrorMail(Context mContext, String filePath) {
		try {
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			String subject = "Error Description"; // here subject
			String body = "Sorry for your inconvenience .\nWe assure you that we will solve this problem as soon possible."
					+ "\n\nThanks for using app."; // here email body

			sendIntent.setType("plain/text");
			sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			sendIntent.putExtra(Intent.EXTRA_EMAIL,
					new String[]{"rhbussolutions@gmail.com"}); // your developer email id
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
			sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",new File(filePath)));
			sendIntent.setType("message/rfc822");
			startActivity(Utils.createEmailOnlyChooserIntent(this, sendIntent, getString(R.string.app_version)));
			//mContext.startActivity(Intent.createChooser(sendIntent, "Complete action using"));
		}catch (Exception e){
			MDToast.makeText(getApplication(), ""+ e, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
		}
	}

	void saveAsFile(String ErrorContent, Context context) {
		Log.e("Ganesh",""+ErrorContent);
		try {
			File root = new File(Environment.getExternalStorageDirectory(), "RHBUS");
			if (!root.exists()) {
				root.mkdirs();
			}
			root = new File(directoryPath, "Logs");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, "ErrorLog.txt");
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(ErrorContent);
			writer.flush();
			writer.close();
		}catch (IOException e){
			MDToast.makeText(getApplication(), ""+ e, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
		}catch (Exception e){
			MDToast.makeText(getApplication(), ""+ e, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			Intent tender = new Intent(CrashActivity.this, LoginActivity.class);
			startActivity(tender);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause(){
		Bungee.zoom(this);
		finish();
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
}
