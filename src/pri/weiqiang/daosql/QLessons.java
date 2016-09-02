package pri.weiqiang.daosql;

import java.util.List;

import android.util.Log;
import de.greenrobot.dao.query.QueryBuilder;
import pri.weiqiang.daojapanese.lessons;
import pri.weiqiang.daojapanese.lessonsDao;
import pri.weiqiang.daojapanese.lessonsDao.Properties;

/**
 * @author 54wall
 * @date 创建时间：2016-5-5 上午10:30:56
 * @version 1.0
 * 使用greenDAO时，只能导入一个Properties（import de.greenrobot.voall.lesson_titleDao.Properties;），
 * 不允许导入多个所以对于多张表只能通过新建类完成
 */
public class QLessons {

	
	/* 在调用前必须有title_Dao=daoSession.getLesson_titleDao();完成连接
	 * str 输入了查询的关键词
	 * */
	public List<lessons> queryLessons(lessonsDao lessons_Dao,String str) {
		QueryBuilder<lessons> qb_lessons = lessons_Dao.queryBuilder();
		/*eq也可以使用，模糊查找会找到会将标日I 标日II 都当成合适的查找目标，这并不是我想要的结果*/
//		qb_lessons.where(Properties.Book.like("%" + str + "%"));
		qb_lessons.where(Properties.Book.eq(str));
		List<lessons> lessons_list = qb_lessons.list();
//		for (int i = 0; i < lessons_list.size(); i++) {			
//			Log.e("getLesson", lessons_list.get(i).getBook());
//			Log.e("getLesson_title", lessons_list.get(i).getTitle());
//			}
		return lessons_list;
		
		}
	
}

