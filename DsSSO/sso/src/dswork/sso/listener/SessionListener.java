package dswork.sso.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import dswork.sso.service.AuthFactoryService;
import dswork.sso.service.TicketService;

public class SessionListener implements HttpSessionListener
{
	public final static String DS_SSO_TICKET = "DS_SSO_TICKET";
	public final static String DS_SSO_CODE = "DS_SSO_CODE";
	public void sessionCreated(HttpSessionEvent event)
	{
		System.out.println("sessionCreated:::" + event.getSession().getId());
	}

	public void sessionDestroyed(HttpSessionEvent event)
	{
		System.out.println("sessionDestroyed:::" + event.getSession().getId());
		try
		{
			HttpSession session = event.getSession();
			String ticket = String.valueOf(session.getAttribute(SessionListener.DS_SSO_TICKET));
			if(!ticket.equals("null") && ticket.length() > 0)
			{
				((AuthFactoryService)dswork.spring.BeanFactory.getBean(AuthFactoryService.class)).saveLogLogout(ticket, true, false);
				TicketService.removeSession(ticket);// 应该是超时
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
