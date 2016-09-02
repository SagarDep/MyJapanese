package pri.weiqiang.encryption;
/**
 * @author  54wall 
 * @date 创建时间：2016-5-25 下午4:25:57
 * @version 1.0 
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
/**
 * 漫画显示主类。
 * 
 * @author Mars.CN
 * 
 */
public class CartoonView extends View{
    public static final String PACKAGE_TYPE_ZIP = "ZIP"; // zip包
    public static final String PACKAGE_TYPE_MCP = "MCP"; // 专用包
    public static final int PAGE_AREA_SIZE=40;            //翻页区域
    private Context mContext = null;
     
    private ArrayList<ZipEntry> pics = null; // zip包中的文件列表
    private ZipFile zipFile = null; // 漫画包文件
    private int playIndex = 0; // 正在播放中的图片。
    private Bitmap playBitmap = null; // 正在播放中的图片
    private Bitmap nextBitmap = null; // 下一张图片
    private Bitmap lastBitmap = null; // 上一张图片
    private Paint mPaint = new Paint(); // 全局画笔
    private float zoom = -1; // 缩放级别，目前只支持全屏与按大小播放两种格式
    private int px = 0, py = 0; // 画图位置
    private String cartoonPackagePath = ""; // 图片包的路径（一般为sdcard/cmsreader/cartoon/中位置）
    private String packageType = ""; // 图片包类型
    private int width, height; // 画布大小
     
    private _ActionEvent actionEvent = null;    //目前正在操作中的Event，在双击事件中可能会用到其中的坐标等信息
 
    @Override
    protected void onDraw(Canvas canvas) {
//        System.out.println("开始重绘");
        super.onDraw(canvas);
        Bitmap p = getBitmap();
        canvas.drawBitmap(p, 0, 0, mPaint);
//        System.out.println("画图成功");
    }
 
    /**
     * 构造阅读器主类
     * 
     * @param context
     * @param cpkPath
     */
    public CartoonView(Context context, String cpkPath) {
        super(context);
        mContext = context;
        setCartoonPackagePath(cpkPath);
        Activity act = (Activity) context;
        width = act.getWindowManager().getDefaultDisplay().getWidth();
        height = act.getWindowManager().getDefaultDisplay().getHeight();
         
        registerDoubleClickListener();
    }
     
    /**
     * 注册一个双击事件
     */
    private void registerDoubleClickListener(){
        setOnClickListener(new OnClickListener() {
            private static final int DOUBLE_CLICK_TIME = 350;        //双击间隔时间350毫秒
            private boolean waitDouble = true;                        //等待双击
            @Override
            public void onClick(View v) {
                if(waitDouble){
                    waitDouble = false;        //与执行双击事件
                    new Thread(){
                        public void run(){
                            try {
                                sleep(DOUBLE_CLICK_TIME);    //等待双击时间，否则执行单击事件
                                if(!waitDouble){
                                    //如果过了等待事件还是预执行双击状态，则视为单击
                                    waitDouble = true;
                                    onSingleClick();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                             
                        }
                    }.start();
                }else{
                    waitDouble = true;
                    onDoubleClick();    //执行双击
                }
            }
        });
    }
     
    /**
     * 触发双击事件
     */
    private void onDoubleClick(){
        //System.out.println("双击图片,坐标：" + actionEvent.getX() + "," + actionEvent.getY());
        zoomPicture();
    }
     
    /**
     * 调整图片大小。<br />
     * 如果zoom小于1，则zoom的大小改为1，如果zoom的大小等于1，则缩放至全屏大小。<br />
     */
    private void zoomPicture(){
        if(zoom<1){
            zoom=1;
            /*
             * 如果是全屏播放，则已鼠标双击位置为中心进行放大，具体操作如下：
             * 确定单击实际位置：1.计算出原缩放比例， 2.坐标/原比例得到实际位置
             * 将此位置移动至屏幕中心，并重新计算出新的XY坐标位置
             */
            float fzoom=0;
            fzoom = (float) height / playBitmap.getHeight();
            if (playBitmap.getWidth() * fzoom > width) {
                fzoom = (float) width / playBitmap.getWidth();
            }
            float fx = actionEvent.getX()/fzoom;
            float fy = actionEvent.getY()/fzoom;
            px = (int)((width)/2-fx);
            py = (int)((height)/2-fy);
            moveXY(0,0);
        }else{
            zoom=-1;
        }
        invalidate();        //刷新画布
    }
     
    /**
     * 触发单击时间
     */
    private void onSingleClick(){
        //System.out.println("单击图片 " + actionEvent.getX() + "," + actionEvent.getY());
        //如果单击的是,并且zoom不为1，则判断触发的是否翻页区域
        if(zoom!=1){
            if(actionEvent.getX()<=PAGE_AREA_SIZE){
                //表示反向上一页
                playIndex--;
                if(playIndex<0){
                    playIndex=0;
                    //Toast.makeText(mContext, "前面没有内容了", Toast.LENGTH_SHORT).show();
                }
            }else if(actionEvent.getX()>=width-PAGE_AREA_SIZE){
                playIndex++;
                if(playIndex>=pics.size()){
                    playIndex = pics.size()-1;
                    //Toast.makeText(mContext, "后面没有内容了", Toast.LENGTH_SHORT).show();
                }
            }
            playBitmap = createBitmap(playIndex);
            //invalidate();//重绘
            postInvalidate();    //必须调用外部重绘，否则执行出错
        }
    }
     
    /**
     * 重构父类的onTouchFevent事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //按下的时候把event保存下来，再双击事件中可能会用到坐标值
        switch(event.getAction()){
        case MotionEvent.ACTION_DOWN :    //按下事件
            break;
        case MotionEvent.ACTION_MOVE :    //拖动事件
            //System.out.println("ACTION_MOVE : " + event.getX() + "," + event.getY());
            //判断是否等于1，如果等于1，则表示全屏播放，可以进行移动，否则翻页
            if(zoom==1){
                if(actionEvent!=null && actionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    //如果上一次的动作是移动，则才可以移动图片
                    moveXY(event.getX()-actionEvent.getX(),event.getY()-actionEvent.getY());
                    invalidate();
                }
            }else{
                //翻页，只允许左右移动
            }
            break;
        }
        actionEvent = new _ActionEvent(event);
        return super.onTouchEvent(event);
    }
     
    /**
     * 移动图片
     * @param x
     * @param y
     */
    private void moveXY(float x , float y){
        px += x;
        //判断x是否超过左右边界
        //px=px<playBitmap.getWidth()-width?playBitmap.getWidth()-width:px;
        px = px<width-playBitmap.getWidth()?width-playBitmap.getWidth():px;
        px = px>0?0:px;
        py += y;
        py = py<height-playBitmap.getHeight()?height-playBitmap.getHeight():py;
        py = py>0?0:py;
        //判断y是否超过上下边界
         
    }
 
    /**
     * 重构父类的构造方法。
     * 
     * @param context
     */
    public CartoonView(Context context) {
        this(context, "");
    }
 
    /**
     * 初始化图片包。
     */
    public void initPackage() {
        if (getPackageType().equals(PACKAGE_TYPE_MCP)) {
            // 如果是mcp格式的文件，则转移为zip格式直接读取
        } else if (getPackageType().equals(PACKAGE_TYPE_ZIP)) {
            // 如果是zip文件格式，则直接生成
            try {
                zipFile = new ZipFile(getCartoonPackagePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) zipFile.entries(); // 获取zip文件中的目录及文件列表
        ZipEntry entry = null;
        pics = new ArrayList<ZipEntry>();
        while (e.hasMoreElements()) {
            entry = e.nextElement();
            if (!entry.isDirectory()) {
                // 如果文件不是目录，则添加到列表中
                pics.add(entry);
            }
        }
 
        // 初始化当前播放图片
        playBitmap = createBitmap(playIndex);
    }
 
    /**
     * 获得图片包路径。
     * 
     * @return
     */
    public String getCartoonPackagePath() {
        return cartoonPackagePath;
    }
 
    /**
     * 设置图片包路径。
     * 
     * @param cartoonPackagePath
     */
    public void setCartoonPackagePath(String cartoonPackagePath) {
        this.cartoonPackagePath = cartoonPackagePath;
        // 设置类型
        if (cartoonPackagePath.endsWith(".mcp")) {
            setPackageType(PACKAGE_TYPE_MCP);
        } else {
            setPackageType(PACKAGE_TYPE_ZIP);
        }
        initPackage();
    }
 
    /**
     * 获取图片包格式。
     * 
     * @return
     */
    public String getPackageType() {
        return packageType;
    }
 
    /**
     * 设置图片包格式。
     * 
     * @param packageType
     */
    private void setPackageType(String packageType) {
        this.packageType = packageType;
    }
 
    /**
     * 根据图片编号创建一张图片，编号从0开始。
     * 
     * @param pictureid
     * @return
     */
    private Bitmap createBitmap(int pictureid) {
//        System.out.println("CreateBitmapBegin");
        Bitmap result = null;
        try {
            result = new BitmapDrawable(zipFile.getInputStream(pics
                    .get(pictureid))).getBitmap();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("CreateBitmapEnd");
        return result;
    }
     
    /**
     * 创建缓存中的图片序列，提高刷新速度。<br />
     * 将lastBitmap到nextBitmap一次瞬移，最后判断playBitmap是否为空，如果为空，则创建当前序列图像。<br />
     * 在调用此方法时，确认playIndex已经重新赋值之过。
     * @para index 1表示向后移动，-1表示向前移动。
     */
//    private synchronized void displayPictures(short index){
//       
//    }
 
    /**
     * 创建一张画在屏幕上的图片。
     * 
     * @return
     */
    private Bitmap getBitmap() {
        Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.BLACK);
        // 画当前播放中的图片。
        if (zoom < 1) {
            // 如果是全屏模式
            /*
             * 1.缩放高，得到缩放比例 2.宽度乘以缩放比例，如果得到的实际宽度比屏幕宽度达，则缩放比例按宽计算
             * 3.得到绘制比例的高和宽，并绘制图片
             */
            zoom = (float) height / playBitmap.getHeight();
            if (playBitmap.getWidth() * zoom > width) {
                zoom = (float) width / playBitmap.getWidth();
            }
            int c_width = (int) (playBitmap.getWidth() * zoom);
            int c_height = (int) (playBitmap.getHeight() * zoom);
            // 计算出绘制位置
            px = (width - c_width) / 2;
            py = (height - c_height) / 2;
            Matrix matrix = new Matrix();
            matrix.postScale(zoom, zoom);
 
            //缩放并绘制图片
            canvas.drawBitmap(Bitmap.createBitmap(playBitmap, 0, 0, playBitmap
                    .getWidth(), playBitmap.getHeight(), matrix, true), px, py,
                    mPaint);
        }else{
            //画图
//            System.out.println(px+","+py);
            canvas.drawBitmap(playBitmap, px, py, mPaint);
             
        }
        return result;
    }
     
     
    /**
     * 内部动作类。
     * @author 王充
     *
     */
    private class _ActionEvent{
        private float x,y;
        private int action;
        public float getX() {
            return x;
        }
        public void setX(float x) {
            this.x = x;
        }
        public float getY() {
            return y;
        }
        public void setY(float y) {
            this.y = y;
        }
        public int getAction() {
            return action;
        }
        public void setAction(int action) {
            this.action = action;
        }
        public _ActionEvent(MotionEvent event){
            setX(event.getX());
            setY(event.getY());
            setAction(event.getAction());
        }
    }
 
}