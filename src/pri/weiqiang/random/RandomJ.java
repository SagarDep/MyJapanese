package pri.weiqiang.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;
import pri.weiqiang.daojapanese.words;

/**
 * @author  54wall 
 * @date 创建时间：2016-5-5 下午2:43:53
 * @version 1.0 
 * 务必增加乱序输出
 */
public class RandomJ {
	
	/* java.lang.RuntimeException: Unable to start activity ComponentInfo{pri.weiqiang.myjapanese
	 * /pri.weiqiang.myjapanese.MyExpandableListViewDemo}: java.lang.IllegalArgumentException: 
	 * n <= 0: 0*/
    public List<words> name(List<words> sourceList) {
    	ArrayList<words> randomList = new ArrayList<words>( sourceList.size( ) );
        do{
            int randomIndex = Math.abs( new Random( ).nextInt( sourceList.size() ) );
//            Log.e("ran_list", randomList.get(0).getTranslation() );
            randomList.add( sourceList.remove( randomIndex ) );
            Log.e("ran_list", randomList.get(0).getTranslation() );
        }while( sourceList.size( ) > 0 );

        return randomList;
	}
    
}
