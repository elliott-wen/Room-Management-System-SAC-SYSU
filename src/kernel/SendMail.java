package kernel;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
public class SendMail {
     public static void  SendAcceptMail(final String address,final String signupId,final String roomname,final String advice,final String sTime, final String eTime)
	{
    	 Thread thread1 = new Thread(new Runnable(){public void run(){
    	 send(Constant.mailAddress,address,"团委活动室_申请通过(自动发送，请勿回复)",String.format("<b>您好，您的申请(房间名:%s,申请Id:%s)已经初步获得通过，"
    	 +"如果有任何变动，我们会再次发送邮件通知您。<br/>申请使用时间为:%s - %s<br/>审批意见:%s<br/>请自行将附件承诺书打印填写并于登记使用时提交到前台。<br/>本邮件只作为参考，最终情况请登录网站查询。"
    	 +"<br/><span style=\"float:right\">共青团中山大学委员会</span></b>",roomname,signupId,sTime,eTime,advice), 1);
    	 }});
    	 thread1.start();
    }
	public  static void SendRejectMail(final String address,final String signupId,final String roomname,final String advice,final String sTime, final String eTime)
	{
		Thread thread1 = new Thread(new Runnable(){public void run(){
			send(Constant.mailAddress,address,"团委活动室_申请被拒绝(自动发送，请勿回复)",String.format("<b>您好，您的申请(房间名:%s,申请Id:%s)因为不符合部分申请条件而被拒绝，"
		+"如果有任何变动，我们会再次发送邮件通知您。<br/>申请使用时间为:%s - %s<br/>审批意见:%s<br/>本邮件只作为参考，最终情况请登录网站查询。"
		+"<br/><span style=\"float:right\">共青团中山大学委员会</span></b>",roomname,signupId,sTime,eTime,advice), 0);
	      }});
	    	 thread1.start(); 
		
	}
	public  static void SendSignupMail(final String address,final String signupId,final String roomname,final String sTime,final String eTime)
	{
		Thread thread1 = new Thread(new Runnable(){public void run(){
			send(Constant.mailAddress,address,"团委活动室_申请提交(自动发送，请勿回复)",String.format("<b>您好，您的申请(房间名:%s,申请Id:%s)已经在审批队列。<br/>申请使用时间为:%s - %s"
		+"<br/>审批时间为周一上午及周二、周四下午，如果有任何变动，我们会再次发送邮件通知您。<br/>本邮件只作为参考，最终情况请登录网站查询。"
		+"<br/><span style=\"float:right\">共青团中山大学委员会</span></b>",roomname,signupId, sTime, eTime), 0);
	      }});
	    	 thread1.start();
		
	}
	private static class Constant {
		public static final String mailAddress ="sysuhdzx@163.com";
		public static final String mailCount ="sysuhdzx";
		public static final String mailPassword ="276201510";
		public static final String mailServer ="smtp.163.com";
	}
	private static void send(String str_from,String str_to,String str_title,String str_content, int type)
	{

		

		try {

		//建立邮件会话

		Properties props=new Properties(); //用来在一个文件中存储键-值对的，其中键和值是用等号分隔的，

		//存储发送邮件服务器的信息

		props.put("mail.smtp.host",Constant.mailServer);

		//同时通过验证

		props.put("mail.smtp.auth","true");

		//根据属性新建一个邮件会话

		Session s=Session.getInstance(props);

		s.setDebug(true); //有他会打印一些调试信息。

		//由邮件会话新建一个消息对象

		MimeMessage message=new MimeMessage(s);

		//设置邮件

		InternetAddress from= new InternetAddress(str_from); 

		message.setFrom(from); //设置发件人的地址

		//

		// //设置收件人,并设置其接收类型为TO

		InternetAddress to=new InternetAddress(str_to); 

		message.setRecipient(Message.RecipientType.TO, to);

		//设置标题

		message.setSubject(str_title); //java学习

		//设置信件内容
		if(type == 1){
			//发送附件
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(str_content, "text/html;charset=gbk");
			
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource("/usr/tomcat/webapps/ROOT/promise.pdf");
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("中山大学团委活动室使用承诺书.pdf");
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			
		}else{
			message.setContent(str_content, "text/html;charset=gbk");
		}
		//设置发信时间

		message.setSentDate(new Date());

		//存储邮件信息

		message.saveChanges();

		//发送邮件

		Transport transport=s.getTransport("smtp");

		//以smtp方式登录邮箱,第一个参数是发送邮件用的邮件服务器SMTP地址,第二个参数为用户名,第三个参数为密码

		transport.connect(Constant.mailServer,Constant.mailCount,Constant.mailPassword);

		//发送邮件,其中第二个参数是所有已设好的收件人地址

		transport.sendMessage(message,message.getAllRecipients());

		transport.close();

		} catch (Exception e) {

		e.printStackTrace();

		}

		}
}
