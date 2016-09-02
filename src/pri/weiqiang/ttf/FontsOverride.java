package pri.weiqiang.ttf;
import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Typeface;
/**
 * @author  54wall 
 * @date 创建时间：2016-6-20 下午2:45:43
 * @version 1.0 
 */
public final class FontsOverride {
	 
    public static void setDefaultFont(Context context,
            String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }
 
    protected static void replaceFont(String staticTypefaceFieldName,
            final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}