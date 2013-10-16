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
    	 send(Constant.mailAddress,address,"��ί���_����ͨ��(�Զ����ͣ�����ظ�)",String.format("<b>���ã���������(������:%s,����Id:%s)�Ѿ��������ͨ����"
    	 +"������κα䶯�����ǻ��ٴη����ʼ�֪ͨ����<br/>����ʹ��ʱ��Ϊ:%s - %s<br/>�������:%s<br/>�����н�������ŵ���ӡ��д���ڵǼ�ʹ��ʱ�ύ��ǰ̨��<br/>���ʼ�ֻ��Ϊ�ο�������������¼��վ��ѯ��"
    	 +"<br/><span style=\"float:right\">��������ɽ��ѧίԱ��</span></b>",roomname,signupId,sTime,eTime,advice), 1);
    	 }});
    	 thread1.start();
    }
	public  static void SendRejectMail(final String address,final String signupId,final String roomname,final String advice,final String sTime, final String eTime)
	{
		Thread thread1 = new Thread(new Runnable(){public void run(){
			send(Constant.mailAddress,address,"��ί���_���뱻�ܾ�(�Զ����ͣ�����ظ�)",String.format("<b>���ã���������(������:%s,����Id:%s)��Ϊ�����ϲ����������������ܾ���"
		+"������κα䶯�����ǻ��ٴη����ʼ�֪ͨ����<br/>����ʹ��ʱ��Ϊ:%s - %s<br/>�������:%s<br/>���ʼ�ֻ��Ϊ�ο�������������¼��վ��ѯ��"
		+"<br/><span style=\"float:right\">��������ɽ��ѧίԱ��</span></b>",roomname,signupId,sTime,eTime,advice), 0);
	      }});
	    	 thread1.start(); 
		
	}
	public  static void SendSignupMail(final String address,final String signupId,final String roomname,final String sTime,final String eTime)
	{
		Thread thread1 = new Thread(new Runnable(){public void run(){
			send(Constant.mailAddress,address,"��ί���_�����ύ(�Զ����ͣ�����ظ�)",String.format("<b>���ã���������(������:%s,����Id:%s)�Ѿ����������С�<br/>����ʹ��ʱ��Ϊ:%s - %s"
		+"<br/>����ʱ��Ϊ��һ���缰�ܶ����������磬������κα䶯�����ǻ��ٴη����ʼ�֪ͨ����<br/>���ʼ�ֻ��Ϊ�ο�������������¼��վ��ѯ��"
		+"<br/><span style=\"float:right\">��������ɽ��ѧίԱ��</span></b>",roomname,signupId, sTime, eTime), 0);
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

		//�����ʼ��Ự

		Properties props=new Properties(); //������һ���ļ��д洢��-ֵ�Եģ����м���ֵ���õȺŷָ��ģ�

		//�洢�����ʼ�����������Ϣ

		props.put("mail.smtp.host",Constant.mailServer);

		//ͬʱͨ����֤

		props.put("mail.smtp.auth","true");

		//���������½�һ���ʼ��Ự

		Session s=Session.getInstance(props);

		s.setDebug(true); //�������ӡһЩ������Ϣ��

		//���ʼ��Ự�½�һ����Ϣ����

		MimeMessage message=new MimeMessage(s);

		//�����ʼ�

		InternetAddress from= new InternetAddress(str_from); 

		message.setFrom(from); //���÷����˵ĵ�ַ

		//

		// //�����ռ���,���������������ΪTO

		InternetAddress to=new InternetAddress(str_to); 

		message.setRecipient(Message.RecipientType.TO, to);

		//���ñ���

		message.setSubject(str_title); //javaѧϰ

		//�����ż�����
		if(type == 1){
			//���͸���
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(str_content, "text/html;charset=gbk");
			
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource("/usr/tomcat/webapps/ROOT/promise.pdf");
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("��ɽ��ѧ��ί���ʹ�ó�ŵ��.pdf");
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			
		}else{
			message.setContent(str_content, "text/html;charset=gbk");
		}
		//���÷���ʱ��

		message.setSentDate(new Date());

		//�洢�ʼ���Ϣ

		message.saveChanges();

		//�����ʼ�

		Transport transport=s.getTransport("smtp");

		//��smtp��ʽ��¼����,��һ�������Ƿ����ʼ��õ��ʼ�������SMTP��ַ,�ڶ�������Ϊ�û���,����������Ϊ����

		transport.connect(Constant.mailServer,Constant.mailCount,Constant.mailPassword);

		//�����ʼ�,���еڶ�����������������õ��ռ��˵�ַ

		transport.sendMessage(message,message.getAllRecipients());

		transport.close();

		} catch (Exception e) {

		e.printStackTrace();

		}

		}
}
