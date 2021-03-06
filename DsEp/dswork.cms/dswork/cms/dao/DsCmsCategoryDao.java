/**
 * 栏目Dao
 */
package dswork.cms.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import dswork.cms.model.DsCmsCategory;
import dswork.core.db.BaseDao;

@Repository
@SuppressWarnings("all")
public class DsCmsCategoryDao extends BaseDao<DsCmsCategory, Long>
{
	@Override
	public Class getEntityClass()
	{
		return DsCmsCategory.class;
	}

	/**
	 * 排序节点
	 * @param id 主键
	 * @param seq 排序位置
	 * @param siteid 站点ID
	 */
	public void updateSeq(long id, int seq, long siteid)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("seq", seq);
		map.put("siteid", siteid);
		executeUpdate("updateSeq", map);
	}

	/**
	 * 更新单页栏目内容
	 * @param po 更新内容及发布状态
	 */
	public void updateContent(DsCmsCategory po)
	{
		executeUpdate("updateContent", po);
	}

	/**
	 * 更新发布状态
	 * @param id 
	 * @param status 
	 * @return int
	 */
	public int updateStatus(Long id, int status)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("status", status);
		return executeUpdate("updateStatus", map);
	}
}
