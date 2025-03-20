package app.dietdash.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@WebFilter({"/api/auth/submit", "/api/auth/test2", "/api/auth/register", "/api/auth/send-forgot-email"})
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 3;
    private static final long WINDOW_SIZE_MS = TimeUnit.MINUTES.toMillis(5);

    private final ConcurrentHashMap<String, RequestInfo> requestCache = new ConcurrentHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String ip = httpRequest.getRemoteAddr();

        RequestInfo requestInfo = requestCache.getOrDefault(ip, new RequestInfo(System.currentTimeMillis(), 0));

        long currentTime = System.currentTimeMillis();

        if (currentTime - requestInfo.timestamp > WINDOW_SIZE_MS) {
            requestInfo = new RequestInfo(currentTime, 1);
        } else {
            requestInfo.count++;
        }

        if (requestInfo.count > MAX_REQUESTS_PER_MINUTE) {
            httpResponse.getWriter().write("Too many requests. Please try again later.");
            httpResponse.setStatus(429);
            return;
        }

        requestCache.put(ip, requestInfo);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private static class RequestInfo {
        long timestamp;
        int count;

        RequestInfo(long timestamp, int count) {
            this.timestamp = timestamp;
            this.count = count;
        }
    }
}
