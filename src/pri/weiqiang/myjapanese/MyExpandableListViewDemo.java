package pri.weiqiang.myjapanese;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;
import java.lang.reflect.Field;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.tuesda.circlerefreshlayout.CircleRefreshLayout;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.speech.tts.TextToSpeech;

import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.ImageView;
import pri.weiqiang.about.AboutMyJC;
/*加入greenDao*/
import pri.weiqiang.daojapanese.DaoMaster;
import pri.weiqiang.daojapanese.DaoSession;
import pri.weiqiang.daojapanese.lessons;
import pri.weiqiang.daojapanese.words;
import pri.weiqiang.daojapanese.wordsDao;
import pri.weiqiang.daojapanese.lesson_titleDao;
import pri.weiqiang.daojapanese.lessonsDao;
import pri.weiqiang.daosql.QLessons;
import pri.weiqiang.daosql.QWords;
import pri.weiqiang.encryption.VoiceEncryptionActivity;
import pri.weiqiang.random.RandomEntity;
import pri.weiqiang.search.SearchActivity;


import pri.weiqiang.url.GetAudioActivity;
import pri.weiqiang.vocabulary.VocabDraggableView;

;

public class MyExpandableListViewDemo extends Activity implements
		OnGestureListener {
	
	CharSequence[] items = { "默认乱序", "展开全部"};
	boolean[] checkedItems = {false,false};
	
	/* 有的otf文件有错误，会报native typeface cannot be made，是因为替换时把“fonts/”落下了 */
	static String fontPath_child = "fonts/A-OTF-NachinStd-Regular.otf";
	static Typeface tf_child;
	static String fontPath_group = "fonts/A-OTF-NachinStd-Regular.otf";
	/* tf = Typeface.createFromAsset(getAssets(), fontPath);直接放这里还是错误的 */
	static Typeface tf_group;
	private MediaPlayer player_word;
	private MediaPlayer player_list;
	private MediaPlayer player_word_c;
	private MediaPlayer player_list_c;
	private String rootPath = Environment.getExternalStorageDirectory()
			.getPath();
	private String playerPath = rootPath + File.separator + "MyJC/";
	private String singlePath;
	private String singlePath_c;
	private ExpandableListView elistview; // 定义树型组件
	private ExpandableListAdapter adapter; // 定义适配器对象
	private Spinner classes = null;// 下拉列表框内容
	private Spinner books = null;// 下拉列表框内容
	private Spinner classes_dialog = null;// 下拉列表框内容
	private Spinner books_dialog = null;// 下拉列表框内容
	private CheckBox show = null;
	private CheckBox checkBox_random = null;
	private Button circlerefresh_stop = null;// 下拉列表框内容
	/* 加入greenDao */
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private wordsDao words_Dao;
	private lesson_titleDao title_Dao;
	private lessonsDao lessons_Dao;
	/* classes_name必须有初始值，否则会无法初始化 */
	String classes_name = null;// String classes_name="新编日语I_01";
	/* 为了update可以方便的更改entity，所以使用全局变量 */
	List<words> words_list = new ArrayList<words>();
	List<words> words_List_backup = new ArrayList<words>();
	List<words> ran_list = new ArrayList<words>();
	/**/
	List<RandomEntity> ranList = new ArrayList<RandomEntity>();
	/* 通过java.util.List.indexOf(Object obj)获取id（即正常排序顺序） */
	List<Integer> arr = new ArrayList<Integer>();
	boolean radom_state = false;
	boolean expand_state = false;
	/* radom_mark与radom_state重复了 */
	// boolean radom_mark = false;
	/*
	 * 使用SharedPreferences记住spinner上回保留的位置：http://www.bubuko.com/infodetail-716156.
	 * html
	 */
	SharedPreferences settings;
	Editor editorsettings;
	int i_SharedPreferences = 0;/* 控制第一次进入app */
	int i_SharedPreferences_dialog = 0;/* 第一次控制点击进入floating ball。同Activity中 */
	private ArrayAdapter<String> adapterTest;
	private ArrayAdapter<CharSequence> adapterClasses = null;// 在设置spinner时使用二级联动时需要
	private ArrayAdapter<CharSequence> adapterBooks = null;// 在设置spinner时使用二级联动时需要
	public final static String URL = "/data/data/pri.weiqiang.myjapanese/files";// 数据库文件
	public final static String DB_FILE_NAME = "vocab.db";
	String[] Books_String = { "大家的日本语第一册", "大家的日本语第二册", "新版标准日本语中级上",
			"新版标准日本语中级下", "新版标准日本语初级上", "新版标准日本语初级下", "新编日语I", "新编日语II",
			"新编日语III", "新编日语IV" };
	/* 专门为了playList */
	int len;// 定义全部长度
	private int songIndex = 0;
	EditText editText = null;
	Button button = null;
	TextView textView = null;
	SQLiteDatabase db = null;
	View myView;
	/* TTS */
	Button ttsButton;
	/* Random */
	Button ranButton;
	/* Encryption */
	Button encrypButton;
	/* adobe audiotion */
	Button adobeButton;
	private JapaneseTTS tts;
	private boolean unconnected = false; 
	private CircleRefreshLayout mRefreshLayout;
	/* 把lessons_list作为全局变量，从而在保证二级spinner不超过范围 */
	private List<lessons> lessons_list = new ArrayList<lessons>();
	/* 后台播放单词表的线程 */
	Thread thread_playList = new MyThread_playList();
	/* 播放单词列表 */
	private ImageView mMusicPlay;
	public static boolean isPlay = false;
	/**/
	FloatingActionButton fab ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main); // 默认布局管理器
		/* 因为使用新主题，无法使用xml中的全屏化，这里在Activity中实现 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/* 一定不要放到getGroupView中会慢死，那样相当于每次都进行100次Typeface.createFromAsset操作 */
		tf_group = Typeface.createFromAsset(getAssets(), fontPath_group);
		tf_child = Typeface.createFromAsset(getAssets(), fontPath_child);
		this.circlerefresh_stop = (Button) super
				.findViewById(R.id.btn_mRefreshLayout);
		this.ttsButton = (Button) super.findViewById(R.id.button2tts);
		this.ranButton = (Button) super.findViewById(R.id.button2ran);
		this.encrypButton = (Button) super.findViewById(R.id.button2enc);
		this.adobeButton = (Button) super.findViewById(R.id.button_adobe);

		this.classes = (Spinner) super.findViewById(R.id.classes);// 取得下拉列表框
		this.books = (Spinner) super.findViewById(R.id.books);// 取得下拉列表框		
		MyExpandableListViewDemo.this.adapterBooks = new ArrayAdapter<CharSequence>(
				MyExpandableListViewDemo.this,
				android.R.layout.simple_spinner_item, Books_String); // 定义所有的列表项

		MyExpandableListViewDemo.this.adapterBooks
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		MyExpandableListViewDemo.this.books
				.setAdapter(MyExpandableListViewDemo.this.adapterBooks);// 设置二级下拉列表的选项内容

		// /*使用SharedPreferences记住spinner上回保留的位置：http://www.bubuko.com/infodetail-716156.html*/
		// settings = getSharedPreferences("preferences_settings",0);
		// //与上面的保持一致，或者settings =
		// getSharedPreferences("preferences_settings",Context.MODE_PRIVATE);
		// editorsettings = settings.edit();
		// int position_books = settings.getInt("SelectedPosition-books", 0 );
		// /*如果不屏蔽下边：则会报：java.lang.IndexOutOfBoundsException: Invalid index 10,
		// size is 10*/
		// books.setSelection(position_books); //设置spinner的值，让其当前选择项是与以前的一样。
		// Log.e("books.setSelection(", String.valueOf(position_books));
		// /*calsses一定要放在books之后，否则是没有效果过，因为books更新同时，classes同时更新
		// * 该处许屏蔽因为后边的已经有条件判断了，若此处增加下面，则没回必返回每一books的第一个class*/
		// // int position_classes = settings.getInt("SelectedPosition-classes",
		// 0 );
		// // classes.setSelection(position_classes);
		// // Log.e("classes.setSelection", String.valueOf(position_classes));

		this.show = (CheckBox) super.findViewById(R.id.showchinese); // 取得CheckBox显示组件
		this.checkBox_random = (CheckBox) super.findViewById(R.id.startRandom); // 取得CheckBox显示组件
		this.elistview = (ExpandableListView) super
				.findViewById(R.id.elistview); // 取得组件
		this.elistview.setGroupIndicator(null); // 设置 属性 GroupIndicator,
												// 去掉默认向下的箭头

		this.adapter = new MyExpandableListAdapter(this); // 实例化适配器
		/**
		 * 在代码中实现列表动画:http://www.tuicool.com/articles/Rzqeui，将动画放到此处，
		 * 仅第一次进入生成布局文件时会产生作用
		 */
		this.elistview.setAdapter(this.adapter); // 设置适配器
		super.registerForContextMenu(this.elistview); // 注册上下文菜单
		this.elistview.setOnChildClickListener(new OnChildClickListenerImpl()); // 设置子项单击事件
		this.elistview.setOnGroupClickListener(new OnGroupClickListenerImpl()); // 设置组项单击事件
		this.elistview
				.setOnGroupCollapseListener(new OnGroupCollapseListenerImpl()); // 关闭分组事件
		this.elistview
				.setOnGroupExpandListener(new OnGroupExpandListenerImpl()); // 展开分组事件
		/* 没有分割线还挺好看，看看直接把xml中的分割线去掉吧 */
		this.elistview.setChildDivider(null);
		this.elistview.setDivider(null);
		/* 这样可以避免动画 */
		elistview.setLayoutAnimation(null); // 为ListView 添加动画

		/*
		 * 不写这个会报空指针NullPointerException
		 * dispatchTouchEvent(MyExpandableListViewDemo.java:1211)
		 */
		/* 不要删 */
		// gestureDetector = new GestureDetector(this); //
		// 生成GestureDetector对象，用于检测手势事件
		/* FloatingActionButton */
		fab = (FloatingActionButton) super.findViewById(R.id.fab);

		fab.attachToListView(elistview, new ScrollDirectionListener() {
			@Override
			public void onScrollDown() {
			}

			@Override
			public void onScrollUp() {
			}
		}, new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		fab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});
		this.books
				.setOnItemSelectedListener(new OnItemSelectedListenerImpl_books());
		this.classes
				.setOnItemSelectedListener(new OnItemSelectedListenerImpl_classes());

		/*
		 * 使用SharedPreferences记住spinner上回保留的位置：http://www.bubuko.com/infodetail-
		 * 716156.html
		 */
		settings = getSharedPreferences("preferences_settings", 0); // 与上面的保持一致，或者settings
																	// =
																	// getSharedPreferences("preferences_settings",Context.MODE_PRIVATE);
		editorsettings = settings.edit();
		int position_books = settings.getInt("SelectedPosition-books", 0);
		/*
		 * 如果不屏蔽下边：则会报：java.lang.IndexOutOfBoundsException: Invalid index 10,
		 * size is 10 books.setSelection应该放到setOnItemSelectedListener之后吧
		 */
		books.setSelection(position_books); // 设置spinner的值，让其当前选择项是与以前的一样。
		Log.e("books.setSelection(", String.valueOf(position_books));
		/*
		 * calsses一定要放在books之后，否则是没有效果过，因为books更新同时，classes同时更新
		 * 该处许屏蔽因为后边的已经有条件判断了，若此处增加下面，则没回必返回每一books的第一个class
		 */
		// int position_classes = settings.getInt("SelectedPosition-classes", 0
		// );
		// classes.setSelection(position_classes);
		// Log.e("classes.setSelection", String.valueOf(position_classes));

		// 以下是为CheckBox设置监听器，从而控制所有expandablelistview展开
		/* 看能否避免执行两次 */
		// int position_classes = settings.getInt("SelectedPosition-classes",
		// 0);
		// classes.setSelection(position_classes);

		this.show
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							onGroupExpand();
						} else {
							collapseGroup();
						}
					}
				});
		/* 为乱序输出进行监听 */
		this.checkBox_random
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							radom_state = true;
							randaomUI();
							Log.e("radom_state", String.valueOf(radom_state));
						} else {
							radom_state = false;
							initWidgets();
							Log.e("radom_state", String.valueOf(radom_state));
						}
					}
				});
		/* TTS */
		this.ttsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MyExpandableListViewDemo.this,
						JapaneseTTSActivity.class);
				MyExpandableListViewDemo.this.startActivity(it);
			}
		});
		/* adobe audio */
		this.adobeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				out_adobe();
			}
		});
		/* encryp */
		this.encrypButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MyExpandableListViewDemo.this,
						VoiceEncryptionActivity.class);
				MyExpandableListViewDemo.this.startActivity(it);
			}
		});
		/* 是UI转换为随机排列 */
		this.ranButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				randaomUI();
			}
		});

		/* greenDao */
		copyDB();
		File file = new File(URL, DB_FILE_NAME);
		db = SQLiteDatabase.openOrCreateDatabase(file, null);
		/* 通过修改"notes-2-db"改变不同数据库的表名，卡伊看到数据库中不同的数据，而且全部都保存了下来 */

		/*
		 * http://stackoverflow.com/questions/25771689/greendao-store-data-in-sqlite
		 * -file-on-android
		 */
		daoMaster = new DaoMaster(db);


		
		
		
		
		
		DaoMaster.createAllTables(db, true);
		daoSession = daoMaster.newSession();
		words_Dao = daoSession.getWordsDao();
		/* 没有连接在单独类Volesson中会报空指针 */
		title_Dao = daoSession.getLesson_titleDao();
		lessons_Dao = daoSession.getLessonsDao();

		/* 将数据库拷贝到SD卡，用来观察数据库变化 */
		// File f = new File(this.getFilesDir().getPath() + "/vocab.db");
		// Log.e("vocab所在路径", this.getFilesDir().getPath() + "/vocab.db");
		// String sdcardPath = Environment.getExternalStorageDirectory()
		// .getAbsolutePath();
		// File o = new File(sdcardPath + "/jc1535.db"); // sdcard上的目标地址
		// if (f.exists()) {
		// FileChannel outF;
		// try {
		// outF = new FileOutputStream(o).getChannel();
		// new FileInputStream(f).getChannel().transferTo(0, f.length(),
		// outF);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// Log.e("1", "输出成功");
		// }
		/**/
		initWidgets();
		/* 直接报错 */
		// ExpandCollapse1st();
		/* CircleRefreshLayout */
		mRefreshLayout = (CircleRefreshLayout) findViewById(R.id.refresh_layout);
		mRefreshLayout
				.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
					@Override
					public void refreshing() {
						// do something when refresh starts
						new Thread(new ThreadShow()).start();

					}

					@Override
					public void completeRefresh() {
						// do something when refresh complete

					}
				});
		circlerefresh_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRefreshLayout.finishRefreshing();
				radom_state = true;
				randaomUI();
				radom_state = false;

			}
		});
		/**/
		tts = new JapaneseTTS(this, null);
		/* 没有这句话肯定就报java.lang.NullPointerException，对象一定要new。一直以为是权限或者SD未识别…… */
		player_word = new MediaPlayer();
		player_word_c = new MediaPlayer();
		player_list = new MediaPlayer();
		player_list_c = new MediaPlayer();
		
//		v_elistView_read_word = elistview.getChildAt(0);
	}

	/* 无效！！！为更新主线程，使用handler用于接收消息 */
	Handler handler_spinner_activity = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				/*
				 * 存在这种bug就是想选spinner，但是UI的spinner还是UI的，下一课选择时，
				 * 不会考虑AlertDialog中的spinner已经改变
				 */
				// int position_dialog =
				// MyExpandableListViewDemo.this.classes.getSelectedItemPosition();
				int position_dialog = msg.arg1;
				/** 直接classes.setSelection(4)，会报java.lang.NullPointerException */
				MyExpandableListViewDemo.this.classes
						.setSelection(position_dialog + 1);
				System.out.println("handler_spinner_activity receive....");
				System.gc();
			}
			/* 同步spinner：books */
			if (msg.what == 2) {
				int position_dialog = msg.arg1;
				/** 直接classes.setSelection(4)，会报java.lang.NullPointerException */
				MyExpandableListViewDemo.this.classes
						.setSelection(position_dialog - 1);
				System.out.println("handler_spinner_activity receive....");
				System.gc();
			}
			/* 同步spinner：books */
			if (msg.what == 3) {
				int position_dialog = msg.arg1;
				MyExpandableListViewDemo.this.books
						.setSelection(position_dialog);
				System.out.println("handler_spinner_activity receive....");
				System.gc();
			}
			/* 同步spinner：classes */
			if (msg.what == 4) {
				int position_dialog = msg.arg1;
				/**
				 * 没有MyExpandableListViewDemo.this.，直接classes.setSelection(4)，
				 * 会报java.lang.NullPointerException
				 */
				MyExpandableListViewDemo.this.classes
						.setSelection(position_dialog);
				System.out.println("handler_spinner_activity receive....");
				System.gc();
			}
		};
	};
	
	/* 为mRefreshLayout配合的计时器，使用handler用于接收消息 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				mRefreshLayout.finishRefreshing();
				radom_state = true;
				randaomUI();
//				radom_state=false;//避免翻页后依然处于乱序状态，需要保证处于乱序，不然添加生词需要保持乱序状态
				System.out.println("receive....");
				/* new Thread(new ThreadShow()).start();会产生多个线程，所以考虑一下清空内存 */
				System.gc();
			}
			/* 在播放mp3时，通过handler更新UI线程 */
			if (msg.what == 2) {
				/*但屏蔽下边时，但滑动UI时，Android实际上还是在刷新UI，只要移动，就会出现红色，如果不滑动，则不会出现红色*/
//				initWidgets();				
				/*这里与MarkClickListenerImpl不同之处就是，当屏幕滑动时与播放音乐还有刷新UI并不是同时进行，这就导致了，下边的
				 * 一系列存在偏差，这这就导致了，滑动屏幕，后台播放音乐，UI并不是保留之前视线的内容*/
//				View v_elistView_read_word = elistview.getChildAt(0);
//				int top = (v_elistView_read_word == null) ? 0 : v_elistView_read_word.getTop();
//				Log.i("top_read_word", String.valueOf(top));
//				int i_1stPos = elistview.getFirstVisiblePosition();
//				Log.i("i_1stPos_read_word", String.valueOf(i_1stPos));
//				elistview.setSelectionFromTop(i_1stPos, top);				
				// radom_state=false;//避免翻页后依然处于乱序状态
				System.out.println("因为播放音乐更新UI……");				
			}
			if (msg.what == 3) {
				System.out.println("floatbuton playing....");
				/*fab的背景颜色更新也要放到Activity中，不能再dialog中进行*/
//				fab.setBackgroundResource(R.drawable.off);//完全设置图像了，没有填充色
//				fab.setColorNormal(Color.BLACK);//设置背景颜色
				/*http://www.jianshu.com/p/ed5d7f8e63e1
				 * 在xml中通过设置android:src="@drawable/ic_add_white_24dp"设置背景*/
				fab.setImageResource(R.drawable.ic_add_white_24dp);
			}
			if (msg.what == 4) {
				System.out.println("floatbutton normal....");
				fab.setImageResource(R.drawable.ic_play_white_24dp);
			}
		};
	};

	// 线程类
	class ThreadShow implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			/* 多了一个try-catch外多了while (true)，当然在后台一直执行了 */
			try {
				Thread.sleep(2000);
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);

				System.out.println("send...");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("thread error...");
			}
		}
	}

	
	
	/* 将播放音乐放到一自定义线程中，然后播放时让其sleep */
	public class MyThread_playList extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			songplay();
			Message msg = new Message();
			msg.what = 2;
			handler.sendMessage(msg);
			System.out.println("更新UI……放音乐");
		}
	}
	// 继承BaseExpandableListAdapter并覆写类中的抽象方法
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		public String[] groups = { "Fail to search the database.", };
		public String[][] children = { { "没有查询到改信息，或者数据库使用失败", "总之，数据库使用失败" },
		/*
		 * List<Children_List>children_list=newArrayList();
		 * 在MyExpandableListAdapter不能使用ArrayList
		 */
		}; // 定义组项
		public int[] mark_num = { 1, };
		private Context context = null; // 保存上下文对象
		// 文件的路径
		public final static String URL = "/data/data/pri.weiqiang.myjapanese/files";
		// 数据库文件
		public final static String DB_FILE_NAME = "location.db";
		public final static String TABLE_NAME = "location_date";
		SQLiteDatabase db = null;

		/*
		 * http://stackoverflow.com/questions/7817916/android-notifydatasetchanged
		 * -for-expandablelistview-not-working
		 */
		/*
		 * 自动更新 可以进入BaseExpandableListAdapter，将其所有方法全部复写，应该就不会有之前的问题了
		 */
		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			super.registerDataSetObserver(observer);
		}

		public void SetArr(String[] array, String[][] array2, int[] array3) {
			groups = array;
			children = array2;
			mark_num = array3;

		}

		public MyExpandableListAdapter(Context context) { // 构造方法接收
			this.context = context;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) { // 取得指定的子项
			return this.children[groupPosition][childPosition];

		}

		@Override
		public long getChildId(int groupPosition, int childPosition) { // 取得子项ID
			return childPosition;
		}

		public TextView buildTextView() { // 自定义方法，建立文本
			AbsListView.LayoutParams param = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT); // 指定布局参数
			TextView textView = new TextView(this.context); // 创建TextView
			textView.setTypeface(Typeface.SERIF);
			textView.setLayoutParams(param); // 设置布局参数
			textView.setTextSize(19); // 设置文字大小
			textView.setGravity(Gravity.LEFT); // 左对齐
			textView.setPadding(10, 0, 0, 0); // 间距
			return textView; // 返回组件

		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {// 返回子项组件
			TextView textView = buildTextView(); // 创建TextView
			textView.setText(getChild(groupPosition, childPosition).toString()); // 设置显示文字
			textView.setTextColor(Color.GRAY);
			/* 仅日语增加字体，汉字不要使用体育字体 */
			if (isLastChild) {

				textView.setTextSize(19); // 设置文字大小
				// textView.setTypeface(tf_child);
			} else {
				// textView.setTypeface(tf_child);
				// textView.setTextColor(Color.RED);
			}

			// textView.setTextColor(Color.RED);
			return textView;

		}

		@Override
		public int getChildrenCount(int groupPosition) { // 取得子项个数
			return this.children[groupPosition].length; // 取得子项个数
		}

		@Override
		public Object getGroup(int groupPosition) { // 取得组对象
			return this.groups[groupPosition];
		}

		@Override
		public int getGroupCount() { // 取得组个数
			return this.groups.length;
		}

		@Override
		public long getGroupId(int groupPosition) { // 取得组ID
			return groupPosition;
		}

		/* 取得Mark对象 */
		public int getMark(int groupPosition) {
			return this.mark_num[groupPosition];
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) { // 取得组显示组件
			LayoutInflater inflater = LayoutInflater.from(this.context);
			View view = inflater.inflate(R.layout.group_view, null);
			final TextView textView = (TextView) view
					.findViewById(R.id.textView_group);
			textView.setTextSize(24); // 设置文字大小
			textView.setTextColor(Color.BLACK);
//			if (songIndex == groupPosition) {
//				textView.setTextColor(Color.RED);
//			} else {
//				textView.setTextColor(Color.BLACK);
//			}
			textView.setTypeface(Typeface.SERIF);
			textView.setGravity(Gravity.CENTER_VERTICAL); // 左对齐
			/* 设置成这个样子就居中了，上下间隔，还是要多试试 */
			textView.setPadding(10, 22, 0, 10); // 间距
			textView.setText(getGroup(groupPosition).toString());
			/* 更换外部字体 */
			// tf = Typeface.createFromAsset(getAssets(),
			// fontPath);textView.setTypeface(tf_group);
			/** TTS。可以考虑填在child中直接读音标也可以 */
			Button tts_Button = (Button) view.findViewById(R.id.group_speak);
			tts_Button.setFocusable(false);
			tts_Button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					/*对于处于播放状态的情况下，点击每一个item时，则是从点击的位置开始进行批量播放*/
					if (isPlay) {
						songIndex=groupPosition-1;
					} else {						
						/* 本地播放，分为乱序与常序 */
						if (radom_state) {
							/* 详情见button改变时状态的处理 */
							words entity = words_list.get(arr.get(groupPosition));
							Log.e("Random groupPosition",
									String.valueOf(groupPosition));
							Log.e(String.valueOf(arr.get(groupPosition)),
									String.valueOf(arr.get(groupPosition)));
							singlePath = playerPath + entity.getId() + ".mp3";
							singlePath_c = playerPath + "/C/c_" + entity.getId()
									+ ".mp3";
							File file = new File(singlePath);
							/* 对有音频的进行音频播放，否则使用tts */
							if (file.exists()) {
								/*
								 * 出现mediaplayer的（-38，
								 * 0）使用create方法的方式可以直接播放，但是new情况下（player_word=new
								 * MediaPlayer()）必须按如下进行
								 */
								try {
									player_word.reset(); /* 重置MediaPlayer */
									player_word.setDataSource(singlePath);/* 设置要播放的文件的路径 */
									player_word.prepare();/* 准备播放 */
									player_word.start();/* 开始播放 */
								} catch (IOException e) {

								}
								/*
								 * 注意：不能这样使用，因为会阻碍UI线程，出现了点击后需要停顿2s，才能进行滑动等其他动作，很像ANR
								 */
								// try {
								// Thread.sleep(2000);
								// } catch (InterruptedException e1) {
								// // TODO Auto-generated catch block
								// e1.printStackTrace();
								// }
								/* play the chinese */
								try {
									player_word_c.reset(); /* 重置MediaPlayer */
									player_word_c.setDataSource(singlePath_c);/* 设置要播放的文件的路径 */
									player_word_c.prepare();/* 准备播放 */
									player_word_c.start();/* 开始播放 */
								} catch (IOException e) {
									Log.e("IOException", String.valueOf(e));
								}

							} else {;
								/* 没有音频则进行TTS播放， 首先使用的是web端返回语音，TTS可以返回任何文字，但声音不好 */
								HashMap<String, String> params = new HashMap<String, String>();
								params.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
										String.valueOf(AudioManager.STREAM_MUSIC));
								params.put(JapaneseTTS.KEY_PARAM_SPEAKER, "male01");
								
								unconnected=tts.speak(getGroup(groupPosition).toString(),
										TextToSpeech.QUEUE_FLUSH, params);
								/*语音播放需要联网*/
								if (unconnected) {
									Toast.makeText(MyExpandableListViewDemo.this,
											"语音输出，需要联网。", Toast.LENGTH_SHORT).show();
								}
	

							}

						} else {
							/*
							 * 原来的加密版的思路是解密后将解密后的文件存在SD卡上，这样mediaPlayer才能正常解码，
							 * 这将导致SD卡寿命大幅减少，现在的解决方案就是使用自解码，然后用AudioTrack直接播放音频流，/
							 * /* 非解密版
							 */
							words entity = words_list.get(groupPosition);
							singlePath = playerPath + entity.getId() + ".mp3";
							singlePath_c = playerPath + "/C/c_" + entity.getId()
									+ ".mp3";
							Log.e("COM groupPosition",
									String.valueOf(groupPosition));
							Log.e("mp3 path", singlePath);
							File file = new File(singlePath);
							if (file.exists()) {
								try {
									player_word.reset(); /* 重置MediaPlayer */
									player_word.setDataSource(singlePath);/* 设置要播放的文件的路径 */
									player_word.prepare();/* 准备播放 */
									player_word.start();/* 开始播放 */
								} catch (IOException e) {

								}
								/* 中间的间隔时间，用于联想记忆 */
								// try {
								// Thread.sleep(1000);
								// } catch (InterruptedException e1) {
								// // TODO Auto-generated catch block
								// e1.printStackTrace();
								// }
								/* 目前不需要 play the chinese */
								// try {
								// player_word_c.reset(); /* 重置MediaPlayer */
								// player_word_c.setDataSource(singlePath_c);/*
								// 设置要播放的文件的路径 */
								// player_word_c.prepare();/* 准备播放 */
								// player_word_c.start();/* 开始播放 */
								// } catch (IOException e) {
								// }
							} else {
								/* 没有音频则进行TTS播放， 首先使用的是web端返回语音，TTS可以返回任何文字，但声音不好 */
								HashMap<String, String> params = new HashMap<String, String>();
								params.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
										String.valueOf(AudioManager.STREAM_MUSIC));
								/* 可以选择人声 */
								params.put(JapaneseTTS.KEY_PARAM_SPEAKER, "male01");
								unconnected=tts.speak(getGroup(groupPosition).toString(),
										TextToSpeech.QUEUE_FLUSH, params);
								/*语音播放需要联网*/
								if (unconnected) {
									Toast.makeText(MyExpandableListViewDemo.this,
											"语音输出，需要联网。", Toast.LENGTH_SHORT).show();
								}
								
								
							}
						}

					
					}
}
			});
			/* 收藏夹功能 */
			final ImageView logo1 = (ImageView) view
					.findViewById(R.id.imageView1);
			logo1.setFocusable(false);
			logo1.setImageResource(R.drawable.btn_star_press);
			final int mark_fav = getMark(groupPosition);
			if (mark_fav == 0) {
				logo1.setImageResource(R.drawable.btn_play_press);
			}
			class MarkClickListenerImpl implements OnClickListener {
				public void onClick(View v) {
					/* 无效 */
					// ExpandCollapse1st();
					/* 4464【l】是长类型 */
					// words entity=new words(4464l);
					/*
					 * java.lang.IllegalArgumentException: the bind value at
					 * index 2 is null
					 * 应该是参数应该是全部需要输入的，所以entity不能new，且通过debug进入后，看到全部是null
					 */
					// entity.setFav(1);
					// words_Dao.update(entity);
					/*
					 * java.lang.IllegalStateException: The content of the
					 * adapter has changed but ListView did not receive a
					 * notification. Make sure the content of your adapter is
					 * not modified from a background thread, but only from the
					 * UI thread. Make sure your adapter calls
					 * notifyDataSetChanged() when its content changes. [in
					 * ListView(2131165193, class
					 * android.widget.ExpandableListView) with Adapter(class
					 * android.widget.ExpandableListConnector)] 这里的情况是这样的
					 */
					/* 将words_list作为全局变量，从而方便获取个体entity */
					/*
					 * 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21
					 * 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40
					 * 41 42 43 16 21 39 40 38 15 20 5 36 27 32 23 4 17 37 30 1
					 * 0 11 10 24 8 13 7 14 26 43 9 12 18 2 19 42 31 29 3 28 6
					 * 22 41 33 35 25 34 0 17 161 16 212 30 39
					 */

					if (radom_state) {
						/*************************************/
						/* 乱序之后，groupPositon和原来的位置不一定一致，需要注意 */
						/*********************************/
						/* 指数是可能和groupPosition相同的情况，所以不可以那么做 */
						int i_arr_index = arr.indexOf(groupPosition);
						Log.e("groupPosition", String.valueOf(groupPosition));
						Log.e("i_arr_index", String.valueOf(i_arr_index));
						Log.e("arr.get(groupPosition)",
								String.valueOf(arr.get(groupPosition)));
						/* i_arr_index不是正确的位置 */
						words entity = words_list.get(arr.get(groupPosition));

						if (entity.getFav() == 0) {
							entity.setFav(1);
						} else {
							entity.setFav(0);
						}
						words_Dao.update(entity);
						/* notifyDataSetInvalidated();必须加,如果不加，则无论第一 */
						notifyDataSetInvalidated();
						notifyDataSetChanged();
						/*
						 * 使用randaomUI_Fav版：
						 * 乱序时不可以在刷新UI界面了，但是不刷新UI图标就没有变化，并且刷新UI必须记住本次的顺序
						 * ，不然就会再次乱序
						 */
						randaomUI_Fav();
						/*
						 * randaomUI_Fav 不加也不行
						 * 移动到指定位置，如果没有下边的语句，如果app没有使用一次展开，那么
						 * ，只要没有使用展开，点击收藏按钮，就会是elistview位于第一行
						 */
						View v_elistView = elistview.getChildAt(0);
						int top = (v_elistView == null) ? 0 : v_elistView
								.getTop();
						int i_1stPos = elistview.getFirstVisiblePosition();
						elistview.setSelectionFromTop(i_1stPos, top);
						/*
						 * 就是因为增加了radom_state = false;导致在乱序下的添加收藏功能又与正序纠缠在一起
						 * radom_state必须成对出现，否则将一直处于radom_mark状态 radom_state =
						 * false;
						 */

					} else {
						words entity = words_list.get(groupPosition);
						if (entity.getFav() == 0) {
							entity.setFav(1);
						} else {
							entity.setFav(0);
						}
						words_Dao.update(entity);
						/* notifyDataSetInvalidated();必须加 */
						notifyDataSetInvalidated();
						/* 可以分别屏蔽，看到底是哪个起效 */
						notifyDataSetChanged();
						initWidgets();
						/*
						 * 移动到指定位置，如果没有下边的语句，如果app没有使用一次展开，那么，只要没有使用展开，点击收藏按钮，
						 * 就会是elistview位于第一行
						 */
						View v_elistView = elistview.getChildAt(0);
						int top = (v_elistView == null) ? 0 : v_elistView
								.getTop();									
						Log.i("top_MarkClickListenerImpl", String.valueOf(top));
						int i_1stPos = elistview.getFirstVisiblePosition();
						Log.i("i_1stPos_MarkClickListenerImpl", String.valueOf(i_1stPos));
						elistview.setSelectionFromTop(i_1stPos, top);
					}
				}
			}
			logo1.setOnClickListener(new MarkClickListenerImpl());
			this.notifyDataSetChanged();// 这里是注意看看数据更改时候会产生反应

			return view;

		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	/* 为点击收藏而专门设计的随机UI */
	public void randaomUI_Fav() {
		if (radom_state) {
			/* 因为randaomUI启动之后，words_list依然停留在内存中，所以不需要进行DB操作，且不需要动画，直接拿来用 */
			int words_size = words_list.size();
			String[] array1 = new String[words_size];
			String[][] array2 = new String[words_size][2];
			int[] array3 = new int[words_size];

			/* randaomUI_Fav保留默认位置，所以arr不需要洗牌Collections.shuffle(arr); */

			for (int i = 0; i < words_size; i++) {
				array1[i] = words_list.get(arr.get(i)).getWord();// Group：word
				array2[i][0] = words_list.get(arr.get(i)).getPhonetic();// Child：phonetic
				/* 增加序号，进行闲时校对 */
				array2[i][1] = words_list.get(arr.get(i)).getTranslation();// Child：translation
				array3[i] = words_list.get(arr.get(i)).getFav();// fav
			}
			((MyExpandableListAdapter) this.adapter).SetArr(array1, array2,
					array3);
			/*
			 * 总是移动view后，数据才进行更新，会不会是这里的原因 加完后，立刻解决了以前的问题
			 */
			((MyExpandableListAdapter) this.adapter).notifyDataSetChanged();
			((MyExpandableListAdapter) this.adapter).notifyDataSetInvalidated();

		}

//		if (expand_state) {
//			collapseGroup();
//		}
	}

	/* 一般随机UI */
	public void randaomUI() {
		/* 仅在放生乱序排列时产生动画效果，用以显示处于乱序状态，一般布局文件放到初始化UI中肯定没有错误 */
		Animation animation = (Animation) AnimationUtils.loadAnimation(this,
				R.anim.list_anim);
		LayoutAnimationController lac = new LayoutAnimationController(animation);
		// lac.setDelay(0.0000005f); //设置动画间隔时间
		lac.setOrder(LayoutAnimationController.ORDER_RANDOM); // 设置列表的显示顺序
		elistview.setLayoutAnimation(lac); // 为ListView 添加动画

		if (radom_state) {
			QWords qWords = new QWords();
			words_list = qWords.queryWords_Lesson_id(words_Dao, classes_name);
			/*
			 * 不要多次调用words_list.size()，使用int
			 * words_size=words_list.size()，保证后续仅调用一次
			 */
			int words_size = words_list.size();
			/*
			 * 如此这般也不行：words_List_backup与words_list内都是null，看见words_List_backup=
			 * words_list
			 */
			// for (int i = 0; i < words_size; i++) {
			// words_List_backup.get(i).=words_List_backup.get(i);
			// }
			/*
			 * 【E】 java.lang.IllegalArgumentException: n <= 0: 0 看下int
			 * java.util.Random.nextInt(int n)
			 * 几乎可以断定应该是开始initWidgets的数列是空的所以会出现0:0，机最大的数是0, 所以无法出现
			 */
			// RandomJ randomJ=new RandomJ();
			// ran_list=randomJ.name(words_list);
			// for (int i = 0; i < words_size; i++) {
			// /* Invalid index 0, size is 0
			// * 在使用随机后，原Arraylist可能已经被破坏
			// * */
			// Log.e("words_list", words_list.get(i).getTranslation() );
			//
			// }
			String[] array1 = new String[words_size];
			String[][] array2 = new String[words_size][2];
			int[] array3 = new int[words_size];

			/*
			 * 使用Random之后，原表会发生更改，这么做之后，会有一个弊端，就是update数据库时，原表实际上市不存在了，所以
			 * 我的想法就是仅在UI处进行乱序 http://www.xuebuyuan.com/593802.html
			 */
			// List<Integer> arr = new ArrayList<Integer>();
			/*
			 * Invalid index 60, size is 60:for (int i = 0; i 【=<】words_size;
			 * i++)
			 */

			/* 每次UI更新应该会要置空，不然会覆盖 */
			/* arr=null;java.lang.NullPointerException */
			// arr=null;
			/* radom_mark与radom_state应该是同类 */
			// if (radom_state) {
			//
			// } else {
			// arr.clear();//
			// 使用null一定注意，三思，arr=null之后应该是直接把指向堆（已经开辟好的）的指针指向了空（没有开辟好的空间）
			// for (int i = 0; i < words_size; i++) {
			// arr.add(i);
			// }
			// System.out.println("打乱前:");
			// for (int i : arr) {
			// System.out.print(i + " ");
			// }
			// Collections.shuffle(arr);
			// System.out.println("打乱后:");
			// for (int i : arr) {
			// System.out.print(i + " ");
			// }
			// }

			/*
			 * ArrayList必须放入元素，下边的会报 java.lang.RuntimeException: Unable to start
			 * activity
			 * ComponentInfo{pri.weiqiang.myjapanese/pri.weiqiang.myjapanese
			 * .MyExpandableListViewDemo}: java.lang.IndexOutOfBoundsException:
			 * Invalid index 0, size is 0
			 */
			// for (int i = 0; i < words_size; i++) {
			// ranList.get(i).order=i;
			// ranList.get(i).random=arr.get(i);
			// Log.e("order", String.valueOf(ranList.get(i).order));
			// Log.e("random", String.valueOf(ranList.get(i).random));
			// }

			arr.clear();// 使用null一定注意，三思，arr=null之后应该是直接把指向堆（已经开辟好的）的指针指向了空（没有开辟好的空间）
			for (int i = 0; i < words_size; i++) {
				arr.add(i);
			}
			System.out.println("打乱前:");
			for (int i : arr) {
				System.out.print(i + " ");
			}
			/* 乱序Arraylist(也就是List) */
			Collections.shuffle(arr);
			System.out.println("打乱后:");
			for (int i : arr) {
				System.out.print(i + " ");
			}

			/**/
			for (int i = 0; i < words_size; i++) {
				array1[i] = words_list.get(arr.get(i)).getWord();// Group：word
				array2[i][0] = words_list.get(arr.get(i)).getPhonetic();// Child：phonetic
				/* 增加序号，进行闲时校对 */
				array2[i][1] = words_list.get(arr.get(i)).getTranslation();// Child：translation
				array3[i] = words_list.get(arr.get(i)).getFav();// fav
			}
			((MyExpandableListAdapter) this.adapter).SetArr(array1, array2,
					array3);
			/*
			 * 总是移动view后，数据才进行更新，会不会是这里的原因 加完后，立刻解决了以前的问题
			 */
			((MyExpandableListAdapter) this.adapter).notifyDataSetChanged();
			((MyExpandableListAdapter) this.adapter).notifyDataSetInvalidated();

		}
//		if (expand_state) {
//			collapseGroup();
//		}
	}

	/* 有序化UI */
	public void initWidgets() {
		if (!radom_state) {
			QWords qWords = new QWords();
			words_list = qWords.queryWords_Lesson_id(words_Dao, classes_name);
			/*
			 * 不要多次调用words_list.size()，使用int
			 * words_size=words_list.size()，保证后续仅调用一次
			 */
			int words_size = words_list.size();
			String[] array1 = new String[words_size];
			String[][] array2 = new String[words_size][2];
			int[] array3 = new int[words_size];
			for (int i = 0; i < words_size; i++) {
				array1[i] = words_list.get(i).getWord();// Group：word
				array2[i][0] = words_list.get(i).getPhonetic();// Child：phonetic
				array2[i][1] = words_list.get(i).getTranslation();// Child：translation
				array3[i] = words_list.get(i).getFav();// fav
			}
			((MyExpandableListAdapter) this.adapter).SetArr(array1, array2,
					array3);
			/* 总是移动view后，数据才进行更新，会不会是这里的原因:经过验证，不增加此处，进行SetArr后，UI不会进行更新 */
			((MyExpandableListAdapter) this.adapter).notifyDataSetChanged();
			((MyExpandableListAdapter) this.adapter).notifyDataSetInvalidated();
		}
		if (expand_state) {
			collapseGroup();
		}
	}

	/* 将raw文件中的数据库文件拷贝至手机中的程序内存当中 */
	public boolean copyDB() {
		try {
			// 判断程序内存中是否有拷贝后的文件
			if (!(new File(URL)).exists()) {
				InputStream is = getResources().openRawResource(R.raw.vocab);
				FileOutputStream fos = this.openFileOutput(DB_FILE_NAME,
						Context.MODE_WORLD_READABLE);
				// 一次拷贝的缓冲大小1M
				byte[] buffer = new byte[1024 * 1024];
				int count = 0;
				// 循环拷贝数据库文件
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
				Log.e("copyDB", "susccees");
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("copyDB", "fail");
			return false;
		}
	}

	/* 设置子类全部展开2015.9.27 */
	private void onGroupExpand() {
		int len = this.adapter.getGroupCount();
		for (int i = 0; i < len; i++) {
			this.elistview.expandGroup(i);
		}
	}

	/*
	 * 2016.5.8,最近还有一个问题就是在打开app第一次，不展开一项，后续只要不在视野UI内的elistview中收藏单词就会调回到视野中，
	 * 我想在启动app的第一次展开合并最后一项
	 * 我将ExpandCollapse1st放到了收藏按钮中，然后执行了，即使elistview展开了最后一项，
	 * 还是只要没有手动展开，点击收藏夹，总是返回第一行
	 */

	private void collapseGroup() {
		int len = this.adapter.getGroupCount();
		for (int i = 0; i < len; i++) {
			this.elistview.collapseGroup(i);
		}
	}

	/* 用来配合统计各单元单词数量 */
	private void out_adobe() {

		for (int j = 0; j < 1; j++) {
			String book_name = "新版标准日本语初级下";
			Log.e("book_name", book_name);
			QLessons qLessons = new QLessons();
			QWords qWords = new QWords();
			List<lessons> lessons_list = new ArrayList<lessons>();
			List<words> words_list = new ArrayList<words>();
			lessons_list = qLessons.queryLessons(lessons_Dao, book_name);
			File file = new File(Environment.getExternalStorageDirectory()
					.getPath()
					+ File.separator
					+ "a_SDK_fail"
					+ File.separator
					+ "adobe.txt");
			PrintStream out_model;
			for (int i = 0; i < lessons_list.size(); i++) {
				words_list = qWords.queryWords_Lesson_id(words_Dao,
						lessons_list.get(i).getTitle());
				try {
					out_model = new PrintStream(
							new FileOutputStream(file, true));
					out_model.println(lessons_list.get(i).getTitle() + ":"
							+ words_list.size() + "\n");
					out_model.close();
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}
			}
		}
	}

	/* 播放本页面全部单词 */
	private void playList() {
		/* len此处必须使用全局变量，不然nextsong()无法访问 */
		len = this.adapter.getGroupCount();
		player_list.setOnCompletionListener(new CompletionListener());
		songplay();

	}

	/* 为mediaplayer添加完成减轻监听器 */
	private final class CompletionListener implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			nextsong();
		}

	}

	/* 当播放单曲完成后，自动进入下一首 */
	private void nextsong() {
		if (songIndex < len - 1) {
			songIndex = songIndex + 1;
			/* 目前每次都重新定义一个线程，Thread.sleep在这个线程分钟进行睡眠 
			 * 开始的思路是将线程thread_playList_1或者thread_playList销毁，但是实际上他在不断的new，
			 * 我销毁的对象可能已经是不用的了，所以直接在这里增加一个播放状态的判断*/			
			if (isPlay) {
				Thread thread_playList_1 = new MyThread_playList();
				thread_playList_1.start();
			}
			
		} else {
			/* 重新开始，后续增加到下一章 */
			songIndex = 0;
		}
	}

	private void songplay() {
		/* 本地播放，分为乱序与常序 */
		if (radom_state) {
			/* 详情见button改变时状态的处理 */
			words entity = words_list.get(arr.get(songIndex));
			Log.e("Random songIndex", String.valueOf(songIndex));
			Log.e(String.valueOf(arr.get(songIndex)),
					String.valueOf(arr.get(songIndex)));
			singlePath = playerPath + entity.getId() + ".mp3";
			singlePath_c = playerPath + "/C/c_" + entity.getId() + ".mp3";
			File file = new File(singlePath);
			/* 对有音频的进行音频播放，否则使用tts */
			if (file.exists()) {
				/*
				 * 出现mediaplayer的（-38，
				 * 0）使用create方法的方式可以直接播放，但是new情况下（player_list=new
				 * MediaPlayer()）必须按如下进行
				 */
				try {
					player_list.reset(); /* 重置MediaPlayer */
					player_list.setDataSource(singlePath);/* 设置要播放的文件的路径 */
					player_list.prepare();/* 准备播放 */
					player_list.start();/* 开始播放 */
				} catch (IOException e) {

				}
				/* play the chinese */
				try {
					player_list_c.reset(); /* 重置MediaPlayer */
					player_list_c.setDataSource(singlePath_c);/* 设置要播放的文件的路径 */
					player_list_c.prepare();/* 准备播放 */
					player_list_c.start();/* 开始播放 */
				} catch (IOException e) {
					Log.e("IOException", String.valueOf(e));
				}

			} else {
				/* 没有音频则进行TTS播放， 首先使用的是web端返回语音，TTS可以返回任何文字，但声音不好 */
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
						String.valueOf(AudioManager.STREAM_MUSIC));
				params.put(JapaneseTTS.KEY_PARAM_SPEAKER, "male01");
				// tts.speak(getGroup(songIndex).toString(),TextToSpeech.QUEUE_FLUSH,
				// params);
			}

		} else {
			/*
			 * 原来的加密版的思路是解密后将解密后的文件存在SD卡上，这样mediaPlayer才能正常解码，
			 * 这将导致SD卡寿命大幅减少，现在的解决方案就是使用自解码，然后用AudioTrack直接播放音频流，/ /* 非解密版
			 */
			words entity = words_list.get(songIndex);
			singlePath = playerPath + entity.getId() + ".mp3";
			singlePath_c = playerPath + "/C/c_" + entity.getId() + ".mp3";
			Log.e("COM songIndex", String.valueOf(songIndex));
			Log.e("mp3 path", singlePath);
			File file = new File(singlePath);
			if (file.exists()) {
				try {
					player_list.reset(); /* 重置MediaPlayer */
					player_list.setDataSource(singlePath);/* 设置要播放的文件的路径 */
					player_list.prepare();/* 准备播放 */
					player_list.start();/* 开始播放 */
				} catch (IOException e) {

				}
				/*
				 * 播放并修改listview的颜色，但是不能更新UI线程
				 * android.view.ViewRootImpl$CalledFromWrongThreadException:
				 * Only the original thread that created a view hierarchy can
				 * touch its views.
				 */
				// initWidgets();
				/*
				 * 注意：不建议使用这样，因为会阻碍UI线程，出现了点击后需要停顿2s，才能进行滑动等其他动作，很像ANR
				 * 且Thread.sleep(2000);必须放置到run()中才有效果
				 */
				// try {
				// Thread.sleep(2000);
				// thread_playList.sleep(2000);
				// } catch (InterruptedException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }

			} else {
				/* 没有音频则进行TTS播放， 首先使用的是web端返回语音，TTS可以返回任何文字，但声音不好 */
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
						String.valueOf(AudioManager.STREAM_MUSIC));
				/* 可以选择人声 */
				params.put(JapaneseTTS.KEY_PARAM_SPEAKER, "male01");
				// tts.speak(getGroup(songIndex).toString(),TextToSpeech.QUEUE_FLUSH,
				// params);
			}
		}

	}

	private void play_state() {

		if (isPlay) {
			mMusicPlay.setImageResource(R.drawable.lock_suspend);
		} else {
			mMusicPlay.setImageResource(R.drawable.lock_play);
		}
	}
    /**/
    private void showDialog_checkbox() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		/*
		 * this.mMusicPlay = (ImageView)dialog.findViewById(R.id.music_play);
		 * 放在这个位置也是可以的，主要还是dialog.findViewById中的dialog
		 */
		builder.setTitle("我的日语单词本");		
		/*http://www.cnblogs.com/linjiqin/archive/2011/03/10/1980184.html*/
		//checkedItems设为全局变量才能保证记忆功能啊
		checkedItems[0]=radom_state;
		builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener(){
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            	checkedItems[which]=isChecked;
                String result = "您选择了：";
                for (int i = 0; i < checkedItems.length; i++) {
                	/*选中时发生*/
                    if(checkedItems[0])
                    {
 						Log.e("result", result+checkedItems[0]);
                    	radom_state = true;
						randaomUI();
						Log.e("radom_state", String.valueOf(radom_state));
						
                    }else {
                    	radom_state = false;
						initWidgets();
						Log.e("radom_state", String.valueOf(radom_state));
					}
                    
                    if(checkedItems[1])
                    {
                    	Log.e("result", result+checkedItems[1]);  
                    	expand_state=true;
						onGroupExpand();
                    }
                    else {
                    	collapseGroup();
                    	expand_state=false;
					}
               }
            }
        });
		builder.setNeutralButton("退出", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Log.e("877", "退出");
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					// 将mShowing变量设为false，表示对话框已关闭
					field.set(dialog, true);
					dialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		builder.show();

    	
    }
    
	/* AlertDialog */
	private void showDialog() {
		/*
		 * 这里使用了 android.support.v7.app.AlertDialog.Builder可以直接在头部写 import
		 * android.support.v7.app.AlertDialog 那么下面就可以写成
		 * AlertDialog.Builder,使用xml进行复杂的自定义xml
		 */
		i_SharedPreferences_dialog = 0;
		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.dialog,(ViewGroup) findViewById(R.id.dialog));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		/*
		 * this.mMusicPlay = (ImageView)dialog.findViewById(R.id.music_play);
		 * 放在这个位置也是可以的，主要还是dialog.findViewById中的dialog
		 */
		builder.setTitle("鲤鱼日语");
//		final CharSequence[] items = { "我的生词本", "查询单词", "关于我的日语单词本", "关于其他",
//				"下载音频文件", "音频加密", "语音文本", "播放本页面单词" };
		
		final CharSequence[] items = { "我的生词本", "查询单词", "开启或关闭乱序和展开","关于鲤鱼日语"};		
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					System.out.println(item);
					Intent case_0 = new Intent(MyExpandableListViewDemo.this,
							VocabDraggableView.class);
					MyExpandableListViewDemo.this.startActivity(case_0);
					/* 不加break会一直执行 */
					break;
				case 1:
					System.out.println(item);
					Intent case_1 = new Intent(MyExpandableListViewDemo.this,
							SearchActivity.class);
					MyExpandableListViewDemo.this.startActivity(case_1);
					break;

					
				case 2:
					showDialog_checkbox();
//					System.out.println(item);
//					Intent case_3 = new Intent(MyExpandableListViewDemo.this,
//							AboutMyJC.class);
//					MyExpandableListViewDemo.this.startActivity(case_3);
					break;
				case 3:
					System.out.println(item);
//					Intent case_4 = new Intent(MyExpandableListViewDemo.this,
//							GetAudioActivity.class);
					Intent case_4 = new Intent(MyExpandableListViewDemo.this,
					AboutMyJC.class);
					MyExpandableListViewDemo.this.startActivity(case_4);
					break;
				case 4:
					System.out.println(item);
					Intent case_5 = new Intent(MyExpandableListViewDemo.this,
							VoiceEncryptionActivity.class);
					MyExpandableListViewDemo.this.startActivity(case_5);
					break;
				case 5:
					System.out.println(item);
					Intent case_6 = new Intent(MyExpandableListViewDemo.this,
							JapaneseTTSActivity.class);
					MyExpandableListViewDemo.this.startActivity(case_6);
					break;
				/* 播放本页面单词 */
				case 6:
					System.out.println(item);
					len = MyExpandableListViewDemo.this.adapter.getGroupCount();
					player_list.setOnCompletionListener(new CompletionListener());
					/*加不加MyExpandableListViewDemo.this都不行，结论就是在dialog中无法影响Activity*/
//					MyExpandableListViewDemo.this.fab.setBackgroundResource(R.drawable.off);
//					fab.setBackgroundResource(R.drawable.off);
					fab.setColorNormal(Color.BLACK);
					thread_playList.start();
					break;

				}
			}
		});

		
		builder.setPositiveButton("下一课", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				int position_dialog = classes_dialog.getSelectedItemPosition();

				if (position_dialog + 1 < lessons_list.size()) {
					classes_dialog.setSelection(position_dialog + 1);
					Log.e("869", String.valueOf(position_dialog));
					/* 通过队列消息，间接更新UI */
					Message msg = new Message();
					msg.what = 1;
					msg.arg1 = position_dialog;
					handler_spinner_activity.sendMessage(msg);
				} else {
					Toast.makeText(MyExpandableListViewDemo.this,
							"已到本书最后一课，请选择其他数目", Toast.LENGTH_SHORT).show();
				}

				/* 点击不消失：http://blog.csdn.net/howlaa/article/details/42290867 */
				// try
				// { /*Field：java.lang.reflect.Field*/
				// Field field = dialog.getClass()
				// .getSuperclass().getDeclaredField(
				// "mShowing" );
				// field.setAccessible( true );
				// // 将mShowing变量设为false，表示对话框已关闭
				// field.set(dialog, false );
				// dialog.dismiss();
				//
				// }
				// catch (Exception e)
				// {
				// e.printStackTrace();
				// }
				/**/
				/* 如果AlertDialog不在UI线程，那么下边的一切更新UI的动作都不可能实现 */
				// initWidgets();
				/*
				 * 我的想法是希望点击下一课，直接退出，但是即使增加Thread.sleep(1000);，依然还是不可以记录spinner的改变
				 * ，主要原因还是在AlertDialog中无法更新UI
				 */
				// try {
				// Thread.sleep(1000);
				// } catch (InterruptedException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				// try
				// {
				// Field field = dialog.getClass()
				// .getSuperclass().getDeclaredField(
				// "mShowing" );
				// field.setAccessible( true );
				// // 将mShowing变量设为false，表示对话框已关闭
				// field.set(dialog, true );
				// dialog.dismiss();
				// }
				// catch (Exception e)
				// {
				// e.printStackTrace();
				// }

			}
		});
		builder.setNeutralButton("退出", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Log.e("877", "退出");
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					// 将mShowing变量设为false，表示对话框已关闭
					field.set(dialog, true);
					dialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		builder.setNegativeButton("上一课", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int position_dialog = classes_dialog.getSelectedItemPosition();
				classes_dialog.setSelection(position_dialog - 1);
				Log.e("1041", String.valueOf(position_dialog));
				/* 通过队列消息，间接更新UI */
				Message msg = new Message();
				msg.what = 2;
				msg.arg1 = position_dialog;
				handler_spinner_activity.sendMessage(msg);
				/*
				 * classes_dialog.setSelection(position_dialog -
				 * 1);会调用OnItemSelectedListenerImpl_dialog_classes
				 * ，所以那里边有更新UI所以这里不需要
				 */
				// initWidgets();不需要再次使用initWidgets
			}
		});		
		builder.setView(dialog);
		builder.show();
		
		/*
		 * 将获取布局组件放置到builder.show();可以避免java.lang.NullPointerException
		 * dialog.findViewById一定是dialog不要落下 this.mMusicPlay中的this可加可不加不会报错
		 * 并且注意下面全部是错误的 this.mMusicPlay =
		 * (ImageView)findViewById(R.id.music_play); this.mMusicPlay =
		 * (ImageView)super.findViewById(R.id.music_play);
		 */
//		this.mMusicPlay = (ImageView) dialog.findViewById(R.id.music_play);
//		if (isPlay) {
//			this.mMusicPlay.setImageResource(R.drawable.lock_suspend);
//		} else {
//			this.mMusicPlay.setImageResource(R.drawable.lock_play);
//		}
//		this.mMusicPlay.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (isPlay) {
//					isPlay = false;
//					play_state();
//					/* 通过队列消息，间接更新UI */
//					Message msg = new Message();
//					msg.what = 3;
//					handler.sendMessage(msg);
//					/*下边的线程使用全部错误*/
////					thread_playList = null;
////					thread_playList.destroy();//java.lang.UnsupportedOperationException
////					thread_playList.stop();//java.lang.UnsupportedOperationException
////					thread_playList.interrupt();
//				} else {
//					isPlay = true;
//					play_state();
//					Message msg = new Message();
//					msg.what = 4;
//					handler.sendMessage(msg);
//					len = MyExpandableListViewDemo.this.adapter.getGroupCount();
//					player_list.setOnCompletionListener(new CompletionListener());
//					thread_playList = new MyThread_playList();
//					thread_playList.start();
//
//				}
//			}
//		});

		/* 下边一大块我放到builder.show()之前总觉得不太好，动作理应放到不布局之后 */
		/*
		 * this.classes = (Spinner)
		 * dialog.findViewById(R.id.spinner1);//不可以使用super。不然直接空指针
		 */
		this.classes_dialog = (Spinner) dialog
				.findViewById(R.id.spinner_classes_dialog);// 取得下拉列表框
		this.books_dialog = (Spinner) dialog
				.findViewById(R.id.spinner_books_dialog);// 取得下拉列表框
		this.books_dialog
				.setOnItemSelectedListener(new OnItemSelectedListenerImpl_dialog_books());		
		/*使用自定义spinner*/
		adapterTest = new TestArrayAdapter(MyExpandableListViewDemo.this,
				Books_String);
		/* 复制粘贴注意修改classes为classes_dialog */
		MyExpandableListViewDemo.this.books_dialog.setAdapter(MyExpandableListViewDemo.this.adapterTest);				
		
		this.classes_dialog
				.setOnItemSelectedListener(new OnItemSelectedListenerImpl_dialog_classes());
		settings = getSharedPreferences("preferences_settings", 0); // 与上面的保持一致，或者settings//getSharedPreferences("preferences_settings",Context.MODE_PRIVATE);
		editorsettings = settings.edit();
		int position_books = settings.getInt("SelectedPosition-books", 0);
		this.books_dialog.setSelection(position_books); // 设置spinner的值，让其当前选择项是与以前的一样。
		// books.setAdapter(adapterBooks);
		// classes.setAdapter(adapterClasses);// 设置二级下拉列表的选项内容
		// books.setSelection(position_books,true);
		// classes.setSelection(position_classes,true);adapterClasses.notifyDataSetChanged();adapterBooks.notifyDataSetChanged();
		/* 尽量不要屏蔽 */
		// adapterBooks.notifyDataSetInvalidated();
		// adapterClasses.notifyDataSetInvalidated();
		/* 这里应该实际在进入AlertDialog中其实就已经执行了，而不是在进入spinner时 */
		i_SharedPreferences_dialog = 1;
		/*
		 * 没有下边没，因为notifyDataSetInvalidated，所以也会自动刷新,
		 * 好像是我在OnItemSelectedListenerImpl_dialog_classes加了刷新UI
		 */
		// if (radom_state) {
		// randaomUI();
		// } else {
		// initWidgets();
		// }

	}

	/*
	 * 不一致会隐藏很多问题看dialog布局文件中的这一项，毕竟有四个spinner肯定会有问题android:entries=
	 * "@array/classes_lables"
	 */
	public class OnItemSelectedListenerImpl_books implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) { // 表示选项改变的时候触发
			String book_name = parent.getItemAtPosition(position).toString(); //
			Log.e("book_name", book_name);
			QLessons qLessons = new QLessons();
			/*
			 * 可以试试List<lessons> lessons_list =null,看能不能行List<lessons>
			 * lessons_list =null;可以不影响使用，可以深究
			 */
			// List<lessons> lessons_list = new ArrayList<lessons>();
			lessons_list = qLessons.queryLessons(lessons_Dao, book_name);
			/*
			 * 为了判断spinner的二级在setSelection()不超出一定范围，将String[]
			 * Lessons，作为全局变量，但是无法给定空间，所以把 lessons_list作为全局变量
			 */
			String[] Lessons = new String[lessons_list.size()];
			// 开始写i < lessons.length()直接报错，因为一直是空的啊
			for (int i = 0; i < lessons_list.size(); i++) {
				Lessons[i] = lessons_list.get(i).getTitle();
			}

			/**/
			// 使用自定义的ArrayAdapter
			adapterTest = new TestArrayAdapter(MyExpandableListViewDemo.this,
					Lessons);
			MyExpandableListViewDemo.this.classes
					.setAdapter(MyExpandableListViewDemo.this.adapterTest);

			/* 默认格式 */
			// MyExpandableListViewDemo.this.adapterClasses = new
			// ArrayAdapter<CharSequence>(
			// MyExpandableListViewDemo.this,
			// android.R.layout.simple_spinner_item,
			// Lessons); // 定义所有的列表项
			// MyExpandableListViewDemo.this.adapterClasses
			// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//
			// MyExpandableListViewDemo.this.classes
			// .setAdapter(MyExpandableListViewDemo.this.adapterClasses);//
			// 设置二级下拉列表的选项内容
			/* OnItemSelectedListener可以返回position，以此为参数进行传递,num_b作为全局变量可以得到保存 */
			/* http://www.bubuko.com/infodetail-716156.html */
			editorsettings.putInt("SelectedPosition-books", position);
			editorsettings.putString("SelectedName", book_name);
			editorsettings.commit();

			/*
			 * OnItemSelectedListenerImpl_books在初始化classes这一级spinner后，对其进行设置，
			 * 可以避免之前两次调用
			 */
			i_SharedPreferences = 1;
			int position_classes = settings.getInt("SelectedPosition-classes",
					0);
			classes.setSelection(position_classes);
			initWidgets();

		}

		public void onNothingSelected(AdapterView<?> arg0) { // 表示没有选项的时候触发
			// 一般此方法现在不关心
		}
	}

	/* copy OnItemSelectedListenerImpl_books */
	public class OnItemSelectedListenerImpl_dialog_books implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) { // 表示选项改变的时候触发
			String book_name = parent.getItemAtPosition(position).toString();
			Log.e("book_name", book_name);
			QLessons qLessons = new QLessons();
			// List<lessons> lessons_list = new ArrayList<lessons>();
			lessons_list = qLessons.queryLessons(lessons_Dao, book_name);
			String[] Lessons = new String[lessons_list.size()];
			for (int i = 0; i < lessons_list.size(); i++) {
				
				Lessons[i] = lessons_list.get(i).getTitle();
				
			}
			/*使用自定义格式的spinner*/
			adapterTest = new TestArrayAdapter(MyExpandableListViewDemo.this,
					Lessons);
			/* 复制粘贴注意修改classes为classes_dialog */
			MyExpandableListViewDemo.this.classes_dialog
					.setAdapter(MyExpandableListViewDemo.this.adapterTest);
			
			int position_books = settings.getInt("SelectedPosition-books", 0);
			String book_name_old = parent.getItemAtPosition(position_books)
					.toString();
			Log.e("book_name_old", book_name_old);
			Log.e("book_name", book_name);
			/* book_name==book_name_old：字符串比较地址没有意义 */
			if (book_name.equals(book_name_old)) {
				/* book_name没有变化 */
				Log.e("same", book_name);
				/*
				 * 但是我是怎么想到这个方法的呢，没有下边三句，就会调用两次，可以深入探讨一下
				 * [Important]避免首先显示第一课，再跳转至其他课
				 * ,就没有出现使用两次OnItemSelectedListenerImpl_classes
				 */
				i_SharedPreferences_dialog = 0;
				int position_classes = settings.getInt(
						"SelectedPosition-classes", 0);
				/*
				 * 原来的问题：这里有一个问题就是二级spinner会一直记忆位置，这将导致books已经更换，classes也跟着更换，
				 * 但是classes的初始位置为0，而不是一直保存，当
				 * 旧classes的数量小于新classes，就会报java.lang.
				 * ArrayIndexOutOfBoundsException: length=18; index=19
				 */
				classes_dialog.setSelection(position_classes);
			} else {
				/* book_name变化了 */
				Log.e("diff", book_name);
				Log.e("diff", ""); /* Log.e("diff", "");居然什么都不输出…… */
				i_SharedPreferences_dialog = 0;
				classes_dialog.setSelection(0);
			}
			editorsettings.putInt("SelectedPosition-books", position);
			editorsettings.putString("SelectedName", book_name);
			editorsettings.commit();
			Log.e("dialog_books", String.valueOf(position));

			/* [bug]这里会返回第一行 */
			initWidgets();
			/* 通过队列消息，间接更新Activity的UI，从而控制两个spinner为同步状态 */
			int position_dialog = books_dialog.getSelectedItemPosition();
			Message msg = new Message();
			msg.what = 3;
			msg.arg1 = position_dialog;
			handler_spinner_activity.sendMessage(msg);

		}

		public void onNothingSelected(AdapterView<?> arg0) { // 表示没有选项的时候触发
			// 一般此方法现在不关心
		}
	}

	private class OnItemSelectedListenerImpl_classes implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) { // 表示选项改变的时候触发
			/* 下边应该放到if里吧 */
			/*
			 * i_SharedPreferences==0表示第一次进入Activity！
			 * 在设置spinner时，在瞬时打开books，classes其实又被设置了
			 * ，就是classes刚刚被设置为记忆位置，但是在调用books时就会又被重新置为0，这样就会默认为0了
			 */
			if (i_SharedPreferences == 0) {
				/*
				 * i_SharedPreferences的作用在于第一次进入app时保证books所在spinner初始化后，
				 * 不对classes所在spinner初始化产生影响
				 */
				/* 放在OnItemSelectedListenerImpl_books中可避免两次调用，可以深入了解一下，写一篇* */
				i_SharedPreferences = 1;
				int position_classes = settings.getInt(
						"SelectedPosition-classes", 0);
				/*
				 * java.lang.IndexOutOfBoundsException: Invalid index 10, size
				 * is 10
				 */
				/*
				 * 根据logcat输出，看见当调用setSelection时会先进行i_SharedPreferences == 0的内容
				 * 后进行i_SharedPreferences != 0的内容，这可能就是
				 * setSelection触发了OnItemSelectedListenerImpl_classes
				 */
				classes.setSelection(position_classes, false);
				classes_name = parent.getItemAtPosition(position).toString();
				Log.e("Impl_classes_1", classes_name);

			} else {
				/* http://www.bubuko.com/infodetail-716156.html */
				editorsettings.putInt("SelectedPosition-classes", position);
				editorsettings.putString("SelectedName", classes_name);
				editorsettings.commit();
				classes_name = parent.getItemAtPosition(position).toString();
				Log.e("Impl_classes_2", classes_name);
			}
			if (radom_state) {
				randaomUI();
			} else {
				initWidgets();
			}
			/* 这样可以自动闭合了，但不是很好的方法 */
			collapseGroup();

			/* 2016.6.2******************** */
			/* 代替手动刷新，也不是好方法2016.4.18 */
			// View v_elistView = elistview.getChildAt(0); //
			// // 假设你的代码中ListView对象的变量名是mList
			// /* NO USE：int y_elist= elistview.getScrollY() */
			// int top = (v_elistView == null) ? 0 : v_elistView.getTop();
			// int i_1stPos = elistview.getFirstVisiblePosition();
			// elistview.setSelectionFromTop(i_1stPos, top);
			// /**/
			// if (num_b == 1) {
			// num_c = position + 1;
			// // String value = parent.getItemAtPosition(position).toString();
			// // // 取得选中的选项
			// // MyExpandableListViewDemo.this.info.setText(
			// // "选择的classes为"+num_c+"CheckBox为"+show); // 设置文本组件内容
			// // 这里因为要设置setText，这样就不能有以上的句子了，所以显示为报错
			// /*5.5*/
			// initWidgets();
			// /* 这样可以自动闭合了，但不是很好的方法 */
			// collapseGroup();
			// /* 代替手动刷新，也不是好方法2016.4.18 */
			// View v_elistView = elistview.getChildAt(0); //
			// // 假设你的代码中ListView对象的变量名是mList
			// /* NO USE：int y_elist= elistview.getScrollY() */
			// int top = (v_elistView == null) ? 0 : v_elistView.getTop();
			// int i_1stPos = elistview.getFirstVisiblePosition();
			// elistview.setSelectionFromTop(i_1stPos, top);
			// }
			// if (num_b == 2) {
			// // 因为onItemSelected中的int
			// // position全部是从0开始，所以编程的时候就要注意这一点，选择下册的时候，要注意在 position加上一个定值
			// num_c = position + 25;
			// // String value = parent.getItemAtPosition(position).toString();
			// // // 取得选中的选项
			// // MyExpandableListViewDemo.this.info.setText(
			// // "选择的classes为"+num_c+"CheckBox为"+len); // 设置文本组件内容
			// /*5.5*/
			// initWidgets();
			// /* 这样可以自动闭合了，但不是很好的方法 */
			// collapseGroup();
			// /* 代替手动刷新，也不是好方法2016.4.18 */
			// View v_elistView = elistview.getChildAt(0); //
			// 假设你的代码中ListView对象的变量名是mList
			// /* NO USE：int y_elist= elistview.getScrollY() */
			// int top = (v_elistView == null) ? 0 : v_elistView.getTop();
			// int i_1stPos = elistview.getFirstVisiblePosition();
			// elistview.setSelectionFromTop(i_1stPos, top);
			// }
			/* 2016.6.2 */

		}

		public void onNothingSelected(AdapterView<?> arg0) { // 表示没有选项的时候触发
			// 一般此方法现在不关心
		}
	}

	/* copy OnItemSelectedListenerImpl_classes */
	private class OnItemSelectedListenerImpl_dialog_classes implements
			OnItemSelectedListener {
		/* 用来解决UI在不更换class时，不进行UI刷新 */
		boolean i_remember_classes = true;

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) { // 表示选项改变的时候触发
			if (i_SharedPreferences_dialog == 1) {
				/* i_SharedPreferences_dialog =0;必须存在 */
				i_SharedPreferences_dialog = 0;
				int position_classes = settings.getInt(
						"SelectedPosition-classes", 0);
				/* 因为复制粘贴，classes_dialog开始是classes */
				classes_dialog.setSelection(position_classes);
				classes_name = parent.getItemAtPosition(position).toString();
				Log.e("dialog_classes_1", classes_name);

			} else {
				editorsettings.putInt("SelectedPosition-classes", position);
				editorsettings.putString("SelectedName", classes_name);
				editorsettings.commit();
				classes_name = parent.getItemAtPosition(position).toString();
				Log.e("dialog_classes_2", classes_name);
				i_remember_classes = false;
			}
			if (radom_state) {
				/* 无效：让Activity中的spinner能够及时更新变化* */
				// MyExpandableListViewDemo.this.adapterBooks.notifyDataSetChanged();
				// MyExpandableListViewDemo.this.adapterClasses.notifyDataSetChanged();
				/* 如果是乱序，目前有bug进入后会强制属刷新UI，并且会是正序 */
				randaomUI_Fav();
				View v_elistView = elistview.getChildAt(0);
				int top = (v_elistView == null) ? 0 : v_elistView.getTop();
				int i_1stPos = elistview.getFirstVisiblePosition();
				elistview.setSelectionFromTop(i_1stPos, top);

				/* [bug]只要进入AlertDialog，就停止播放，然后重置为0 */
				player_list.stop();
				player_list_c.stop();
				/* songIndex更改为0会有些问题，就是只要进入AlertDialog，songIndex就会被重置为0
				 * 但是songIndex不重置为0即使换页了，依然还是之前页面残留的songIndex*/
//				songIndex = 0;
			} else {

				initWidgets();
				/* 具体见MarkClickListenerImpl(就是如何保证乱序下单击收藏，乱序的顺序不变的功能) */
				/* "如果class被选择那么，就不再记录位置，如果没有改变就记录移动位置" */
				if (i_remember_classes) {
					View v_elistView = elistview.getChildAt(0);
					int top = (v_elistView == null) ? 0 : v_elistView.getTop();
					int i_1stPos = elistview.getFirstVisiblePosition();
					elistview.setSelectionFromTop(i_1stPos, top);
				}
				/*
				 * [bug]在页面刷新时注意停止播放语音下边停止后，后台依然在按原来的顺序，即已经新list下的播放顺序是承接上一个，所以不可以
				 */
				player_list.stop();
				player_list_c.stop();
				/* songIndex起始位置初始为0就可以了 */
//				songIndex = 0;

			}
			/* 通过队列消息，间接更新UI，从而控制两个spinner为同步状态 */
			int position_dialog = classes_dialog.getSelectedItemPosition();
			Message msg = new Message();
			msg.what = 4;
			msg.arg1 = position_dialog;
			Log.e("radom_state", String.valueOf(radom_state));
			handler_spinner_activity.sendMessage(msg);
			/* 这样可以自动闭合了，但不是很好的方法 */
			collapseGroup();
		}

		public void onNothingSelected(AdapterView<?> arg0) { // 表示没有选项的时候触发
			// 一般此方法现在不关心
		}
	}







	private class OnChildClickListenerImpl implements OnChildClickListener {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			// Toast.makeText(
			// MyExpandableListViewDemo.this,
			// "子选项被选中，groupPosition = " + groupPosition
			// + "，childPosition = " + childPosition,
			// Toast.LENGTH_SHORT).show(); // 显示提示框
			return false;
		}

	}

	private class OnGroupClickListenerImpl implements OnGroupClickListener {

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			// Toast.makeText(MyExpandableListViewDemo.this,
			// "分组被选中，groupPosition = " + groupPosition,
			// Toast.LENGTH_SHORT).show(); // 显示提示框
			return false;
		}

	}

	private class OnGroupCollapseListenerImpl implements
			OnGroupCollapseListener {

		@Override
		public void onGroupCollapse(int groupPosition) {
			// Toast.makeText(MyExpandableListViewDemo.this,
			// "关闭分组，groupPosition = " + groupPosition, Toast.LENGTH_SHORT)
			// .show(); // 显示提示框
		}

	}

	private class OnGroupExpandListenerImpl implements OnGroupExpandListener {

		@Override
		public void onGroupExpand(int groupPosition) {
			// Toast.makeText(MyExpandableListViewDemo.this,
			// "打开分组，groupPosition = " + groupPosition, Toast.LENGTH_SHORT)
			// .show(); // 显示提示框
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		// ExpandableListView.ExpandableListContextMenuInfo info =
		// (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
		// int type = ExpandableListView
		// .getPackedPositionType(info.packedPosition); // 取得操作的菜单项
		// int group = ExpandableListView
		// .getPackedPositionGroup(info.packedPosition); // 取得菜单项所在的菜单组
		// int child = ExpandableListView
		// .getPackedPositionChild(info.packedPosition); // 取得子菜单项的索引
		// Toast.makeText(MyExpandableListViewDemo.this,
		// "type = " + type + "，group = " + group + "，child = " + child,
		// Toast.LENGTH_SHORT).show(); // 显示提示框
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;

	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;

	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// 对手指滑动的距离进行了计算，如果滑动距离大于120像素，就做切换动作，否则不做任何切换动作。
		// 从左向右滑动
		if (arg0.getX() - arg1.getX() > 120) {
			// 添加动画
			Log.e("向右", "向右");
			int position_fling = classes.getSelectedItemPosition();

			classes.setSelection(position_fling + 1);
			initWidgets();
			return true;
		}// 从右向左滑动
		else if (arg0.getX() - arg1.getX() < -120) {
			Log.e("向左", "向左");
			int position_fling = classes.getSelectedItemPosition();
			classes.setSelection(position_fling - 1);
			initWidgets();
			return true;
		}
		return true;
	}
	/*
	 * 重写此方法将触控事件优先分发给GestureDetector，以解决滑动ListView无法切换屏幕的问题
	 * http://www.bkjia.com/Androidjc/865363.html
	 */
	/* 注意在使用 gestureDetector时必须有 gestureDetector = new GestureDetector(this); */
	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// // TODO Auto-generated method stub
	// this.gestureDetector.onTouchEvent(ev);
	// return super.dispatchTouchEvent(ev);
	// }
}