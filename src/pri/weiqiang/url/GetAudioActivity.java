package pri.weiqiang.url;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import pri.weiqiang.myjapanese.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author yangxiaolong
 * @2014-5-6
 */
public class GetAudioActivity extends AppCompatActivity implements OnClickListener {

	private static final String TAG = GetAudioActivity.class.getSimpleName();

	/** 显示下载进度TextView */
	private TextView mMessageView;
	/** 显示下载进度ProgressBar */
	private ProgressBar mProgressbar;
	private GridView myGridView = null ;						// GridView组件
	private String[] bookRes = new String[] { "大家的日本语第一册", "大家的日本语第二册",
			"新版标准日本语中级上", "新版标准日本语中级下", "新版标准日本语初级上", "新版标准日本语初级下",
			"新编日语I", "新编日语II", "新编日语III","新编日语IV"
			 };	

	private String book_audio_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio);
		/*Toolbar*/ 
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_audio_activity);
        toolbar.setTitle("下载音频文件");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.myGridView = (GridView) super.findViewById(R.id.myGridView) ;	// 取得组件
		this.myGridView.setAdapter(new BookAdapter(this, this.bookRes)); // 设置图片
		this.myGridView.setOnItemClickListener(new OnItemClickListenerImpl()) ;
		findViewById(R.id.download_btn).setOnClickListener(this);
		mMessageView = (TextView) findViewById(R.id.download_message);
		mProgressbar = (ProgressBar) findViewById(R.id.download_progress);
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			book_audio_name=bookRes[position];
			showDialog(book_audio_name);
			
		}
	}
	/*AlertDialog*/
	private void showDialog(String book_name_arg){  

		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.dialog_audio,(ViewGroup) findViewById(R.id.dialog_audio));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(book_name_arg);
		builder.setMessage("下载真人音频资源文件后，可以离线联系单词发音，是否下载"+book_name_arg+"的音频资源文件？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		      @Override
		      public void onClick(DialogInterface dialog, int which) {
		    	  doDownload();
		      }
		  });	
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});	
		builder.setView(dialog);
		builder.show();		 		
    }
	
	
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.download_btn) {
			doDownload();
		}
	}

	/*使用Handler更新UI界面信息*/
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			mProgressbar.setProgress(msg.getData().getInt("size"));
			float temp = (float) mProgressbar.getProgress()/ (float) mProgressbar.getMax();
			int progress = (int) (temp * 100);
			if (progress == 100) {
				Toast.makeText(GetAudioActivity.this, "下载完成！", Toast.LENGTH_LONG).show();
			}
			mMessageView.setText("下载进度:" + progress + " %");

		}
	};/*这里是new了一个对象，所以有";",所以不会报错*/

	/*下载准备工作，获取SD卡路径、开启线程*/
	private void doDownload() {
		// 获取SD卡路径
		String path = Environment.getExternalStorageDirectory()
				+ "/amosdownload/";
		File file = new File(path);
		// 如果SD卡目录不存在创建
		if (!file.exists()) {
			file.mkdir();
		}
		// 设置progressBar初始化
		mProgressbar.setProgress(0);

		// 简单起见，我先把URL和文件名称写死，其实这些都可以通过HttpHeader获取到		
		String downloadUrl ="http://sdl4.yunpan.cn/share.php?method=Share.download&cqid=5768ff8ca62caf199a40c59425db4d6a&dt=4.9c33cc55643e135728a20ca9b0bee5ba&e=1464405110&fhash=6da42fc841856e0996533b112742e1c8cf4dde6c&fname=%25E7%25AA%2597%25E5%258F%25A3%25E6%258A%2596%25E5%258A%25A8%25EF%25BC%2588%25E6%25BA%2590%25E7%25A0%2581%25EF%25BC%2589.rar&fsize=1097184&nid=14585457986152923&st=832a1d15526ef27076229dfabf25b740&xqid=344664014";
		String fileName = "窗口抖动3.rar";
		int threadNum = 5;
		String filepath = path + fileName;
		Log.d(TAG, "download file  path:" + filepath);
		downloadTask task = new downloadTask(downloadUrl, threadNum, filepath);
		task.start();
	}

	/**
	 * 多线程文件下载
	 * 
	 * @author yangxiaolong
	 * @2014-8-7
	 */
	class downloadTask extends Thread {
		private String downloadUrl;// 下载链接地址
		private int threadNum;// 开启的线程数
		private String filePath;// 保存文件路径地址
		private int blockSize;// 每一个线程的下载量

		public downloadTask(String downloadUrl, int threadNum, String fileptah) {
			this.downloadUrl = downloadUrl;
			this.threadNum = threadNum;
			this.filePath = fileptah;
		}

		@Override
		public void run() {

			FileDownloadThread[] threads = new FileDownloadThread[threadNum];
			try {
				URL url = new URL(downloadUrl);
				Log.d(TAG, "download file http path:" + downloadUrl);
				URLConnection conn = url.openConnection();

				// 读取下载文件总大小
				int fileSize = conn.getContentLength();
				/*开始提示读取文件失败，专注于是代码问题，连开始可以下载的文件也不能下载了，是不是地址失效了，原来是网没有连*/
				if (fileSize <= 0) {
					System.out.println(String.valueOf(fileSize));
					System.out.println("读取文件失败");
					return;
				}
				// 设置ProgressBar最大的长度为文件Size
				mProgressbar.setMax(fileSize);

				// 计算每条线程下载的数据长度
				blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
						: fileSize / threadNum + 1;

				Log.d(TAG, "fileSize:" + fileSize + "  blockSize:"+blockSize);

				File file = new File(filePath);
				for (int i = 0; i < threads.length; i++) {
					// 启动线程，分别下载每个线程需要下载的部分
					threads[i] = new FileDownloadThread(url, file, blockSize,
							(i + 1));
					threads[i].setName("Thread:" + i);
					threads[i].start();
				}

				boolean isfinished = false;
				int downloadedAllSize = 0;
				while (!isfinished) {
					isfinished = true;
					// 当前所有线程下载总量
					downloadedAllSize = 0;
					for (int i = 0; i < threads.length; i++) {
						downloadedAllSize += threads[i].getDownloadLength();
						if (!threads[i].isCompleted()) {
							isfinished = false;
						}
					}
					// 通知handler去更新视图组件
					Message msg = new Message();
					msg.getData().putInt("size", downloadedAllSize);
					mHandler.sendMessage(msg);
					// Log.d(TAG, "current downloadSize:" + downloadedAllSize);
					Thread.sleep(1000);// 休息1秒后再读取下载进度
				}
				Log.d(TAG, " all of downloadSize:" + downloadedAllSize);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

}
