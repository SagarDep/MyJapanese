package pri.weiqiang.ttf;

import java.lang.reflect.Field;

import android.app.Application;
import android.graphics.Typeface;

public class app extends Application{
	public static Typeface typeFace;

	@Override
	public void onCreate() {
		super.onCreate();
		setTypeface();
	}
	public void setTypeface(){
		/*现在使用的是思源字体*/
//		typeFace = Typeface.createFromAsset(getAssets(), "fonts/A-OTF-NachinStd-Regular.otf");
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/A-OTF-NachinStd-Regular.otf");
		try
		{
			Field field = Typeface.class.getDeclaredField("DEFAULT");
			field.setAccessible(true);
			field.set(null, typeFace);
			Field field_1 = Typeface.class.getDeclaredField("DEFAULT_BOLD");
			
			field_1.setAccessible(true);
			field_1.set(null, typeFace);
			Field field_2 = Typeface.class.getDeclaredField("SANS_SERIF");
			field_2.setAccessible(true);
			field_2.set(null, typeFace);
			Field field_3 = Typeface.class.getDeclaredField("SERIF");
			field_3.setAccessible(true);
			field_3.set(null, typeFace);
			Field field_4 = Typeface.class.getDeclaredField("MONOSPACE");
			field_4.setAccessible(true);
			field_4.set(null, typeFace);
			/*以下全部报错*/
//			Field field_5 = Typeface.class.getDeclaredField("NORMAL");
//			field_5.setAccessible(true);
//			field_5.set(null, typeFace);
//			Field field_6 = Typeface.class.getDeclaredField("BOLD");
//			field_6.setAccessible(true);
//			field_6.set(null, typeFace);
//			Field field_7 = Typeface.class.getDeclaredField("ITALIC");
//			field_7.setAccessible(true);
//			field_7.set(null, typeFace);
//			Field field_8 = Typeface.class.getDeclaredField("BOLD_ITALIC");
//			field_8.setAccessible(true);
//			field_8.set(null, typeFace);
			
			
			
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}	
	}
}
