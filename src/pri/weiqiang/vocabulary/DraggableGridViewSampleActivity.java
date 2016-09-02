package pri.weiqiang.vocabulary;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Random;

import pri.weiqiang.myjapanese.R;

public class DraggableGridViewSampleActivity extends Activity {
	static Random random = new Random();
	
//	static String[] words = "the of and a to in is be that was he for it with as his I on have at by not they this had are but from or she an which you one we all were her would there their will when who him been has more if no out do so can what up said about other into than its time only could new them man some these then two first may any like now my such make over our even most me state after also made many did must before back see through way where get much go well your know should down work year because come people just say each those take day good how long Mr own too little use US very great still men here life both between old under last never place same another think house while high right might came off find states since used give against three himself look few general hand school part small American home during number again Mrs around thought went without however govern don't does got public United point end become head once course fact upon need system set every war put form water took"
//			.split(" ");

	static String[] words = "标准日本语"
			.split(" ");
	/*应该与每个gridview的高度一致*/
	int i=180;
	DraggableGridView dgv;
	Button button1, button2;
	ArrayList<String> poem = new ArrayList<String>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*R cannot be resolved to a variable:变为import pri.weiqiang.myjapanese.R;就不会有错了*/
        setContentView(R.layout.darg_view);
        
        dgv = ((DraggableGridView)findViewById(R.id.vgv));
//        button1 = ((Button)findViewById(R.id.button1));
//        button2 = ((Button)findViewById(R.id.button2));
        
        setListeners();
    }
    private void setListeners()
    {
    	dgv.setOnRearrangeListener(new DraggableGridView.OnRearrangeListener() {
			public void onRearrange(int oldIndex, int newIndex) {
				String word = poem.remove(oldIndex);
				if (oldIndex < newIndex)
					poem.add(newIndex, word);
				else
					poem.add(newIndex, word);
			}
		});
    	dgv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				dgv.removeViewAt(arg2);
				poem.remove(arg2);
			}
		});
    	button1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String word = words[random.nextInt(words.length)];
				ImageView view = new ImageView(DraggableGridViewSampleActivity.this);
				view.setImageBitmap(getThumb(word));
				dgv.addView(view);
				/*同时增加滚动长度*/
				i=i+180;
//				dgv.setLayoutParams(new LinearLayout.LayoutParams(BIND_ADJUST_WITH_ACTIVITY, i, BIND_ADJUST_WITH_ACTIVITY));
				dgv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, i, LinearLayout.LayoutParams.FILL_PARENT));
				poem.add(word);
			}
		});
    	button2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String finishedPoem = "";
				for (String s : poem)
					finishedPoem += s + " ";
				new AlertDialog.Builder(DraggableGridViewSampleActivity.this)
			    .setTitle("Here's your poem!")
			    .setMessage(finishedPoem).show();
			}
		});
    }
    
    private Bitmap getThumb(String s)
	{
		Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bmp);
	    Paint paint = new Paint();
	    
	    paint.setColor(Color.rgb(random.nextInt(128), random.nextInt(128), random.nextInt(128)));
	    paint.setTextSize(24);
	    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	    canvas.drawRect(new Rect(0, 0, 150, 150), paint);
	    paint.setColor(Color.WHITE);
	    paint.setTextAlign(Paint.Align.CENTER);
	    canvas.drawText(s, 75, 75, paint);
	    
		return bmp;
    	
    	
//    	/*Bitmap.createBitmap与 canvas.drawRect的长宽保持一致*/
//    	/*在DraggableGridView的initAttributes（），无论怎么更改createBitmap，矩形几乎不变*/
//		//Bitmap bmp = Bitmap.createBitmap(250, 250, Bitmap.Config.RGB_565);
//    	/**感觉这里的width控制的是矩形相对字体尺寸的大小，就是填充的百分率*/
//    	Bitmap bmp = Bitmap.createBitmap(320, 320, Bitmap.Config.RGB_565);
//		Canvas canvas = new Canvas(bmp);
//	    Paint paint = new Paint();
//	    
//	    paint.setColor(Color.rgb(random.nextInt(128), random.nextInt(128), random.nextInt(128)));
//	    paint.setTextSize(48);
//	    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
//	    /*Rect的长宽应该等于canvas的长宽*/
//	    canvas.drawRect(new Rect(0, 0, 320, 320), paint);
////	    canvas.drawRect(new Rect(0, 0, 250, 250), paint);
//	    paint.setColor(Color.WHITE);
//	    paint.setTextAlign(Paint.Align.CENTER);
////	    canvas.drawText(s, 75, 75, paint);
//	    /*正好是canvas.drawRect中长宽的一半*/
//	    canvas.drawText(s, 160, 160, paint);
//	    canvas.rotate(90);
//	    /*http://www.cnblogs.com/hmyprograming/archive/2012/03/23/2414173.html*/
////	    TextPaint textPaint = new TextPaint();
////	    textPaint.setARGB(0xFF, 0xFF, 0, 0);
////	    textPaint.setTextSize(20.0F);
////	    String aboutTheGame = "关于本游戏：本游戏是做测试用的，这些文字也是，都不是瞎写的！ ";
////	    /** aboutTheGame ：要 绘制 的 字符串   ,textPaint(TextPaint 类型)设置了字符串格式及属性 的画笔,240为设置 画多宽后 换行，后面的参数是对齐方式...*/
////	    StaticLayout layout = new StaticLayout(aboutTheGame,textPaint,240,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
////	    //从 (20,80)的位置开始绘制
////	    canvas.translate(20,80);
////	    layout.draw(canvas);
////	    canvas.restore();
//		return bmp;
	}
}