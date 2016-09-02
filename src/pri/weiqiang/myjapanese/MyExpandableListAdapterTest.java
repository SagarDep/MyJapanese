package pri.weiqiang.myjapanese;


import java.io.File;
import android.content.Context;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.DisplayMetrics;
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

// 继承BaseExpandableListAdapter并覆写类中的抽象方法
public class MyExpandableListAdapterTest extends BaseExpandableListAdapter {
	public String[] groups = { "Fail to search the database.", 

	}; // 组名称
//	public String[]children = { "中国人", "日本人", "韓国人", "アメリカ人", "フランス人", "学生",
//			"先生", "留学生", "教授", "社員", "会社員", "店員", "研修生", "企業", "大学", "父", "課長",
//			"社長", "出迎え", "あの人", "をたし〔代〕", "あなた〔代〕", "どうも〔副〕", "はい〔叹〕",
//			"いいえ〔叹〕", "あっ〔叹〕", "李", "王", "張", "森", "林", "小野", "吉田", "田中", "中村",
//			"太郎", "金", "デュポン〔专〕", "スミス〔专〕", "ジョンソン〔专〕", "中国", "東京大学", "北京大学",
//			"ＪＣ企画", "北京旅行社",
//
//	}; 

	public String[][] children = { {  "没有查询到改信息，或者数据库使用失败", "总之，数据库使用失败" },
			

	}; // 定义组项
	
	public String[] mark_num = { "1", 

	};
	private Context context = null; // 保存上下文对象
	
	private MediaPlayer myMediaPlayer = null; // 媒体播放
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
		public final static int MARK_INDEX = 5;//这是新增的收藏项用来记没有背下的单词
		SQLiteDatabase db = null;
	     /* 可以进入BaseExpandableListAdapter，将其所有方法全部复写，应该就不会有之前的问题了*/
		@Override
	    public void registerDataSetObserver(DataSetObserver observer) {
	        super.registerDataSetObserver(observer);    
	    }
		/*2016.4.18*/
	public void SetArr(String[] array,String[][] array2,String[] array3)
	{
		groups=array;children=array2;mark_num=array3;
		
	}
	
	
	public MyExpandableListAdapterTest(Context context) { // 构造方法接收
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
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // 指定布局参数
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
		textView.setTextColor(Color.RED);
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

	public Object getMark(int groupPosition) { // 取得Mark对象
		return this.mark_num[groupPosition];
	}
	
	
	
	
	
	@Override
	
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) { // 取得组显示组件
		LayoutInflater inflater = LayoutInflater.from(this.context);
		View view = inflater.inflate(R.layout.group_view, null);
		final TextView textView = (TextView) view.findViewById(R.id.textView_group);
		textView.setTextSize(31); // 设置文字大小
        textView.setTextColor(Color.BLACK);
		textView.setGravity(Gravity.LEFT); // 左对齐
	    textView.setPadding(10, 0, 0, 0); // 间距
	    textView.setText(getGroup(groupPosition).toString()); 
	    
	   //textView.setText(getGroup(groupPosition).toString());   
	    final ImageView logo1 = (ImageView) view.findViewById(R.id.imageView1);
	    //ImageView logo2 = (ImageView) view.findViewById(R.id.imageView2);
	    logo1.setFocusable(false);
	    //logo2.setFocusable(false);
	    //logo1.setImageResource(R.drawable.groupview_image);
	    logo1.setImageResource(R.drawable.btn_star_press);
	    //logo1.setBackgroundResource(R.drawable.on);是更改的背景，而logo1.setImageResource(R.drawable.on)是更换图片
	    String mark_adpter= getMark(groupPosition).toString();
	    
	    final int mark_int=Integer.parseInt(mark_adpter);
	    if(mark_int == 0)
	    {
	    	logo1.setImageResource(R.drawable.btn_play_press);
	    	
	    } 
	    
	    
	    class MarkClickListenerImpl implements OnClickListener {
	    	public void onClick(View v){
	    		File file = new File(URL, DB_FILE_NAME);
	    		db = SQLiteDatabase.openOrCreateDatabase(file, null);
//	    		textView.setText("Mark");
//	    		String selectionArgs[] = new String[] { "%" + keyWord + "%","%" + keyWord + "%","%" + keyWord + "%"
//	   		 }; 							// 查询参数
//	          //String columns[] = new String[] {"number","city","location"} ;	// 查询列
//	          //String sql = "SELECT number,city,location From location_date Where number = ? OR city = ? OR location = ?";
//	          String sql = "SELECT * From location_date SET mark=? WHERE ";
	    		String sql = "UPDATE " + TABLE_NAME
	    				+ " SET mark=? WHERE number=? AND city=?"; 
	    		//原来输入的是String sql = "UPDATE " + TABLE_NAME
				//+ " SET mark=? WHERE number=? AND city=?"; 中的AND变成","就会报错：near ",": syntax error (code 1): , while compiling: UPDATE location_date SET mark=? WHERE number=?,c

	    		// SQL语句
	    		if(mark_int == 0){
	    		Object args[] = new Object[] {"1", getGroup(groupPosition).toString(),getChild(groupPosition, 0).toString() }; 
	    		db.execSQL(sql, args);
	    		logo1.setImageResource(R.drawable.btn_star_press);
	    		}//这里是为了添加和取消收藏，但是还是不明白为什么许多位置要加final
	    		else{
	    		Object args[] = new Object[] {"0", getGroup(groupPosition).toString(),getChild(groupPosition, 0).toString() };
	    		db.execSQL(sql, args);
	    		logo1.setImageResource(R.drawable.btn_play_press);
	    		}
	    // 设置参数
	    		/*2016.4.18.10.32*/
	    		notifyDataSetInvalidated();
	    		/*2016.4.18.10.32*/
	    			// 执行SQL语句
	    		db.close() ;
	    		
	    	}
	    }
	    logo1.setOnClickListener(new MarkClickListenerImpl());
	    
	    //这里需要注意的是直接比较getMark(groupPosition).toString()=="0";不会成功，可见toString类看来并不是一个简单的string，这里应该是堆和栈的关系
	    
	    this.notifyDataSetChanged();//这里是注意看看数据更改时候会产生反应
	    
        return view;
//		LinearLayout ll = new LinearLayout(this.context);
//		// 设置子控件的显示方式为水平
//		ll.setOrientation(0);
//
//		AbsListView.LayoutParams param = new AbsListView.LayoutParams(260, ViewGroup.LayoutParams.FILL_PARENT); // 指定布局参数
//		TextView textView = new TextView(this.context); // 创建TextView
//		textView.setLayoutParams(param); // 设置布局参数
//		textView.setTextSize(30); // 设置文字大小
//		textView.setTextColor(Color.BLACK);
//		textView.setGravity(Gravity.LEFT); // 左对齐
//		textView.setPadding(10, 0, 0, 0); // 间距
//		textView.setText(getGroup(groupPosition).toString());
//
//		ll.addView(textView);
//		RatingBar logo4 =new RatingBar(this.context);
//		
//		logo4.setPadding(10, 0, 0, 0);
//		logo4.setId(110);//?
//		AbsListView.LayoutParams lparParams4 = new AbsListView.LayoutParams(
//				165, ViewGroup.LayoutParams.FILL_PARENT);
//		logo4.setLayoutParams(lparParams4);
////		logo4.findViewById(R.drawable.check);
//		logo4.setNumStars(1);
////		logo4.setStepSize(1);
//		//logo4.setBackgroundResource(R.drawable.check);
//		//如果不加logo4.setFocusable(false);则将无法展开ExpandableListViiew,因为RatingBar抢夺了焦点
//		logo4.setFocusable(false);
//
//		ll.addView(logo4);
//
//		return ll;
//
        

	}
	
	
	
	
	
	
	
//	public View getGroupView(int groupPosition, boolean isExpanded,
//			View convertView, ViewGroup parent) { // 取得组显示组件
//
//		LinearLayout ll = new LinearLayout(this.context);
//		// 设置子控件的显示方式为水平
//		ll.setOrientation(0);
//
//
////得到当前的屏幕的宽度就可以为下边的字体和AbsListView的图片设置宽度了，才能自适应各种屏幕
////		DisplayMetrics metric = new DisplayMetrics();  
////		int dis_width = metric.widthPixels;     // 屏幕宽度（像素）  
////		int dis_height = metric.heightPixels;   // 屏幕高度（像素）
////		int dis_height_0=dis_width*1/24;
////		int dis_w_0=dis_width*11/16;
////		int dis_w_1 =dis_width*1/4;
////		int dis_w_2 =dis_width*1/16;//直接使用调用屏幕分辩率，直接在所有的groupview全部是空白的2015.10.11
//		AbsListView.LayoutParams param = new AbsListView.LayoutParams(260, ViewGroup.LayoutParams.FILL_PARENT); // 指定布局参数
//		TextView textView = new TextView(this.context); // 创建TextView
//		textView.setLayoutParams(param); // 设置布局参数
//		textView.setTextSize(30); // 设置文字大小
//		textView.setTextColor(Color.BLACK);
//		textView.setGravity(Gravity.LEFT); // 左对齐
//		textView.setPadding(10, 0, 0, 0); // 间距
//		textView.setText(getGroup(groupPosition).toString());
//
//		ll.addView(textView);
//
//		// 下边的checkbox是添加checkbox，但是实验不是很成功，改变背景之后，那个绿色的小图标箭头还是有
////		CheckBox logo4 =new CheckBox(this.context);
////		
////		logo4.setPadding(100, 0, 0, 0);
////		logo4.setId(110);//?
////		AbsListView.LayoutParams lparParams4 = new AbsListView.LayoutParams(
////				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////		logo4.setLayoutParams(lparParams4);
////		logo4.findViewById(R.drawable.check);
//		//logo4.setBackgroundResource(R.drawable.check);
//		RatingBar logo4 =new RatingBar(this.context);
//		
//		logo4.setPadding(10, 0, 0, 0);
//		logo4.setId(110);//?
//		AbsListView.LayoutParams lparParams4 = new AbsListView.LayoutParams(
//				165, ViewGroup.LayoutParams.FILL_PARENT);
//		logo4.setLayoutParams(lparParams4);
////		logo4.findViewById(R.drawable.check);
//		logo4.setNumStars(1);
////		logo4.setStepSize(1);
//		//logo4.setBackgroundResource(R.drawable.check);
//		//如果不加logo4.setFocusable(false);则将无法展开ExpandableListViiew,因为RatingBar抢夺了焦点
//		logo4.setFocusable(false);
//
//		ll.addView(logo4);
//		
//		
//		
//		
////		ImageView logo2 = new ImageView(this.context);
////		logo2.setPadding(100, 0, 0, 0);
////		logo2.setId(110);//?
////		// 设置logo的大小(50（padding）+46=96)
////		AbsListView.LayoutParams lparParams2 = new AbsListView.LayoutParams(
////				176, ViewGroup.LayoutParams.FILL_PARENT);
////		logo2.setLayoutParams(lparParams2);
////		logo2.setImageResource(R.drawable.btn_play_press);
////		ll.addView(logo2);
////		ImageView logo3 = new ImageView(this.context);
////		
////		// 定义第三个ImageView用于显示列表图片
////		logo3.setPadding(0, 0, 0, 0);
////		// 设置logo的大小(50（padding）+46=96)
////		AbsListView.LayoutParams lparParams3 = new AbsListView.LayoutParams(46,
////				ViewGroup.LayoutParams.FILL_PARENT);
////		logo3.setLayoutParams(lparParams3);
////		logo3.setImageResource(R.drawable.btn_star_press);
//		//new
//
//		class PlayOnClickListenerImpl implements OnClickListener {
//			Object myMediaPlayer;
//			
//			@Override
//			public void onClick(View view) {
//				this.myMediaPlayer = MediaPlayer.create(
//						context, R.raw.mldn_ad); // 找到指定的资源
//				((MediaPlayer) this.myMediaPlayer)
//						.setOnCompletionListener(new OnCompletionListener() {
//							private boolean playFlag;
//
//							@Override
//							public void onCompletion(MediaPlayer media) {
//								this.playFlag = false; // 播放完毕
//								media.release(); // 释放所有状态
//							}
//						}); // 播放完毕监听
////				MyMediaPlayeDemo.this.seekbar
////						.setMax(MyMediaPlayeDemo.this.myMediaPlayer.getDuration()); // 设置拖动条长度为媒体长度
////				UpdateSeekBar update = new UpdateSeekBar(); // 启动子线成更新拖动条
////				update.execute(1000); // 休眠1秒
////				MyMediaPlayeDemo.this.seekbar
////						.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImpl()); // 拖动条改变音乐播放位置
//				if (this.myMediaPlayer != null) {
//					((MediaPlayer) this.myMediaPlayer).stop(); // 停止播放
//				}
//				try {
//					((MediaPlayer) this.myMediaPlayer).prepare(); // 进入到预备状态
//					((MediaPlayer) this.myMediaPlayer).start(); // 播放文件
////					this.info.setText("正在播放音频文件..."); // 设置文字
//				} catch (Exception e) {
//					//MyMediaPlayeDemo.this.info.setText("文件播放出现异常，" + e);// 设置文字
//				}
//			}
//		}
//		
//		class StopOnClickListenerImpl implements OnClickListener {
//			
//
//			private Object myMediaPlayer;//因为这里不是与PlayOnClickListenerImpl中同一个myMediaPlayer，所以按起来没有反应
//
//			@Override
//			public void onClick(View view) {
//				if (this.myMediaPlayer != null) {
//					((MediaPlayer) this.myMediaPlayer).stop(); // 停止播放
////					MyMediaPlayeDemo.this.info.setText("停止播放音频文件...");
//				}
//			}
//		}
//		
//		
//		
//		
//		
////		logo3.setOnClickListener(new StopOnClickListenerImpl()); // 按钮单击事件
////		logo2.setOnClickListener(new PlayOnClickListenerImpl()); // 按钮单击事件
//		
//		//new
////		ll.addView(logo3);
//		
//		
//		return ll;
//
//
//	}
	

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
