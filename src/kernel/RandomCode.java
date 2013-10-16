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
	
	
	private final static int width=100;//高度
	private final static int height=30;//宽度
	private final static int codeNum=4;//个数
	private final static int x=width/(codeNum+1);
	private final static int fontHeight=height-2;
	private final static int codeY=height-4;
	private final static char[] codes = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
	public static void GetRandomCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		        BufferedImage buffImg = new BufferedImage(
				width, height,BufferedImage.TYPE_INT_RGB);
				//产生图形上下文
				Graphics2D g = buffImg.createGraphics();
				//创建随机数产生函数
				Random random = new Random();
				//将验证码图像背景填充为黑色
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, width, height);
				//创建字体格式，字体的大小则根据验证码图片的高度来设定。
				Font font = new Font("Fixedsys", Font.PLAIN, fontHeight);
				//设置字体。
				g.setFont(font);
				//为验证码图片画边框，为一个像素。
				g.setColor(Color.WHITE);
				g.drawRect(0, 0, width - 1, height - 1);
				//随机生产222跳图片干扰线条，使验证码图片中的字符不被轻易识别
				g.setColor(Color.WHITE);
				for(int i = 0; i<10; i++)
				{
					int x = random.nextInt(width);
					int y = random.nextInt(height);
					int xl = random.nextInt(12);
					int yl = random.nextInt(12);
					g.drawLine(x, y, x + xl, y + yl);
				}
				//randomCode保存随机产生的验证码
				StringBuffer randomCode = new StringBuffer();
				//定义颜色三素
				int red = 0, green = 0, blue = 0;
				//随机生产codeNum个数字验证码
				for (int i = 0; i < codeNum; i++) {
					//得到随机产生的验证码
					String strRand = String.valueOf(codes[random.nextInt(codes.length)]);
					//使用随机函数产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
					//深色， < 100
					red = random.nextInt(56) + 200;
					green = random.nextInt(56) + 200;
					blue = random.nextInt(128) + 128;
					//用随机产生的颜色将验证码绘制到图像中。
					g.setColor(new Color(red, green, blue));
					g.drawString(strRand, (i + 1) * x, codeY);
					
					randomCode.append(strRand);
				}
				// 将生产的验证码保存到Session中
				HttpSession session = request.getSession();
				session.setAttribute("validate", randomCode.toString());
				// 设置图像缓存为no-cache。
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
				response.setContentType("image/gif");
				//将最终生产的验证码图片输出到Servlet的输出流中
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
				System.out.println("验证码错误1");
				return false;
			}
			else
			{
				//System.out.println(ucode+" "+gcode);
				if(gcode.equals(ucode.toUpperCase()))
				{
					System.out.println("验证码成功");
					return true;
				}
				else
				{
					session.removeAttribute("validate");
					System.out.println("验证码错误2");
					return false;
				}
			}
		}
		
	}
}


	


