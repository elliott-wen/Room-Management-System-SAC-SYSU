package kernel;

import java.sql.Connection;
import java.sql.DriverManager;
public class DatabasePool {
	private static String jdbcUrl="jdbc:mysql://localhost:3306/hdzx?user=hdzx&password=hdzx276201510&useUnicode=true&characterEncoding=UTF8";
	public static Connection getConnection() 
	{
		
		//System.out.println(jdbcUrl);
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=DriverManager.getConnection(jdbcUrl);
			return con;
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			return null;
		} 
	}
}
