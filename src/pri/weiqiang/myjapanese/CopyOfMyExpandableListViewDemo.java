package pri.weiqiang.myjapanese;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.R.array;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import java.io.File;
import java.nio.channels.FileChannel;

import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.speech.tts.TextToSpeech;


import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;//安置paly播放音乐的可以使很多组件，不一定非要是imagebutton或者
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
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
import pri.weiqiang.daosql.QTitle;
import pri.weiqiang.random.RandomEntity;
import pri.weiqiang.random.RandomJ;
import pri.weiqiang.search.SearchActivity;

import pri.weiqiang.vocabulary.VocabDraggableView;
public class CopyOfMyExpandableListViewDemo extends Activity {
	/*有的otf文件有错误，会报native typeface cannot be made，是因为替换时把“fonts/”落下了*/
	static String fontPath_child = "fonts/A-OTF-KyokaICAPro-Medium.otf";
	static Typeface tf_child ;
	static String fontPath_group = "fonts/A-OTF-NachinStd-Regular.otf";
	/*tf = Typeface.createFromAsset(getAssets(), fontPath);直接放这里还是错误的*/
	static Typeface tf_group ;
	// private ExpandableListView elistview = null; // 定义树型组件
	// private ExpandableListAdapter adapter = null; // 定义适配器对象
	private ExpandableListView elistview; // 定义树型组件
	private ExpandableListAdapter adapter; // 定义适配器对象
	private Spinner classes = null;// 下拉列表框内容
	private Spinner books = null;// 下拉列表框内容
	private CheckBox show = null;
	private CheckBox checkBox_random = null;
	private Button word = null;// 下拉列表框内容
	private Button search = null;// 下拉列表框内容
	private Button about = null;// 下拉列表框内容	
	/*加入greenDao*/
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private wordsDao words_Dao;
    private lesson_titleDao title_Dao;
    private lessonsDao	lessons_Dao;
    /*classes_name必须有初始值，否则会无法初始化*/
    String classes_name=null;//String classes_name="新编日语I_01";
    /*为了update可以方便的更改entity，所以使用全局变量*/
    List<words> words_list = new ArrayList<words>();
    List<words> words_List_backup = new ArrayList<words>();
    List<words> ran_list = new ArrayList<words>();
    /**/
    List<RandomEntity> ranList=new ArrayList<RandomEntity>();
    /*通过java.util.List.indexOf(Object obj)获取id（即正常排序顺序）*/
   List<Integer> arr = new ArrayList<Integer>();
   boolean radom_state=false;
   boolean radom_mark=false;
   /*使用SharedPreferences记住spinner上回保留的位置：http://www.bubuko.com/infodetail-716156.html*/
   SharedPreferences settings;
   Editor editorsettings;  
   int i_SharedPreferences=0;
   /*使用数据库完成下边的数组输入*/
//	private String[][] classes_Data = new String[][] {
//			{ "第1课", "第2课", "第3课", "第4课", "第5课", "第6课", "第7课", "第8课", "第9课",
//					"第10课", "第11课", "第12课", "第13课", "第14课", "第15课", "第16课",
//					"第17课", "第18课", "第19课", "第20课", "第21课", "第22课", "第23课",
//					"第24课" }, // 针对于一级的子信息
//			{ "第25课", "第26课", "第27课", "第28课", "第29课", "第30课", "第31课", "第32课",
//					"第33课", "第34课", "第35课", "第36课", "第37课", "第38课", "第39课",
//					"第40课", "第41课", "第42课", "第43课", "第44课", "第45课", "第46课",
//					"第47课", "第48课" }, // 针对于二级的子信息
//	}; // 子菜单项
	private TextView info = null; // 以后事件发生之后取得下拉列表框的内容
	private ArrayAdapter<CharSequence> adapterClasses = null;// 在设置spinner时使用二级联动时需要
	// new
	// 文件的路径
	public final static String URL = "/data/data/pri.weiqiang.myjapanese/files";
	// 数据库文件
	public final static String DB_FILE_NAME = "vocab.db";
	// 索引ID
	public final static int ID_INDEX = 0;
	public final static int NUMBER_INDEX = 1;
	public final static int LOCATION_INDEX = 2;
	public final static int CITY_INDEX = 3;
	public final static int CLASSES_INDEX = 4;
	public final static int MARK_INDEX = 5;// 这是新增的收藏项用来记没有背下的单词
	int num_c = 1;// 通过num来定义spinner选择的是哪一课程
	int num_b = 1;// 通过num来定义spinner选择的是哪一本书
	int len;// 定义全部长度
	EditText editText = null;
	Button button = null;
	TextView textView = null;
	SQLiteDatabase db = null;
	View myView;
	/* TTS */
	Button ttsButton;
	/*Random*/
	Button ranButton;
	private JapaneseTTS tts;
	// new
	/*老版本中并没有出现第一次单击藏夹按钮，UI更新至最上行，可以根据BeyondCompare找到解决问题的方法*/
	/*老版本中并没有出现第一次单击藏夹按钮，UI更新至最上行，可以根据BeyondCompare找到解决问题的方法*/
	/*老版本中并没有出现第一次单击藏夹按钮，UI更新至最上行，可以根据BeyondCompare找到解决问题的方法*/
	/*老版本中并没有出现第一次单击藏夹按钮，UI更新至最上行，可以根据BeyondCompare找到解决问题的方法*/
	/*老版本中并没有出现第一次单击藏夹按钮，UI更新至最上行，可以根据BeyondCompare找到解决问题的方法*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main); // 默认布局管理器
		/*一定不要放到getGroupView中会慢死，那样相当于每次都进行100次Typeface.createFromAsset操作*/
		tf_group = Typeface.createFromAsset(getAssets(), fontPath_group);
		tf_child = Typeface.createFromAsset(getAssets(), fontPath_child);
		this.ttsButton = (Button) super.findViewById(R.id.button2tts);
		this.ranButton=(Button)super.findViewById(R.id.button2ran);
		this.search.setOnClickListener(new OnClickListenerImpl_search());
		this.about.setOnClickListener(new OnClickListenerImpl_about());
		this.word.setOnClickListener(new OnClickListenerImpl_word());
		this.classes = (Spinner) super.findViewById(R.id.classes);// 取得下拉列表框
		this.books = (Spinner) super.findViewById(R.id.books);// 取得下拉列表框
		/*使用SharedPreferences记住spinner上回保留的位置：http://www.bubuko.com/infodetail-716156.html*/
		settings = getSharedPreferences("preferences_settings",0); //与上面的保持一致，或者settings = getSharedPreferences("preferences_settings",Context.MODE_PRIVATE);
		int position_books = settings.getInt("SelectedPosition-books", 0 );
		/*如果不屏蔽下边：则会报：java.lang.IndexOutOfBoundsException: Invalid index 10, size is 10*/
//		int position_classes = settings.getInt("SelectedPosition-classes", 0 );
//		classes.setSelection(position_classes);
//		Log.e("classes.setSelection", String.valueOf(position_classes));	
		
		books.setSelection(position_books); //设置spinner的值，让其当前选择项是与以前的一样。		
		editorsettings = settings.edit();
		Log.e("books.setSelection(", String.valueOf(position_books));

		/**/
		// this.info = (TextView) super.findViewById(R.id.info); // 取得文本显示组件
		this.show = (CheckBox) super.findViewById(R.id.showchinese); // 取得CheckBox显示组件
		this.checkBox_random = (CheckBox) super.findViewById(R.id.startRandom); // 取得CheckBox显示组件

		this.elistview = (ExpandableListView) super
				.findViewById(R.id.elistview); // 取得组件
		
		this.elistview.setGroupIndicator(null); // 设置 属性 GroupIndicator
												// 去掉默认向下的箭头
		this.adapter = new MyExpandableListAdapter(this); // 实例化适配器
		this.elistview.setAdapter(this.adapter); // 设置适配器
		super.registerForContextMenu(this.elistview); // 注册上下文菜单
		this.elistview.setOnChildClickListener(new OnChildClickListenerImpl()); // 设置子项单击事件
		this.elistview.setOnGroupClickListener(new OnGroupClickListenerImpl()); // 设置组项单击事件
		this.elistview
				.setOnGroupCollapseListener(new OnGroupCollapseListenerImpl()); // 关闭分组事件
		this.elistview
				.setOnGroupExpandListener(new OnGroupExpandListenerImpl()); // 展开分组事件
		/*没有分割线还挺好看，看看直接把xml中的分割线去掉吧*/
		this.elistview.setChildDivider(null);
		this.elistview.setDivider(null);
		/**/
		FloatingActionButton fab = (FloatingActionButton) super.findViewById(R.id.fab);

		fab.attachToListView(elistview, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                Log.d("ListViewFragment", "onScrollDown()");
            }

            @Override
            public void onScrollUp() {
                Log.d("ListViewFragment", "onScrollUp()");
            }
        }, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d("ListViewFragment", "onScrollStateChanged()");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("ListViewFragment", "onScroll()");
            }
        });
        /**/
        fab.setOnClickListener(new OnClickListener(){
		
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

		// 以下是为CheckBox设置监听器，从而控制所有expandablelistview展开
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
		/*为乱序输出进行监听*/
				this.checkBox_random
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton buttonView,
									boolean isChecked) {
								// TODO Auto-generated method stub
								if (isChecked) {
									radom_state=true;
									randaomUI();
								} else {
									radom_state=false;
									initWidgets();
								}
							}
						});
		/*TTS*/
		this.ttsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(CopyOfMyExpandableListViewDemo.this,
						JapaneseTTSActivity.class);
				CopyOfMyExpandableListViewDemo.this.startActivity(it);
			}
		});
		/*是UI转换为随机排列*/
		this.ranButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				randaomUI();
			}
		});
		/*greenDao*/
		 copyDB();
		 File file = new File(URL, DB_FILE_NAME);
		 db = SQLiteDatabase.openOrCreateDatabase(file, null);
		 /*通过修改"notes-2-db"改变不同数据库的表名，卡伊看到数据库中不同的数据，而且全部都保存了下来*/
	        Log.e("database position", db.getPath());
	        /*http://stackoverflow.com/questions/25771689/greendao-store-data-in-sqlite-file-on-android*/
	        daoMaster = new DaoMaster(db);
	        DaoMaster.createAllTables(db, true);	        
	        daoSession = daoMaster.newSession();
	        words_Dao = daoSession.getWordsDao();
	        /*没有连接在单独类Volesson中会报空指针*/
	        title_Dao=daoSession.getLesson_titleDao();
	        lessons_Dao=daoSession.getLessonsDao(); 

			File f = new File(this.getFilesDir().getPath()+"/vocab.db");
			Log.e("vocab所在路径", this.getFilesDir().getPath()+"/vocab.db");
			/*将数据库拷贝到SD卡，用意观察数据库变化*/
			String sdcardPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			File o = new File(sdcardPath + "/jc1535.db"); // sdcard上的目标地址
			if (f.exists()) {
				FileChannel outF;
				try {
					outF = new FileOutputStream(o).getChannel();
					new FileInputStream(f).getChannel().transferTo(0, f.length(),
							outF);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.e("1", "输出成功");
			} 
	    /**/			
		initWidgets();
		
		/**/
		tts = new JapaneseTTS(this, null);
		
	}

	// 继承BaseExpandableListAdapter并覆写类中的抽象方法
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		public String[] groups = { "Fail to search the database.", };
		public String[][] children = { { "没有查询到改信息，或者数据库使用失败", "总之，数据库使用失败" },
		/*
		 * List<Children_List>children_list=new
		 * ArrayList();在MyExpandableListAdapter不能使用ArrayList
		 */
		}; // 定义组项
		public int[] mark_num = { 1, };
		private Context context = null; // 保存上下文对象
		// 文件的路径
		public final static String URL = "/data/data/pri.weiqiang.myjapanese/files";
		// 数据库文件
		public final static String DB_FILE_NAME = "location.db";
		public final static String TABLE_NAME = "location_date";
		// 索引ID
		public final static int ID_INDEX = 0;
		public final static int NUMBER_INDEX = 1;
		public final static int LOCATION_INDEX = 2;
		public final static int CITY_INDEX = 3;
		public final static int CLASSES_INDEX = 4;
		public final static int MARK_INDEX = 5;// 这是新增的收藏项用来记没有背下的单词
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
			textView.setLayoutParams(param); // 设置布局参数
			textView.setTextSize(22); // 设置文字大小
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
			textView.setTypeface(tf_child);
			//textView.setTextColor(Color.RED);
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
		/*取得Mark对象*/
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

			textView.setTextSize(25); // 设置文字大小
			textView.setTextColor(Color.BLACK);
			textView.setTypeface(Typeface.SANS_SERIF);
			textView.setGravity(Gravity.CENTER_VERTICAL); // 左对齐
			/*设置成这个样子就居中了，上下间隔，还是要多试试*/
			textView.setPadding(10, 22, 0, 10); // 间距
			textView.setText(getGroup(groupPosition).toString());
			/*更换外部字体*/
			//tf = Typeface.createFromAsset(getAssets(), fontPath);
			textView.setTypeface(tf_group);

			/** TTS。可以考虑填在child中直接读音标也可以 */
			Button tts_Button = (Button) view.findViewById(R.id.group_speak);
			tts_Button.setFocusable(false);
			tts_Button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					HashMap<String, String> params = new HashMap<String, String>();
					params.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
							String.valueOf(AudioManager.STREAM_MUSIC));
					params.put(JapaneseTTS.KEY_PARAM_SPEAKER, "male01");
					tts.speak(getGroup(groupPosition).toString(),
							TextToSpeech.QUEUE_FLUSH, params);
				}
			});
			/* TTS */
			final ImageView logo1 = (ImageView) view
					.findViewById(R.id.imageView1);
			// ImageView logo2 = (ImageView) view.findViewById(R.id.imageView2);
			logo1.setFocusable(false);
			// logo2.setFocusable(false);
			// logo1.setImageResource(R.drawable.groupview_image);
			logo1.setImageResource(R.drawable.btn_star_press);
			// logo1.setBackgroundResource(R.drawable.on);是更改的背景，而logo1.setImageResource(R.drawable.on)是更换图片
			/*5.5*/
//			String mark_adpter = getMark(groupPosition).toString();
//
//			final int mark_int = Integer.parseInt(mark_adpter);
//			if (mark_int == 0) {
//				logo1.setImageResource(R.drawable.btn_play_press);
//
//			}
			/*5.5*/
			final int mark_fav=getMark(groupPosition);
			if (mark_fav== 0) {
				logo1.setImageResource(R.drawable.btn_play_press);
			}
			class MarkClickListenerImpl implements OnClickListener {
				public void onClick(View v) {
					
					/*4464【l】是长类型*/					
//					words entity=new words(4464l);
					/*java.lang.IllegalArgumentException: the bind value at index 2 is null
					 * 应该是参数应该是全部需要输入的，所以entity不能new，且通过debug进入后，看到全部是null*/
//					entity.setFav(1);
//					words_Dao.update(entity);
					/*java.lang.IllegalStateException: The content of the adapter has changed
					 but ListView did not receive a notification. Make sure the content 
					of your adapter is not modified from a background thread, but only from 
					the UI thread. Make sure your adapter calls notifyDataSetChanged() when 
					its content changes. [in ListView(2131165193, class android.widget.ExpandableListView) 
					with Adapter(class android.widget.ExpandableListConnector)]
					这里的情况是这样的
					*/
					/*将words_list作为全局变量，从而方便获取个体entity*/
/*0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43
 16 21 39 40 38 15 20 5 36 27 32 23 4 17 37 30 1 0 11 10 24 8 13 7 14 26 43 9 12 18 2 19 42 31 29 3 28 6 22 41 33 35 25 34 

					0 17 16
					1 16 21
					2 30 39*/
					
					if (radom_state) {
						/*************************************/
						/*乱序之后，groupPositon和原来的位置不一定一致，需要注意*/
						/*********************************/
						/*指数是可能和groupPosition相同的情况，所以不可以那么做*/
						int i_arr_index=arr.indexOf(groupPosition);
						
						Log.e("groupPosition", String.valueOf(groupPosition));
						Log.e("i_arr_index", String.valueOf(i_arr_index));
						Log.e("arr.get(groupPosition)", String.valueOf(arr.get(groupPosition)));
						/*i_arr_index不是正确的位置*/
						words entity=words_list.get(arr.get(groupPosition));

						if (entity.getFav()==0) {
							entity.setFav(1);
						}
						else {
							entity.setFav(0);
						}
						words_Dao.update(entity);
						/*notifyDataSetInvalidated();必须加,如果不加，则无论第一*/
						notifyDataSetInvalidated();
						/*16.5.8*/
						notifyDataSetChanged();
						/*randaomUI();启动randaomUI(),必须增加一个判断，原来的arr(随机数组不能clear掉)*/
						radom_mark=true;
						randaomUI();
						/*radom_mark必须成对出现，否则将一直处于radom_mark状态*/
						radom_mark=false;
						
					}
					else {
						words entity=words_list.get(groupPosition);

						if (entity.getFav()==0) {
							entity.setFav(1);
						}
						else {
							entity.setFav(0);
						}
						words_Dao.update(entity);
						/*notifyDataSetInvalidated();必须加*/
						notifyDataSetInvalidated();
						/*16.5.8*/
						notifyDataSetChanged();
						initWidgets();
					}
					
					
					
					/*方法2*/				
					/*因为返回去找entity还不如直接使用生气了语句，使用id直接精确定位，然后进行修改，demo中的遍历也是拿sql的语句直接写的*/
					
//					String sql = "UPDATE " + TABLE_NAME+ " SET fav=? WHERE number=? AND city=?";					
//					if (mark_fav == 0) {
//						Object args[] = new Object[] { 1,
//								getGroup(groupPosition).toString(),
//								getChild(groupPosition, 0).toString() };
//						db.execSQL(sql, args);
//						// logo1.setImageResource(R.drawable.btn_star_press);
//					}// 这里是为了添加和取消收藏，但是还是不明白为什么许多位置要加final
//					else {
//						Object args[] = new Object[] { 0,
//								getGroup(groupPosition).toString(),
//								getChild(groupPosition, 0).toString() };
//						db.execSQL(sql, args);
//						// logo1.setImageResource(R.drawable.btn_play_press);
//					}
//					/*
//					 * notifyDataSetChanged()可能是起作用了，也可能没有起作用，起作用就是数据库及时更新了，
//					 * 但是UI界面并不会及时更新，所以要加一个UI更新命令
//					 */
//					/* 可以解决实时刷新的问题，但是只有过一页就会看出来刷新 */
//					View v_elistView = elistview.getChildAt(0); // 假设你的代码中ListView对象的变量名是mList
//					notifyDataSetInvalidated();
//					initWidgets();
//					db.close();
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
//					/**/
//					File file = new File(URL, DB_FILE_NAME);
//					db = SQLiteDatabase.openOrCreateDatabase(file, null);
//					// textView.setText("Mark");
//					// String selectionArgs[] = new String[] { "%" + keyWord +
//					// "%","%" + keyWord + "%","%" + keyWord + "%"
//					// }; // 查询参数
//					// //String columns[] = new String[]
//					// {"number","city","location"} ; // 查询列
//					// //String sql =
//					// "SELECT number,city,location From location_date Where number = ? OR city = ? OR location = ?";
//					// String sql =
//					// "SELECT * From location_date SET mark=? WHERE ";
//					String sql = "UPDATE " + TABLE_NAME
//							+ " SET mark=? WHERE number=? AND city=?";
//					// 原来输入的是String sql = "UPDATE " + TABLE_NAME
//					// + " SET mark=? WHERE number=? AND city=?";
//					// 中的AND变成","就会报错：near ",": syntax error (code 1): , while
//					// compiling: UPDATE location_date SET mark=? WHERE
//					// number=?,c
//
//					// SQL语句
//					if (mark_int == 0) {
//						Object args[] = new Object[] { "1",
//								getGroup(groupPosition).toString(),
//								getChild(groupPosition, 0).toString() };
//						db.execSQL(sql, args);
//						// logo1.setImageResource(R.drawable.btn_star_press);
//					}// 这里是为了添加和取消收藏，但是还是不明白为什么许多位置要加final
//					else {
//						Object args[] = new Object[] { "0",
//								getGroup(groupPosition).toString(),
//								getChild(groupPosition, 0).toString() };
//						db.execSQL(sql, args);
//						// logo1.setImageResource(R.drawable.btn_play_press);
//					}
//					/*
//					 * notifyDataSetChanged()可能是起作用了，也可能没有起作用，起作用就是数据库及时更新了，
//					 * 但是UI界面并不会及时更新，所以要加一个UI更新命令
//					 */
//					/* 可以解决实时刷新的问题，但是只有过一页就会看出来刷新 */
//					View v_elistView = elistview.getChildAt(0); // 假设你的代码中ListView对象的变量名是mList
//					/* NO USE：int y_elist= elistview.getScrollY() */
//					int top = (v_elistView == null) ? 0 : v_elistView.getTop();
//					int i_1stPos = elistview.getFirstVisiblePosition();
//					/* 下边的参数传错了 */
//					// int position = ((AdapterView<ListAdapter>)
//					// v_elistView).getFirstVisiblePosition();
//					/* 自动更新之关键，使用排除法验证哪个起效了 */
//					// ((BaseExpandableListAdapter)
//					// adapter).notifyDataSetChanged();
//					// ((BaseExpandableListAdapter)
//					// adapter).notifyDataSetInvalidated();
//					// notifyDataSetChanged();
//					notifyDataSetInvalidated();
//					initWidgets();
//					/* 如果实现自动更新那下边的保留列表首句位置也不可以不用了。[已验证]注释掉可以 */
//					// elistview.setSelectionFromTop(i_1stPos, top);
//					/* childPostion不是1就是0 */
//					db.close();
				}
			}
			logo1.setOnClickListener(new MarkClickListenerImpl());

			// 这里需要注意的是直接比较getMark(groupPosition).toString()=="0";不会成功，可见toString类看来并不是一个简单的string，这里应该是堆和栈的关系
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

	public void randaomUI() {//randaomUI()
		if (radom_state) 
		{
			QWords qWords = new QWords();						
			words_list=qWords.queryWords_Lesson_id(words_Dao, classes_name);
			/*不要多次调用words_list.size()，使用int words_size=words_list.size()，保证后续仅调用一次*/
			int words_size=words_list.size();
			/*如此这般也不行：words_List_backup与words_list内都是null，看见words_List_backup=words_list*/
//			for (int i = 0; i < words_size; i++) {
//				words_List_backup.get(i).=words_List_backup.get(i);
//			}			
			/*【E】 java.lang.IllegalArgumentException: n <= 0: 0
			 *  看下int java.util.Random.nextInt(int n) 几乎可以断定应该是开始initWidgets的数列是空的所以会出现0:0，机最大的数是0,
			 *  所以无法出现
			 *  */
//			RandomJ randomJ=new RandomJ();
//			ran_list=randomJ.name(words_list);
//			for (int i = 0; i < words_size; i++) {
//				/* Invalid index 0, size is 0
//				 * 在使用随机后，原Arraylist可能已经被破坏
//				 * */
//				Log.e("words_list", words_list.get(i).getTranslation() );
//
//			}
			String[] array1 = new String[words_size];
			String[][] array2 = new String[words_size][2];
			int[] array3 = new int[words_size];
			
			/*使用Random之后，原表会发生更改，这么做之后，会有一个弊端，就是update数据库时，原表实际上市不存在了，所以
			 * 我的想法就是仅在UI处进行乱序
			 * http://www.xuebuyuan.com/593802.html*/
//			List<Integer> arr = new ArrayList<Integer>();
			/*Invalid index 60, size is 60:for (int i = 0; i 【=<】words_size; i++)*/
			
			/*每次UI更新应该会要置空，不然会覆盖*/
			/*arr=null;java.lang.NullPointerException*/
//			arr=null;
			
			if (radom_mark) {
				
			} else {
				arr.clear();//使用null一定注意，三思，arr=null之后应该是直接把指向堆（已经开辟好的）的指针指向了空（没有开辟好的空间）		        
				for (int i = 0; i <words_size; i++)
		        {	 arr.add(i);
		        }
		        System.out.println("打乱前:");
		        for (int i : arr)
		        {
		            System.out.print(i + " ");
		        }
		        Collections.shuffle(arr);
		        System.out.println("打乱后:");
		        for (int i : arr)
		        {
		            System.out.print(i + " ");
		        }
			}		        
			
	        /*ArrayList必须放入元素，下边的会报
	         * java.lang.RuntimeException: Unable to start activity ComponentInfo{pri.weiqiang.myjapanese/pri.weiqiang.myjapanese.MyExpandableListViewDemo}: 
	         * java.lang.IndexOutOfBoundsException: Invalid index 0, size is 0*/
//	        for (int i = 0; i < words_size; i++) {
//	        	ranList.get(i).order=i;
//	        	ranList.get(i).random=arr.get(i);	
//	        	Log.e("order", String.valueOf(ranList.get(i).order));
//	        	Log.e("random", String.valueOf(ranList.get(i).random));
//			}
			/**/
			for (int i = 0; i < words_size; i++) {
				array1[i] = words_list.get(arr.get(i)).getWord();//Group：word
				array2[i][0] = words_list.get(arr.get(i)).getPhonetic();//Child：phonetic
				array2[i][1] = words_list.get(arr.get(i)).getTranslation();//Child：translation
				array3[i] = words_list.get(arr.get(i)).getFav();//fav				
			}
			/**/
//			for (int i = 0; i < words_size; i++) {
//				array1[i] = words_list.get(i).getWord();//Group：word
//				array2[i][0] = words_list.get(i).getPhonetic();//Child：phonetic
//				array2[i][1] = words_list.get(i).getTranslation();//Child：translation
//				array3[i] = words_list.get(i).getFav();//fav				
//			}
			((MyExpandableListAdapter) this.adapter).SetArr(array1, array2,array3);			
			/*总是移动view后，数据才进行更新，会不会是这里的原因
			 * 加完后，立刻解决了以前的问题*/
			((MyExpandableListAdapter) this.adapter).notifyDataSetChanged();
			((MyExpandableListAdapter) this.adapter).notifyDataSetInvalidated();
			
		}
}
	

	/*有序化UI*/
	public void  initWidgets() {
		if(!radom_state)
		{
			QWords qWords = new QWords();			
		
		words_list=qWords.queryWords_Lesson_id(words_Dao, classes_name);
		/*不要多次调用words_list.size()，使用int words_size=words_list.size()，保证后续仅调用一次*/
		int words_size=words_list.size();
		String[] array1 = new String[words_size];
		String[][] array2 = new String[words_size][2];
		int[] array3 = new int[words_size];
		/**/
		for (int i = 0; i < words_size; i++) {
			array1[i] = words_list.get(i).getWord();//Group：word
			array2[i][0] = words_list.get(i).getPhonetic();//Child：phonetic
			array2[i][1] = words_list.get(i).getTranslation();//Child：translation
			array3[i] = words_list.get(i).getFav();//fav			
		}
		((MyExpandableListAdapter) this.adapter).SetArr(array1, array2,array3);		
		/*总是移动view后，数据才进行更新，会不会是这里的原因:经过验证，不增加此处，进行SetArr后，UI不会进行更新*/
		((MyExpandableListAdapter) this.adapter).notifyDataSetChanged();
		((MyExpandableListAdapter) this.adapter).notifyDataSetInvalidated();
		}
	}

	

	/*将raw文件中的数据库文件拷贝至手机中的程序内存当中*/
	public boolean copyDB() {
		try {
			// 判断程序内存中是否有拷贝后的文件
			if (!(new File(URL)).exists()) {
				InputStream is = getResources().openRawResource(R.raw.vocab);
				FileOutputStream fos = this.openFileOutput(DB_FILE_NAME,Context.MODE_WORLD_READABLE);
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

	/*设置子类全部展开2015.9.27*/
	private void onGroupExpand() {
		int len = this.adapter.getGroupCount();
		for (int i = 0; i < len; i++) {
			this.elistview.expandGroup(i);
		}
	}
	/*2016.5.8,最近还有一个问题就是在打开app第一次，不展开一项，后续只要不在视野UI内的elistview中收藏单词就会调回到视野中，我想在启动app的第一次展开合并最后一项*/
	private void ExpandCollapse1st() {
		int len = this.adapter.getGroupCount();
		this.elistview.expandGroup(len);
		this.elistview.collapseGroup(len);
	}

	private void collapseGroup() {
		int len = this.adapter.getGroupCount();
		for (int i = 0; i < len; i++) {
			this.elistview.collapseGroup(i);
		}
	}
	/*AlertDialog*/
	private void showDialog(){  
        AlertDialog dialog = new AlertDialog.Builder(this).create();//创建一个AlertDialog对象  
        View view = getLayoutInflater().inflate(R.layout.dialog, null);//自定义布局  
        dialog.setView(view, 0, 0, 0, 0);//把自定义的布局设置到dialog中，注意，布局设置一定要在show之前。从第二个参数分别填充内容与边框之间左、上、右、下、的像素  
        dialog.show();//一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了  
        int width = getWindowManager().getDefaultDisplay().getWidth();//得到当前显示设备的宽度，单位是像素  
        android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();//得到这个dialog界面的参数对象  
        params.width = width-(width/6);//设置dialog的界面宽度  
        params.height =  LayoutParams.WRAP_CONTENT;//设置dialog高度为包裹内容  
        params.gravity = Gravity.CENTER;//设置dialog的重心  
        //dialog.getWindow().setLayout(width-(width/6),  LayoutParams.WRAP_CONTENT);//用这个方法设置dialog大小也可以，但是这个方法不能设置重心之类的参数，推荐用Attributes设置  
        dialog.getWindow().setAttributes(params);//最后把这个参数对象设置进去，即与dialog绑定  
          
    }  
	/**/
	public class OnItemSelectedListenerImpl_books implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) { // 表示选项改变的时候触发
		// num = position+1;
		String book_name = parent.getItemAtPosition(position).toString(); //
		Log.e("book_name", book_name);
		
		// 取得选中的选项
		// MyExpandableListViewDemo.this.info.setText( "选择的课程为"+num); //
		// 设置文本组件内容
		initWidgets();
		QLessons qLessons=new QLessons();
		/*可以试试List<lessons> lessons_list =null,看能不能行
		 *List<lessons> lessons_list =null;可以不影响使用，可以深究*/
		List<lessons> lessons_list = new ArrayList<lessons>();
		

		lessons_list=qLessons.queryLessons(lessons_Dao, book_name);
		String[] Lessons=new String[lessons_list.size()];
		//开始写i < lessons.length()直接报错，因为一直是空的啊
		for (int i = 0; i < lessons_list.size(); i++) {
			Lessons[i]=lessons_list.get(i).getTitle();
//			Log.e("Lessons[i]", lessons_list.get(i).getTitle());
		}
//		String[] classes_test = new String[]{"123123123","123","324"};
			CopyOfMyExpandableListViewDemo.this.adapterClasses = new ArrayAdapter<CharSequence>(
					CopyOfMyExpandableListViewDemo.this,
					android.R.layout.simple_spinner_item,
					Lessons); // 定义所有的列表项
//					MyExpandableListViewDemo.this.classes_Data[position]); // 定义所有的列表项
			CopyOfMyExpandableListViewDemo.this.adapterClasses
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			CopyOfMyExpandableListViewDemo.this.classes
					.setAdapter(CopyOfMyExpandableListViewDemo.this.adapterClasses);// 设置二级下拉列表的选项内容
			/*OnItemSelectedListener可以返回position，以此为参数进行传递,num_b作为全局变量可以得到保存*/
			num_b = position + 1;
			/*http://www.bubuko.com/infodetail-716156.html*/
            editorsettings.putInt("SelectedPosition-books", position);
            editorsettings.putString("SelectedName", book_name);
            editorsettings.commit();
            Log.e("SelectedPosition-books", String.valueOf(position));

		}

		public void onNothingSelected(AdapterView<?> arg0) { // 表示没有选项的时候触发
			// 一般此方法现在不关心
		}
	}

	private class OnItemSelectedListenerImpl_classes implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) { // 表示选项改变的时候触发
			classes_name = parent.getItemAtPosition(position).toString(); 
			Log.e("classes_name", classes_name);
			/*在设置spinner时，在瞬时打开books，classes其实又被设置了，就是classes刚刚被设置为记忆位置，但是在调用books时就会又被重新置为0，这样就会默认为0了*/
			if (i_SharedPreferences==0) {	
				i_SharedPreferences=1;
				int position_classes = settings.getInt("SelectedPosition-classes", 0 );
				/*java.lang.IndexOutOfBoundsException: Invalid index 10, size is 10*/					
				classes.setSelection(position_classes);
			
			}
			else {
				/*http://www.bubuko.com/infodetail-716156.html*/
	            editorsettings.putInt("SelectedPosition-classes", position);
	            editorsettings.putString("SelectedName", classes_name);
	            editorsettings.commit();
	            Log.e("SelectedPosition-classes", String.valueOf(position));
			}				
			if (radom_state) {
				randaomUI();
			} else {
				initWidgets();
			}
			/* 这样可以自动闭合了，但不是很好的方法 */
			collapseGroup();			
//			/* 代替手动刷新，也不是好方法2016.4.18 */
//			View v_elistView = elistview.getChildAt(0); // 假设你的代码中ListView对象的变量名是mList
//			/* NO USE：int y_elist= elistview.getScrollY() */
//			int top = (v_elistView == null) ? 0 : v_elistView.getTop();
//			int i_1stPos = elistview.getFirstVisiblePosition();
//			elistview.setSelectionFromTop(i_1stPos, top);
			/**/
//			if (num_b == 1) {
//				num_c = position + 1;
//				// String value = parent.getItemAtPosition(position).toString();
//				// // 取得选中的选项
//				// MyExpandableListViewDemo.this.info.setText(
//				// "选择的classes为"+num_c+"CheckBox为"+show); // 设置文本组件内容
//				// 这里因为要设置setText，这样就不能有以上的句子了，所以显示为报错
//				/*5.5*/
//				initWidgets();
//				/* 这样可以自动闭合了，但不是很好的方法 */
//				collapseGroup();
//				/* 代替手动刷新，也不是好方法2016.4.18 */
//				View v_elistView = elistview.getChildAt(0); // 假设你的代码中ListView对象的变量名是mList
//				/* NO USE：int y_elist= elistview.getScrollY() */
//				int top = (v_elistView == null) ? 0 : v_elistView.getTop();
//				int i_1stPos = elistview.getFirstVisiblePosition();
//				elistview.setSelectionFromTop(i_1stPos, top);
//			}
//			if (num_b == 2) {
//				// 因为onItemSelected中的int
//				// position全部是从0开始，所以编程的时候就要注意这一点，选择下册的时候，要注意在 position加上一个定值
//				num_c = position + 25;
//				// String value = parent.getItemAtPosition(position).toString();
//				// // 取得选中的选项
//				// MyExpandableListViewDemo.this.info.setText(
//				// "选择的classes为"+num_c+"CheckBox为"+len); // 设置文本组件内容
//				/*5.5*/
//				initWidgets();
//				/* 这样可以自动闭合了，但不是很好的方法 */
//				collapseGroup();
//				/* 代替手动刷新，也不是好方法2016.4.18 */
//				View v_elistView = elistview.getChildAt(0); // 假设你的代码中ListView对象的变量名是mList
//				/* NO USE：int y_elist= elistview.getScrollY() */
//				int top = (v_elistView == null) ? 0 : v_elistView.getTop();
//				int i_1stPos = elistview.getFirstVisiblePosition();
//				elistview.setSelectionFromTop(i_1stPos, top);
//			}

		}

		public void onNothingSelected(AdapterView<?> arg0) { // 表示没有选项的时候触发
			// 一般此方法现在不关心
		}
	}

	private class OnClickListenerImpl_search implements OnClickListener {
		@Override
		public void onClick(View view) {
			Intent it = new Intent(CopyOfMyExpandableListViewDemo.this,
					SearchActivity.class);
			CopyOfMyExpandableListViewDemo.this.startActivity(it);
		}

	}

	private class OnClickListenerImpl_about implements OnClickListener {
		@Override
		public void onClick(View view) {
			Intent it = new Intent(CopyOfMyExpandableListViewDemo.this,
					GuideActivity.class);
			CopyOfMyExpandableListViewDemo.this.startActivity(it);
		}

	}

	private class OnClickListenerImpl_word implements OnClickListener {
		@Override
		public void onClick(View view) {
//			Intent it = new Intent(MyExpandableListViewDemo.this,
//					RememberOfWord.class);
			Intent it = new Intent(CopyOfMyExpandableListViewDemo.this,
					VocabDraggableView.class);
			CopyOfMyExpandableListViewDemo.this.startActivity(it);
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
}