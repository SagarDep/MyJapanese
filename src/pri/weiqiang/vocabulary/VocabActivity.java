package pri.weiqiang.vocabulary;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.tuesda.circlerefreshlayout.CircleRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View;
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
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import pri.weiqiang.daojapanese.DaoMaster;
import pri.weiqiang.daojapanese.DaoSession;
import pri.weiqiang.daojapanese.words;
import pri.weiqiang.daojapanese.wordsDao;
import pri.weiqiang.daojapanese.lesson_titleDao;
import pri.weiqiang.daojapanese.lessonsDao;
import pri.weiqiang.daosql.QWords;
import pri.weiqiang.myjapanese.JapaneseTTS;
import pri.weiqiang.myjapanese.MyExpandableListViewDemo;
import pri.weiqiang.myjapanese.R;
public class VocabActivity extends AppCompatActivity {
	
	private boolean unconnected = false;
	private ExpandableListView elistview; // 定义树型组件
	private ExpandableListAdapter adapter; // 定义适配器对象
	private CheckBox show = null;
	private CheckBox checkBox_random = null;	
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private wordsDao words_Dao;
    private lesson_titleDao title_Dao;
    private lessonsDao	lessons_Dao;
    private Toolbar toolbar;
    /*classes_name必须有初始值，否则会无法初始化*/
    String classes_name=null;
    /*为了update可以方便的更改entity，所以使用全局变量*/
    List<words> words_list = new ArrayList<words>();
    List<words> words_List_backup = new ArrayList<words>();
    /*通过java.util.List.indexOf(Object obj)获取id（即正常排序顺序）*/
    List<Integer> arr = new ArrayList<Integer>();
    boolean radom_state=false;
    boolean radom_mark=false;
    /*使用SharedPreferences记住spinner上回保留的位置：http://www.bubuko.com/infodetail-716156.html*/
    SharedPreferences settings;
    Editor editorsettings;  
    int i_SharedPreferences=0;
	public final static String URL = "/data/data/pri.weiqiang.myjapanese/files";
	public final static String DB_FILE_NAME = "vocab.db";
	int len;// 定义全部长度
	EditText editText = null;
	Button button = null;
	TextView textView = null;
	SQLiteDatabase db = null;
	View myView;
	private JapaneseTTS tts;
	String lesson_vocab;
	private CircleRefreshLayout mRefreshLayout;	
	FloatingActionButton fab_vocab;
	/*老版本中并没有出现第一次单击藏夹按钮，UI更新至最上行，可以根据BeyondCompare找到解决问题的方法*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		lesson_vocab = bundle.getString("lesson_string");
		this.setContentView(R.layout.main_vocab); // 默认布局管理器
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
		WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		/*复制粘贴代码一定要注意时候完全改完了相关的名称
		 * Toolbar:R.id.id_toolbar_search所以toolbar一直空指针异常，报Unable to start activity ComponentInfo和NullPointerException*/
//        toolbar = (Toolbar) findViewById(R.id.id_toolbar_vocab);
//		/*发现只要有下边的就会报错*/
//        toolbar.setTitle(lesson_vocab);
//        toolbar.setTitleTextColor(Color.WHITE);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
		settings = getSharedPreferences("preferences_settings",0); //与上面的保持一致，或者settings = getSharedPreferences("preferences_settings",Context.MODE_PRIVATE);
		int position_books = settings.getInt("SelectedPosition-books", 0 );			
		editorsettings = settings.edit();
		Log.e("books.setSelection(", String.valueOf(position_books));
//		this.show = (CheckBox) super.findViewById(R.id.showchinese_vocab); // 取得CheckBox显示组件
//		this.checkBox_random = (CheckBox) super.findViewById(R.id.startRandom_vocab); // 取得CheckBox显示组件
		this.elistview = (ExpandableListView) super.findViewById(R.id.elistview_vocab); // 取得组件
		this.elistview.setGroupIndicator(null); // 设置 属性 GroupIndicator,去掉默认向下的箭头												
		this.adapter = new MyExpandableListAdapter(this); // 实例化适配器
		this.elistview.setAdapter(this.adapter); // 设置适配器
		super.registerForContextMenu(this.elistview); // 注册上下文菜单
		this.elistview.setOnChildClickListener(new OnChildClickListenerImpl()); // 设置子项单击事件
		this.elistview.setOnGroupClickListener(new OnGroupClickListenerImpl()); // 设置组项单击事件
		this.elistview.setOnGroupCollapseListener(new OnGroupCollapseListenerImpl()); // 关闭分组事件
		this.elistview.setOnGroupExpandListener(new OnGroupExpandListenerImpl()); // 展开分组事件
		/* 没有分割线还挺好看，看看直接把xml中的分割线去掉吧 */
		this.elistview.setChildDivider(null);
		this.elistview.setDivider(null);
		/*在toolbar中的checkbox*/
//		this.show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//		@Override
//		public void onCheckedChanged(CompoundButton buttonView,
//				boolean isChecked) {
//			// TODO Auto-generated method stub
//			if (isChecked) {
//				onGroupExpand();
//			} else {
//				collapseGroup();
//				}
//			}
//		});

		/*toolbar中的checkbox，为乱序输出进行监听*/
//		this.checkBox_random.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//		@Override
//		public void onCheckedChanged(CompoundButton buttonView,
//				boolean isChecked) {
//			// TODO Auto-generated method stub
//			if (isChecked) {
//				radom_state=true;
//				randaomUI();
//			} else {
//				radom_state=false;
//				initWidgets();
//			}
//		}
//		});
		
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
		initWidgets();
		tts = new JapaneseTTS(this, null);
		/*CircleRefreshLayout*/
		mRefreshLayout = (CircleRefreshLayout) findViewById(R.id.refresh_layout_vocab);		
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
		/* FloatingActionButton */
		fab_vocab = (FloatingActionButton) super.findViewById(R.id.fab_vocab);

		fab_vocab.attachToListView(elistview, new ScrollDirectionListener() {
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
		fab_vocab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});
		
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

    
    
    private void showDialog() {

		LayoutInflater inflater = getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		/*
		 * this.mMusicPlay = (ImageView)dialog.findViewById(R.id.music_play);
		 * 放在这个位置也是可以的，主要还是dialog.findViewById中的dialog
		 */
		builder.setTitle(lesson_vocab);		
		/*http://www.cnblogs.com/linjiqin/archive/2011/03/10/1980184.html*/
		final CharSequence[] items = { "默认展开乱序", "默认展开全部"};
		final boolean[] checkedItems = {false,false};
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
                    	
						onGroupExpand();
                    }
                    else {
                    	collapseGroup();
                    	
					}
               }
//                editText.setText(result.substring(0, result.length()-1));
            }
        });
		builder.setItems(items, null); 
		
		builder.setNeutralButton("返回上页", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		builder.show();
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
		/*
		 * http://stackoverflow.com/questions/7817916/android-notifydatasetchanged -for-expandablelistview-not-working
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



			// textView.setText(getGroup(groupPosition).toString());
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
						Toast.makeText(VocabActivity.this,
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

	public void randaomUI() {
		if (radom_state) 
		{
			QWords qWords = new QWords();	
			words_list=qWords.queryWords_Fav_Lesson(words_Dao, lesson_vocab);
			/*不要多次调用words_list.size()，使用int words_size=words_list.size()，保证后续仅调用一次*/
			int words_size=words_list.size();
			String[] array1 = new String[words_size];
			String[][] array2 = new String[words_size][2];
			int[] array3 = new int[words_size];			
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
		if(!radom_state)
		{
			QWords qWords = new QWords();			
		
		words_list=qWords.queryWords_Fav_Lesson(words_Dao, lesson_vocab);
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
	private class OnChildClickListenerImpl implements OnChildClickListener {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			// Toast.makeText(
			// VocabActivity.this,
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
			// Toast.makeText(VocabActivity.this,
			// "分组被选中，groupPosition = " + groupPosition,
			// Toast.LENGTH_SHORT).show(); // 显示提示框
			return false;
		}

	}

	private class OnGroupCollapseListenerImpl implements
			OnGroupCollapseListener {

		@Override
		public void onGroupCollapse(int groupPosition) {
			// Toast.makeText(VocabActivity.this,
			// "关闭分组，groupPosition = " + groupPosition, Toast.LENGTH_SHORT)
			// .show(); // 显示提示框
		}

	}

	private class OnGroupExpandListenerImpl implements OnGroupExpandListener {

		@Override
		public void onGroupExpand(int groupPosition) {
			// Toast.makeText(VocabActivity.this,
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
		// Toast.makeText(VocabActivity.this,
		// "type = " + type + "，group = " + group + "，child = " + child,
		// Toast.LENGTH_SHORT).show(); // 显示提示框
	}
}