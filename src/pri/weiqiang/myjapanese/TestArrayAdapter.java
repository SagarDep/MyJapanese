package pri.weiqiang.myjapanese;

/**
 * @author  54wall 
 * @date 创建时间：2016-5-19 下午5:20:07
 * @version 1.0 
 */
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/* 用来修改默认格式的Spinner的字体和大小颜色等等
 * http://www.2cto.com/kf/201412/358450.html*/
public class TestArrayAdapter extends ArrayAdapter<String> {
	private Context mContext;
	private String[] mStringArray;

	public TestArrayAdapter(Context context, String[] stringArray) {
		super(context, android.R.layout.simple_spinner_item, stringArray);
		mContext = context;
		mStringArray = stringArray;
	}
	/*点开，弹出显示的spinner的值*/
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// 修改Spinner展开后的字体颜色
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(
					android.R.layout.simple_spinner_dropdown_item, parent,
					false);
		}

		// 此处text1是Spinner默认的用来显示文字的TextView
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(mStringArray[position]);
		tv.setTextSize(18.5f);//18f
		tv.setTextColor(Color.BLACK);
//		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		return convertView;

	}
	/*当前位置显示的spinner的值*/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 修改Spinner选择后结果的字体颜色
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(
					android.R.layout.simple_spinner_item, parent, false);
		}

		// 此处text1是Spinner默认的用来显示文字的TextView
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(mStringArray[position]);
		tv.setTextSize(18.5f);
		tv.setTextColor(Color.BLACK);
		/*水平居中*/
//		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		return convertView;
	}

}
