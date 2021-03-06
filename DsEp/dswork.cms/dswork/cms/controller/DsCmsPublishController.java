package dswork.cms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dswork.cms.model.DsCmsCategory;
import dswork.cms.model.DsCmsPage;
import dswork.cms.model.DsCmsPermission;
import dswork.cms.model.DsCmsSite;
import dswork.cms.service.DsCmsPublishService;
import dswork.core.page.Page;
import dswork.core.page.PageNav;
import dswork.core.page.PageRequest;
import dswork.core.util.FileUtil;
import dswork.core.util.TimeUtil;
import dswork.mvc.BaseController;

@Scope("prototype")
@Controller
@RequestMapping("/cms/publish")
public class DsCmsPublishController extends BaseController
{
	@Autowired
	private DsCmsPublishService service;

	@RequestMapping("getCategoryTree")
	public String getCategoryEditTree()
	{
		try
		{
			Long id = req.getLong("siteid", -1), siteid = -1L;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("own", getOwn());
			map.put("account", getAccount());
			List<DsCmsSite> siteList = service.queryListSite(map);
			if(siteList != null && siteList.size() > 0)
			{
				put("siteList", siteList);
				if(id >= 0)
				{
					for(DsCmsSite m : siteList)
					{
						if(m.getId().longValue() == id)
						{
							siteid = m.getId();
							break;
						}
					}
				}
				if(siteid == -1)
				{
					siteid = siteList.get(0).getId();
				}
			}
			if(siteid >= 0)
			{
				DsCmsPermission permission = service.getPermission(siteid, getAccount());
				List<DsCmsCategory> cateList = new ArrayList<DsCmsCategory>();
				if(permission != null)
				{
					List<DsCmsCategory> _cateList = service.queryListCategory(siteid);
					cateList = DsCmsUtil.categoryAccess(_cateList, permission.getPublish());
				}
				put("siteList", siteList);
				put("cateList", cateList);
			}
			put("siteid", siteid);
			return "/cms/publish/getCategoryTree.jsp";
		}
		catch(Exception e)
		{
			return null;
		}
	}

	// 获得page分页
	@RequestMapping("/getPage")
	public String getPage()
	{
		Long categoryid = req.getLong("id");
		DsCmsCategory m = service.getCategory(categoryid);
		DsCmsPermission permission = service.getPermission(m.getSiteid(), getAccount());
		if(permission.checkPublish(m.getId()))
		{
			if(m.getScope() == 0 && checkOwn(m.getSiteid()))// 列表
			{
				PageRequest pr = getPageRequest();
				pr.getFilters().remove("id");
				pr.getFilters().put("siteid", m.getSiteid());
				pr.getFilters().put("categoryid", m.getId());
				Page<DsCmsPage> pageModel = service.queryPage(pr);
				put("pageModel", pageModel);
				put("pageNav", new PageNav<DsCmsPage>(request, pageModel));
				put("po", m);
				return "/cms/publish/getPage.jsp";
			}
		}
		return null;
	}
	
	// 获取page明细
	@RequestMapping("/getPageById")
	public String getPageById()
	{
		Long id = req.getLong("keyIndex");
		put("po", service.get(id));
		return "/cms/publish/getPageById.jsp";
	}
	
	// 获取category明细
	@RequestMapping("/getCategoryById")
	public String getCategoryById()
	{
		Long id = req.getLong("id");
		DsCmsCategory po = service.getCategory(id);
		DsCmsPermission permission = service.getPermission(po.getSiteid(), getAccount());
		if(permission.checkPublish(po.getId()))
		{
			put("po", po);
			return "/cms/publish/getCategoryById.jsp";
		}
		return null;
	}
	
	// 获取外链category明细
	@RequestMapping("/getCategoryUrlById")
	public String getCategoryUrlById()
	{
		Long id = req.getLong("id");
		DsCmsCategory po = service.getCategory(id);
		DsCmsPermission permission = service.getPermission(po.getSiteid(), getAccount());
		if(permission.checkPublish(po.getId()))
		{
			put("po", po);
			return "/cms/publish/getCategoryUrlById.jsp";
		}
		return null;
	}







	// 发布指定的信息
	@RequestMapping("/build")
	public void build()
	{
		Long siteid = req.getLong("siteid", -1);
		Long categoryid = req.getLong("categoryid", -1);
		Long pageid = req.getLong("pageid", -1);
		int pagesize = req.getInt("pagesize", 25);
		_building(true, siteid, categoryid, pageid, pagesize);
	}

	// 删除指定的信息
	@RequestMapping("/unbuild")
	public void unbuild()
	{
		Long siteid = req.getLong("siteid", -1);
		Long categoryid = req.getLong("categoryid", -1);
		Long pageid = req.getLong("pageid", -1);
		int pagesize = req.getInt("pagesize", 25);
		_building(false, siteid, categoryid, pageid, pagesize);
	}
	
	/**
	 * 生成或删除信息
	 * @param isCreateOrDelete true生成，false删除
	 * @param siteid
	 * @param categoryid
	 * @param pageid
	 * @param pagesize
	 */
	private void _building(boolean isCreateOrDelete, long siteid, long categoryid, long pageid, int pagesize)
	{
		pagesize = (pagesize<=0) ? 25 : pagesize;
		try
		{
			if(siteid >= 0)
			{
				DsCmsSite site = service.getSite(siteid);
				if(site != null)
				{
					site.setFolder(String.valueOf(site.getFolder()).replace("\\", "").replace("/", ""));
				}
				if(site != null && site.getFolder().trim().length() > 0 && checkOwn(site.getOwn()))
				{
					String path = "http://" + getLocalAddr() + ":" + request.getLocalPort() + request.getContextPath() + "/cms/page/buildHTML.chtml?siteid=" + siteid;
					//首页：categoryid==-1，pageid==-1
					//全部栏目：categoryid==0，pageid==-1
					//指定栏目：categoryid>0，pageid==-1
					//全部内容：categoryid==0，pageid==0
					//栏目内容：categoryid>0，pageid==0
					//指定内容：pageid>0
					if(pageid > 0)// 指定内容
					{
						DsCmsPage p = service.get(pageid);
						if(p.getSiteid() == siteid)
						{
							try
							{
								_buildFile(isCreateOrDelete ? path + "&pageid=" + p.getId() : null, p.getUrl(), site.getFolder());
								service.updatePageStatus(p.getId(), 8);
								print("1");
							}
							catch (Exception e)
							{
								print("0");
							}
						}
						else
						{
							print("0:无权操作");
						}
					}
					else if(pageid == -1)// 栏目首页
					{
						List<DsCmsCategory> list = new ArrayList<DsCmsCategory>();
						if(categoryid == 0)// 全部栏目首页
						{
							List<DsCmsCategory> _list = service.queryListCategory(siteid);
							DsCmsPermission permission = service.getPermission(siteid, getAccount());
							for(DsCmsCategory c : _list)
							{
								if(permission.checkPublish(c.getId()))
								{
									list.add(c);
								}
							}
						}
						else if(categoryid > 0)// 指定栏目首页
						{
							DsCmsCategory c = service.getCategory(categoryid);
							list.add(c);
						}
						if(categoryid >= 0)// 栏目首页，这里不能用list.size，因为可能长度就是0
						{
							for(DsCmsCategory c : list)
							{
								try
								{
									if(c.getScope() == 2)// 外链没有东西生成的
									{
										_deleteFile(site.getFolder(), c.getFolder(), true, true);
										service.updateCategoryStatus(c.getId(), 8);
										continue;
									}
									_deleteFile(site.getFolder(), c.getFolder(), true, false);// 删除栏目首页
									if(isCreateOrDelete && c.getSiteid() == siteid)
									{
										_buildFile(path + "&categoryid=" + c.getId() + "&page=1&pagesize=" + pagesize, c.getUrl(), site.getFolder());
										Map<String, Object> map = new HashMap<String, Object>();
										map.put("siteid", site.getId());
										map.put("categoryid", c.getId());
										map.put("releasetime", TimeUtil.getCurrentTime());
										PageRequest rq = new PageRequest(map);
										rq.setPageSize(pagesize);
										rq.setCurrentPage(1);
										Page<DsCmsPage> pageModel = service.queryPage(rq);
										for(int i = 2; i <= pageModel.getLastPage(); i++)
										{
											_buildFile(path + "&categoryid=" + c.getId() + "&page=" + i + "&pagesize=" + pagesize, c.getUrl().replaceAll("\\.html", "_" + i + ".html"), site.getFolder());
										}
										service.updateCategoryStatus(c.getId(), 8);
									}
								}
								catch (Exception e)
								{
								}
							}
						}
						else// 首页
						{
							_buildFile(isCreateOrDelete ? path : null, "/index.html", site.getFolder());
						}
						print("1");
					}
					else if(pageid == 0)// 栏目内容
					{
						List<DsCmsCategory> list = new ArrayList<DsCmsCategory>();
						if(categoryid == 0)// 全部栏目内容
						{
							List<DsCmsCategory> _list = service.queryListCategory(siteid);
							DsCmsPermission permission = service.getPermission(siteid, getAccount());
							for(DsCmsCategory c : _list)
							{
								if(permission.checkPublish(c.getId()))
								{
									list.add(c);
								}
							}
						}
						else if(categoryid > 0)// 指定栏目内容
						{
							DsCmsCategory c = service.getCategory(categoryid);
							list.add(c);
						}
						for(DsCmsCategory c : list)
						{
							if(c.getScope() == 2)// 外链没有东西生成的
							{
								_deleteFile(site.getFolder(), c.getFolder(), true, true);
								continue;
							}
							_deleteFile(site.getFolder(), c.getFolder(), false, true);// 删除内容
							if(isCreateOrDelete)
							{
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("siteid", site.getId());
								map.put("releasetime", TimeUtil.getCurrentTime());
								map.put("categoryid", c.getId());
								PageRequest rq = new PageRequest(1, pagesize, map);
								Page<DsCmsPage> pageModel = service.queryPage(rq);
								for(DsCmsPage p : pageModel.getResult())
								{
									try
									{
										_buildFile(path + "&pageid=" + p.getId(), p.getUrl(), site.getFolder());
										service.updatePageStatus(p.getId(), 8);
									}
									catch (Exception e)
									{
									}
								}
								for(int i = 2; i <= pageModel.getLastPage(); i++)
								{
									map.clear();
									map.put("siteid", site.getId());
									map.put("releasetime", TimeUtil.getCurrentTime());
									map.put("categoryid", c.getId());
									rq.setFilters(map);
									rq.setPageSize(pagesize);
									rq.setCurrentPage(i);
									Page<DsCmsPage> n = service.queryPage(rq);
									for(DsCmsPage p : n.getResult())
									{
										try
										{
											_buildFile(path + "&pageid=" + p.getId(), p.getUrl(), site.getFolder());
											service.updatePageStatus(p.getId(), 8);
										}
										catch (Exception e)
										{
										}
									}
								}
							}
						}
						print("1");
					}
					else
					{
						print("0");
					}
				}
				else
				{
					print("0");
				}
			}
			else
			{
				print("0");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			print(isCreateOrDelete ? "0:生成失败" : "0:删除失败");
		}
	}
//	private void _building(boolean isCreateOrDelete, long siteid, long categoryid, long pageid, int pagesize)
//	{
//		pagesize = (pagesize<=0) ? 25 : pagesize;
//		try
//		{
//			if(siteid >= 0)
//			{
//				DsCmsSite site = service.getSite(siteid);
//				if(site != null)
//				{
//					site.setFolder(String.valueOf(site.getFolder()).replace("\\", "").replace("/", ""));
//				}
//				if(site != null && site.getFolder().trim().length() > 0 && checkOwn(site.getOwn()))
//				{
//					String path = "http://" + getLocalAddr() + ":" + request.getLocalPort() + request.getContextPath() + "/cms/page/buildHTML.chtml?siteid=" + siteid;
//					//首页：categoryid==-1，pageid==-1
//					//全部栏目：categoryid==0，pageid==-1
//					//指定栏目：categoryid>0，pageid==-1
//					//全部内容：categoryid==0，pageid==0
//					//栏目内容：categoryid>0，pageid==0
//					//指定内容：pageid>0
//					if(pageid > 0)
//					{
//						//TODO 查询指定的内容页
//					}
//					else
//					{
//						if(categoryid >= 0)
//						{
//							if(categoryid > 0)
//							{
//								//TODO 查询指定栏目
//							}
//							else if(categoryid == 0)
//							{
//								//TODO 查询全部栏目
//							}
//							if(pageid == 0)
//							{
//								//TODO 查询栏目下的内容页并清空栏目列表
//								//TODO 循环内容列表、生成并更新状态
//							}
//						}
//						else
//						{
//							if(pageid == -1)
//							{
//								//TODO 生成首页
//							}
//						}
//					}
//					//TODO 循环栏目列表、生成并更新状态
//				}
//				else
//				{
//					print("0");
//				}
//			}
//			else
//			{
//				print("0");
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			print(isCreateOrDelete ? "0:生成失败" : "0:删除失败");
//		}
//	}

	private void _deleteFile(String siteFolder, String categoryFolder, boolean deleteCategory, boolean deletePage)
	{
		// 这部分处理不当，整个站点的都会被删除
		if(siteFolder != null && siteFolder.trim().length() > 0 && categoryFolder != null && categoryFolder.trim().length() > 0)
		{
			java.io.File file = new java.io.File(getCmsRoot() + "/html/" + siteFolder + "/html/a/" + categoryFolder);
			if(file.exists())
			{
				for(java.io.File f : file.listFiles())
				{
					if(f.isDirectory())
					{
						FileUtil.delete(f.getPath());// 不应该存在目录
					}
					if(f.isFile())
					{
						if(f.getName().startsWith("index"))
						{
							if(deleteCategory)
							{
								f.delete();// 删除栏目首页
							}
						}
						else
						{
							if(deletePage)
							{
								f.delete();// 删除栏目内容
							}
						}
					}
				}
			}
		}
	}

	private void _buildFile(String path, String urlpath, String siteFolder)
	{
		try
		{
			String p = getCmsRoot().replaceAll("\\\\", "/") + "html/" + siteFolder + "/html/" + urlpath;
			if(path == null)
			{
				FileUtil.delete(p);
			}
			else
			{
				java.net.URL url = new java.net.URL(path);
				// java.net.URLConnection conn = url.openConnection();
				// conn.setRequestProperty("Cookie", cookie);
				// conn.connect();
				FileUtil.writeFile(p, url.openStream(), true);
			}
		}
		catch(Exception e)
		{
		}
	}

	private String getCmsRoot()
	{
		return request.getSession().getServletContext().getRealPath("/") + "/";
	}
	
	private String getLocalAddr()
	{
		String addr = request.getLocalAddr();
		if(addr != null && addr.indexOf(":") != -1)
		{
			addr = "[" + addr + "]";
		}
		return addr;
	}







	private boolean checkOwn(Long siteid)
	{
		try
		{
			return service.getSite(siteid).getOwn().equals(getOwn());
		}
		catch(Exception e)
		{
			return false;
		}
	}

	private boolean checkOwn(String own)
	{
		try
		{
			return own.equals(getOwn());
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	private String getOwn()
	{
		return common.auth.AuthUtil.getLoginUser(request).getOwn();
	}
	
	private String getAccount()
	{
		return common.auth.AuthUtil.getLoginUser(request).getAccount();
	}
}
