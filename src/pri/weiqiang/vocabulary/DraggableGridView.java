package pri.weiqiang.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;


import java.util.ArrayList;
import java.util.Collections;
/*更换app之后还是要import pri.weiqiang.myjapanese.R;*/
import pri.weiqiang.myjapanese.R;

/**
 * Created by andyken on 16/3/22.
 */
public class DraggableGridView extends ViewGroup implements View.OnTouchListener, View.OnClickListener ,View.OnLongClickListener{

	private AttributeSet attributeSet;
	private int draggedIndex = -1, lastX = -1, lastY = -1, lastTargetIndex = -1;
	private int xPadding, yPadding;//the x-axis and y-axis padding of the item
	private int itemWidth, itemHeight, colCount;
	/*直接传入一组ArrayList进而与reorderChidren*/
	private ArrayList<String> remPostions = new ArrayList<String>();
	private ArrayList<Integer> newPositions = new ArrayList<Integer>();
	private static int ANIM_DURATION = 150;

	private AdapterView.OnItemClickListener onItemClickListener;
	private OnRearrangeListener onRearrangeListener;
	SharedPreferences settings_remPos;
	Editor editorsettings_remPos;
	public DraggableGridView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.attributeSet = attributeSet;
		/*不是Activity子类，无法直接settings = getSharedPreferences("preferences_settings",0); */
		settings_remPos = context.getSharedPreferences("remPostions",0); 
		editorsettings_remPos = settings_remPos.edit();
		init();
	}

	private void init() {
		initAttributes();
		initData();
		initEventListener();
	}
	/*改变每排数据也要更改这里*/
	private void initAttributes() {
		
		
		
		TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.DraggableGridView);
		try {
			/*修改xml中的宽和高即可*/
			itemWidth = (int) typedArray.getDimension(R.styleable.DraggableGridView_itemWidth, 0);
			itemHeight = (int) typedArray.getDimension(R.styleable.DraggableGridView_itemHeight, 0);
			colCount = typedArray.getInteger(R.styleable.DraggableGridView_colCount, 0);
			yPadding = (int) typedArray.getDimension(R.styleable.DraggableGridView_yPadding, 20);
			
			
			
			
			
			/*把0换成1000是没有效果的,这里是调试的思路*/
//			itemWidth = (int) typedArray.getDimension(R.styleable.DraggableGridView_itemWidth, 1000);
//			itemWidth = (int) typedArray.getDimension(R.styleable.DraggableGridView_itemWidth, 0);//0
//			/*通过调整itemWidth的倍数再减去一些值，是可以决定上层的DraggableGridView之间的间距的*/
//			itemWidth=2*itemWidth-40;
//			Log.e("2*itemWidth", String.valueOf(itemWidth));
//			itemHeight = (int) typedArray.getDimension(R.styleable.DraggableGridView_itemHeight, 0);//0			
//			itemHeight=2*itemHeight-40;
//			Log.e("2*itemHeight", String.valueOf(itemHeight));
//			/*colCount即column count每排显示个数*/
//			colCount = typedArray.getInteger(R.styleable.DraggableGridView_colCount, 0);
//			colCount=colCount-2;
//			Log.e("colCount-2", String.valueOf(colCount));
//			/*是y方向即纵向的间隔*/
//			yPadding = (int) typedArray.getDimension(R.styleable.DraggableGridView_yPadding, 0);			
//			yPadding = yPadding-80;
//			Log.e("yPadding-20", String.valueOf(yPadding));
		} finally {
			typedArray.recycle();
		}
	}

	private void initData() {
		setChildrenDrawingOrderEnabled(true);
	}

	private void initEventListener() {
		super.setOnClickListener(this);
		setOnTouchListener(this);
		setOnLongClickListener(this);
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		newPositions.add(-1);
	}

	@Override
	public void removeViewAt(int index) {
		super.removeViewAt(index);
		newPositions.remove(index);
	}

	@Override
	public void onLayout(boolean changed, int l, int t, int r, int b) {
		xPadding = ((r - l) - (itemWidth * colCount)) / (colCount + 1);
		for (int i = 0; i < getChildCount(); i++) {
			if (i != draggedIndex) {
				Point xy = getCoorFromIndex(i);
				getChildAt(i).layout(xy.x, xy.y, xy.x + itemWidth, xy.y + itemHeight);
			}
		}
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		//将正在移动的item放在最后一个绘制 防止出现正在移动的item被遮住的问题
		if (draggedIndex == -1){
			return i;
		} else if (i == childCount - 1){
			return draggedIndex;
		} else if (i >= draggedIndex){
			return i + 1;
		}
		return i;
	}

	/**
	 * get index from coordinate
	 * @param x
	 * @param y
	 * @return
	 */
	private int getIndexFromCoor(int x, int y) {
		int col = getColFromCoor(x);
		int row = getRowFromCoor(y);
		if (col == -1 || row == -1){
			return -1;
		}
		int index = row * colCount + col;
		if (index >= getChildCount()){
			return -1;
		}
		return index;
	}

	private int getColFromCoor(int coor) {
		coor -= xPadding;
		for (int i = 0; coor > 0; i++) {
			if (coor < itemWidth)
				return i;
			coor -= (itemWidth + xPadding);
		}
		return -1;
	}

	private int getRowFromCoor(int coor) {
		coor -= yPadding;
		for (int i = 0; coor > 0; i++) {
			if (coor < itemHeight)
				return i;
			coor -= (itemHeight + yPadding);
		}
		return -1;
	}

	/**
	 * 判断当前移动到的位置 当当前位置在另一个item区域时交换
	 * @param x
	 * @param y
	 * @return
	 */
	private int getTargetFromCoor(int x, int y) {
		if (getRowFromCoor(y) == -1) {
			//touch is between rows
			return -1;
		}
		int target = getIndexFromCoor(x, y);
		//将item移动到最后的item之后
		if (target == getChildCount()) {
			target = getChildCount() - 1;
		}
		return target;
	}

	private Point getCoorFromIndex(int index) {
		int col = index % colCount;
		int row = index / colCount;
		return new Point(xPadding + (itemWidth + xPadding) * col,
				yPadding + (itemHeight + yPadding) * row);
	}
	/*增加调转命令到另一个Activity*/
	public void onClick(View view) {
		/*因为children remPostions 两个arraylist顺序号完全同步的，所以，可以通过children的指数，获得remPostions中第一个，从而跳转到合适的位置*/
//		if (onItemClickListener != null && getIndex() != -1) {
//			onItemClickListener.onItemClick(null, getChildAt(getIndex()), getIndex(), getIndex() / colCount);
//		}
		
		if (onItemClickListener != null && getIndex() != -1) {
		//onItemClickListener.onItemClick(null, getChildAt(getIndex()), getIndex(), getIndex() / colCount);
			Log.e("getIndex", String.valueOf(remPostions.get(getIndex())));
			/*如果是新建一个context，会报空指针，现在报：*/
			/*android.content.ActivityNotFoundException: Unable to find explicit activity class {
			 * pri.weiqiang.myjapanese/pri.weiqiang.vocabulary.VocabActivity}; have you declared 
			 * this activity in your AndroidManifest.xml?*/
			/*解决在非Activity中使用startActivity:http://blog.csdn.net/job_dinge/article/details/7928005*/
			/*不再Activity中如何使用Activity跳转:http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2013/0907/1531.html*/
			Intent it = new Intent(view.getContext(),
					VocabActivity.class);			
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			Bundle bundle = new Bundle();
			/*使用bundle传递Lesson_ID*/
			bundle.putCharSequence("lesson_string", remPostions.get(getIndex()));
			it.putExtras(bundle);
			view.getContext().startActivity(it);	 
	}
	}

	public boolean onLongClick(View view) {
		int index = getIndex();
		if (index != -1) {
			//如果长按的位置在
			draggedIndex = index;
			animateActionDown();
			return true;
		}
		return false;
	}

	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				lastX = (int) event.getX();
				lastY = (int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				int deltaX = (int) event.getX() - lastX;
				int deltaY = (int) event.getY() - lastY;
				if (draggedIndex != -1) {
					int x = (int) event.getX(), y = (int) event.getY();
					View draggedView = getChildAt(draggedIndex);
					int itemLeft = draggedView.getLeft(), itemTop = draggedView.getTop();
					draggedView.layout(itemLeft + deltaX, itemTop + deltaY, itemLeft + deltaX + itemWidth, itemTop + deltaY + itemHeight);
					//得到当前点击位置所在的item的index
					int targetIndex = getTargetFromCoor(x, y);
					if (lastTargetIndex != targetIndex && targetIndex != -1) {
						animateGap(targetIndex);
						lastTargetIndex = targetIndex;
					}
				}
				lastX = (int) event.getX();
				lastY = (int) event.getY();
				break;
			case MotionEvent.ACTION_UP:
				if (draggedIndex != -1) {
					//如果存在item交换 则重新排列子view
					if (lastTargetIndex != -1) {
						reorderChildren();
					}
					animateActionUp();
					lastTargetIndex = -1;
					draggedIndex = -1;
				}
				break;
		}
		//如果存在拖动item 则消费掉该事件
		if (draggedIndex != -1) {
			return true;
		}
		return false;
	}

	/**
	 * actionDown动画
	 */
	private void animateActionDown() {
		View v = getChildAt(draggedIndex);
		AnimationSet animSet = new AnimationSet(true);
		AlphaAnimation alpha = new AlphaAnimation(1, .5f);
		alpha.setDuration(ANIM_DURATION);
		animSet.addAnimation(alpha);
		animSet.setFillEnabled(true);
		animSet.setFillAfter(true);
		v.clearAnimation();
		v.startAnimation(animSet);
	}

	/**
	 * actionUp动画
	 */
	private void animateActionUp() {
		View v = getChildAt(draggedIndex);
		AlphaAnimation alpha = new AlphaAnimation(.5f, 1);
		alpha.setDuration(ANIM_DURATION);
		AnimationSet animSet = new AnimationSet(true);
		animSet.addAnimation(alpha);
		animSet.setFillEnabled(true);
		animSet.setFillAfter(true);
		v.clearAnimation();
		v.startAnimation(animSet);
	}

	/**
	 * 拖动某个item时其他item的移动动画
	 * animate the other item when the dragged item moving
	 * @param targetIndex
	 */
	private void animateGap(int targetIndex) {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (i == draggedIndex)
				continue;
			int newPos = i;
			if (draggedIndex < targetIndex && i >= draggedIndex + 1 && i <= targetIndex)
				newPos--;
			else if (targetIndex < draggedIndex && i >= targetIndex && i < draggedIndex)
				newPos++;

			//animate
			int oldPos = i;
			if (newPositions.get(i) != -1)
				oldPos = newPositions.get(i);
			if (oldPos == newPos)
				continue;

			Point oldXY = getCoorFromIndex(oldPos);
			Point newXY = getCoorFromIndex(newPos);
			Point oldOffset = new Point(oldXY.x - v.getLeft(), oldXY.y - v.getTop());
			Point newOffset = new Point(newXY.x - v.getLeft(), newXY.y - v.getTop());

			TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, oldOffset.x,
					Animation.ABSOLUTE, newOffset.x,
					Animation.ABSOLUTE, oldOffset.y,
					Animation.ABSOLUTE, newOffset.y);
			translate.setDuration(ANIM_DURATION);
			translate.setFillEnabled(true);
			translate.setFillAfter(true);
			v.clearAnimation();
			v.startAnimation(translate);

			newPositions.set(i, newPos);
		}
	}
	/*SL is good!*/
	/*直接传入一组ArrayList进而与reorderChidren中的ArrayList<View> children进行同步，即它改变位置的同时，我传入的ArrayList也改变，并且记录下来，这样就可以记忆各个便签保留的位置了*/
	public void setRememberPositon(ArrayList<String> remPos){
		remPostions=remPos;		
	}
	/*每次移动图标之后，必须要返回重新排列的arraylist*/
	public ArrayList<String> getRememberPositon( ){
		return 	remPostions;	
	}
	
	private void reorderChildren() {
		//FIGURE OUT HOW TO REORDER CHILDREN WITHOUT REMOVING THEM ALL AND RECONSTRUCTING THE LIST!!!
		if (onRearrangeListener != null) {
			onRearrangeListener.onRearrange(draggedIndex, lastTargetIndex);
		}
		ArrayList<View> children = new ArrayList<View>();
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).clearAnimation();
			children.add(getChildAt(i));
		}
		removeAllViews();
		/*开始调整children的顺序*/
		while (draggedIndex != lastTargetIndex)
			/*因为是自动生成的各个便签表，所以增加这样后续就不需要了*/
			if (lastTargetIndex == children.size()) {
				/*新增的话，直接放到children的最右边*/
				// dragged and dropped to the right of the last element
				children.add(children.remove(draggedIndex));
				draggedIndex = lastTargetIndex;
			} else if (draggedIndex < lastTargetIndex) {
				/*向左交换位置*/
				// shift to the right
				Collections.swap(children, draggedIndex, draggedIndex + 1);
				/*对传入的remPostions同时进行更换*/
				Collections.swap(remPostions, draggedIndex, draggedIndex + 1);
				draggedIndex++;
			}
			else if (draggedIndex > lastTargetIndex) {
				/*向右交换位置*/
				// shift to the left
				Collections.swap(children, draggedIndex, draggedIndex - 1);
				/*对传入的remPostions同时进行更换*/
				Collections.swap(remPostions, draggedIndex, draggedIndex - 1);
				draggedIndex--;
			}		
		/*下边的已经是更改顺序之后的children了，这里动一个手脚，可以控制顺序*/
		for (int i = 0; i < children.size(); i++) {
			newPositions.set(i, -1);
			addView(children.get(i));
		}
		for (int i = 0; i < remPostions.size(); i++) {
			Log.e("remPostions", remPostions.get(i));
			editorsettings_remPos.putString("i"+i, remPostions.get(i));
		}
		editorsettings_remPos.commit();
		/*各个图标的位置一旦开始改变，就一定要remPostions作为记录保存*/
		/*http://www.bubuko.com/infodetail-716156.html*/



		/*开始这里会提示你是否变为layout，选择忽略就可以正常编译了，可能是命名和编译器中保留字有写冲突，所以提示会报错*/
		onLayout(true, getLeft(), getTop(), getRight(), getBottom());
	}

	/**
	 * get the index of dragging item
	 * @return
	 */
	private int getIndex(){
		return getIndexFromCoor(lastX, lastY);
	}

	public void setOnRearrangeListener(OnRearrangeListener onRearrangeListener) {
		this.onRearrangeListener = onRearrangeListener;
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public interface OnRearrangeListener {

		public abstract void onRearrange(int oldIndex, int newIndex);
	}
}
