package com.gcc.airbase.common.filter;

import com.gcc.airbase.common.filter.wrapper.RequestWrapper;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebFault;
import java.io.IOException;

@Component
@WebFault
public class BaseFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        ServletRequest requestwrapper = new RequestWrapper(httpServletRequest);
        filterChain.doFilter(requestwrapper,servletResponse);
    }

}
