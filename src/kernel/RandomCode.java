package kernel;

import java.awt.Color;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RandomCode {
	
	
	private final static int width=100;//�߶�
	private final static int height=30;//���
	private final static int codeNum=4;//����
	private final static int x=width/(codeNum+1);
	private final static int fontHeight=height-2;
	private final static int codeY=height-4;
	private final static char[] codes = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
	public static void GetRandomCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		        BufferedImage buffImg = new BufferedImage(
				width, height,BufferedImage.TYPE_INT_RGB);
				//����ͼ��������
				Graphics2D g = buffImg.createGraphics();
				//�����������������
				Random random = new Random();
				//����֤��ͼ�񱳾����Ϊ��ɫ
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, width, height);
				//���������ʽ������Ĵ�С�������֤��ͼƬ�ĸ߶����趨��
				Font font = new Font("Fixedsys", Font.PLAIN, fontHeight);
				//�������塣
				g.setFont(font);
				//Ϊ��֤��ͼƬ���߿�Ϊһ�����ء�
				g.setColor(Color.WHITE);
				g.drawRect(0, 0, width - 1, height - 1);
				//�������222��ͼƬ����������ʹ��֤��ͼƬ�е��ַ���������ʶ��
				g.setColor(Color.WHITE);
				for(int i = 0; i<10; i++)
				{
					int x = random.nextInt(width);
					int y = random.nextInt(height);
					int xl = random.nextInt(12);
					int yl = random.nextInt(12);
					g.drawLine(x, y, x + xl, y + yl);
				}
				//randomCode���������������֤��
				StringBuffer randomCode = new StringBuffer();
				//������ɫ����
				int red = 0, green = 0, blue = 0;
				//�������codeNum��������֤��
				for (int i = 0; i < codeNum; i++) {
					//�õ������������֤��
					String strRand = String.valueOf(codes[random.nextInt(codes.length)]);
					//ʹ��������������������ɫ������������ɫֵ�����������ÿλ���ֵ���ɫֵ������ͬ��
					//��ɫ�� < 100
					red = random.nextInt(56) + 200;
					green = random.nextInt(56) + 200;
					blue = random.nextInt(128) + 128;
					//�������������ɫ����֤����Ƶ�ͼ���С�
					g.setColor(new Color(red, green, blue));
					g.drawString(strRand, (i + 1) * x, codeY);
					
					randomCode.append(strRand);
				}
				// ����������֤�뱣�浽Session��
				HttpSession session = request.getSession();
				session.setAttribute("validate", randomCode.toString());
				// ����ͼ�񻺴�Ϊno-cache��
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
				response.setContentType("image/gif");
				//��������������֤��ͼƬ�����Servlet���������
				ServletOutputStream sos = response.getOutputStream();
				ImageIO.write(buffImg, "gif", sos);
				sos.close();
	}
	public static boolean CheckRandomCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		String gcode=(String)session.getAttribute("validate");
		session.removeAttribute("validate");
		if(gcode==null||gcode.equals(""))
		{
			return false;
		}
		else
		{
			String ucode=request.getParameter("ucode");
			if(ucode==null||ucode.equals(""))
			{
				System.out.println("��֤�����1");
				return false;
			}
			else
			{
				//System.out.println(ucode+" "+gcode);
				if(gcode.equals(ucode.toUpperCase()))
				{
					System.out.println("��֤��ɹ�");
					return true;
				}
				else
				{
					session.removeAttribute("validate");
					System.out.println("��֤�����2");
					return false;
				}
			}
		}
		
	}
}


	


