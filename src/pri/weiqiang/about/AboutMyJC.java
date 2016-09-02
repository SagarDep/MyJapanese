package pri.weiqiang.about;

import java.io.File;

import pri.weiqiang.myjapanese.GuideActivity;
import pri.weiqiang.myjapanese.R;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * @author  54wall 
 * @date 创建时间：2016-5-19 上午9:01:58
 * @version 1.0 
 */
public class AboutMyJC  extends AppCompatActivity {
	

//	Resources  r =this.getResources();
//	Resources  r = getApplicationContext().getResources();
	Resources  r;
    Button btn_guide_again;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main_about);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
		WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		r = getResources();
    	Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);    	
    	toolbar.setTitle("关于鲤鱼日语");
		toolbar.setNavigationIcon(R.drawable.back_shoes);
		toolbar.setTitleTextColor(Color.argb(255, 255, 255, 255));
		toolbar.setBackgroundColor(Color.argb(255, 255, 143, 161));
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		btn_guide_again=(Button)findViewById(R.id.btn_guide_again);
		btn_guide_again.setTextColor(Color.argb(255, 0, 150, 136));
		btn_guide_again.setOnClickListener(new View.OnClickListener(){
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent case_2 = new Intent(AboutMyJC.this,
						GuideActivity.class);
				AboutMyJC.this.startActivity(case_2);
			}
		});

    	
    }
    
    /** 
     * 分享功能 
     *  
     * @param context 
     *            上下文 
     * @param activityTitle 
     *            Activity的名字 
    * @param msgTitle 
     *            消息标题 
     * @param msgText 
    *            消息内容 
     * @param imgPath 
     *            图片路径，不分享图片则传null 
     */ 
    public void shareMsg(String activityTitle, String msgTitle, String msgText,  
            String imgPath) {  
        Intent intent = new Intent(Intent.ACTION_SEND);  
        if (imgPath == null || imgPath.equals("")) {  
            intent.setType("text/plain"); // 纯文本  
        } else {
        	//http://blog.csdn.net/u011131296/article/details/46636897
//        	Uri u =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"  
//            		+ r.getResourcePackageName(R.drawable.welcome_android) + "/"  
//            		+ r.getResourceTypeName(R.drawable.welcome_android) + "/"  
//            		+ r.getResourceEntryName(R.drawable.welcome_android));          
//            intent.putExtra(Intent.EXTRA_STREAM, u);
       	
            File f = new File(imgPath);          
            if (f != null && f.exists() && f.isFile()) {  
                intent.setType("image/jpg");                  
                Uri u = Uri.fromFile(f);              
                intent.putExtra(Intent.EXTRA_STREAM, u);  
            }  
        }  
//    	Uri u =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"  
//        		+ r.getResourcePackageName(R.drawable.welcome_android) + "/"  
//        		+ r.getResourceTypeName(R.drawable.welcome_android) + "/"  
//        		+ r.getResourceEntryName(R.drawable.welcome_android));
//    	Log.e("u",  String.valueOf(u));
//        intent.putExtra(Intent.EXTRA_STREAM, u);
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);  
        intent.putExtra(Intent.EXTRA_TEXT, msgText);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        startActivity(Intent.createChooser(intent, activityTitle));  
    } 
    
}