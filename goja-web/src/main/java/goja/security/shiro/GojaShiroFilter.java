package goja.security.shiro;

import goja.core.StringPool;
import goja.core.app.GojaConfig;
import com.jfinal.kit.PropKit;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;

import org.apache.commons.io.FileUtils;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <p> Goja 扩展的 Shiro 拦截器，主要是为了避免 shiro.ini 的配置</p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class GojaShiroFilter extends AbstractShiroFilter {
    private static final Logger logger = LoggerFactory.getLogger(GojaShiroFilter.class);

    private static Properties shiroConfig;

    @Override
    public void init() throws Exception {
        super.init();


        String shiroConfigFile = GojaConfig.getAppSecurityConfig();
        final File configFolderFile = GojaConfig.getConfigFolderFile();
        shiroConfig = configFolderFile == null ? PropKit.use(shiroConfigFile).getProperties()
                : PropKit.use(FileUtils.getFile(configFolderFile, shiroConfigFile)).getProperties();

        WebSecurityManager webSecurityManager = initSecurityManager();
        FilterChainManager manager = createFilterChainManager();

        //Expose the constructed FilterChainManager by first wrapping it in a
        // FilterChainResolver implementation. The AbstractShiroFilter implementations
        // do not know about FilterChainManagers - only resolvers:
        PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
        chainResolver.setFilterChainManager(manager);

        setSecurityManager(webSecurityManager);
        setFilterChainResolver(chainResolver);
    }

    protected FilterChainManager createFilterChainManager() {

        final DefaultFilterChainManager manager = new DefaultFilterChainManager();
        Map<String, Filter> defaultFilters = manager.getFilters();
        //apply global settings if necessary:
        for (Filter filter : defaultFilters.values()) {
            applyGlobalPropertiesIfNecessary(filter);
        }

        if (shiroConfig != null && !shiroConfig.isEmpty()) {
            for (Object urlKey : shiroConfig.keySet()) {
                String url = String.valueOf(urlKey);
                if (org.apache.commons.lang3.StringUtils.equals("login.url", url)
                        || org.apache.commons.lang3.StringUtils.equals("success.url", url)
                        || org.apache.commons.lang3.StringUtils.equals("unauthorized.url", url)
                        || org.apache.commons.lang3.StringUtils.equals("session.expired", url)
                        ) {
                    continue;
                }
                //  { "authc", "roles[admin,user]", "perms[file:edit]" }
                manager.createChain(url, MoreObjects.firstNonNull(shiroConfig.getProperty(url), "anon"));
            }
        }
        return manager;
    }

    private WebSecurityManager initSecurityManager() {
        AppDbRealm appDbRealm = new AppDbRealm();
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(appDbRealm);
        final EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:goja/ehcache/shiro-ehcache.xml");
        securityManager.setCacheManager(cacheManager);
        final DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setSessionIdCookieEnabled(true);
        // 默认一年过期时间
        final String expiredTimes = shiroConfig.getProperty("session.expired", "10800000");
        defaultWebSessionManager.setGlobalSessionTimeout(MoreObjects.firstNonNull(Ints.tryParse(expiredTimes), 10800000));
        securityManager.setSessionManager(defaultWebSessionManager);
        return securityManager;
    }

    private void applyLoginUrlIfNecessary(Filter filter) {
        String loginUrl = shiroConfig.getProperty("login.url", "/login");
        if (StringUtils.hasText(loginUrl) && (filter instanceof AccessControlFilter)) {
            AccessControlFilter acFilter = (AccessControlFilter) filter;
            //only apply the login url if they haven't explicitly configured one already:
            String existingLoginUrl = acFilter.getLoginUrl();
            if (AccessControlFilter.DEFAULT_LOGIN_URL.equals(existingLoginUrl)) {
                acFilter.setLoginUrl(loginUrl);
            }
        }
    }

    private void applySuccessUrlIfNecessary(Filter filter) {
        String successUrl = shiroConfig.getProperty("success.url", "/");
        if (StringUtils.hasText(successUrl) && (filter instanceof AuthenticationFilter)) {
            AuthenticationFilter authcFilter = (AuthenticationFilter) filter;
            //only apply the successUrl if they haven't explicitly configured one already:
            String existingSuccessUrl = authcFilter.getSuccessUrl();
            if (AuthenticationFilter.DEFAULT_SUCCESS_URL.equals(existingSuccessUrl)) {
                authcFilter.setSuccessUrl(successUrl);
            }
        }
    }

    private void applyUnauthorizedUrlIfNecessary(Filter filter) {
        String unauthorizedUrl = shiroConfig.getProperty("unauthorized.url", "/auth");
        if (StringUtils.hasText(unauthorizedUrl) && (filter instanceof AuthorizationFilter)) {
            AuthorizationFilter authzFilter = (AuthorizationFilter) filter;
            //only apply the unauthorizedUrl if they haven't explicitly configured one already:
            String existingUnauthorizedUrl = authzFilter.getUnauthorizedUrl();
            if (existingUnauthorizedUrl == null) {
                authzFilter.setUnauthorizedUrl(unauthorizedUrl);
            }
        }
    }

    private void applyGlobalPropertiesIfNecessary(Filter filter) {
        applyLoginUrlIfNecessary(filter);
        applySuccessUrlIfNecessary(filter);
        applyUnauthorizedUrlIfNecessary(filter);
    }

    @Override
    protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse,
                                    FilterChain chain) throws ServletException, IOException {
        // Solving garbled form submission problem
        servletRequest.setCharacterEncoding(StringPool.UTF_8);
        servletResponse.setCharacterEncoding(StringPool.UTF_8);
        super.doFilterInternal(servletRequest, servletResponse, chain);
    }
}
