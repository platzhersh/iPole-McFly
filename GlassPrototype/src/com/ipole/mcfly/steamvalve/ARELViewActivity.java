// Copyright 2007-2013 Metaio GmbH. All rights reserved.
package com.ipole.mcfly.steamvalve;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.metaio.sdk.ARELActivity;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;


public class ARELViewActivity extends ARELActivity implements RecognitionListener {

	@Override
	protected int getGUILayout() {
		// Attaching layout to the activity
		return 0;
	}
	
	
	@Override
	@SuppressLint("SetJavaScriptEnabled") 
	protected void onStart() 
	{
	    super.onStart();

	    // create and add the AREL WebView
	    //mWebView = (WebView)findViewById(R.id.arelwebview);

	    // attach a WebView to the AREL interpreter and initialize it
	    mARELInterpreter.initWebView(mWebView, this);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.addJavascriptInterface(new JsObject(), "myInterface");
	    //myWebView.loadUrl("file:///android_asset/html/index.html");
	    //makeText(getApplicationContext(), "WebView ready " + mWebView.getUrl(), Toast.LENGTH_LONG).show();

	}

	//Here you define all the methods that you want to access from Javascript
	private class JsObject {
	    
		@JavascriptInterface
	    public void showToast(String s){
	        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	    }
	}

	
    private static final String MENU_SEARCH = "menu";
    private static final String JOB_SEARCH = "job description";
    private static final String REPORT_SEARCH = "reporting";
    private static final String ASSISTANCE_SEARCH = "assistance call";
    private static final String DIALOG_SEARCH = "dialog";
    private static final String SHOW_HELP = "show help";
    private static final String HELP = "help";
    private static final String HIDE_HELP = "hide help";
    private static final String CLOSE_HELP = "close help";
    private static final String CLOSE = "close";
    private static final String CANCEL = "cancel";
    private static final String HAPPY_EASTER = "back to the future";
    //private static final String ACCEPT = "accept";
    //private static final String CANCEL = "cancel";

    private SpeechRecognizer recognizer;
    
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        makeText(getApplicationContext(), "Starting voice recognition", Toast.LENGTH_LONG).show();
                

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(ARELViewActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                	makeText(getApplicationContext(), "Failed to init recognizer " + result, Toast.LENGTH_LONG).show();
                	Log.e("Sphinx", result.toString());
                } else {
                	System.out.println("MENU_SEARCH == null: " + (MENU_SEARCH == null));
                	makeText(getApplicationContext(), "Voice recognition ready", Toast.LENGTH_LONG).show();
                    switchSearch(MENU_SEARCH);
                }
            }
        }.execute();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();
        if (text.equals(JOB_SEARCH)) {
        	// call Javascript
        	mWebView.loadUrl("javascript:toastFunctionTL()");
        	switchSearch(JOB_SEARCH);
        }            
        else if (text.equals(ASSISTANCE_SEARCH)){
        	mWebView.loadUrl("javascript:toastFunctionBR()");
        	switchSearch(MENU_SEARCH);
        }
        else if(text.equals(REPORT_SEARCH)) {
        	mWebView.loadUrl("javascript:toastFunctionTR()");
        	switchSearch(MENU_SEARCH);
        } else if (text.equals(SHOW_HELP) | text.equals(HELP)) {
        	mWebView.loadUrl("javascript:showHelp()");      
        } else if (text.equals(HIDE_HELP) | text.equals(CLOSE_HELP) | text.equals(CLOSE) | text.equals(CANCEL)) {
        	mWebView.loadUrl("javascript:hideHelp()");      
        } else if (text.equals(HAPPY_EASTER)) {
        	mWebView.loadUrl("javascript:backToTheFuture()");      
        }
        
        	
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    
    }

    @Override
    public void onEndOfSpeech() {
       	switchSearch(recognizer.getSearchName());
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        recognizer.startListening(searchName);
    }

    private void setupRecognizer(File assetsDir) {
        File modelsDir = new File(assetsDir, "models");
        recognizer = defaultSetup()
                .setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
                .setDictionary(new File(modelsDir, "dict/cmu07a.dic"))
                .setRawLogDir(assetsDir).setKeywordThreshold(1e-20f)
                .getRecognizer();
        recognizer.addListener(this);

        // Create keyword-activation search.
        // recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        // Create grammar-based searches.
        File menuGrammar = new File(modelsDir, "grammar/menu.gram");
        
        System.out.println("menugrammar == null: " + (menuGrammar == null));
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
        
        File jobGrammar = new File(modelsDir, "grammar/manual.gram");
        recognizer.addGrammarSearch(JOB_SEARCH, jobGrammar);
        
        File dialogGrammar = new File(modelsDir, "grammar/manual.gram");
        recognizer.addGrammarSearch(DIALOG_SEARCH, dialogGrammar);
        
    }

}
