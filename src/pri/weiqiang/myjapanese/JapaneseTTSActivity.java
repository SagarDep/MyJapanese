package pri.weiqiang.myjapanese;

import java.io.File;
import java.util.HashMap;

import pri.weiqiang.myjapanese.JapaneseTTS.State;

import android.app.Activity;
import android.media.AudioManager;



import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class JapaneseTTSActivity extends Activity {
    
    private EditText inputEdit;
    private Button speakButton;
    private TextView stateText;
    private Spinner speakerSpinner;
    private JapaneseTTS tts;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.tts);
        
        tts = new JapaneseTTS(this, null);
        tts.setOnStateChangedListener(onStateChanged);
        tts.setOnUtteranceCompletedListener(onTtsComplete);
        tts.setOnErrorListener(onError);
        
        inputEdit = (EditText)findViewById(R.id.inputEdit);
        speakButton = (Button)findViewById(R.id.speakButton);
        speakButton.setOnClickListener(onSpeakButtonClick);
        stateText = (TextView)findViewById(R.id.stateText);
        speakerSpinner = (Spinner)findViewById(R.id.speakerSpinner);
        
        String[] speakers = {"male01", "female01", "male02"};
        ArrayAdapter<String> adapter =
            new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, speakers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speakerSpinner.setAdapter(adapter);
        speakerSpinner.setSelection(0);
        
    }
    
    private OnClickListener onSpeakButtonClick = new OnClickListener() {
        public void onClick(View v) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_MUSIC));
//            params.put(JapaneseTTS.KEY_PARAM_SPEAKER,
//                    (String)speakerSpinner.getSelectedItem());
            params.put(JapaneseTTS.KEY_PARAM_SPEAKER,
            		"male01");
            tts.speak(inputEdit.getText().toString(), TextToSpeech.QUEUE_FLUSH, params);
            saveToFile();
        }
    };

    private JapaneseTTS.OnStateChangedListener onStateChanged =
            new JapaneseTTS.OnStateChangedListener() {
        public void onStateChanged(State state, String utteranceId) {
            if (state == JapaneseTTS.State.LOADING) {
                stateText.setText("语音识别中");
            } else if (state == JapaneseTTS.State.SPEAKING) {
                stateText.setText("语音输出");
            }
        }
    };
    
    private OnUtteranceCompletedListener onTtsComplete = new OnUtteranceCompletedListener() {
        public void onUtteranceCompleted(String utteranceId) {
            stateText.setText("");
        }
    };
    /**/
    private JapaneseTTS.OnErrorListener onError =
            new JapaneseTTS.OnErrorListener() {
        public void onError(Exception exception, String utteranceId) {
            stateText.setText(String.format("Err[: %s", exception.toString()));
        }
    };
/*音频缓存可以保存到本地，将保存的文件必须放置到文件夹，而不是指定位置*/
    private void saveToFile() {
        final String text = inputEdit.getText().toString();
        new Thread(){
            public void run() {
                if (tts.synthesizeToFile(text, null, Environment.getExternalStorageDirectory().getPath()+"/JapaneseTTS/"+text+".mp3") ==
                    TextToSpeech.SUCCESS) {
                	/*android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
                	 *只有主线程可以对UI进行修改，这一直是按Android的原则 
                	 * */
//                    stateText.setText("保存完毕");
                }
            }
        }.start();
    }
    
}