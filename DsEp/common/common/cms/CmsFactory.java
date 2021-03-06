/**
 * CMSService
 */
package common.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import dswork.core.page.Page;

public class CmsFactory
{
	private static DsCmsDao dao = null;

	private static Long toLong(Object v)
	{
		try
		{
			return Long.parseLong(String.valueOf(v));
		}
		catch(Exception e)
		{
			return 0L;
		}
	}
	private Long siteid = 0L;
	Map<String, Object> site = new HashMap<String, Object>();

	public CmsFactory(HttpServletRequest request)
	{
		try
		{
			if(getDao() == null)
			{
				init();
			}
			String tmp = String.valueOf(request.getParameter("siteid"));
			siteid = toLong(tmp);
			site = getDao().getSite(siteid);
		}
		catch(Exception ex)
		{
		}
	}

	protected DsCmsDao getDao()
	{
		return dao;
	}

	protected void init()
	{
		dao = (DsCmsDao) dswork.spring.BeanFactory.getBean("dsCmsDao");
	}

	public Map<String, Object> getSite()
	{
		return site;
	}

	public Map<String, Object> getCategory(Object categoryid)
	{
		return getDao().getCategory(siteid, toLong(categoryid));
	}

	public Map<String, Object> get(String pageid)
	{
		return getDao().get(siteid, toLong(pageid));
	}

	public List<Map<String, Object>> queryList(int currentPage, int pageSize, boolean onlyImage, boolean onlyPage, boolean isDesc, Object... categoryids)
	{
		StringBuilder idArray = new StringBuilder();
		idArray.append("0");
		for(int i = 0; i < categoryids.length; i++)
		{
			idArray.append(",").append(toLong(categoryids[i]));
		}
		Page<Map<String, Object>> page = getDao().queryPage(siteid, currentPage, pageSize, idArray.toString(), isDesc, onlyImage, onlyPage, null);
		return page.getResult();
	}

	public Map<String, Object> queryPage(int currentPage, int pageSize, boolean onlyImage, boolean onlyPage, boolean isDesc, String url, Object categoryid)
	{
		if(currentPage <= 0)
		{
			currentPage = 1;
		}
		if(pageSize <= 0)
		{
			pageSize = 25;
		}
		StringBuilder idArray = new StringBuilder();
		idArray.append(toLong(categoryid));
		Page<Map<String, Object>> page = getDao().queryPage(siteid, currentPage, pageSize, idArray.toString(), isDesc, onlyImage, onlyPage, null);
		Map<String, Object> map = new HashMap<String, Object>();
		currentPage = page.getCurrentPage();// 更新当前页
		map.put("list", page.getResult());
		Map<String, Object> pagemap = new HashMap<String, Object>();
		pagemap.put("page", currentPage);
		pagemap.put("pagesize", pageSize);
		pagemap.put("first", 1);
		pagemap.put("firsturl", url);
		int tmp = initpage(currentPage - 1, page.getLastPage());
		pagemap.put("prev", tmp);
		pagemap.put("prevurl", (tmp == 1 ? url : (url.replaceAll("\\.html", "_" + tmp + ".html"))));
		tmp = initpage(currentPage + 1, page.getLastPage());
		pagemap.put("next", tmp);
		pagemap.put("nexturl", (tmp == 1 ? url : (url.replaceAll("\\.html", "_" + tmp + ".html"))));
		tmp = page.getLastPage();
		pagemap.put("last", tmp);
		pagemap.put("lasturl", (tmp == 1 ? url : (url.replaceAll("\\.html", "_" + tmp + ".html"))));
		map.put("datauri", url.replaceAll("\\.html", ""));
		map.put("datapage", pagemap);
		StringBuilder sb = new StringBuilder();
		int viewpage = 3, temppage = 1;// 左右显示个数
		sb.append("<a");
		if(currentPage == 1)
		{
			sb.append(" class=\"selected\"");
		}
		else
		{
			sb.append(" href=\"").append(site.get("url")).append(url).append("\"");
		}
		sb.append(">1</a>");
		temppage = currentPage - viewpage - 1;
		if(temppage > 1)
		{
			String u = url.replaceAll("\\.html", "_" + temppage + ".html");
			sb.append("<a href=\"").append(site.get("url")).append(u).append("\">...</a>");
		}
		for(int i = currentPage - viewpage; i <= currentPage + viewpage && i < page.getLastPage(); i++)
		{
			if(i > 1)
			{
				String u = (i == 1 ? url : (url.replaceAll("\\.html", "_" + i + ".html")));
				sb.append("<a");
				if(currentPage == i)
				{
					sb.append(" class=\"selected\"");
				}
				else
				{
					sb.append(" href=\"").append(site.get("url")).append(u).append("\"");
				}
				sb.append(">").append(i).append("</a>");
			}
		}
		temppage = currentPage + viewpage + 1;
		if(temppage < page.getLastPage())
		{
			String u = url.replaceAll("\\.html", "_" + temppage + ".html");
			sb.append("<a href=\"").append(site.get("url")).append(u).append("\">...</a>");
		}
		if(1 != page.getLastPage())
		{
			String u = url.replaceAll("\\.html", "_" + page.getLastPage() + ".html");
			sb.append("<a");
			if(currentPage == page.getLastPage())
			{
				sb.append(" class=\"selected\"");
			}
			else
			{
				sb.append(" href=\"").append(site.get("url")).append(u).append("\"");
			}
			sb.append(">").append(page.getLastPage()).append("</a>");
		}
		map.put("datapageview", sb.toString());// 翻页字符串
		return map;
	}

	public Map<String, Object> queryPage(int currentPage, int pageSize, boolean isDesc, String keyvalue, Object... categoryids)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			StringBuilder idArray = new StringBuilder();
			if(categoryids.length > 0)
			{
				idArray.append("0");
				for(int i = 0; i < categoryids.length; i++)
				{
					idArray.append(",").append(toLong(categoryids[i]));
				}
			}
			Page<Map<String, Object>> page = getDao().queryPage(siteid, currentPage, pageSize, idArray.toString(), isDesc, false, false, keyvalue);
			map.put("status", "1");// success
			map.put("msg", "success");
			map.put("size", page.getTotalCount());
			map.put("page", page.getCurrentPage());
			map.put("pagesize", page.getPageSize());
			map.put("totalpage", page.getTotalPage());
			map.put("rows", page.getResult());
		}
		catch(Exception ex)
		{
			map.put("status", "0");
			map.put("msg", "error");
		}
		return map;
	}

	private int initpage(int page, int total)
	{
		if(page < 0)
		{
			page = 1;
		}
		if(page > total)
		{
			page = total;
		}
		return page;
	}

	/**
	 * 查询指定栏目下的子栏目(包括所有递归子栏目)
	 * @param categoryid
	 *        父栏目，查询根栏目为空
	 * @return List&lt;Map&lt;String, Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryCategory(Object categoryid)
	{
		String pid = String.valueOf(toLong(categoryid));
		List<Map<String, Object>> list = getDao().queryCategory(siteid);
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> m : list)
		{
			String p = String.valueOf(m.get("pid"));
			if(p.equals("null"))
			{
				m.put("pid", "0");
				p = "0";
			}
			m.put("list", new ArrayList<Map<String, Object>>());
			map.put(String.valueOf(m.get("id")), m);
			if(p.equals(pid))
			{
				rs.add(m);
			}
		}
		for(Map<String, Object> m : list)
		{
			String p = String.valueOf(m.get("pid"));
			if(!p.equals("0") && map.get(p) != null)
			{
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> l = (ArrayList<Map<String, Object>>) (((Map<String, Object>) (map.get(p))).get("list"));
				l.add(m);
			}
		}
		return rs;
	}
}
