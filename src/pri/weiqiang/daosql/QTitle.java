package pri.weiqiang.daosql;

import java.util.List;

import android.util.Log;
import de.greenrobot.dao.query.QueryBuilder;
import pri.weiqiang.daojapanese.lesson_title;
import pri.weiqiang.daojapanese.lesson_titleDao;
import pri.weiqiang.daojapanese.lesson_titleDao.Properties;

/**
 * @author 54wall
 * @date 创建时间：2016-5-5 上午10:30:56
 * @version 1.0
 * 使用greenDAO时，只能导入一个Properties（import de.greenrobot.voall.lesson_titleDao.Properties;），
 * 不允许导入多个所以对于多张表只能通过新建类完成
 */

/*刚刚在想Q的几个类全部基本都一样，还不如房子啊一起，但是忘记了，一个类智能导入一个Properties*/
public class QTitle {
	
	/* 在调用前必须有title_Dao=daoSession.getLesson_titleDao();完成连接*/
	public void queryTitle(lesson_titleDao title_Dao,String str) {
		QueryBuilder<lesson_title> qb_title = title_Dao.queryBuilder();
		qb_title.where(Properties.Lesson.like("%" + "新标日初级_1" + "%"));
		List<lesson_title> title_list = qb_title.list();
//		for (int i = 0; i < title_list.size(); i++) {			
//			Log.e("getLesson", title_list.get(i).getLesson());
//			Log.e("getLesson_title", title_list.get(i).getLesson_title());
//			}
		
		}
	}

