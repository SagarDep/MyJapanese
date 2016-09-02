package pri.weiqiang.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import java.nio.channels.FileChannel;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.tuesda.circlerefreshlayout.CircleRefreshLayout;

import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;


import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.ImageView;
/*加入greenDao*/
import pri.weiqiang.daojapanese.DaoMaster;
import pri.weiqiang.daojapanese.DaoSession;
import pri.weiqiang.daojapanese.words;
import pri.weiqiang.daojapanese.wordsDao;
import pri.weiqiang.daojapanese.lessonsDao;
import pri.weiqiang.daosql.QWords;
import pri.weiqiang.myjapanese.JapaneseTTS;
import pri.weiqiang.myjapanese.R;
import pri.weiqiang.random.RandomEntity;
import pri.weiqiang.search.SearchActivity;
public class SearchActivity extends AppCompatActivity {
	private boolean unconnected = false;
	static String fontPath_child = "fonts/A-OTF-NachinStd-Regular.otf";
	static Typeface tf_child ;
	static String fontPath_group = "fonts/A-OTF-NachinStd-Regular.otf";
	static Typeface tf_group ;
	private ExpandableListView elistview; // 定义树型组件
	private ExpandableListAdapter adapter; // 定义适配器对象
	private CheckBox show = null;		
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private wordsDao words_Dao;
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
   int i_SharedPreferences=0;/*控制第一次进入app*/
   int i_SharedPreferences_dialog=1;/*控制点击进入floating ball*/
   private ArrayAdapter<CharSequence> adapterClasses = null;// 在设置spinner时使用二级联动时需要
	private ArrayAdapter<CharSequence> adapterBooks = null;// 在设置spinner时使用二级联动时需要
	public final static String URL = "/data/data/pri.weiqiang.myjapanese/files";// 数据库文件
	public final static String DB_FILE_NAME = "vocab.db";
	int len;// 定义全部长度
	EditText editText = null;
	Button button = null;
	TextView textView = null;
	SQLiteDatabase db = null;
	View myView;
	private JapaneseTTS tts;
	private CircleRefreshLayout mRefreshLayout;
	private String editStr;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_search); // 默认布局管理器
		/*Toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar_search);
        toolbar.setBackgroundColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.back_shoes_blue);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
		editText = (EditText) this.findViewById(R.id.searchword_search);
		editText.addTextChangedListener(textWatcher); // 为editText设置监听内容变化		
		 /*因为使用新主题，无法使用xml中的全屏化，这里在Activity中实现*/ 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
        WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		/*一定不要放到getGroupView中会慢死，那样相当于每次都进行100次Typeface.createFromAsset操作*/
		tf_group = Typeface.createFromAsset(getAssets(), fontPath_group);
		tf_child = Typeface.createFromAsset(getAssets(), fontPath_child);
		this.show = (CheckBox) super.findViewById(R.id.showchinese_search); // 取得CheckBox显示组件
		this.elistview = (ExpandableListView) super.findViewById(R.id.elistview_search); // 取得组件		
		this.elistview.setGroupIndicator(null); // 设置 属性 GroupIndicator// 去掉默认向下的箭头
		this.adapter = new MyExpandableListAdapter(this); // 实例化适配器		
		this.elistview.setAdapter(this.adapter); // 设置适配器
		super.registerForContextMenu(this.elistview); // 注册上下文菜单
		this.elistview.setOnChildClickListener(new OnChildClickListenerImpl()); // 设置子项单击事件
		this.elistview.setOnGroupClickListener(new OnGroupClickListenerImpl()); // 设置组项单击事件
		this.elistview.setOnGroupCollapseListener(new OnGroupCollapseListenerImpl()); // 关闭分组事件
		this.elistview.setOnGroupExpandListener(new OnGroupExpandListenerImpl()); // 展开分组事件
		this.elistview.setChildDivider(null);
		this.elistview.setDivider(null);
		FloatingActionButton fab = (FloatingActionButton) super.findViewById(R.id.fab_search);
		fab.attachToListView(elistview, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {}
            @Override
            public void onScrollUp() {}
        }, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {               }
        });
        fab.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});

		
		/*使用SharedPreferences记住spinner上回保留的位置：http://www.bubuko.com/infodetail-716156.html*/
		settings = getSharedPreferences("preferences_settings",0); //与上面的保持一致，或者settings = getSharedPreferences("preferences_settings",Context.MODE_PRIVATE);
		editorsettings = settings.edit();
		int position_books = settings.getInt("SelectedPosition-books", 0 );
		Log.e("books.setSelection(", String.valueOf(position_books));
		
		// 以下是为CheckBox设置监听器，从而控制所有expandablelistview展开
		this.show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
		/*greenDao*/
		 copyDB();
		 File file = new File(URL, DB_FILE_NAME);
		 db = SQLiteDatabase.openOrCreateDatabase(file, null);
		 /*通过修改"notes-2-db"改变不同数据库的表名，卡伊看到数据库中不同的数据，而且全部都保存了下来*/
	        
	        /*http://stackoverflow.com/questions/25771689/greendao-store-data-in-sqlite-file-on-android*/
	        daoMaster = new DaoMaster(db);
	        DaoMaster.createAllTables(db, true);	        
	        daoSession = daoMaster.newSession();
	        words_Dao = daoSession.getWordsDao();
	        daoSession.getLesson_titleDao();
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
		mRefreshLayout = (CircleRefreshLayout) findViewById(R.id.refresh_layout_search);		
        mRefreshLayout.setOnRefreshListener(
                new CircleRefreshLayout.OnCircleRefreshListener() {
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

		tts = new JapaneseTTS(this, null);
		
	}	
	/*为mRefreshLayout配合的计时器，使用handler用于接收消息*/
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
            if (msg.what == 1) {           	
				mRefreshLayout.finishRefreshing();				
            	radom_state=true;
            	randaomUI();
            	radom_state=false;//避免翻页后依然处于乱序状态
                System.out.println("receive....");  
                /*new Thread(new ThreadShow()).start();会产生多个线程，所以考虑一下清空内存*/
                System.gc();
            }  
        };  
    };  


    // 线程类  
    class ThreadShow implements Runnable {  
  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
        	/*多了一个try-catch外多了while (true)，当然在后台一直执行了*/ 
                try {  
                    Thread.sleep(1000);  
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

	// 继承BaseExpandableListAdapter并覆写类中的抽象方法
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		public String[] groups = { "Fail to search the database.", };
		public String[][] children = { { "没有查询到改信息，或者数据库使用失败", "总之，数据库使用失败" },
		/*List<Children_List>children_list=newArrayList();在MyExpandableListAdapter不能使用ArrayList*/
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
					unconnected=tts.speak(getGroup(groupPosition).toString(),
							TextToSpeech.QUEUE_FLUSH, params);
					/*语音播放需要联网*/
					if (unconnected) {
						Toast.makeText(SearchActivity.this,
								"语音输出，需要联网。", Toast.LENGTH_SHORT).show();
					}
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
					Log.e("MarkClickListenerImpl", "1");
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
 					16 21 39 40 38 15 20 5 36 27 32 23 4 17 37 30 1 0 11 10 24 8 13 7 14 26 43 9 12 18 2 19 42 31 29 3 28 6 22 41 33 35 25 34 0 17 161 16 212 30 39*/
					
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
		/*仅在放生乱序排列时产生动画效果，用以显示处于乱序状态，一般布局文件放到初始化UI中肯定没有错误*/
		Animation animation = (Animation) AnimationUtils.loadAnimation(
		          this, R.anim.list_anim);
		        LayoutAnimationController lac = new LayoutAnimationController(animation);
		        lac.setDelay(0.0000005f);  //设置动画间隔时间
		        lac.setOrder(LayoutAnimationController.ORDER_RANDOM); //设置列表的显示顺序
		        elistview.setLayoutAnimation(lac);  //为ListView 添加动画
		
		
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
			((MyExpandableListAdapter) this.adapter).SetArr(array1, array2,array3);			
			/*总是移动view后，数据才进行更新，会不会是这里的原因
			 * 加完后，立刻解决了以前的问题*/
			((MyExpandableListAdapter) this.adapter).notifyDataSetChanged();
			((MyExpandableListAdapter) this.adapter).notifyDataSetInvalidated();
			
		}
}
	

	/*有序化UI*/
	public void  initWidgets() {
		editStr = editText.getText().toString();
		if(!radom_state)
		{
		QWords qWords = new QWords();
		Log.e("760", editStr);
		if (editStr==null) {
			editStr="中国人";
			Log.e("763", editStr);
		}
		if (editStr=="") {
			editStr="中国人";
			Log.e("767", editStr);
		}
		words_list=qWords.queryWords_All_Search(words_Dao, editStr);
		Log.e("755", editStr);
		/*不要多次调用words_list.size()，使用int words_size=words_list.size()，保证后续仅调用一次*/
		int words_size=words_list.size();
		String[] array1 = new String[words_size];
		String[][] array2 = new String[words_size][2];
		int[] array3 = new int[words_size];
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


	private void collapseGroup() {
		int len = this.adapter.getGroupCount();
		for (int i = 0; i < len; i++) {
			this.elistview.collapseGroup(i);
		}
	}
	/*AlertDialog*/
	private void showDialog(){


		LayoutInflater inflater = getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		/*
		 * this.mMusicPlay = (ImageView)dialog.findViewById(R.id.music_play);
		 * 放在这个位置也是可以的，主要还是dialog.findViewById中的dialog
		 */
		builder.setTitle("查询单词");		
		final CharSequence[] items = { "展开全部"};
		final boolean[] checkedItems = {false};
		builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener(){
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            	checkedItems[which]=isChecked;
                String result = "您选择了：";
                for (int i = 0; i < checkedItems.length; i++) {
                	/*选中时发生*/
                    if(checkedItems[0])
                    {                   	                    
						onGroupExpand();
                    }
                    else {
                    	collapseGroup();
                    	
					}
                    
                }

            }
        });
		builder.setItems(items, null); 		
		builder.setNeutralButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				onBackPressed();
			}
		});

		builder.show();

	}  

	private class OnChildClickListenerImpl implements OnChildClickListener {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			return false;
		}

	}

	private class OnGroupClickListenerImpl implements OnGroupClickListener {

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {

			return false;
		}

	}

	private class OnGroupCollapseListenerImpl implements
			OnGroupCollapseListener {

		@Override
		public void onGroupCollapse(int groupPosition) {

		}

	}

	private class OnGroupExpandListenerImpl implements OnGroupExpandListener {

		@Override
		public void onGroupExpand(int groupPosition) {

		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

	}


	
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			initWidgets();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
//			initWidgets();
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
//			initWidgets();
		}
	};
}