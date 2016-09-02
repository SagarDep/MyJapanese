package pri.weiqiang.encryption;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import pri.weiqiang.myjapanese.R;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
/*https://github.com/piterwilson/MP3StreamPlayer/blob/master/src/com/piterwilson/audio/MP3RadioStreamPlayer.java*/

public class JLayerActivity extends Activity {

    private Decoder mDecoder;
    private AudioTrack mAudioTrack;
    private String rootPath = Environment.getExternalStorageDirectory().getPath();
    /*使用new File 所以MyJC后不带'/'*/
	private String playerPath = rootPath + File.separator + "MyJC";
	private File oldFile = new File(playerPath, "9804.mp3");
	//1024*50对于mi1s会报java.lang.IllegalArgumentException: Invalid audio buffer size.
	ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024*2);//全部存入，需要大量内存，但因尝试改小试试//【米3未报错】1024*50
	private static final String seed = "VoiceEncryptionActivity"; // 密匙即种子

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_jlayer);
        /*To get preferred buffer size and sampling rate.http://stackoverflow.com/questions/8043387/android-audiorecord-supported-sampling-rates*/
//        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        /*在米1s上有这个错误，就是因为android版本不见容：java.lang.NoSuchMethodError: android.media.AudioManager.getProperty*/
//        String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
//        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
//        Log.d("Buffer Size and sample rate", "Size :" + size + " & Rate: " + rate);
        
        final int sampleRate = 44100;

        final int minBufferSize = AudioTrack.getMinBufferSize(sampleRate,
        		AudioFormat.CHANNEL_OUT_STEREO,   //MI3：CHANNEL_OUT_STEREO //[]AudioFormat.CHANNEL_OUT_STEREO//CHANNEL_OUT_MONO影响不大，只要是new AudioTrack构建时选择AudioFormat.CHANNEL_OUT_STEREO即可     		
                AudioFormat.ENCODING_PCM_16BIT);
        /*刚刚拿了一个旧的mp2，试过，当然是错误的*/   
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,                
                AudioFormat.ENCODING_PCM_16BIT,/*【复制，粘贴，调参数】[old]CHANNEL_OUT_STEREO 声音嘈杂  CHANNEL_OUT_DEFAULT CHANNEL_IN_DEFAULT有明显的改善* 使用这个有明显的改善，至少可以听出声音了AudioFormat.CHANNEL_CONFIGURATION_DEFAULT,*/
                2*minBufferSize,
                AudioTrack.MODE_STREAM);

        mDecoder = new Decoder();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                	/*与正常播放未加密的mp3不同是的，首先要得到解码的mp3的byte[]数组*/
                	byte[] oldByte_track = new byte[(int) oldFile.length()];          				
                	FileInputStream fis = new FileInputStream(oldFile);
        			fis.read(oldByte_track);
        			byte[] newByte_track = AESUtils.decryptVoice(seed, oldByte_track);
        			fis.close();
    				InputStream in = new ByteArrayInputStream(newByte_track); 
    				Bitstream bitstream = new Bitstream(in);
                	/*播放正常未加密的mp3*/
//                	FileInputStream fis = new FileInputStream(oldFile);
//                	Bitstream bitstream = new Bitstream(fis);
    				/*原demo代码*/
//                	InputStream fis = new InputStream(oldFile);
//                  InputStream in = new URL("http://icecast.omroep.nl:80/radio1-sb-mp3").openConnection().getInputStream();

                    final int READ_THRESHOLD = 2147483647;//我试着改动了，没有变化;
                    int framesReaded = READ_THRESHOLD;

                    Header header;
                    for(; framesReaded-- > 0 && (header = bitstream.readFrame()) != null;) {
                        SampleBuffer sampleBuffer = (SampleBuffer) mDecoder.decodeFrame(header, bitstream);
                        Log.e("header",String.valueOf(header.framesize ));
//                        short[] buffer = sampleBuffer.getBuffer();                        
//                        mAudioTrack.write(buffer, 0, buffer.length);
                        
                        /*54*/
                        short[] buffer = sampleBuffer.getBuffer();
                        for (short s : buffer) {
                          outStream.write(s & 0xff);
                          outStream.write((s >> 8 ) & 0xff);
                          }                                                                       
                        bitstream.closeFrame();
                    }
                    /*54new*/
                    byte[] Byte_JLayer=outStream.toByteArray();
                    mAudioTrack.write(Byte_JLayer, 0, Byte_JLayer.length);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        mAudioTrack.play();
     	mAudioTrack.setPlaybackPositionUpdateListener(null);
        
     
//        player_list.setOnCompletionListener(new CompletionListener());
       
    }
    
//    /*为mediaplayer添加完成减轻监听器*/
//	final class CompletionListener implements OnCompletionListener{
//
//        @Override
//        public void onCompletion(MediaPlayer mp) {
//                        
//        }        
//        
//    }
//	
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAudioTrack.stop();
    }
}