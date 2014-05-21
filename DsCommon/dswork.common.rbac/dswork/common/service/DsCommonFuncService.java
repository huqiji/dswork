/**
 * 功能Service
 */
package dswork.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dswork.common.dao.DsCommonFuncDao;
import dswork.common.dao.DsCommonRoleDao;
import dswork.common.dao.DsCommonSystemDao;
import dswork.common.model.DsCommonFunc;
import dswork.common.model.DsCommonRes;
import dswork.common.model.DsCommonSystem;
import dswork.core.page.PageRequest;

@Service
@SuppressWarnings("all")
public class DsCommonFuncService
{
	@Autowired
	private DsCommonSystemDao systemDao;
	@Autowired
	private DsCommonFuncDao funcDao;
	@Autowired
	private DsCommonRoleDao roleDao;

	public DsCommonSystem getSystem(Long primaryKey)
	{
		return (DsCommonSystem) systemDao.get(primaryKey);
	}

	/**
	 * 新增对象
	 * @param po 功能对象
	 */
	public void save(DsCommonFunc po)
	{
		funcDao.save(po);
	}

	/**
	 * 删除功能,包括其资源和被分配到角色的信息。
	 * @param id 功能主键
	 */
	public int delete(Long id)
	{
		roleDao.deleteRoleFuncByFuncid(id);
		return funcDao.delete(id);
	}

	/**
	 * 更新对象
	 * @param model 功能对象
	 * @return int
	 */
	public int update(DsCommonFunc po)
	{
		return funcDao.update(po);
	}

	/**
	 * 更新移动
	 * @param ids 功能主键数组
	 * @param targetId 目标节点，为0则是根节点
	 */
	public void updatePid(Long[] ids, long targetId)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		for(int i = 0; i < ids.length; i++)
		{
			if(ids[i] > 0)
			{
				funcDao.updatePid(ids[i], targetId, map);
			}
		}
	}

	/**
	 * 更新排序
	 * @param ids 功能主键数组
	 */
	public void updateSeq(Long[] ids)
	{
		for (int i = 0; i < ids.length; i++)
		{
			funcDao.updateSeq(ids[i], i + 1L);
		}
	}

	/**
	 * 判断标识是否存在
	 * @param alias 标识
	 * @param systemid 所属系统主键
	 * @return boolean 存在true，不存在false
	 */
	public boolean isExistByAlias(String alias, long systemid)
	{
		return funcDao.isExistByAlias(alias, systemid);
	}

	/**
	 * 查询单个功能对象
	 * @param primaryKey 功能主键
	 * @return DsCommonFunc
	 */
	public DsCommonFunc get(Long primaryKey)
	{
		return (DsCommonFunc) funcDao.get(primaryKey);
	}
	
	/**
	 * 根据系统主键和上级功能主键取得列表数据
	 * @param systemid 系统主键
	 * @param pid 上级功能主键
	 * @return List&lt;DsCommonFunc&gt;
	 */
	public List<DsCommonFunc> queryList(Long systemid, Long pid)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		PageRequest request = new PageRequest();
		map.put("systemid", systemid);
		map.put("pid", pid);
		request.setFilters(map);
		return funcDao.queryList(request);
	}

	/**
	 * 获得节点的子节点个数
	 * @param pid 上级功能主键
	 * @return int
	 */
	public int getCountByPid(long pid)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pid", pid);
		PageRequest pageRequest = new PageRequest();
		pageRequest.setFilters(map);
		return funcDao.queryCount(pageRequest);
	}
}
