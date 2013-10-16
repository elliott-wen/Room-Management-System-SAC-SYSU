package kernel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


@WebFilter(urlPatterns={"/*"},
initParams={@WebInitParam(name="ANTIINJECT_LIST",value="/WEB-INF/antiinject_list.txt")})
public class AntiInjection implements Filter {

	private static Map<String, String> escapeMap=new HashMap<String, String>();;
    public AntiInjection() {
         
    }

	
	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		HttpServletRequest requestWrapper = new CharacterRequestWrapper((HttpServletRequest) request, escapeMap);
        chain.doFilter(requestWrapper, response);
	}

	
	public void init(FilterConfig fConfig) throws ServletException 
	{
		if(escapeMap.isEmpty())//第一次初始化
		{
			BufferedReader reader = null;
	        try {
	            String antiInjectListFile = fConfig.getInitParameter("ANTIINJECT_LIST");
	            reader = new BufferedReader(new InputStreamReader(
	                        fConfig.getServletContext()
	                            .getResourceAsStream(antiInjectListFile)));
	            String input = null;
	          
	            while ((input = reader.readLine()) != null) 
	            {
	                String[] tokens = input.split("\t");
	                escapeMap.put(tokens[0], tokens[1]);
	            }
	        	} 
	        catch (Exception ex) {
	            System.out.println("Loading AntiInject Modules Error");
	        }
	        finally {
	            try 
	            {
	                reader.close();
	            } 
	            catch (Exception ex) 
	            {
	            }
	        }
		}
	}
	//=======私有类型===用来防止注入========
	private class CharacterRequestWrapper extends HttpServletRequestWrapper
	{
		private Map<String, String> escapeMap;
		public CharacterRequestWrapper(HttpServletRequest request,Map<String, String> escapeMap) 
		{
			super(request);
			this.escapeMap = escapeMap;
		}
		@Override
	    public String getParameter(String name) 
		{
	        return doEscape(getRequest().getParameter(name));
	    }
		private String doEscape(String parameter) 
		{
	        if(parameter == null) {
	            return null;
	        }
	        String result = parameter;
	        for(String origin : escapeMap.keySet()) {
	            String escape = escapeMap.get(origin);
	            result = result.replaceAll(origin, escape);
	        }
	        return result;
	    }
	}

}
