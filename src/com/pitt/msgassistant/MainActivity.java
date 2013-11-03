package com.pitt.msgassistant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Jiaxin
 * @version 1.0
 * @since Sep 29, 2013
 */
public class MainActivity extends Activity implements OnClickListener, OnInitListener{
	
	private ImageButton titleBtn,speechBtn;
	private TitlePopup titlePopup;
	
    //voice recognition and general variables
    //variable for checking Voice Recognition support on user device
    private static final int VR_REQUEST = 999;
    private static final String LOG = "MainActivity";

    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech repeatTTS;
    private ListView wordlist;
    private EditText editText;
    private String lastWord;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		initView();		
		initData();
		
		PackageManager packageManager = getPackageManager();
        if (packageManager != null) {
            List<ResolveInfo> intActivities = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);

            // get the user speech result
            if (intActivities.size()!=0){
                speechBtn.setOnClickListener(this);

                //set up the TTS, after click ,it could speak up.
                Intent checkTTSIntent = new Intent();
                checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                //startActivityForResult(Intent intent, int requestCode)
                startActivityForResult(checkTTSIntent,MY_DATA_CHECK_CODE);  // add MY_DATA_CHECK_CODE requestCode

            } else {
                // didn't get user speech result || device don't support
                speechBtn.setEnabled(false);
                Toast.makeText(getApplicationContext(),"your device do not support speech recognition",1).show();
                Log.i(LOG,"devise does not support this machine");
            }
        }

        wordlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView wordView = (TextView)view;
                String wordChosen = (String)wordView.getText();
                Log.i(LOG, "chosen: " + wordChosen);
                Toast.makeText(MainActivity.this, "You said: "+ wordChosen,1).show();

                //int speak(String text, int queueMode, HashMap<String, String> params)  call TTS function
                repeatTTS.speak("You said: " +wordChosen,TextToSpeech.QUEUE_FLUSH,null);
                lastWord = editText.getText().toString();
                Log.i(LOG, lastWord);
                if (lastWord.equals("")) {
                	editText.setText(wordChosen);
				}else {
					editText.setText(editText.getText().toString()+", "+wordChosen);
				}
                
            }
        });
	
	}


	private void initView(){
		titleBtn = (ImageButton) findViewById(R.id.title_btn);
        speechBtn = (ImageButton) this.findViewById(R.id.speech_btn);
        wordlist = (ListView) this.findViewById(R.id.word_list);
        editText = (EditText) this.findViewById(R.id.resultmessage);
		
		titleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				titlePopup.show(v);
			}
		});
		
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, editText);
	}	

	private void initData(){
		titlePopup.addAction(new ActionItem(this, "Message", R.drawable.mm_title_btn_compose_normal));
		titlePopup.addAction(new ActionItem(this, "Email", R.drawable.mm_title_btn_receiver_normal));
		titlePopup.addAction(new ActionItem(this, "Social", R.drawable.mm_title_btn_keyboard_normal));
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
	        case R.id.speech_btn:
	            listenToSpeech();
	            break;
		}
	}	
	
      
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // We use requestCode to justify where it comes from
        // http://blog.sina.com.cn/s/blog_533074eb01011lmg.html

        // get STT result
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
            // get all the results to an ArrayList
            ArrayList<String>  suggestWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // ArrayAdapter(Context context, int resource, List<T> objects)  -- setAdapter
            wordlist.setAdapter(new ArrayAdapter<String>(this, R.layout.word,suggestWords));
        }

        // TTS result
        if (requestCode == MY_DATA_CHECK_CODE) {

            // this code just used for justify if textToSpeech is OK ???
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
                repeatTTS = new TextToSpeech(this, this);
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
    
    private void listenToSpeech() {
        //start the speech recognition intent passing required data
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //indicate package
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        //message to display while listening
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say a word!");
        //set speech model
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //specify number of results to retrieve
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,10);
        //start listening
        startActivityForResult(listenIntent,VR_REQUEST);
    }

	@Override
	public void onInit(int initStatus) {
		// TODO Auto-generated method stub
		//if successful, set locale
        if (initStatus == TextToSpeech.SUCCESS)
            repeatTTS.setLanguage(Locale.UK);

            //***choose your own locale here***  we can set English here!
            //repeatTTS.setLanguage(Locale.CHINA);
            //repeatTTS.setLanguage(Locale.CHINESE);
	}


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
