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

public class SearchWord extends Activity {
	private ExpandableListView elistview_search = null; // 定义树型组件
	private ExpandableListAdapter adapter = null; // 定义适配器对象
	private CheckBox show = null;// 下拉列表框内容
	private EditText editText = null;
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
		this.setContentView(R.layout.search_word); // 默认布局管理器
		editText = (EditText) this.findViewById(R.id.searchword);
		editText.addTextChangedListener(textWatcher); // 为editText设置监听内容变化
//		button = (Button) this.findViewById(R.id.button_search);
//		button.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				initWidgets_search();
//			}
//		});
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			initWidgets_search();
			// TODO Auto-generated method stub
			// Log.d("TAG","afterTextChanged--------------->");
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			initWidgets_search();
			// TODO Auto-generated method stub
			// Log.d("TAG","beforeTextChanged--------------->");
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			initWidgets_search();
			// Log.d("TAG","onTextChanged--------------->");
			// str = text.getText().toString();
			// try {
			// //if ((heighText.getText().toString())!=null)
			// Integer.parseInt(str);
			//
			// } catch (Exception e) {
			// // TODO: handle exception
			// showDialog();
			// }
			//
		}
	};

	private void initWidgets_search() {
		this.show = (CheckBox) super.findViewById(R.id.showchinese); // 取得CheckBox显示组件
		this.elistview_search = (ExpandableListView) super
				.findViewById(R.id.elistview_search); // 取得组件
		this.elistview_search.setGroupIndicator(null); // 设置 属性 GroupIndicator
														// 去掉默认向下的箭头
		this.adapter = new MyExpandableListAdapterTest(this); // 实例化适配器
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
		// 得到数据库文件
		File file = new File(URL, DB_FILE_NAME);
		db = SQLiteDatabase.openOrCreateDatabase(file, null);
		List<String> all_1 = new ArrayList<String>();
		List<String> all_2 = new ArrayList<String>();
		List<String> all_3 = new ArrayList<String>();
		List<String> all_4 = new ArrayList<String>() ;//新增的mark项
		// 2015.10.7
		String editStr = editText.getText().toString();
		String keyWord = editStr ; // 查询关键字
		
		//String selectionArgs[] = new String[] { "%" + keyWord + "%" }; // 查询参数
		//String sql = "SELECT location From location_date Where location = ? ";
		
		String selectionArgs[] = new String[] { "%" + keyWord + "%","%" + keyWord + "%","%" + keyWord + "%"
		 }; 							// 查询参数
       //String columns[] = new String[] {"number","city","location"} ;	// 查询列
       //String sql = "SELECT number,city,location From location_date Where number = ? OR city = ? OR location = ?";
       String sql = "SELECT * From location_date Where number like ? OR city like ? OR location like ?";
       //String sql = "SELECT 这里更换成* 就对了number,city,calsses From location_date Where number like ? OR city like ? OR calsses like ?";
		
		
		
		
		
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
			((MyExpandableListAdapterTest) this.adapter).SetArr(array1, array2,array3);
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
}