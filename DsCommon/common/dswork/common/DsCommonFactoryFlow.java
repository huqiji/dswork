package dswork.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dswork.common.model.IFlowTask;
import dswork.common.model.IFlowWaiting;
import dswork.common.service.DsCommonService;
import dswork.spring.BeanFactory;

public class DsCommonFactoryFlow
{
	private static DsCommonService service = null;

	private static void init()
	{
		if(service == null)
		{
			service = (DsCommonService) BeanFactory.getBean("dsCommonService");
		}
	}

	/**
	 * 流程启动
	 * @param alias 启动流程的标识
	 * @param ywlsh 业务流水号
	 * @param caccount 提交人账号
	 * @param cname 提交人姓名
	 * @param piDay 时限天数
	 * @param isWorkDay 时限天数类型(false日历日,true工作日)
	 * @param taskInterface 接口类（暂时无用）
	 * @return 流程实例ID
	 */
	public String start(String alias, String ywlsh, String caccount, String cname, int piDay, boolean isWorkDay, String taskInterface)
	{
		try
		{
			init();
			return service.saveStart(alias, ywlsh, caccount, cname, piDay, isWorkDay, taskInterface);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 流程启动并返回开始节点待办信息
	 * @param alias 启动流程的标识
	 * @param ywlsh 业务流水号
	 * @param caccount 提交人账号
	 * @param cname 提交人姓名
	 * @param piDay 时限天数
	 * @param isWorkDay 时限天数类型(false日历日,true工作日)
	 * @param taskInterface 接口类（暂时无用）
	 * @return 流程实例的start待办信息或null
	 */
	public IFlowWaiting startWaiting(String alias, String ywlsh, String caccount, String cname, int piDay, boolean isWorkDay, String taskInterface)
	{
		try
		{
			init();
			return service.saveFlowStart(alias, ywlsh, caccount, cname, piDay, isWorkDay, taskInterface);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void stop(String piid)
	{
		try
		{
			init();
			service.saveStop(Long.parseLong(piid));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 流程处理
	 * @param waitid 待办事项ID
	 * @param nextTalias 下级任务列表，如果为null，处理当前任务后，会结束流程
	 * @param paccount 当前处理人账号
	 * @param pname 当前处理人姓名
	 * @param resultType 处理类型
	 * @param resultMsg 处理意见
	 * @return true|false
	 */
	public boolean process(long waitid, String[] nextTalias, String paccount, String pname, String resultType, String resultMsg)
	{
		try
		{
			init();
			return service.saveProcess(waitid, nextTalias, paccount, pname, resultType, resultMsg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public List<IFlowWaiting> queryWaiting(String account)
	{
		try
		{
			init();
			return service.queryFlowWaiting(account);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public boolean takeWaiting(long waitid, String user)
	{
		try
		{
			if(user != null && user.trim().length() > 0)
			{
				service.updateFlowWaitingUser(waitid, user);
				return true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public IFlowWaiting getWaiting(long waitid)
	{
		try
		{
			init();
			return service.getFlowWaiting(waitid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, String> getTaskList(long flowid)
	{
		Map<String, String> map = new HashMap<String, String>();
		try
		{
			init();
			List<IFlowTask> list = service.queryFlowTask(flowid);
			if(list != null)
			{
				for(IFlowTask m : list)
				{
					map.put(m.getTalias(), m.getTname());
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
}
