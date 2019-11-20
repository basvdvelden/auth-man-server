package nl.management.auth.server.security.filters;

import nl.management.auth.server.common.JedisService;
import nl.management.auth.server.security.constants.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class BlacklistFilter extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(BlacklistFilter.class);
    // TODO: Find better solution for NO_FILTER, methods should also be checked inc. other things.
    private final static List<String> NO_FILTER = Arrays.asList("/logout", "/users/authenticate", "/users/register");
    private final JedisService jedisService;

    @Autowired
    public BlacklistFilter(JedisService jedisService) {
        this.jedisService = jedisService;
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader(SecurityConstants.TOKEN_HEADER);
        if (token != null && shouldFilter(req)) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");
            boolean blacklisted = jedisService.isBlacklisted(token);
            if (blacklisted) {
                LOG.warn("Blacklisted JWT: {}", token);
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login again!");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean shouldFilter(HttpServletRequest request) {
        for (String url: NO_FILTER) {
            if (request.getRequestURI().contains(url)) {
                LOG.info("Not filtering : {}", request.getRequestURI());
                return false;
            }
        }
        return true;
    }
}
