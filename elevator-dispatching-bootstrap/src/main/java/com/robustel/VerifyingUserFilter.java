package com.robustel;

import com.robustel.utils.ThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author YangXuehong
 * @date 2021/7/13
 */
@Component
@WebFilter(filterName = "verifyingUserFilter", urlPatterns = {"/*"})
public class VerifyingUserFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String securityKey = req.getHeader("security-key");
        if (StringUtils.isBlank(securityKey)) {
            ThreadLocalUtil.set("SECURITY-KEY", securityKey);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            ThreadLocalUtil.remove("SECURITY-KEY");
        }

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}
