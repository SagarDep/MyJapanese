package pri.weiqiang.vocabulary;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import pri.weiqiang.daojapanese.DaoMaster;
import pri.weiqiang.daojapanese.DaoSession;
import pri.weiqiang.daojapanese.lesson_titleDao;
import pri.weiqiang.daojapanese.lessonsDao;
import pri.weiqiang.daojapanese.words;
import pri.weiqiang.daojapanese.wordsDao;
import pri.weiqiang.daosql.QWords;
import pri.weiqiang.myjapanese.R;

public class VocabDraggableView extends AppCompatActivity {

	static Random random = new Random();
	// static String[] words =
	// "the of and a to in is be that was he for it with as his I on have at by not they this had are but from or she an which you one we all were her would there their will when who him been has more if no out do so can what up said about other into than its time only could new them man some these then two first may any like now my such make over our even most me state after also made many did must before back see through way where get much go well your know should down work year because come people just say each those take day good how long Mr own too little use US very great still men here life both between old under last never place same another think house while high right might came off find states since used give against three himself look few general hand school part small American home during number again Mrs around thought went without however govern don't does got public United point end become head once course fact upon need system set every war put form water took"
	// .split(" ");
	/* 读取生词表，将生词坐在的classes全部填入words[]中 */
	static String[] words = "标准日本语".split(" ");
	/* 应该与每个gridview的高度一致 */
	int gridivew_height = 180;
	DraggableGridView dgv;
	Button button1, button2;
	ArrayList<String> poem = new ArrayList<String>();
	List<words> words_list = new ArrayList<words>();
	/* 无效：以List<words>即words_list作为每一个元素 */
	List<List<words>> words_each = new ArrayList<List<words>>();
	/* 无效：即使新建一个WordsDiv，还是空指针List<WordsDiv>words_div=new ArrayList<WordsDiv>(); */
	List<words> words_clone = new ArrayList<words>();
	private ArrayList<String> draPostions = new ArrayList<String>();
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private wordsDao words_Dao;
	public final static String URL = "/data/data/pri.weiqiang.myjapanese/files";// 文件的路径
	public final static String DB_FILE_NAME = "vocab.db";// 数据库文件
	SQLiteDatabase db = null;
	SharedPreferences settings_remPos;
	Editor editorsettings_remPos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * R cannot be resolved to a variable:变为import
		 * pri.weiqiang.myjapanese.R;就不会有错了
		 */
		setContentView(R.layout.darg_view);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dgv = ((DraggableGridView) findViewById(R.id.vgv));
//		button1 = ((Button) findViewById(R.id.button1));
//		button2 = ((Button) findViewById(R.id.button2));
		/* Toolbar */
		Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar_drag);
		toolbar.setTitle("单词本");
		toolbar.setNavigationIcon(R.drawable.back_shoes);
		toolbar.setTitleTextColor(Color.argb(255, 255, 255, 255));
		toolbar.setBackgroundColor(Color.argb(255, 255, 143, 161));
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		setListeners();
		File file = new File(URL, DB_FILE_NAME);
		db = SQLiteDatabase.openOrCreateDatabase(file, null);
		daoMaster = new DaoMaster(db);
		DaoMaster.createAllTables(db, true);
		daoSession = daoMaster.newSession();
		words_Dao = daoSession.getWordsDao();
		/* 没有连接在单独类Volesson中会报空指针 */
		QWords qWords = new QWords();
		words_list = qWords.queryWords_Fav(words_Dao);
		/**/
		words_clone.addAll(words_list);
		int words_size = words_list.size();
		Log.e("全部记忆单词数量（未分组）", String.valueOf(words_size));
		for (int i_fav = 0; i_fav < words_size; i_fav++) {
			Log.e("getTranslation", words_list.get(i_fav).getTranslation());
			Log.e("getLesson_id", words_list.get(i_fav).getLesson_id());
		}
		/*
		 * 删除arraylist中某属性重复的值
		 * http://blog.csdn.net/mad1989/article/details/38274359 从前向后
		 */
		for (int i_all = 0; i_all < words_list.size() - 1; i_all++) {
			/* 从后向前 */
			for (int j = words_list.size() - 1; j > i_all; j--) {
				if (words_list.get(j).getLesson_id()
						.equals(words_list.get(i_all).getLesson_id())) {
					// words_test.add(words_list.get(j));
					/* words_list移除，这样减少比较次数（因为已经比较完了） */
					words_list.remove(j);
				}
			}
		}
		for (int i_last = 0; i_last < words_list.size(); i_last++) {
			Log.e("after remove", words_list.get(i_last).getLesson_id());
			draPostions.add(words_list.get(i_last).getLesson_id());
		}
		/*
		 * getSharedPreferences(String name, int
		 * mode)中的name需要与DraggableGridView中一致，一个读，一个写
		 */
		/*
		 * 下边代码没有保证更新情况下正确，这里要增加一个判断，就是需要注意在收藏夹中单词进行删除返回后，UI并灭有进行更新，
		 * 这就导致了remPostions存储的还是旧的东西， 首先是调转后镇江返回UI，再看能不能实现，再商量后续的问题
		 */
		// settings_remPos = getSharedPreferences("remPostions",0);
		// HashMap<String , String> map_remPos = new HashMap<String , String>();
		// map_remPos=(HashMap<String, String>) settings_remPos.getAll();
		// for (int i = 0; i <map_remPos.size(); i++) {
		// Log.e("map_remPos:VOCAB", map_remPos.get("i"+i) );
		// draPostions.set(i, map_remPos.get("i"+i));
		// }
		/*
		 * 增加传入的arraylist,用来记忆DragView的位置（其实就是Lesson_id的顺序），从UI的显示信息可以看到，
		 * UI显示顺序其实是与传入的words_list，
		 * 顺序是一致的，也就是说，如果我控制了传入的words_list的顺序，其实也就控制了DragGridView的顺序
		 */
		dgv.setRememberPositon(draPostions);
		/**/
		// for (int i_last = 0; i_last < draPostions.size(); i_last++) {
		// Log.e("words_list", words_list.get(i_last).getLesson_id() );
		// createTablet(words_list.get(i_last).getLesson_id());
		// }
		for (int i_last = 0; i_last < draPostions.size(); i_last++) {
			Log.e("words_list-146", draPostions.get(i_last));
			createTablet(draPostions.get(i_last));
		}
	}

	private void setListeners() {
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
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dgv.removeViewAt(arg2);
				poem.remove(arg2);
			}
		});
		/*保留用于调试*/
//		button1.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				String word = words[random.nextInt(words.length)];
//				ImageView view = new ImageView(VocabDraggableView.this);
//				view.setImageBitmap(getThumb(word));
//				dgv.addView(view);
//				/* 同时增加滚动长度 */
//				gridivew_height = gridivew_height + 180;
//				// dgv.setLayoutParams(new
//				// LinearLayout.LayoutParams(BIND_ADJUST_WITH_ACTIVITY, i,
//				// BIND_ADJUST_WITH_ACTIVITY));
//				dgv.setLayoutParams(new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.FILL_PARENT, gridivew_height,
//						LinearLayout.LayoutParams.FILL_PARENT));
//				poem.add(word);
//			}
//		});
//		button2.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				String finishedPoem = "";
//				for (String s : poem)
//					finishedPoem += s + " ";
//				new AlertDialog.Builder(VocabDraggableView.this)
//						.setTitle("Here's your poem!").setMessage(finishedPoem)
//						.show();
//			}
//		});
	}

	private Bitmap getThumb(String s) {
		Bitmap alterBitmap_ic_wordbook = BitmapFactory.decodeResource(
				super.getResources(), R.drawable.ic_wordbook).copy(
				Bitmap.Config.ARGB_8888, true);
		;
		Canvas canvas = new Canvas(alterBitmap_ic_wordbook);
		canvas.drawBitmap(alterBitmap_ic_wordbook, 0, 0, null);// 这里是需要填入我们的图片bitmap的，感觉这里就像是图层
		Paint paint = new Paint();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(36);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		// canvas.drawText(s, 75,75, paint);
		/*
		 * 下面代码解决了文字自动换行的问题，http://www.cnblogs.com/hmyprograming/archive/2012/03/
		 * 23/2414173.html
		 */
		TextPaint textPaint = new TextPaint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(36);
		/**
		 * * aboutTheGame ：要 绘制 的 字符串 ,textPaint(TextPaint 类型)设置了字符串格式及属性
		 * 的画笔,240为设置 画多宽后 换行，后面的参数是对齐方式...
		 */
		StaticLayout layout = new StaticLayout(s, textPaint, 150,
				Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
		canvas.translate(55, 75); // 从 (x,y)的位置开始绘制
		layout.draw(canvas);

		return alterBitmap_ic_wordbook;

		/* 随机颜色 */
		// Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.RGB_565);
		// Canvas canvas = new Canvas(alterBitmap_ic_wordbook);
		// Paint paint = new Paint();
		// paint.setColor(Color.rgb(random.nextInt(128), random.nextInt(128),
		// random.nextInt(128)));
		// paint.setTextSize(24);
		// paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		// canvas.drawRect(new Rect(0, 0, 150, 150), paint);
		// paint.setColor(Color.WHITE);
		// paint.setTextAlign(Paint.Align.CENTER);
		// canvas.drawText(s, 75, 75, paint);
		// return bmp;
	}

	/* 54 */
	private void createTablet(String word) {

		ImageView view = new ImageView(VocabDraggableView.this);
		view.setImageBitmap(getThumb(word));
		dgv.addView(view);
		/* 同时增加滚动长度 */
		gridivew_height = gridivew_height + 180;
		// dgv.setLayoutParams(new
		// LinearLayout.LayoutParams(BIND_ADJUST_WITH_ACTIVITY, i,
		// BIND_ADJUST_WITH_ACTIVITY));
		dgv.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, gridivew_height,
				LinearLayout.LayoutParams.FILL_PARENT));
		poem.add(word);
	}
}