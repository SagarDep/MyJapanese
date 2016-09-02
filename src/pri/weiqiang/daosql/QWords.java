package pri.weiqiang.daosql;

import java.util.List;

import android.util.Log;
import de.greenrobot.dao.query.QueryBuilder;
import pri.weiqiang.daojapanese.words;
import pri.weiqiang.daojapanese.wordsDao;
import pri.weiqiang.daojapanese.wordsDao.Properties;

/**
 * @author 54wall
 * @date 创建时间：2016-5-5 上午10:30:56
 * @version 1.0
 */
public class QWords {

	
	/* 在调用前必须有title_Dao=daoSession.getLesson_titleDao();完成连接
	 * str 输入了查询的关键词
	 * */
	public List<words> queryWords_Lesson_id(wordsDao words_Dao,String str) {
		QueryBuilder<words> qb_words = words_Dao.queryBuilder();
		/*使用eq是否会提高效率*/
		qb_words.where(Properties.Lesson_id.like("%" + str + "%"));
		/*会直接报错*/
//		qb_words.where(Properties.Lesson_id.eq(str));
		List<words> words_list = qb_words.list();
//		for (int i = 0; i < words_list.size(); i++) {			
//			Log.e("getWord", words_list.get(i).getWord());
//			Log.e("getPhonetic", words_list.get(i).getPhonetic());
//			}
		return words_list;
		
		}
	
	/*查询Words表中全部被加入生词本的单词*/
	public List<words> queryWords_Fav(wordsDao words_Dao) {
		QueryBuilder<words> qb_words = words_Dao.queryBuilder();
		/*使用eq是否会提高效率*/
		qb_words.where(Properties.Fav.eq(1));
		List<words> words_list = qb_words.list();
//		for (int i = 0; i < words_list.size(); i++) {			
//			Log.e("getWord", words_list.get(i).getWord());
//			Log.e("getPhonetic", words_list.get(i).getPhonetic());
//			}
		return words_list;
		
		}
	/*查询Words表某一个lesson中的生词*/
	public List<words> queryWords_Fav_Lesson(wordsDao words_Dao,String Lesson_name) {
		QueryBuilder<words> qb_words = words_Dao.queryBuilder();
		/*使用eq是否会提高效率*/
		qb_words.where(Properties.Fav.eq(1),
				Properties.Lesson_id.eq(Lesson_name));
		List<words> words_list = qb_words.list();
//		for (int i = 0; i < words_list.size(); i++) {			
//			Log.e("getWord", words_list.get(i).getWord());
//			Log.e("getPhonetic", words_list.get(i).getPhonetic());
//			}
		return words_list;
		
		}
	/*为查询界面适配查询器*/
	public List<words> queryWords_All_Search(wordsDao words_Dao,String str) {
		QueryBuilder<words> qb_words = words_Dao.queryBuilder();
		/*使用eq是否会提高效率*/
//		qb_words.or(Properties.Translation.like("%" + str + "%"),
//				Properties.Word.like("%" + str + "%"),
//				Properties.Phonetic.like("%" + str + "%"));
		
		qb_words.whereOr(Properties.Translation.like("%" + str + "%"),
				Properties.Word.like("%" + str + "%"),
				Properties.Phonetic.like("%" + str + "%")
				);
		
		List<words> words_list = qb_words.list();
		
//		for (int i = 0; i < words_list.size(); i++) {			
//			Log.e("getWord", words_list.get(i).getWord());
//			Log.e("getPhonetic", words_list.get(i).getPhonetic());
//			}
		Log.e("QWords 78", String.valueOf(words_list.size()));
		/*这里需要注意的是即使存入editText的内容是空的，words_list便会返回全部值，还是没有找到错误点，words_list=null会直接空指针*/
		if (words_list.size()>=13048) {
			words_list.clear();			
		}
		return words_list;
		
		}
	
}

