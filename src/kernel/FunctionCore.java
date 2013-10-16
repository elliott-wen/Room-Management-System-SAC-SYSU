package kernel;


import java.io.File;
import java.io.IOException;


import java.io.PrintWriter;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;





public class FunctionCore { 
	static public final int LoginEvent=1;
	static public final int GuestLoginEvent=2;
	static public final int GuestUploadEvent=3;
	static public final int GuestSignupEvent=4;
	static public final int AtOpenRoomEvent=5;
	static public final int AtCloseRoomEvent=6;
	static public final int AdminPermitRoomEvent=7;
	static public final int AdminRejectRoomEvent=8;
	static public final int AdminUpdatePostEvent=8;
	static public final int GuestRemoveApplyEvent=9;
	static public final int OtherEvent=9;
	static public void  getRoomStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException//两个参数一个是显示页面，一个是显示的row数
	{
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		int rows=10;
		int pages=1;
		int totalpages=1;
		String sidx=null;
		String rowsinString=(request.getParameter("rows"));
		String pagesinString=(request.getParameter("page"));
		String sidxinString=(request.getParameter("sidx"));
		String sord=(request.getParameter("sord"));
		try
		{
			if(rowsinString!=null) rows=Integer.valueOf(rowsinString);
			if(pagesinString!=null) pages=Integer.valueOf(pagesinString);
			if(sidxinString==null) sidx="1";else sidx=sidxinString;
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			rt=st.executeQuery("select COUNT(*) AS `count` from `using` where `avail`=1");//获取排树
			rt.next();
			int totalrows=rt.getInt("count");
			rt.close();
			if(totalrows>0)
			{
				totalpages=(int)Math.ceil(((double)totalrows/rows));
				/*System.out.println(totalrows);
				System.out.println(rows);
				System.out.println(totalpages);
				System.out.println(pages);*/
			}
			if(pages>totalpages)
			{
				pages=totalpages;
			}
			
			int start=rows*pages-rows;
			//System.out.println("select * from `using` limit "+start+","+rows);
			rt=st.executeQuery("select * from `using` where `avail`=1 ORDER BY `"+sidx+"` "+sord+"  limit "+start+","+rows);
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<rows>");
			writer.println("<page>"+pages+"</page>");
			writer.println("<total>"+totalpages+"</total>");
			writer.println("<records>"+totalrows+"</records>");
			while(rt.next())
			{
				writer.println("<row>");
				writer.println("<cell><![CDATA["+rt.getString("roomname")+"]]></cell>");
				writer.println("<cell><![CDATA["+rt.getString("username")+"]]></cell>");
				writer.println("<cell><![CDATA["+rt.getString("studentid")+"]]></cell>");
				writer.println("<cell><![CDATA["+rt.getString("partyname")+"]]></cell>");
				writer.println("<cell><![CDATA["+rt.getString("people")+"]]></cell>");
				writer.println("<cell><![CDATA["+rt.getString("date")+"]]></cell>");
				writer.println("<cell><![CDATA["+rt.getString("stime")+"]]></cell>");
				writer.println("<cell><![CDATA["+rt.getString("etime")+"]]></cell>");
				
				writer.println("</row>");
			}
			writer.println("</rows>");
			writer.flush();
			
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when get room status");
		}
		finally
		{
			try{
			if(writer!=null) writer.close();
			if(ct!=null) ct.close();
			if(st!=null) st.close();
			if(rt!=null) rt.close();
			}catch(Exception e)
			{
			}
		}
	}
	static public void searchingRoomToOpen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException

	{
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			String sql=null;
			String studentId=request.getParameter("studentId");
			String signupId=request.getParameter("signupId");
			String roomname=request.getParameter("roomname");
			if(roomname==null||signupId==null||studentId==null)//参数错误
			{
				response.sendError(500,"Error when open room where the parameter is invaild");
				return;
			}
			ct=DatabasePool.getConnection();
			//======获取表的名称
			st=ct.createStatement();
			sql="select `roomtable` from `using` where `roomname`='"+roomname+"'";
			rt=st.executeQuery(sql);
			rt.next();
			String tablename=rt.getString("roomtable");
			rt.close();
			//=====准备查表=====//构造查找字符串
			
			Date curDate=new Date();
			Timestamp curTime=new Timestamp(curDate.getTime());
			
		    sql="select * from `"+tablename+"` where `status`='pass' and (`signupId`='"+signupId+"' and `studentId`='"+studentId+"') and `etime`>'"+curTime.toString()+"'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			//=======查表看看有没有结果
			//====有结果输出xml类型的数据，方便处理====
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			//System.out.println("Debug 153 T "+rt.getRow());
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			if(!rt.next())//无结果直接输出XML结束符
			{
				writer.println("<result>none</result>");
				System.out.println("没有任何内容");
			}
			else//有结果
			{
				System.out.println("准备输出");
				writer.println("<result>");
				writer.println("<signupId>"+rt.getString("signupId")+"</signupId>");
				writer.println("<studentId>"+rt.getString("studentId")+"</studentId>");
				writer.println("<name>"+rt.getString("name")+"</name>");
				writer.println("<tel>"+rt.getString("tel")+"</tel>");
				writer.println("<partyname>"+rt.getString("partyname")+"</partyname>");
				writer.println("<num>"+rt.getString("num")+"</num>");
				writer.println("<stime>"+rt.getString("stime")+"</stime>");
				writer.println("<etime>"+rt.getString("etime")+"</etime>");
				writer.println("<advice>"+rt.getString("advice")+"</advice>");
				writer.println("</result>");
			}
			writer.flush();
			//===========================
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when ready open room");
		}
		finally
		{
			try{
			if(writer!=null) writer.close();
			if(ct!=null) ct.close();
			if(st!=null) st.close();
			if(rt!=null) rt.close();
			}catch(Exception e)
			{
			}
		}
	}
	static public void openRoom(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//先查roomtable，再判断update合法性，最后select，写using
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			String sql=null;
			String signupId=request.getParameter("signupId");
			String roomname=request.getParameter("roomname");
			if(roomname==null||(signupId==null))//参数错误
			{
				response.sendError(500,"Error when open room(stage2) where the parameter is invaild");
				return;
			}
			//===开始检查是否可以开放
			ct=DatabasePool.getConnection();
			//======获取表的名称
			st=ct.createStatement();
			sql="select `roomtable` from `using` where `roomname`='"+roomname+"'";
			rt=st.executeQuery(sql);
			rt.next();
			String tablename=rt.getString("roomtable");
			rt.close();
			//=====准备查表=====//构造sql字符串
			
			Date curDate=new Date();
			
			Timestamp curTime=new Timestamp(curDate.getTime());
			sql="update  `"+tablename+"` set `status`='using' where `status`='pass' and `signupId`='"+signupId+"' and `etime`>'"+curTime.toString()+"'";
			System.out.println(sql);
			int flag=st.executeUpdate(sql);
			if(flag==0)//如果flag=0，说明没有可以开放的房间，可能是恶意攻击。
			{
				response.sendError(505,"Error that is not such room");
				return;
			}
			//===拷贝到using里面，其实如果有更好的方法我们可以不用这样拷贝，求大神=====
			sql="select * from  `"+tablename+"` where `signupId`='"+signupId+"'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			sql="update `using` set `username`='"+rt.getString("name")+
			"' ,`partyname`='"+rt.getString("partyname")+
			"' , `stime`='"+rt.getString("stime")+
			"' , `etime`='"+rt.getString("etime")+
			"' , `studentid`='"+rt.getString("studentId")+
			"' , `people`='"+rt.getString("num")+
			"' , `date`='"+rt.getString("usedate")+
			"' , `signupid`='"+signupId+"' where `roomname`='"+roomname+"'";
			rt.close();
			System.out.println(sql);
			st.executeUpdate(sql);
			//====有结果输出xml类型的数据，方便处理====
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<result>success</result>");
			Log4Action(request,response,"Open room "+roomname+", whose requisition number is "+signupId,AtOpenRoomEvent);
			writer.flush();
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when open room");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			
			}
		}
		
	}
	static public void closeRoom(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//读检查using是否合法，读出signupid和roomtable，删除using，写status=为finished
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			String sql=null;
			String roomname=request.getParameter("roomname");
			String aid=request.getParameter("aid");
			String note=request.getParameter("note");
			String broke=request.getParameter("broke");
			if(roomname==null||aid==null||note==null)//参数错误
			{
				response.sendError(500,"Error when close room where the parameter is invaild");
				return;
			}
			//===开始检查是否可以关闭
			ct=DatabasePool.getConnection();
			//======检查是否已经开启的名称
			st=ct.createStatement();
			//System.out.println("select `roomtable` from `using` where `roomname`='"+roomname+"'");
			
			sql="select * from `using` where `roomname`='"+roomname+"'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			if(rt.getString("username")==null)
			{
				response.sendError(500,"Error when close room that the room is closed");
				return;
			}
			String roomtable=rt.getString("roomtable");
			String signupId=rt.getString("signupid");
			String usernamefordealbroke=rt.getString("username");
			String partynamefordealbroke=rt.getString("partyname");
			String studentidfordealbroke=rt.getString("studentid");
			Date curDatedealbroke=new Date();
			Timestamp curTimedealbroke=new Timestamp(curDatedealbroke.getTime());
			rt.close();
			//=====准备查表=====//构造查找字符串
			
			sql="update `using`  set `username`='' , `partyname`='' , `stime`='' , `etime`='' ,`studentid`='',`people`='' , `date`='' , `people`='', `signupid`='' " +
					"where `roomname`='"+roomname+"'"; 
			System.out.println(sql);
			st.executeUpdate(sql);
			sql="update `"+roomtable+"` set status='finished',`aid`='"+aid+"' ,`feedback`='"+note+"' where `signupId`='"+signupId+"'";
			System.out.println(sql);
			st.executeUpdate(sql);
			//=================
			if(broke!=null&&!broke.isEmpty())
			{
				sql=String.format("insert into `blockusers` (`Username`,`studentid`,`partyname`,`blockdetail`,`blocktime`) " +
						"values ('%s','%s','%s','%s','%s')", 
						usernamefordealbroke,studentidfordealbroke,partynamefordealbroke,note,curTimedealbroke.toString());
				System.out.println(sql);
				st.executeUpdate(sql);
			}
			//=======查表看看有没有结果
			//====有结果输出xml类型的数据，方便处理====
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<result>success</result>");
			Log4Action(request,response,"Close room "+roomname+", whose requisition number is "+signupId, AtCloseRoomEvent);
			writer.flush();
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when close room");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			
			}
		}
	}
	
	static public void Log4Action(HttpServletRequest request, HttpServletResponse response,String event,int eventLevel) throws ServletException, IOException
	{
		Connection ct=null;
		Statement st=null;
		
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			HttpSession s=request.getSession();
			String Username=(String)s.getAttribute("Username");
			Date curDate=new Date();
			Timestamp curTime=new Timestamp(curDate.getTime());
			
			String sql="INSERT INTO `log` (`operator`, `reason`, `ip`, `time`,`level`) VALUES ('"
			+Username+"', '"+event+"','"+request.getRemoteHost()+"', '"+curTime.toString()+"','"+eventLevel+"')";
			System.out.println(sql);
			st.executeUpdate(sql);
		}
		catch(Exception ex)
		{
			System.out.println("Error when writing logs");
			System.out.println(ex.getMessage());
		}
		finally
		{
			try{
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				
				}
			catch(Exception e)	
			{
			}
		}
	}
	static public void CheckExpired(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			String sql=null;
			Date curDate=new Date();
			Timestamp curTime=new Timestamp(curDate.getTime());
			sql="select * from `using` where `avail`=1 and `username`!='' and STR_TO_DATE(`etime`,'%Y-%m-%d %k:%i:%s')<'"+curTime.toString()+"';";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			
			if(rt.next())
				
			{
				writer.println("<expired-list>");
				do{
					writer.println("<expired-room>"+rt.getString("roomname")+"</expired-room>");
				}while(rt.next());	
				writer.println("</expired-list>");
			}
			else
			{
				System.out.println("do not expire");
				writer.println("<expired-list>none</expired-list>");
			}
			
			writer.flush();
		}	
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when check expired room");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			
			}
		}
	}
	static public void GetRoomList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			String sql=null;
			String Prosperity=request.getParameter("Prosperity");//kind表示显示类型，一般默认为0，显示全部为1
			if(Prosperity==null)
			{
				sql="select * from `using` where `avail`=1";
			}
			else
			{
				if(Prosperity.equals("all"))
				{
				   sql="select * from `using`";
				}
				else if(Prosperity.equals("open"))
				{
					sql="select * from `using` where `avail`=1";
				}
				else if(Prosperity.equals("close"))
				{
					sql="select * from `using` where `avail`=0";
				}
			}
			System.out.println(sql);
			rt=st.executeQuery(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<roomlist>");
			while(rt.next())
			{	
				writer.println("<roomname>"+rt.getString("roomname")+"</roomname>");
			}
			writer.println("</roomlist>");
			writer.flush();
		}	
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when check expired room");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			
			}
		}
	}
	static public void Signup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			
			String sql=null;
			String studentId=request.getParameter("studentId");
			String phone=request.getParameter("phone");
			String roomname=request.getParameter("roomname");
			String studentname=request.getParameter("studentname");
			String email=request.getParameter("email");
			String party=request.getParameter("party");
			String num=request.getParameter("num");
			String date=request.getParameter("date");
			String stime=request.getParameter("stime");
			String etime=request.getParameter("etime");
			String note=request.getParameter("note");
			String content=request.getParameter("content");
			HttpSession s=request.getSession();
			String username=(String)s.getAttribute("Username");//用来记录Netid
			if(roomname==null||studentname==null||phone==null||studentId==null||email==null||date==null||stime==null||etime==null||content==null||
					roomname.isEmpty()||studentname.isEmpty()||studentId.isEmpty()||phone.isEmpty()||date.isEmpty()||stime.isEmpty()||etime.isEmpty())//参数错误
			{
				response.sendError(500,"Error when open room where the parameter is invaild");
				return;
			}
			//===判断时间的合法性
			int startTime=Integer.valueOf(stime);
			int endTime;
			if (etime.equals("22.5"))
				endTime=22;
			else
				endTime=Integer.valueOf(etime);
			if(startTime>=endTime)
			{
				response.sendError(500,"Error when open room where the parameter is invaild");
				return;
			}
			//==构造时间==
			
			Date StartDate=null;
			Date EndDate=null;
			String StartString=date+" "+stime+":00:00";
			String EndString = date+" "+etime+":00:00";
			if (etime.equals("22.5"))
				EndString = date+" 22:30:00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			StartDate=sdf.parse(StartString);
			EndDate=sdf.parse(EndString);
			Timestamp startTimestamp=new Timestamp(StartDate.getTime());
			Timestamp endTimestamp=new Timestamp(EndDate.getTime());
			Date curDate=new Date();
			Timestamp curTime=new Timestamp(curDate.getTime());
			//===============
			
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			sql="select * from `using` where `roomname`='"+roomname+"'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			String roomtable=rt.getString("roomtable");
			rt.close();
			//=======判断是否已经有人通过审批=======
			sql=String.format("select * from `%s` where `status`='pass'"
					+ "and ("
						+ "(HOUR(`etime`)>'%s' and HOUR(`stime`)<='%s')"
						+ "or (HOUR(`etime`)>='%s' and HOUR(`stime`)<'%s')" 
						+ "or (HOUR(`etime`)<='%s' and HOUR(`stime`)>='%s')"
						+ ")"
					+ "and `usedate`='%s'"
					, roomtable, stime, stime, etime, etime, etime, stime, date);
			System.out.println(sql);
			rt=st.executeQuery(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			if(rt.next())
			{
				writer.println("<report>");
				writer.println("<result>failed</result>");
				writer.println("</report>");
			}
			else
			{
			//=========
				rt.close();
				sql="INSERT INTO `"+roomtable+"` (`advice`,`aid`,`feedback`,`status`,`studentId`," +
						" `name`, `tel`, `email`,`partyname`,`num`,`usedate`,`content`," +
						"`stime`,`etime`,`signupTime`,`NETID`,`note`)" +
						" VALUES ('','','','pending','"+studentId+"','"+studentname+"','"+phone+"','"
						+email+"','"+party+"','"+num+"','"+date+"','"+content+"','"
						+startTimestamp.toString()+"','"+endTimestamp.toString()+"','"+curTime.toString()+"','"
						+username+"','"+note+"');";
				System.out.println(sql);
				st.executeUpdate(sql);
				rt=st.getGeneratedKeys();
				rt.next();
				writer.println("<report>");
				writer.println("<result>success</result>");
				writer.println("<ID>"+rt.getInt(1)+"</ID>");
				writer.println("</report>");
				SendMail.SendSignupMail(email, rt.getString(1), roomname, startTimestamp.toString(), endTimestamp.toString());
			}
			writer.flush();
			FunctionCore.Log4Action(request, response, "提交了申请",FunctionCore.OtherEvent);
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when signup room");
		}
		finally
		{
			try{
			if(writer!=null) writer.close();
			if(ct!=null) ct.close();
			if(st!=null) st.close();
			if(rt!=null) rt.close();
			}catch(Exception e)
			{
			}
		}
	}

	static public void Signup2(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			
			String sql=null;
			String roomname="活动预告";
			String studentId=request.getParameter("studentId");
			String phone=request.getParameter("phone");
			String studentname=request.getParameter("studentname");
			String email=request.getParameter("email");
			String party=request.getParameter("party");
			String num= "0";
			String date=request.getParameter("date");
			String note= "活动预告。\n视频地址："+request.getParameter("url");
			String content=request.getParameter("content");
			HttpSession s=request.getSession();
			String username=(String)s.getAttribute("Username");//用来记录Netid
			if(roomname==null||studentname==null||phone==null||studentId==null||email==null||date==null||content==null||
					roomname.isEmpty()||studentname.isEmpty()||studentId.isEmpty()||phone.isEmpty()||date.isEmpty())//参数错误
			{
				response.sendError(500,"Error when open room where the parameter is invaild");
				return;
			}
			//==构造时间==
			
			Date StartDate=null;
			Date EndDate=null;
			String StartString=date+" 00:00:00";
			String EndString = date+" 00:00:00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			StartDate=sdf.parse(StartString);
			EndDate=sdf.parse(EndString);
			Timestamp startTimestamp=new Timestamp(StartDate.getTime());
			Timestamp endTimestamp=new Timestamp(EndDate.getTime());
			Date curDate=new Date();
			Timestamp curTime=new Timestamp(curDate.getTime());
			//===============
			
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			sql="select * from `using` where `roomname`='"+roomname+"'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			String roomtable=rt.getString("roomtable");
			rt.close();
				sql="INSERT INTO `"+roomtable+"` (`advice`,`aid`,`feedback`,`status`,`studentId`," +
						" `name`, `tel`, `email`,`partyname`,`num`,`usedate`,`content`," +
						"`stime`,`etime`,`signupTime`,`NETID`,`note`)" +
						" VALUES ('','','','pending','"+studentId+"','"+studentname+"','"+phone+"','"
						+email+"','"+party+"','"+num+"','"+date+"','"+content+"','"
						+startTimestamp.toString()+"','"+endTimestamp.toString()+"','"+curTime.toString()+"','"
						+username+"','"+note+"');";
				System.out.println(sql);
				st.executeUpdate(sql);
				rt=st.getGeneratedKeys();
				rt.next();

				response.setContentType("application/xhtml+xml;charset=utf-8");
				writer=response.getWriter();
				writer.println("<?xml version='1.0' encoding='utf-8'?>");
				writer.println("<report>");
				writer.println("<result>success</result>");
				writer.println("<ID>"+rt.getInt(1)+"</ID>");
				writer.println("</report>");
				SendMail.SendSignupMail(email, rt.getString(1), roomname, startTimestamp.toString(), endTimestamp.toString());
			writer.flush();
			FunctionCore.Log4Action(request, response, "提交了申请",FunctionCore.OtherEvent);
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when signup room");
		}
		finally
		{
			try{
			if(writer!=null) writer.close();
			if(ct!=null) ct.close();
			if(st!=null) st.close();
			if(rt!=null) rt.close();
			}catch(Exception e)
			{
			}
		}
	}
	static public void CheckApplyStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
		
			String sql=null;
			String studentId=request.getParameter("studentId");
			String signupId=request.getParameter("signupId");
			String roomname=request.getParameter("roomname");
			HttpSession s=request.getSession();
			String username=(String)s.getAttribute("Username");
			if(roomname==null||signupId==null||studentId==null)//参数错误
			{
				response.sendError(500,"Error when open room where the parameter is invaild");
				return;
			}
			ct=DatabasePool.getConnection();
			//======获取表的名称
			st=ct.createStatement();
			sql="select `roomtable` from `using` where `avail`=1 and `roomname`='"+roomname+"'";
			rt=st.executeQuery(sql);
			rt.next();
			String tablename=rt.getString("roomtable");
			rt.close();
			//=====准备查表=====//构造查找字符串
		    sql="select * from `"+tablename+"` where (`signupId`='"+signupId+"' and `studentId`='"+studentId+"') and `NETID`='"+username+"';";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			//=======查表看看有没有结果
			//====有结果输出xml类型的数据，方便处理====
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			//System.out.println("Debug 153 T "+rt.getRow());
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			if(!rt.next())//无结果直接输出XML结束符
			{
				writer.println("<result>none</result>");
				System.out.println("准备输出空");
			}
			else//有结果
			{
				System.out.println("准备输出");
				writer.println("<result>");
				writer.println("<signupId>"+rt.getString("signupId")+"</signupId>");
				writer.println("<studentId>"+rt.getString("studentId")+"</studentId>");
				writer.println("<name>"+rt.getString("name")+"</name>");
				writer.println("<tel>"+rt.getString("tel")+"</tel>");
				writer.println("<partyname>"+rt.getString("partyname")+"</partyname>");
				writer.println("<num>"+rt.getString("num")+"</num>");
				writer.println("<stime>"+rt.getString("stime")+"</stime>");
				writer.println("<etime>"+rt.getString("etime")+"</etime>");
				writer.println("<advice>"+rt.getString("advice")+"</advice>");
				//===翻译中文输出结果
				String statusString=rt.getString("status");
				String resultString=null;
				if(statusString.equals("pending"))
					resultString="Unused";
				else if(statusString.equals("rejected"))
					resultString="Rejected";
				else if(statusString.equals("pass"))
					resultString="Pass";
				else resultString="Cancled";
				writer.println("<status>"+resultString+"</status>");
				writer.println("</result>");
			}
			writer.flush();
			//===========================
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when ready to open the room");
		}
		finally
		{
			try{
			if(writer!=null) writer.close();
			if(ct!=null) ct.close();
			if(st!=null) st.close();
			if(rt!=null) rt.close();
			}catch(Exception e)
			{
			}
		}
	}
	static public void RemoveApplyStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
		
			String sql=null;
			String studentId=request.getParameter("studentId");
			String signupId=request.getParameter("signupId");
			String roomname=request.getParameter("roomname");
			HttpSession s=request.getSession();
			String username=(String)s.getAttribute("Username");
			if(roomname==null||signupId==null||studentId==null)//参数错误
			{
				response.sendError(500,"Error when remove room where the parameter is invaild");
				
				return;
			}
			ct=DatabasePool.getConnection();
			//======获取表的名称
			st=ct.createStatement();
			sql="select `roomtable` from `using` where `avail`=1 and `roomname`='"+roomname+"'";
			rt=st.executeQuery(sql);
			rt.next();
			String tablename=rt.getString("roomtable");
			rt.close();
			//=====准备查表=====//构造查找字符串
		    sql="update `"+tablename+"` set `status`='pendingX' where (`signupId`='"+signupId+"' and `studentId`='"+studentId+"') and `NETID`='"+username+"' and (`status`='pending' or `status`='pass');";
			System.out.println(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			//System.out.println("Debug 153 T "+rt.getRow());
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			if(st.executeUpdate(sql)==0)
			//=======查表看看有没有结果
			//====有结果输出xml类型的数据，方便处理====
			{
				writer.println("<result>failed</result>");
				System.out.println("准备输出空");
			} 
			else//有结果
			{
				writer.println("<result>success</result>");
				System.out.println("Prepare to output \"Success\"");
				Log4Action(request,response,"cancle booking "+roomname+"/"+signupId,GuestRemoveApplyEvent);
			}
			writer.flush();
			
			//===========================
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when remove open room");
		}
		finally
		{
			try{
			if(writer!=null) writer.close();
			if(ct!=null) ct.close();
			if(st!=null) st.close();
			if(rt!=null) rt.close();
			}catch(Exception e)
			{
			}
		}
	}
	static public void GetPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			String sql=null;
			String prosperity=request.getParameter("prosperity");
			if(prosperity!=null)
			{
			if(prosperity.equals("public"))//公共的
				sql="select * from `post` where `key`='public'";
			else if (prosperity.equals("private"))
				sql="select * from `post` where `key`='private'";//私人的
			}
			else sql="select * from `post` where `key`='public'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<Post>");
			if(rt.next())
			{	
				writer.println("<Text>"+rt.getString("Message")+"</Text>");
			}
			writer.println("</Post>");
			writer.flush();
		}	
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when check expired room");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			
			}
		}
	}
	static public void SetPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			String sql=null;
			String content=request.getParameter("content");
			String prosperity=request.getParameter("prosperity");
			if(content==null||content.equals(""))
			{
				response.sendError(500,"Error when SetPost");
				return;
			}
			
			if(prosperity!=null)
			{//公共的
				if(prosperity.equals("public"))
				sql=String.format("update `post` set `Message`='%s' where `key`='public'",content);
			    else if(prosperity.equals("private")) 
				sql=String.format("update `post` set `Message`='%s' where `key`='private'",content);
			}
			else sql=String.format("update `post` set `Message`='%s' where `key`='public'",content);
			System.out.println(sql);
			st.executeUpdate(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<result>success</result>");
			writer.flush();
			Log4Action(request,response,"Update the proclamation",AdminUpdatePostEvent);
		}	
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when SetPost");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			}
		}
	}
	public static void InserBlockUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			String sql=null;
			String roomname=request.getParameter("roomname");
			String signupId=request.getParameter("signupId");
			if(roomname==null||signupId==null)
			{
				response.sendError(500,"Error when GetApplyList room");
				return;
			}
			//==读取常规的资料
			sql="select * from `using` where `roomname`='"+roomname+"'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			String roomtable=rt.getString("roomtable");
			rt.close();
			sql="select * from `"+roomtable+"` where `signupId`='"+signupId+"';";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			String NETID=rt.getString("NETID");
			sql="select * from `blockusers` where `Username`='"+NETID+"';";
			rt.close();
			System.out.println(sql);
			rt=st.executeQuery(sql);
			if(!rt.next())
			{
			sql=String.format("INSERT INTO `blockusers` (`Username`,`IP`) values('%s',''); ",NETID);
			System.out.println(sql);
			st.executeUpdate(sql);
			}
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<result>success</result>");
			writer.flush();
			
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when InsertBlockUser");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			
			}
		}
    }
	static public void UploadFile(HttpServletRequest request, HttpServletResponse response,String tempPath,String basePath) throws ServletException, IOException
	{
		//开始处理上传文件
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart)
		{
			String absfilePath=null;
			String filePath=null;
			PrintWriter writer=null;
			try
			{
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setRepository(new File(tempPath));
				ServletFileUpload upload = new ServletFileUpload(factory);
				upload.setSizeMax(2072576);
				List < FileItem > items = upload.parseRequest(request);
				Iterator < FileItem > iter = items.iterator();
				while (iter.hasNext()) 
				{
				    FileItem item = (FileItem) iter.next();
				    if (!item.isFormField()) 
				    {
				    	
				    	String fileType = item.getName();
				    	if(fileType.endsWith(".rar")||fileType.endsWith(".doc")||fileType.endsWith(".docx")||fileType.endsWith(".jpg")) 
				    	{
				    		filePath=getRandomString(16)+fileType.substring(fileType.lastIndexOf("."));
				    		absfilePath=basePath+filePath;
				    		System.out.println(absfilePath);
				    		item.write(new File(absfilePath));
				    	}
				    	else
				    	{
				    		response.sendError(505,"Error when uploading file in unknown type");
							System.out.println("Wrong upload file type");
							return; 
				    	}
				    }
				}
				if(absfilePath!=null)//文件上传完毕
				{
					Log4Action(request,response,"upload file:"+filePath,GuestUploadEvent);
					response.setContentType("text/html;charset=utf-8");
					writer=response.getWriter();
					writer.println("<?xml version='1.0' encoding='utf-8'?>");
					writer.println("<report>");
					writer.println("<result>ok</result>");
					writer.println("<uploadfile>"+filePath+"</uploadfile>");
					writer.println("</report>");
					writer.flush();
				}
			}
			catch(Exception e)
			{
				response.sendError(505,"Error when uploading file too large or something else");
				System.out.println("Error when updating file "+e.getMessage());
			}
		}
		else
		{
			response.sendError(505,"Error when uploading file in unknown way");
			System.out.println("Unknow upload method");
		}
		
	}
	private static String getRandomString(int length) { //length表示生成字符串的长度
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	 }  
    
	public static void GetApplyList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		Statement dst=null;
		ResultSet drt=null;
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			String sql=null;
			String roomname=request.getParameter("roomname");
			String prosperity=request.getParameter("prosperity");
			String date=request.getParameter("date");
			if(roomname==null||prosperity==null)
			{
				response.sendError(500,"Error when GetApplyList room");
				return;
			}
			sql="select * from `using` where `roomname`='"+roomname+"'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			String roomtable=rt.getString("roomtable");
			rt.close();
			if(date==null||date.isEmpty())
			{
				Date curDate=new Date();
				
				Timestamp curTime=new Timestamp(curDate.getTime());
				if(prosperity.equals("pending"))
				{
					sql="select * from `"+roomtable+"` where `status`='pending' and `usedate`>=DATE('"+curTime.toString()+"');";
				}
				else if(prosperity.equals("pass"))
				{
					sql="select * from `"+roomtable+"` where `status`='pass' and `usedate`>=DATE('"+curTime.toString()+"');";
				}
				else if(prosperity.equals("rejected"))
				{
					sql="select * from `"+roomtable+"` where `status`='rejected' and `usedate`>=DATE('"+curTime.toString()+"');";
				}
				else
				{
					sql="select * from `"+roomtable+"` where `status`='finished' and `usedate`>=DATE('"+curTime.toString()+"');";
				}
			}
			else
			{
				if(prosperity.equals("pending"))
				{
					sql="select * from `"+roomtable+"` where `status`='pending' and `usedate`='"+date+"';";
				}
				else if(prosperity.equals("pass"))
				{
					sql="select * from `"+roomtable+"` where `status`='pass' and `usedate`='"+date+"';";
				}
				else if(prosperity.equals("rejected"))
				{
					sql="select * from `"+roomtable+"` where `status`='rejected' and `usedate`='"+date+"';";
				}
				else
				{
					sql="select * from `"+roomtable+"` where `status`='finished' and `usedate`='"+date+"';";
				}
			}
			System.out.println(sql);
			rt=st.executeQuery(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			if(!rt.next())//无结果直接输出XML结束符
			{
				writer.println("<result>none</result>");
				System.out.println("没有任何内容");
			}
			else//有结果
			{
				writer.println("<result>");
				do{
					writer.println("<apply>");
					writer.println("<signupId>"+rt.getString("signupId")+"</signupId>");
					writer.println("<studentId>"+rt.getString("studentId")+"</studentId>");
					writer.println("<name>"+rt.getString("name")+"</name>");
					writer.println("<tel>"+rt.getString("tel")+"</tel>");
					writer.println("<partyname>"+rt.getString("partyname")+"</partyname>");
					writer.println("<num>"+rt.getString("num")+"</num>");
					writer.println("<date>"+rt.getString("usedate")+"</date>");
					writer.println("<stime>"+rt.getString("stime")+"</stime>");
					writer.println("<etime>"+rt.getString("etime")+"</etime>");
					writer.println("<content><![CDATA["+rt.getString("content")+"]]></content>");	
					writer.println("<note><![CDATA["+rt.getString("note")+"]]></note>");
					writer.println("<mail>"+rt.getString("email")+"</mail>");
					writer.println("<roomname>"+roomname+"</roomname>");
					writer.println("<signupTime>"+rt.getString("signupTime")+"</signupTime>");
					writer.println("<feedback><![CDATA["+rt.getString("feedback")+"]]></feedback>");
					writer.println("<aid><![CDATA["+rt.getString("aid")+"]]></aid>");
					String partynamefordealbroke=rt.getString("partyname");
					if(!partynamefordealbroke.equals("个人")&&!partynamefordealbroke.equals("无")) 
						sql=String.format("select * from `blockusers` where `Username`='%s' or `partyname`='%s';",rt.getString("name"),partynamefordealbroke);
					else 
						sql=String.format("select * from `blockusers` where `Username`='%s';",rt.getString("name"));
					System.out.println(sql);
					dst=ct.createStatement();
					drt=dst.executeQuery(sql);
					String blockdetail="";
					if(drt.next())
					{
						do{
							blockdetail+=String.format("时间:%s.社团名称:%s.名字:%s.违规记录:%s\n", 
									drt.getString("blocktime"),drt.getString("partyname"),drt.getString("Username"),drt.getString("blockdetail"));
						}while(drt.next());
					}
					if(drt!=null) drt.close();
					if(dst!=null) dst.close();
					writer.println("<blockdetail><![CDATA["+blockdetail+"]]></blockdetail>");
					writer.println("</apply>");
				}while(rt.next());
				writer.println("</result>");
			}
			writer.flush();
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when GetApplyList room");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				if(drt!=null) drt.close();
				if(dst!=null) dst.close();
				}
			catch(Exception e)	
			{
			
			}
		}
    }
  
    public static void PermitApply(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			String sql=null;
			String roomname=request.getParameter("roomname");
			String signupId=request.getParameter("signupId");
			String advice=request.getParameter("advice");
			if(roomname==null||signupId==null||advice==null)
			{
				response.sendError(500,"Error when GetApplyList room");
				return;
			}
			//==读取常规的资料
			sql="select * from `using` where `roomname`='"+roomname+"'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			String roomtable=rt.getString("roomtable");
			rt.close();
			sql="select `usedate`,HOUR(`stime`),HOUR(`etime`) from `"+roomtable+"` where (`status`='pending' or `status`='rejected') and `signupId`='"+signupId+"';";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			String usedate=rt.getString("usedate");
			String stime=rt.getString(2);
			String etime=rt.getString(3);
			rt.close();
			//=========时间判断重叠,不存在线段，建立线段
			sql=String.format("select * from `%s` where `status`='pass' and " +
					"((HOUR(`etime`)>'%s' and HOUR(`stime`)<'%s') " +
					"or (HOUR(`etime`)>'%s' and HOUR(`stime`)<'%s') " +
					"or(HOUR(`etime`)<='%s' and HOUR(`stime`)>='%s')) " +
					"and `usedate`='%s'",
					roomtable,
					stime,stime,
					etime,etime,
					etime,stime,
					usedate);
			System.out.println(sql);
			rt=st.executeQuery(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
			writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<reply>");
			if(!rt.next())
			{
				rt.close();
				sql=String.format("update `%s` set `status`='pass',`advice`='%s' where `signupId`='%s'", roomtable,advice,signupId);
				System.out.println(sql);
				st.executeUpdate(sql);
				writer.println("<result>success</result>");
				Log4Action(request,response,"Permit using room "+roomname+", whose requisition number is "+signupId,AdminPermitRoomEvent);
				sql="select * from `"+roomtable+"` where `signupId`='"+signupId+"';";
				rt=st.executeQuery(sql);
				rt.next();
				String email=rt.getString("email");
				String sTime=rt.getString("stime");
				String eTime=rt.getString("etime");
				SendMail.SendAcceptMail(email,signupId,roomname,advice,sTime,eTime);
			}
			else
			{
				writer.println("<result>failed</result>");
				writer.println("<conflict>Id:"+rt.getString("signupId")+", name:"+rt.getString("name")+"</conflict>");
			}
			writer.println("</reply>");
			writer.flush();
			writer.close();
			
			
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when PermitApply room");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			
			}
		}
    }
    public static void RejectApply(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			String sql=null;
			String roomname=request.getParameter("roomname");
			String signupId=request.getParameter("signupId");
			String advice=request.getParameter("advice");
			if(roomname==null||signupId==null||advice==null)
			{
				response.sendError(500,"Error when GetApplyList room");
				return;
			}
			//==读取常规的资料
			sql="select * from `using` where `roomname`='"+roomname+"'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			String roomtable=rt.getString("roomtable");
			sql=String.format("update `%s` set `status`='rejected',`advice`='%s' where (`status`='pending' or `status`='pass') and `signupId`='%s'",roomtable,advice,signupId );
			System.out.println(sql);
			st.executeUpdate(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
	        writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<result>success</result>");
			writer.flush();
			Log4Action(request,response,"Reject using room "+roomname+"， whose requisition number is "+signupId,AdminRejectRoomEvent);
			sql="select * from `"+roomtable+"` where `signupId`='"+signupId+"';";
			rt=st.executeQuery(sql);
			rt.next();
			String email=rt.getString("email");
			String sTime=rt.getString("stime");
			String eTime=rt.getString("etime");
			SendMail.SendRejectMail(email,signupId,roomname,advice,sTime,eTime);
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when RejectApply room");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			}
		}
		
    }
    public static void GetAllApply(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			String sql=null;
			String roomname=request.getParameter("roomname");
			String date=request.getParameter("date");
			if(roomname==null||date==null)
			{
				response.sendError(500,"Error when GetApplyList room");
				return;
			}
			sql="select * from `using` where `roomname`='"+roomname+"'";
			System.out.println(sql);
			rt=st.executeQuery(sql);
			rt.next();
			String roomtable=rt.getString("roomtable");
			rt.close();
			sql=String.format("select * from `%s` where `status`='pass' and `usedate`='%s';",roomtable,date);
			System.out.println(sql);
			rt=st.executeQuery(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
	        writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<report>");
			writer.println("<date>"+date+"</date>");
			while(rt.next())
			{
				writer.println("<status>");
				String s_time = rt.getString("stime");
				String e_time = rt.getString("etime");
				s_time = s_time.substring(s_time.indexOf(" "), s_time.lastIndexOf(":"));
				e_time = e_time.substring(e_time.indexOf(" "), e_time.lastIndexOf(":"));
				writer.println(String.format("<passApply> %s，%s，学号：%s，申请号：%s，使用时间:%s - %s</passApply>",
						rt.getString("name"), rt.getString("partyname"), rt.getString("studentId"), rt.getString("signupId"), s_time, e_time));
				writer.println("</status>");
			}
			writer.println("</report>");
			writer.flush();
		}	
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when CheckApply room");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			}
		}
    }
    public static void SwitchRoomOnOff(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	PrintWriter writer=null;
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try
		{
			ct=DatabasePool.getConnection();
			st=ct.createStatement();
			String sql=null;
			String roomname=request.getParameter("roomname");
			String prosperity=request.getParameter("prosperity");
			if(roomname==null||prosperity==null)
			{
				response.sendError(500,"Error when GetApplyList room");
				return;
			}
			if(prosperity.equals("off"))
			{
				sql=String.format("update `using` set `avail`=0 where `roomname`='%s'", roomname);
			}
			else
			{
				sql=String.format("update `using` set `avail`=1 where `roomname`='%s'", roomname);
			}
			System.out.println(sql);
			st.executeUpdate(sql);
			response.setContentType("application/xhtml+xml;charset=utf-8");
	        writer=response.getWriter();
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<result>ok</result>");
			writer.flush();
		}	
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			response.sendError(500,"Error when CheckApply room");
		}
		finally
		{
			try{
				if(writer!=null) writer.close();
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
				}
			catch(Exception e)	
			{
			}
		}
    }
    public static boolean CheckRoutineUpdated(){
		Date curDate=new Date();
		java.text.SimpleDateFormat parseTime = new java.text.SimpleDateFormat("yyyy-MM-dd");
		Connection ct=null;
		Statement st=null;
		ResultSet rt=null;
		try{
			boolean ke = true;
			for(int i=1; i<=7; i++){
				Calendar cal = Calendar.getInstance();
				cal.setTime(curDate);
				cal.add(Calendar.DAY_OF_MONTH, i);
				Date dateToCheck = cal.getTime();
				String sql = "SELECT * FROM `routine_updated` WHERE date = '" + parseTime.format(dateToCheck) + "'";
				ct=DatabasePool.getConnection();
				st=ct.createStatement();
				System.out.println(sql);
				rt = st.executeQuery(sql);
				
				if(!rt.next()){
					RoutineUpdate(dateToCheck);
				}
				ke = false;
			}
			return ke;
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
			return true;
		}
		finally{
			try{
				if(ct!=null) ct.close();
				if(st!=null) st.close();
				if(rt!=null) rt.close();
			}
			catch(Exception e){}
		}
    }
    public static void RoutineUpdate(Date dateToUpdate){
		java.text.SimpleDateFormat parseTime = new java.text.SimpleDateFormat("yyyy-MM-dd");
		java.text.SimpleDateFormat parseTime2 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
    	Date curDate = new Date();
    	String sign_date = parseTime2.format(curDate);
    	sign_date = sign_date.substring(0, sign_date.length()-4);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(dateToUpdate);
    	int day_of_week = cal.get(Calendar.DAY_OF_WEEK)-1;
    	if (day_of_week <= 0) day_of_week = 0;
		String use_date = parseTime.format(dateToUpdate);
    	Connection ct=null;
    	Statement st0=null;
		Statement st1=null;
		ResultSet rt0 = null;
		ResultSet rt1=null;
		Statement st=null;
		try{
			String sql0 = "SELECT roomtable FROM `using` WHERE 1";
			String sql1 = "SELECT * FROM `routine_use` WHERE day = " + day_of_week;
			ct = DatabasePool.getConnection();
			st = ct.createStatement();
			st0 = ct.createStatement();
			System.out.println(sql0);
			rt0 = st0.executeQuery(sql0);
			
			st1 = ct.createStatement();
			System.out.println(sql1);
			rt1 = st1.executeQuery(sql1);
			
			ct.setAutoCommit(false);
			
			while(rt0.next()){
				String sql2 = "DELETE FROM `";
				sql2+= rt0.getString("roomtable")+"` "
						+"WHERE `studentId` = 22255500 and `email` = 'temporary' and `usedate` = '"+use_date+"'";
				st.addBatch(sql2);
				System.out.println(sql2);
			}
			while(rt1.next()){
				String sql2 = "INSERT INTO `";
				sql2 += rt1.getString("room_name") + "` "
						+"(`studentId`, `NETID`, `note`, `status`, `email`, `num`, `content`, "
						+"`aid`, `feedback`,`advice`, "
						+"`name`, `tel`, `partyname`, "
						+"`stime`, `etime`, `usedate`, `signupTime`) "
						+"VALUES"
						+"('22255500', 'X', '社团占用', 'pass', 'temporary', '0', '#', "
						+"'', '', '', ";
				sql2 += "'"+rt1.getString("name")+"', '"+rt1.getString("tel")+"', '"+rt1.getString("partyname")
						+"', '"+use_date+" "+rt1.getString("stime")+":00"
						+"', '"+use_date+" "+rt1.getString("etime")+":00"
						+"', '"+use_date+"', '"+sign_date+":00')";
				st.addBatch(sql2);
				System.out.println(sql2);
			}
			String sql3 = "INSERT INTO `routine_updated` values ('"+use_date+"')";
			st.addBatch(sql3);
			System.out.println(sql3);
			st.executeBatch();
			ct.commit();
			ct.setAutoCommit(true);
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		finally{
			try{
				if (ct!=null) ct.close();
				if (st!=null) st.close();
				if (st0!=null) st0.close();
				if (st1!=null) st1.close();
				if (rt0!=null) rt0.close();
				if (rt1!=null) rt1.close();
			}
			catch(Exception e){}
		}
    }
}
