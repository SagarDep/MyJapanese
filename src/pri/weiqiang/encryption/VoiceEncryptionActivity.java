package pri.weiqiang.encryption;

import java.io.File;  
import java.io.FileDescriptor;
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;    
import javazoom.jl.decoder.Decoder;
import pri.weiqiang.myjapanese.R;
import android.app.Activity;  
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;  
import android.os.Bundle;  
import android.os.Environment;  
import android.util.Log;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button;  
import android.widget.Toast;  
/**
 * @author  54wall 
 * @date 创建时间：2016-5-23 上午10:40:09
 * @version 1.0
 * http://blog.csdn.net/u012964281/article/details/41787857 
 */
public class VoiceEncryptionActivity extends Activity implements  
        OnClickListener {
	private Decoder mDecoder;
    private static final String TAG = "VoiceEncryptionActivity"; 
    /*种子不同就无法解密，可以深入了解下加密原理*/
    private static final String seed = "VoiceEncryptionActivity"; // 种子  
    private MediaPlayer mPlayer; 
    /*AudioTrack*/
    private AudioTrack audioTrack;
    private AudioTrack mAudioTrack;
    private Button mPlayButton;  
    private Button mEncryptionButton;  
    private Button mDecryptionButton;  
    private Button mEncrypAllButton;
    private Button mTrackPlayerButton;
    private Button mJLayerButton;
    private Button mZipButton;
    private Button mMediaCoderButton;
    private File sdCard = Environment.getExternalStorageDirectory();  
//    private File oldFile = new File(sdCard, "recording_old.3gpp");  
    // 音频文件的路径，在res\raw\recording_old.3gpp中找到音频文件，再放到外部存储的根目录下。用于测试  
    private FileInputStream fis = null;  
    private FileOutputStream fos = null;  
    private String rootPath = Environment.getExternalStorageDirectory().getPath();
    /*54*/
    /*使用new File 所以MyJC后不带'/'*/
	private String playerPath = rootPath + File.separator + "MyJC";
	private String playerPath_after = rootPath + File.separator + "MyJC_after";
	
	private String singlePath;
	private File oldFile = new File(playerPath, "9804.mp3"); 
	File oldFile_each;
	File oldFile_each_after;
	private String eachName;
	
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_voice_encryption);  
        mPlayButton = (Button) findViewById(R.id.playButton_encryp);  
        mPlayButton.setOnClickListener(this);  
        mEncryptionButton = (Button) findViewById(R.id.encryptionButton);  
        mEncryptionButton.setOnClickListener(this);  
        mDecryptionButton = (Button) findViewById(R.id.decryptionButton);  
        mDecryptionButton.setOnClickListener(this); 
        mEncrypAllButton = (Button) findViewById(R.id.encrypAllButton);  
        mEncrypAllButton.setOnClickListener(this); 
        mTrackPlayerButton=(Button)findViewById(R.id.trackButton);
        mTrackPlayerButton.setOnClickListener(this); 
        /*开始忘记注册toJLayerButton了，结果出现了Timeline: Activity_launch_request time:163908273*/
        mJLayerButton=(Button)findViewById(R.id.toJLayerButton);
        mJLayerButton.setOnClickListener(this); 
        mZipButton=(Button)findViewById(R.id.toZipButton);
        mZipButton.setOnClickListener(this); 
        singlePath=playerPath+"9804.mp3";
        mMediaCoderButton=(Button)findViewById(R.id.mediacodecButton);
        mMediaCoderButton.setOnClickListener(this); 
        

  
    }  
  
    @SuppressWarnings("static-access")  
    @Override  
    public void onClick(View v) {  
        switch (v.getId()) {  
        case R.id.playButton_encryp:  
            if (mPlayer != null) {  
                mPlayer.release();  
                mPlayer = null;  
            }  
            // mPlayer = MediaPlayer.create(this, R.raw.recording_old);  
            boolean isSuccess = true;  
           
            try {  
                fis = new FileInputStream(oldFile);  
                mPlayer = new MediaPlayer();  
                mPlayer.setDataSource(fis.getFD());  
                mPlayer.prepare(); // 去掉会出错  
                mPlayer.start();  
            } catch (FileNotFoundException e) {  
                isSuccess = false;  
                e.printStackTrace();  
            } catch (IllegalArgumentException e) {  
                isSuccess = false;  
                e.printStackTrace();  
            } catch (IllegalStateException e) {  
                isSuccess = false;  
                e.printStackTrace();  
            } catch (IOException e) {  
                isSuccess = false;  
                e.printStackTrace();  
            } finally {  
                try {  
                    fis.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (!isSuccess)  
                Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();  
            break;  
  
        case R.id.encryptionButton:  
            // 加密保存  
            isSuccess = true;  
            try {  
                fis = new FileInputStream(oldFile);  
                byte[] oldByte = new byte[(int) oldFile.length()];  
                fis.read(oldByte); // 读取  
                byte[] newByte = AESUtils.encryptVoice(seed, oldByte);  
                // 加密  
                fos = new FileOutputStream(oldFile);  
                fos.write(newByte);  
  
            } catch (FileNotFoundException e) {  
                isSuccess = false;  
                e.printStackTrace();  
            } catch (IOException e) {  
                isSuccess = false;  
                e.printStackTrace();  
            } catch (Exception e) {  
                isSuccess = false;  
                e.printStackTrace();  
            } finally {  
                try {  
                    fis.close();  
                    fos.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (isSuccess)  
                Toast.makeText(this, "加密成功", Toast.LENGTH_SHORT).show();  
            else  
                Toast.makeText(this, "加密失败", Toast.LENGTH_SHORT).show();  
  
            Log.i(TAG, "保存成功");  
            break;  
  
        case R.id.decryptionButton:  
            // 解密保存  
            isSuccess = true;  
            byte[] oldByte = new byte[(int) oldFile.length()];  
            try {  
                fis = new FileInputStream(oldFile);  
                fis.read(oldByte);  
                byte[] newByte = AESUtils.decryptVoice(seed, oldByte);  
                // 解密  
                fos = new FileOutputStream(oldFile);  
                fos.write(newByte);  
  
            } catch (FileNotFoundException e) {  
                isSuccess = false;  
                e.printStackTrace();  
            } catch (IOException e) {  
                isSuccess = false;  
                e.printStackTrace();  
            } catch (Exception e) {  
                isSuccess = false;  
                e.printStackTrace();  
            }  
            try {  
                fis.close();  
                fos.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            if (isSuccess)  
                Toast.makeText(this, "解密成功", Toast.LENGTH_SHORT).show();  
            else  
                Toast.makeText(this, "解密失败", Toast.LENGTH_SHORT).show();  
            break;  
            /*将全部音频进行加密*/
        case R.id.encrypAllButton:  
        	for (int i_mp3 = 9804; i_mp3 < 9859; i_mp3++) {
        		eachName=i_mp3+".mp3";
                try {  
                      
                    oldFile_each = new File(playerPath, eachName);                    
                    oldFile_each_after=new File(playerPath_after, eachName);
                    
                    fis = new FileInputStream(oldFile_each);
                    byte[] oldByte_each = new byte[(int) oldFile_each.length()];  
                    fis.read(oldByte_each); // 读取  
                    
                    byte[] newByte = AESUtils.encryptVoice(seed, oldByte_each);  // 加密
                    
                    fos = new FileOutputStream(oldFile_each_after);//储存  
                    fos.write(newByte);  
      
                } catch (FileNotFoundException e) {  
                    isSuccess = false;  
                    e.printStackTrace();  
                } catch (IOException e) {  
                    isSuccess = false;  
                    e.printStackTrace();  
                } catch (Exception e) {  
                    isSuccess = false;  
                    e.printStackTrace();  
                } finally {  
                    try {  
                        fis.close();  
                        fos.close();  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                    Log.e("eachName" ,i_mp3+"");
                }                                                    
			}
            break;  
            /*使用AudioTrack对解密后的音频直接输出，不保存在外部存储器上*/
        case R.id.trackButton:    
            isSuccess = true;  
            byte[] oldByte_track = new byte[(int) oldFile.length()];  
            /* java.lang.IllegalArgumentException: Invalid audio buffer size.
             * 初始没有大小也是不行的，只能byte[] newByte_track = AESUtils.decryptVoice(seed, oldByte_track);
             * byte[] newByte_track;*/         
		try {
				
				fis = new FileInputStream(oldFile);
				fis.read(oldByte_track);
				byte[] newByte_track = AESUtils.decryptVoice(seed, oldByte_track);
				
				
				/************************************/
//				InputStream sbs = new ByteArrayInputStream(newByte_track); 
//				FileInputStream fsbs = (FileInputStream)sbs;
//				 mPlayer = new MediaPlayer();  	                	              
//	                mPlayer.setDataSource(fsbs.getFD());  
//	                mPlayer.prepare(); // 去掉会出错  
//	                mPlayer.start();
	                /*stackoverflow.com搜memoryfile*/
//				 MemoryFile memoryFile = new MemoryFile("MemoryFile_1", fis.available());
//		            memoryFile.allowPurging(false);			           
//		            OutputStream out = memoryFile.getOutputStream();
//		            out.write(newByte_track);
//		            out.close();
//		            InputStream in = memoryFile.getInputStream();
//		            FileInputStream fin = (FileInputStream)in;
//	                mPlayer = new MediaPlayer();  	                	              
//	                mPlayer.setDataSource(fin.getFD());  
//	                mPlayer.prepare(); // 去掉会出错  
//	                mPlayer.start();  
				/************************************/
				/*把byte[]转换成输入流，再交给JLayer对输入流进行解码（目的是正常对mp3）进行解码，从而AudioTrack才能正常播放
				 * http://javapub.iteye.com/blog/665696*/
//				InputStream in = new ByteArrayInputStream(newByte_track); 
//				Bitstream bitstream = new Bitstream(in);
//				 final int READ_THRESHOLD = 2147483647;
//                 int framesReaded = READ_THRESHOLD;                 
//                 final int sampleRate = 44100;
//                 final int minBufferSize = AudioTrack.getMinBufferSize(sampleRate,
//                         AudioFormat.CHANNEL_OUT_STEREO,
//                         AudioFormat.ENCODING_PCM_16BIT);
//                 mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
//                         sampleRate,
//                         AudioFormat.CHANNEL_OUT_STEREO,
//                         AudioFormat.ENCODING_PCM_16BIT,
//                         minBufferSize,
//                         AudioTrack.MODE_STREAM);                 
//                 Header header;
//                 for(; framesReaded-- > 0 && (header = bitstream.readFrame()) != null;) {
//                     SampleBuffer sampleBuffer = (SampleBuffer) mDecoder.decodeFrame(header, bitstream);
//                     short[] buffer = sampleBuffer.getBuffer();                   
//                     mAudioTrack.write(buffer, 0, buffer.length);
//                     bitstream.closeFrame();
//                 }
//
//                 bitstream.closeFrame();                 
//                 mAudioTrack.play();
//                 Log.e("eachName" ,"");
				
				
				
				
				
				
				
				
				
				
				/* https://github.com/twitter-university/AudioTrackPlayerDemo
				 * 可以正常播放demo提供的wmv，其他为验证，应该也需要对wmv进行正常的解码*/
				Log.d(TAG, "Stopping");
				/*会有java.lang.NullPointerException在这句*/
//				audioTrack.stop();
				Log.d(TAG, "Releasing");
//				audioTrack.release();
				this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
						AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
						newByte_track.length, AudioTrack.MODE_STATIC);
				Log.d(TAG, "Writing audio data...");
				this.audioTrack.write(newByte_track, 0, newByte_track.length);
				Log.d(TAG, "Starting playback");
				audioTrack.play();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}  

            break;  
            /*想到一个问题就是即使jlayer可以正常解码mp3，但是对于加密后的mp3（即内部已经全部打乱的）肯定是无法解码的*/
        case R.id.toJLayerButton: 
        	Intent it = new Intent(VoiceEncryptionActivity.this,
					JLayerActivity.class);
        	VoiceEncryptionActivity.this.startActivity(it);
        	break;
        	
        case R.id.toZipButton: 
        	Intent it_1 = new Intent(VoiceEncryptionActivity.this,
					ZipFileActivity.class);
        	VoiceEncryptionActivity.this.startActivity(it_1);
        	break;	
        	
        	
        case R.id.mediacodecButton: 
        	
        	// 解密保存  
            isSuccess = true;  
//            MediaCodecTest mediaTest=new MediaCodecTest();
//            byte[] newByte_mediacodec=mediaTest.decode(rootPath + File.separator + "MyJC/9804.mp3");
            byte[] newByte_mediacodec=null;
            /*MediaCoder2*/
            
            try {
				fis = new FileInputStream(oldFile);
				FileDescriptor descriptor=fis.getFD();
				 MediaCoder2 mediaCoder2=new MediaCoder2();
				mediaCoder2.decode(descriptor.toString());
			
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            MediaCoder2 mediaCoder2=new MediaCoder2();
            
            mediaCoder2.decode(rootPath + File.separator + "MyJC/9804.mp3");
            Log.e("路径", rootPath + File.separator + "MyJC/9804.mp3");
			try {
/*开始我仅仅写了byte[] oldByte_mediacodec= new byte[(int) oldFile.length()];其余什么都没有写（只有fis.read(oldByte); // 读取 发生后才完成读取 ）*/            
				fis = new FileInputStream(oldFile);
				byte[] oldByte_mediacodec= new byte[(int) oldFile.length()];
	            fis.read(oldByte_mediacodec); // 读取  
				Log.d(TAG, "Stopping");
				/*会有java.lang.NullPointerException在这句*/
//				audioTrack.stop();
				Log.d(TAG, "Releasing");
				
//				audioTrack.release();
				byte[] newByte_track;
				this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
						AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
						newByte_mediacodec.length, AudioTrack.MODE_STATIC);
				Log.d(TAG, "Writing audio data...");
				this.audioTrack.write(newByte_mediacodec, 0, newByte_mediacodec.length);
				
//				this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
//						AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
//						oldByte_mediacodec.length, AudioTrack.MODE_STATIC);
//				Log.d(TAG, "Writing audio data...");
//				this.audioTrack.write(oldByte_mediacodec, 0, oldByte_mediacodec.length);
				
				Log.d(TAG, "Starting playback");
				audioTrack.play();
				
			} catch (Exception e) {
				e.printStackTrace();
			}         	
        	break;
        	
        default:  
            break;  
        }  
  
    }  
}  
