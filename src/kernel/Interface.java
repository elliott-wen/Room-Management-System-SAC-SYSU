package kernel;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.netidVerfier.NetIdInfoModel;


@WebServlet(description = "Interfaces for getting information", urlPatterns = { "/Interface.do" }
,initParams={ 
		//@WebInitParam(name = "tempPath", value = "e:/projects/uploadfiles/temp"),
        //@WebInitParam(name = "basePath", value = "e:/projects/uploadfiles/")
		@WebInitParam(name = "tempPath", value = "/tmp/"),
        @WebInitParam(name = "basePath", value = "/home/wwwroot/uploadfiles/")
    })
public class Interface extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int signupDesktop=1;
	private static final int frontDesktop=2;
	private static final int midDesktop=3;
	private static final int backDesktop=5;
       
    public Interface() {
        super();
       
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.execute(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.execute(request, response);
	}
	protected void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//if(this.CheckAuthenication(request, response))//ͨ����֤��
		//{
			String action=request.getParameter("action");
			//System.out.println(action);
			if(action!=null&&!action.isEmpty())//��ʼ�ּ�����
			{
				if(action.equals("Login"))//��¼
				{
					if(RandomCode.CheckRandomCode(request, response)&&this.Authenication(request, response))
					{
						response.setContentType("application/xhtml+xml;charset=utf-8");
						PrintWriter writer=response.getWriter();
						writer.println("<?xml version='1.0' encoding='utf-8'?>\n<result>success</result>");
						writer.flush();
						int grade = (Integer)request.getSession().getAttribute("Class");
						if (grade >= midDesktop){
							if(!FunctionCore.CheckRoutineUpdated()){
								System.out.println("Routines updated.");
							}
							else
								System.out.println("Routines are up to date.");
						}
					}
					else
					{
						response.setContentType("application/xhtml+xml;charset=utf-8");
						PrintWriter writer=response.getWriter();
						writer.println("<?xml version='1.0' encoding='utf-8'?>\n<result>failed</result>");
						writer.flush();
					}
				}
				else if(action.equals("NetIdLogin"))//��¼
				{
					if(RandomCode.CheckRandomCode(request, response)&&this.NetIdLogin(request, response))
					{
						response.setContentType("application/xhtml+xml;charset=utf-8");
						PrintWriter writer=response.getWriter();
						writer.println("<?xml version='1.0' encoding='utf-8'?>\n<result>success</result>");
						writer.flush();
						
					}
					else
					{
						response.setContentType("application/xhtml+xml;charset=utf-8");
						PrintWriter writer=response.getWriter();
						writer.println("<?xml version='1.0' encoding='utf-8'?>\n<result>failed</result>");
						writer.flush();
					}
				}

				else if(action.equals("NetIdLogin2"))//��¼
				{
					if(this.NetIdLogin(request, response))
					{
						response.setContentType("application/xhtml+xml;charset=utf-8");
						PrintWriter writer=response.getWriter();
						writer.println("<?xml version='1.0' encoding='utf-8'?>\n<result>success</result>");
						writer.flush();
						
					}
					else
					{
						response.setContentType("application/xhtml+xml;charset=utf-8");
						PrintWriter writer=response.getWriter();
						writer.println("<?xml version='1.0' encoding='utf-8'?>\n<result>failed</result>");
						writer.flush();
					}
				}
				else if(action.equals("Logout"))//�뿪
				{
					LeaveAuthenication(request,response);
					response.setContentType("application/xhtml+xml;charset=utf-8");
					PrintWriter writer=response.getWriter();
					writer.println("<?xml version='1.0' encoding='utf-8'?>\n<result>success</result>");
					writer.flush();
				}
				else if(action.equals("GetRoomStatus"))//������ȡUsing���������,���ǵ�ǰ����ʹ�õķ���
				{
					
					if(this.CheckAuthenication(request, response, frontDesktop))
					FunctionCore.getRoomStatus(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("GetRoomStatus_noAuth"))//������ȡUsing���������,���ǵ�ǰ����ʹ�õķ���(����֤)
				{
					FunctionCore.getRoomStatus(request, response);
				}
				else if(action.equals("SearchRoomToOpen"))
				{
					if(this.CheckAuthenication(request, response, frontDesktop))
					FunctionCore.searchingRoomToOpen(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("OpenRoom"))
				{
					if(this.CheckAuthenication(request, response, frontDesktop))
					FunctionCore.openRoom(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("CloseRoom"))
				{
					if(this.CheckAuthenication(request, response, frontDesktop))
					FunctionCore.closeRoom(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("CheckExpired"))
				{
					if(this.CheckAuthenication(request, response, frontDesktop))
					FunctionCore.CheckExpired(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("GetPost"))
				{
					if(this.CheckAuthenication(request, response, signupDesktop))
					FunctionCore.GetPost(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("SetPost"))
				{
					if(this.CheckAuthenication(request, response, backDesktop))
					FunctionCore.SetPost(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("CheckApplyStatus"))
				{
					if(RandomCode.CheckRandomCode(request, response)&&this.CheckAuthenication(request, response, signupDesktop))
					FunctionCore.CheckApplyStatus(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("GetRandomCode"))//�����ж�Ȩ��
				{
					RandomCode.GetRandomCode(request, response);
				}
				else if(action.equals("GetRoomList"))
				{
					if(this.CheckAuthenication(request, response, signupDesktop))
					FunctionCore.GetRoomList(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("UploadFile"))
				{
					if(this.CheckAuthenication(request, response, signupDesktop))
					FunctionCore.UploadFile(request, response,this.getInitParameter("tempPath"),this.getInitParameter("basePath"));
					else response.sendError(505);//
				}
				else if(action.equals("Signup"))
				{
					if(RandomCode.CheckRandomCode(request, response)&&this.CheckAuthenication(request, response, signupDesktop))
					FunctionCore.Signup(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("Signup2"))
				{
					if(RandomCode.CheckRandomCode(request, response)&&this.CheckAuthenication(request, response, signupDesktop))
					FunctionCore.Signup2(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("GetApplyList"))
				{
					String roomname=request.getParameter("roomname");
					int auth;
					if(roomname.equals("�Ԥ��"))
						auth = midDesktop;
					else
						auth = backDesktop;
					if(this.CheckAuthenication(request, response, auth))
						FunctionCore.GetApplyList(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("PermitApply"))
				{
					String roomname=request.getParameter("roomname");
					int auth;
					if(roomname.equals("�Ԥ��"))
						auth = midDesktop;
					else
						auth = backDesktop;
					if(this.CheckAuthenication(request, response, auth))
					FunctionCore.PermitApply(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("RejectApply"))
				{
					String roomname=request.getParameter("roomname");
					int auth;
					if(roomname.equals("�Ԥ��"))
						auth = midDesktop;
					else
						auth = backDesktop;
					if(this.CheckAuthenication(request, response, auth))
					FunctionCore.RejectApply(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("InsertBlockUser"))
				{
					if(this.CheckAuthenication(request, response, backDesktop))
					FunctionCore.InserBlockUser(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("GetAllApply"))
				{
					if(this.CheckAuthenication(request, response, signupDesktop))
					FunctionCore.GetAllApply(request, response);
					else response.sendError(505);//
				}
				else if(action.equals("RemoveApplyStatus"))
				{
					
					if(RandomCode.CheckRandomCode(request, response)&&this.CheckAuthenication(request, response, signupDesktop))
					FunctionCore.RemoveApplyStatus(request, response);
					else response.sendError(505);
						
				}
				else
				{
					System.out.println("δ��������:"+action);
					response.sendError(500,"Undefined Action");
				}
			}
			else
			{
				System.out.println("��������ȷ��δָ������");
				response.sendError(500,"Unknown the attetion");
			}
		
		/*else//û��ͨ������Ҫ��֤
		{
			if(this.Authenication(request, response))
			{
				response.getWriter().println("Successful");
			}
			else
			{
				response.getWriter().println("Failed");
			}
		}*/
	}
	protected boolean Authenication(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
			
		    HttpSession s=request.getSession();
			Connection c=null;
			Statement st=null;
			ResultSet rt=null;
			String username=request.getParameter("username");
			String password=request.getParameter("password");
			if(username==null&&password==null)
			{
				return false;
			}
			try
			{
						
					    c=DatabasePool.getConnection();
					    st=c.createStatement();
						String queryString="select * from `adminuser` where `password`=sha('"+password+"') " +
						"and `username`= '"+username+"'";
					
						rt=st.executeQuery(queryString);
						if(rt.next())
						{
								s.setAttribute("LoginFlag", "VAILD");
								s.setAttribute("Class", rt.getObject("Grade"));
								s.setAttribute("Username",username);
								FunctionCore.Log4Action(request, response, "�û���¼ϵͳ",FunctionCore.LoginEvent);
								return true;
						}
						else
						{
							s.invalidate();
							System.out.println("Wrong Password");
							FunctionCore.Log4Action(request, response, "�û���¼ϵͳ����ʧ����",FunctionCore.LoginEvent);
							return false;
						}
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
				return false;
			}
			finally//�رմ���
			{
				try
				{
					
					if(rt!=null)
						rt.close();
					if(st!=null)
						st.close();
					if(c!=null)
						c.close();
					
				}
				catch(Exception e)
				{
					
				}
			}
	}
		
	
	protected void LeaveAuthenication(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession s=request.getSession();
		if(s!=null)
		s.invalidate();
	}
	protected boolean CheckAuthenication(HttpServletRequest request, HttpServletResponse response,int requireLevel) throws ServletException, IOException 
	{
		HttpSession s=request.getSession();
		String LoginFlag=(String)s.getAttribute("LoginFlag");
		if(LoginFlag!=null&&LoginFlag.equals("VAILD"))//�ѷ��ϵ�¼
		{
		
				int level=(Integer)s.getAttribute("Class");
				if(level>=requireLevel)
				{
				return true;
				}
				else
					{
					FunctionCore.Log4Action(request, response, "����ԽȨ",FunctionCore.OtherEvent);
					System.out.println("Ȩ�޴���");
					return false;
					}
			
		}
		else
		{
			
			return false;
		}
	}
	protected boolean NetIdLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		
		HttpSession s=request.getSession();
	    if(!s.isNew())
	    {
	    	s.invalidate();//ɾ���ϴε�session
	    	s=request.getSession();
	    }
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		if(username==null&&password==null)
		{
			return false;
		}
		if(CheckBlockUsers(request, response)){
			System.out.println("�����ʻ���"+username);
			return false;
		}
		NetIdInfoModel verifer=new NetIdInfoModel(username,password);
		if(verifer.Login())
		{
			s.setAttribute("LoginFlag", "VAILD");
			s.setAttribute("Class", signupDesktop);
			s.setAttribute("Username",username);
			//FunctionCore.Log4Action(request, response, "�û���¼����ϵͳ,����Ϊ"+password,FunctionCore.GuestLoginEvent);
			return true;
		}
		else{
			//FunctionCore.Log4Action(request, response, "�û���¼����ϵͳʧ��,����Ϊ"+password,FunctionCore.GuestLoginEvent);
			return false;    
		}
	}
	private boolean CheckBlockUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		Connection c=null;
		Statement st=null;
		ResultSet rt=null;
		String username=request.getParameter("username");
		//String IP=request.getRemoteHost();
		try
		{
					
				    c=DatabasePool.getConnection();
				    st=c.createStatement();
				    String sql="select * from `blockusers` where `Username`='"+username+"' and `blockdetail`='����';";
				    System.out.println(sql);
				    rt=st.executeQuery(sql);
				    if(rt.next())
				    {
				    	System.out.println("������");
				    	return true;
				    	
				    }
				    else
				    {
				    	return false;
				    }
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			return false;
		}
		finally//�رմ���
		{
			try
			{
				
				if(rt!=null)
					rt.close();
				if(st!=null)
					st.close();
				if(c!=null)
					c.close();
			}
			catch(Exception e)
			{
				
			}
		}
	}
	private boolean IsLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession s=request.getSession();
		return(!s.isNew());
	}
}
