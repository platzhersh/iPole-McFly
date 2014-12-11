// Copyright 2007-2013 Metaio GmbH. All rights reserved.
package com.ipole.mcfly.steamvalve;

import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.metaio.sdk.ARELActivity;

public class ARELViewActivity extends ARELActivity {

	@Override
	protected int getGUILayout() {
		// Attaching layout to the activity
		return 0;
	}
	
	
	protected void onStart() 
	{
	    super.onStart();

	    // create and add the AREL WebView
	    //mWebView = (WebView)findViewById(R.id.arelwebview);

	    // attach a WebView to the AREL interpreter and initialize it
	    mARELInterpreter.initWebView(mWebView, this);

	    mWebView.addJavascriptInterface(new JsObject(), "myInterface");

	}

	//Here you define all the methods that you want to access from Javascript
	private class JsObject {
	    
		@JavascriptInterface
	    public void showToast(String s){
	        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	    }
	}
}
