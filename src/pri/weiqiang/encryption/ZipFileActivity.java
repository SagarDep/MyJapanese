package pri.weiqiang.encryption;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import pri.weiqiang.myjapanese.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * @author  54wall 
 * @date 创建时间：2016-5-25 下午4:24:03
 * @version 1.0 
 */
public class ZipFileActivity extends Activity {
	private String rootPath = Environment.getExternalStorageDirectory().getPath();
    /*使用new File 所以MyJC后不带'/'*/
	private String audioPackagePath = rootPath + File.separator + "MyJC/mp3.zip";
	private String unzipPath = rootPath + File.separator + "MyJC/Unzip/";
	private ArrayList<ZipEntry> aduioList = new ArrayList<ZipEntry>(); // zip包中的文件列表
    private ZipFile zipFile = null; // 漫画包文件   
    private Button playzipButton;
    private InputStream inputStream;
		public void onCreate(Bundle savedInstanceState) {

				super.onCreate(savedInstanceState);
				this.setContentView(R.layout.activity_zipfile); // 默认布局管理器
				this.playzipButton=(Button)super.findViewById(R.id.playzipBTN);
				playzipButton.setOnClickListener(new View.OnClickListener(){
				
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						initPackage();
						Unzip(audioPackagePath,unzipPath);
					}
				});
						
		}
	    public void initPackage() {	           
	            try {
	                zipFile = new ZipFile(audioPackagePath);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }	        

			@SuppressWarnings("unchecked")
			Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) zipFile.entries(); // 获取zip文件中的目录及文件列表
	        ZipEntry entry = null;	        
	        /*好像仅仅针对一层文件结构*/
	        
	        while (enumeration.hasMoreElements()) {
	            entry =enumeration.nextElement();
	            if (!entry.isDirectory()) {
	                // 如果文件不是目录，则添加到列表中
	            	aduioList.add(entry);
	            	Log.e("entry", entry.getName());
	            }
	        }	 
	        // 播放第一个mp3
	       
	        try {
	        	inputStream=zipFile.getInputStream(aduioList.get(0));
	        	/*http://stackoverflow.com/questions/8900249/can-android-mediaplayer-play-audio-in-a-zipped-file*/
	            // see Note #3.
	            File tempFile = File.createTempFile("_AUDIO_", ".wav");
	            FileOutputStream out = new FileOutputStream(tempFile);
	            /*利用Apache的IOUtils，可能允许来使用tempFile（临时文件）进行播放*/
//	            IOUtils.copy(inputStream, out);
	            // do something with tempFile (like play it)
	            } 
	        catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	    }
	    /*解压文件到指定位置*/
	    private static void Unzip(String zipFile, String targetDir) {
	    	int BUFFER = 4096; //这里缓冲区我们使用4KB，
	    	String strEntry; //保存每个zip的条目名称

	    	try {
	    	BufferedOutputStream dest = null; //缓冲输出流
	    	FileInputStream fis = new FileInputStream(zipFile);
	    	ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	    	ZipEntry entry; //每个zip条目的实例


	    	while ((entry = zis.getNextEntry()) != null) {

	    		
	    	try {
	    	Log.i("Unzip: ","="+ entry);
	    	int count; 
	    	byte data[] = new byte[BUFFER];
	    	strEntry = entry.getName();


	    	File entryFile = new File(targetDir + strEntry);
	    	File entryDir = new File(entryFile.getParent());
	    	if (!entryDir.exists()) {
	    	entryDir.mkdirs();
	    	}


	    	FileOutputStream fos = new FileOutputStream(entryFile);
	    	dest = new BufferedOutputStream(fos, BUFFER);
	    	while ((count = zis.read(data, 0, BUFFER)) != -1) {
	    	dest.write(data, 0, count);
	    	}
	    	dest.flush();
	    	dest.close();
	    	} catch (Exception ex) {
	    	ex.printStackTrace();
	    	}
	    	}
	    	zis.close();
	    	} catch (Exception cwj) {
	    	cwj.printStackTrace();
	    	}
	    	}


	    
	    
}
