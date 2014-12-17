// Copyright 2007-2013 Metaio GmbH. All rights reserved.
package com.ipole.mcfly.steamvalve;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;

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

	
    private static final String MENU_SEARCH = "menu";
    private static final String JOB_SEARCH = "job description";
    private static final String REPORT_SEARCH = "reporting";
    private static final String ASSISTANCE_SEARCH = "assistance call";
    private static final String DIALOG_SEARCH = "dialog";

    private SpeechRecognizer recognizer;
    
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.main);
        makeText(getApplicationContext(), "Preparing the recognizer", Toast.LENGTH_LONG).show();
                

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
                    switchSearch(MENU_SEARCH);
                }
            }
        }.execute();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();
        if (text.equals(JOB_SEARCH))
            switchSearch(JOB_SEARCH);
        else if (text.equals(ASSISTANCE_SEARCH) | text.equals(REPORT_SEARCH)) {
        	switchSearch(MENU_SEARCH);
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
    	//makeText(getApplicationContext(), "beginning of speech", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEndOfSpeech() {
    	//makeText(getApplicationContext(), "end of speech", Toast.LENGTH_SHORT).show();
        /*if (DIGITS_SEARCH.equals(recognizer.getSearchName())
                //|| FORECAST_SEARCH.equals(recognizer.getSearchName())
                || COMMAND_SEARCH.equals(recognizer.getSearchName()))
            switchSearch(KWS_SEARCH);*/
    	//switchSearch(MENU_SEARCH);
  	
    	switchSearch(recognizer.getSearchName());
    	makeText(getApplicationContext(), "end of speech", Toast.LENGTH_SHORT).show();
    	
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
