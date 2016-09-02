package pri.weiqiang.url;

import pri.weiqiang.myjapanese.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

@SuppressLint("ViewHolder") 
public class BookAdapter extends BaseAdapter {
	private Context context = null;					// Context对象
	private String[] picIds = null;					// 保存所有图片资源

	public BookAdapter(Context context, String[] picIds) {
		this.context = context;						// 接收Context
		this.picIds = picIds;						// 保存图片资源
	}

	@Override
	public int getCount() {							// 取得个数
		return this.picIds.length;
	}

	@Override
	public Object getItem(int position) {			// 取得每一项的信息
		return this.picIds[position];
	}

//	@Override
//	public long getItemId(int position) {			// 取得指定项的ID
//		return this.picIds[position];
//	}

	@Override
	public long getItemId(int i) {
		// TODO Auto-generated method stub
		return 0;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		LayoutInflater inflater = LayoutInflater.from(this.context);
		View view = inflater.inflate(R.layout.grid_layout, null);
		ImageView img = (ImageView) view.findViewById(R.id.img_audio_book);
		/*old*/
//		ImageView img = new ImageView(this.context);	// 定义图片视图
//		img.setImageResource(this.picIds[position]); 	// 给ImageView设置资源
//		img.setScaleType(ImageView.ScaleType.CENTER); 	// 居中显示
		/*super.getResources()改为context.getResources()*/
		Bitmap alterBitmap_ic_wordbook = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_wordbook).copy(Bitmap.Config.ARGB_8888, true);;
		Canvas canvas = new Canvas(alterBitmap_ic_wordbook);
		canvas.drawBitmap(alterBitmap_ic_wordbook, 0, 0, null);// 这里是需要填入我们的图片bitmap的，感觉这里就像是图层
		Paint paint = new Paint();	 
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(36);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
//		canvas.drawText(s, 75,75, paint);	
		/*下面代码解决了文字自动换行的问题，http://www.cnblogs.com/hmyprograming/archive/2012/03/23/2414173.html*/
		TextPaint textPaint = new TextPaint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(36);
		/** * aboutTheGame ：要 绘制 的 字符串 ,textPaint(TextPaint 类型)设置了字符串格式及属性 的画笔,240为设置 画多宽后 换行，后面的参数是对齐方式... */
		/*s先替换为"我的日语单词本"*/
		StaticLayout layout = new StaticLayout(picIds[position],textPaint,150,Alignment.ALIGN_NORMAL,1.0F,0.0F,true); 		
		canvas.translate(55,75); //从 (x,y)的位置开始绘制 
		layout.draw(canvas);
		img.setImageBitmap(alterBitmap_ic_wordbook);
		img.setScaleType(ImageView.ScaleType.CENTER); 	// 居中显示
		return view;

	}

}
