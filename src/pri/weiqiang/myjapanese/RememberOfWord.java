package pri.weiqiang.myjapanese;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
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

import android.widget.ImageButton;//安置paly播放音乐的可以使很多组件，不一定非要是imagebutton或者
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;



public class RememberOfWord extends Activity {
	private ExpandableListView elistview_search = null; // 定义树型组件
	private ExpandableListAdapter adapter = null; // 定义适配器对象
	private CheckBox show = null;// 下拉列表框内容
	
	// 文件的路径
	public final static String URL = "/data/data/pri.weiqiang.myjapanese/files";
	// 数据库文件
	public final static String DB_FILE_NAME = "location.db";
	// 归属地
	public final static String TABLE_NAME = "location_date";
	// 索引ID
	public final static int ID_INDEX = 0;
	public final static int NUMBER_INDEX = 1;
	public final static int LOCATION_INDEX = 2;
	public final static int CITY_INDEX = 3;
	public final static int CLASSES_INDEX = 4;
	public final static int MARK_INDEX = 5;//这是新增的收藏项用来记没有背下的单词
	// int num_c =1;//通过num来定义spinner选择的是哪一课程
	// int num_b =1;//通过num来定义spinner选择的是哪一本书
	int len;// 定义全部长度
	Button button = null;
	TextView textView = null;
	SQLiteDatabase db = null;
	View myView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.remember_word); // 默认布局管理器
		initWidgets_rememberofword();
	}

	public void initWidgets_rememberofword() {
		this.show = (CheckBox) super.findViewById(R.id.showchinese_remember); // 取得CheckBox显示组件
		this.elistview_search = (ExpandableListView) super
				.findViewById(R.id.elistview_remember); // 取得组件
		this.elistview_search.setGroupIndicator(null); // 设置 属性 GroupIndicator
														// 去掉默认向下的箭头
		this.adapter = new MyExpandableListAdapterTest_RememberOfWord(this); // 实例化适配器
		this.elistview_search.setAdapter(this.adapter); // 设置适配器
		super.registerForContextMenu(this.elistview_search); // 注册上下文菜单
		this.elistview_search
				.setOnChildClickListener(new OnChildClickListenerImpl()); // 设置子项单击事件
		this.elistview_search
				.setOnGroupClickListener(new OnGroupClickListenerImpl()); // 设置组项单击事件
		this.elistview_search
				.setOnGroupCollapseListener(new OnGroupCollapseListenerImpl()); // 关闭分组事件
		this.elistview_search
				.setOnGroupExpandListener(new OnGroupExpandListenerImpl()); // 展开分组事件
		// this.classes.setOnItemSelectedListener(new
		// OnItemSelectedListenerImpl());
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
		// 得到数据库文件
		File file = new File(URL, DB_FILE_NAME);
		db = SQLiteDatabase.openOrCreateDatabase(file, null);
		List<String> all_1 = new ArrayList<String>();
		List<String> all_2 = new ArrayList<String>();
		List<String> all_3 = new ArrayList<String>();
		List<String> all_4 = new ArrayList<String>() ;//新增的mark项
		// 2015.10.7

		String keyWord = "1" ; // 查询关键字

		
		String selectionArgs[] = new String[] { "%" + keyWord + "%"}; 							// 查询参数
        String sql = "SELECT * From location_date Where mark like ? ";
       	
		Cursor result = db.rawQuery(
				sql,
				selectionArgs);//"SELECT * From location_date Where location = ? "不用like就无法完成查询
		if (result != null && result.moveToFirst()) {
			for (result.moveToFirst(); !result.isAfterLast(); result
					.moveToNext()) {
				all_1.add(result.getString(NUMBER_INDEX));
				all_2.add(result.getString(LOCATION_INDEX));
				all_3.add(result.getString(CITY_INDEX));
				all_4.add(result.getString(MARK_INDEX));
			}
			result.close();//A SQLiteConnection object for database '/data/data/pri.weiqiang.myjapanese/files/location.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
		    db.close();
			String[] array1 = new String[all_1.size()];
			String[][] array2 = new String[all_1.size()][2];
			String[] array3=new String[all_1.size()];// mark
			for (int i = 0; i < all_1.size(); i++) {
				array1[i] = (String) all_1.get(i);
			}
			for (int i = 0; i < all_1.size(); i++) {

				array2[i][0] = (String) all_2.get(i);
				array2[i][1] = (String) all_3.get(i);
			}
			for(int i=0;i<all_1.size();i++){ 
				array3[i]=(String)all_4.get(i); }//新增的mark项目
			/**原来是MyExpandableListAdapterTest，MyExpandableListAdapterTest_RememberOfWord*/
			((MyExpandableListAdapterTest_RememberOfWord) this.adapter).SetArr(array1, array2,array3);
		}

		// initWidgets_search();这里调用自己没有意义

		;
	}

	// 设置子类全部展开2015.9.27
	private void onGroupExpand() {
		int len = this.adapter.getGroupCount();

		for (int i = 0; i < len; i++) {

			this.elistview_search.expandGroup(i);

		}
	}

	private void collapseGroup() {
		int len = this.adapter.getGroupCount();

		for (int i = 0; i < len; i++) {

			this.elistview_search.collapseGroup(i);

		}
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

	// 继承BaseExpandableListAdapter并覆写类中的抽象方法
	public class MyExpandableListAdapterTest_RememberOfWord extends BaseExpandableListAdapter {
		public String[] groups = { "Fail to search the database.", 

		}; 
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
		
		/*原来是MyExpandableListAdapterTest*/
		public MyExpandableListAdapterTest_RememberOfWord(Context context) { // 构造方法接收
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
		    logo1.setFocusable(false);
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
//		    		textView.setText("Mark");
//		    		String selectionArgs[] = new String[] { "%" + keyWord + "%","%" + keyWord + "%","%" + keyWord + "%"
//		   		 }; 							// 查询参数
//		          //String columns[] = new String[] {"number","city","location"} ;	// 查询列
//		          //String sql = "SELECT number,city,location From location_date Where number = ? OR city = ? OR location = ?";
//		          String sql = "SELECT * From location_date SET mark=? WHERE ";
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
		    		/*2016.4.18.10.32*/
		    		notifyDataSetInvalidated();
		    		/*有初始化就可以，上边的可以不要的*/
		    		initWidgets_rememberofword();
		    		/*2016.4.18.10.32*/
		    			// 执行SQL语句
		    		db.close() ;		    		
		    	}
		    }
		    logo1.setOnClickListener(new MarkClickListenerImpl());		    
		    //这里需要注意的是直接比较getMark(groupPosition).toString()=="0";不会成功，可见toString类看来并不是一个简单的string，这里应该是堆和栈的关系		    
		    this.notifyDataSetChanged();//这里是注意看看数据更改时候会产生反应		    
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

}