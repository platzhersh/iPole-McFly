/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package edu.cmu.pocketsphinx.demo;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public class PocketSphinxActivity extends Activity implements RecognitionListener {

    private static final String MENU_SEARCH = "menu";
    private static final String JOB_SEARCH = "job description";
    private static final String REPORT_SEARCH = "reporting";
    private static final String ASSISTANCE_SEARCH = "assistance call";
    private static final String DIALOG_SEARCH = "dialog";
    private static final String NEXT_SEARCH = "next step";
    private static final String PREVIOUS_SEARCH = "previous step";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        // Prepare the data for UI
        captions = new HashMap<String, Integer>();
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(JOB_SEARCH, R.string.job_caption);
        captions.put(REPORT_SEARCH, R.string.report_caption);
        captions.put(ASSISTANCE_SEARCH, R.string.assistance_caption);

        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.caption_text))
                .setText("Preparing the recognizer");

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(PocketSphinxActivity.this);
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
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Failed to init recognizer " + result);
                } else {
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
            ((TextView) findViewById(R.id.result_text)).setText(text);
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            ((TextView) findViewById(R.id.result_text)).setText("");
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
        String caption = getResources().getString(captions.get(searchName));
        ((TextView) findViewById(R.id.caption_text)).setText(caption);
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
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
        
        File jobGrammar = new File(modelsDir, "grammar/manual.gram");
        recognizer.addGrammarSearch(JOB_SEARCH, jobGrammar);
        
        File dialogGrammar = new File(modelsDir, "grammar/manual.gram");
        recognizer.addGrammarSearch(DIALOG_SEARCH, dialogGrammar);
        
    }
}
