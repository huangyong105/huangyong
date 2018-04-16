package tech.huit.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.filter.FilterNonReentrantException;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Description: 页面缓存过滤器
 */
public class PageEhCacheFilter extends SimplePageCachingFilter {
    private final static Logger log = LoggerFactory.getLogger(PageEhCacheFilter.class);
    private final static String CACHE_URL_PATTERNS = "patterns";
    private static String[] cacheURLs;
    private Map<String, String[]> parmFilter = new HashMap();


    @Override
    public void doInit(FilterConfig filterConfig) throws CacheException {
        String patterns = filterConfig.getInitParameter(CACHE_URL_PATTERNS);
        if (StringUtils.isEmpty(patterns)) {
            return;
        }
        String[] infos = StringUtils.split(patterns, ",");
        Set<String> urls = new HashSet<>();
        for (String info : infos) {
            String[] kv = info.split("->");
            if (kv.length == 1) {
                urls.add(kv[0]);
                continue;
            } else if (kv.length == 2) {
                urls.add(kv[0]);
                parmFilter.put(kv[0], kv[1].split("#"));
                log.info("cachePageParmFilter->url:{} filter:{}", kv[1]);
            } else {
                log.error("cachePageParmError->{}", info);
            }
        }
        cacheURLs = urls.toArray(new String[urls.size()]);
        log.info("cachePageLoadUrls->{}", Arrays.toString(cacheURLs));
        super.doInit(filterConfig);
    }

    @Override
    protected void doFilter(final HttpServletRequest request, final HttpServletResponse response,
                            final FilterChain chain) throws AlreadyGzippedException, AlreadyCommittedException,
            FilterNonReentrantException, LockTimeoutException, Exception {

        String url = request.getRequestURI();
        boolean flag = false;
        if (cacheURLs != null && cacheURLs.length > 0) {
            for (String cacheURL : cacheURLs) {
                if (url.equals(cacheURL)) {
                    flag = true;
                    break;
                }
            }
        }
        // 如果包含我们要缓存的url 就缓存该页面，否则执行正常的页面转向
        if (flag) {
            super.doFilter(request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean headerContains(final HttpServletRequest request, final String header, final String value) {
        logRequestHeaders(request);
        final Enumeration<String> accepted = request.getHeaders(header);
        while (accepted.hasMoreElements()) {
            final String headerValue = accepted.nextElement();
            if (headerValue.indexOf(value) != -1) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean acceptsGzipEncoding(HttpServletRequest request) {
        //兼容ie6/7 gzip压缩
        boolean ie6 = headerContains(request, "User-Agent", "MSIE 6.0");
        boolean ie7 = headerContains(request, "User-Agent", "MSIE 7.0");
        return acceptsEncoding(request, "gzip") || ie6 || ie7;
    }

    @Override
    protected String calculateKey(HttpServletRequest httpRequest) {
        String uri = httpRequest.getRequestURI();
        StringBuilder sb = new StringBuilder(uri).append("->");
        String[] filters = parmFilter.get(uri);
        if (null != filters) {
            for (String filter : filters) {
                int beginIndex = filter.indexOf("(");
                int endIndex = filter.indexOf(")");
                if (-1 == beginIndex || endIndex < beginIndex) {
                    String value = httpRequest.getParameter(filter);
                    sb.append(filter).append('=').append(value);
                } else {
                    //判断是否包含一个条件: rid(type=1) 表示当type=1时才包含这个条件
                    String subFilters = filter.substring(beginIndex + 1, endIndex);
                    for (String subFilter : subFilters.split("&")) {
                        String[] include = subFilter.split("=");
                        if (include.length == 2) {
                            if (include[1].equals(httpRequest.getParameter(include[0]))) {
                                filter = filter.substring(0, beginIndex);
                                sb.append(filter).append('=').append(httpRequest.getParameter(filter));
                                break;
                            }
                        } else {
                            log.error("includeConfError->{}", subFilter);
                        }
                    }
                }
            }
        }
        String key = sb.toString();
        log.debug("cachePageKey->{}", key);
        return key;
    }
}
