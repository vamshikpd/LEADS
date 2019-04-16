package com.plm.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class HttpHeaderValidationFilter implements Filter {

    private static String LEADS_WS_REQUESTKEY;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        FilterConfig filterConfig1 = filterConfig;
        LEADS_WS_REQUESTKEY = filterConfig.getInitParameter("LEADS_WS_REQUESTKEY");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        if (requiresValidation(req.getRequestURI())) {
            String leadsWsRequestKey = extractHeader(req, "LEADS-WS-REQUESTKEY");
            if (!leadsWsRequestKey.isEmpty() && leadsWsRequestKey.equalsIgnoreCase(LEADS_WS_REQUESTKEY)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean requiresValidation(String requestURI) {
        return !requestURI.endsWith(".wsdl");
    }

    private String extractHeader(HttpServletRequest request, String headerName) {
        String headerValue = "";
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            if (key.equalsIgnoreCase(headerName)) {
                headerValue = request.getHeader(key);
                break;
            }
        }

        return headerValue;
    }

    @Override
    public void destroy() {}
}
