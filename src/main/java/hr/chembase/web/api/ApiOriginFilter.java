package hr.chembase.web.api;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

public class ApiOriginFilter implements javax.servlet.Filter {
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
    throws IOException, ServletException
    {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST");
        httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Origin");        
        chain.doFilter(request, httpResponse);
    }

    public void destroy() {}

    public void init(FilterConfig filterConfig) throws ServletException {}
}