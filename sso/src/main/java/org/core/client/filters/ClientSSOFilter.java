package org.core.client.filters;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.api.model.UserSessionDataResponse;
import org.client.common.ConfigProperties;
import org.core.common.enums.CookiesName;
import org.core.common.exceptions.CookieNotFoundException;
import org.core.common.utils.CookieManager;
import org.core.common.utils.StringUtils;
import org.core.connector.rest.api.AccessTokenWrapper;
import org.core.connector.rest.api.IServiceInvoker;
import org.core.connector.rest.invokers.CheckUserSessionInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClientSSOFilter implements Filter {
    private final static Logger logger = LoggerFactory.getLogger(ClientSSOFilter.class);

    @Override
    public void destroy() {
        logger.debug("destroy");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        final String path = ((HttpServletRequest) request).getRequestURI();
        logger.debug("doFilter|path: {}", path);
        if (!path.contains("accessToken") && processRequest(request, response, filterChain)) {
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean processRequest(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse resp = (HttpServletResponse) response;
        try {
            CookieManager.getCookie(req, CookiesName.OAuthUserId);
        } catch (final CookieNotFoundException e) {
            logger.error("doFilter", "Nie odnaleziono Cookie name: OAUTHUSERID");
            // sprawdzamy czy mamy w sesji albo ciasteczku ACCESS_TOKEN.
            // jeśli mamy, to sprawdzamy sesję za pomocą REST web service.
            // jeśli WS odpowie, że na serwerze istnieje aktywna sesja, to
            // przedłużamy tą lokalną,
            try {
                final Cookie accessTokenCookie = CookieManager.getCookie(req, CookiesName.AccessToken);
                if (accessTokenCookie != null) {
                    setAccessTokenLifeTimeCookie(req, resp, accessTokenCookie);

                }
            } catch (final CookieNotFoundException e2) {
                try {
                    final OAuthClientRequest oauthRequest = createAuthorizationCodeRequest();
                    filterChain.doFilter(request, response);
                    resp.sendRedirect(oauthRequest.getLocationUri());

                    // zapisujemy w sesji url z jakiego weszlimy. Moďż˝e uda
                    // siďż˝ pďż˝niej do niego wrďż˝ciďż˝?
                    return true;
                } catch (final OAuthSystemException e1) {
                    logger.error("doFilter", e1);
                }
            }
        }
        return false;
    }

    private OAuthClientRequest createAuthorizationCodeRequest() throws OAuthSystemException {
        final String clientId = ConfigProperties.CLIENT_ID.getValue();
        final String redirectURI = ConfigProperties.ACCESS_TOKEN_URL.getValue();
        return OAuthClientRequest
                .authorizationProvider(OAuthProviderType.MYAUTH).setClientId(clientId)
                .setRedirectURI(redirectURI).setResponseType(ResponseType.CODE.toString())
                .setScope("user").buildQueryMessage();
    }

    private void setAccessTokenLifeTimeCookie(final HttpServletRequest req, final HttpServletResponse resp, final Cookie accessTokenCookie) throws CookieNotFoundException {
        final String accessToken = CookieManager.getCookie(req, CookiesName.AccessToken).getValue();
        final IServiceInvoker<AccessTokenWrapper<String>, UserSessionDataResponse> checkUserSessionInvoker = new CheckUserSessionInvoker<>();
        final UserSessionDataResponse userSessionResponse = checkUserSessionInvoker.invoke(new AccessTokenWrapper<>(accessToken, StringUtils.encodeBase64(accessToken)));
        final Cookie cookie = new Cookie(CookiesName.OAuthUserId.getValue(), userSessionResponse.getUserId());

        cookie.setMaxAge(userSessionResponse.getTokenLifeTime());
        cookie.setHttpOnly(true);
        CookieManager.setCookie(resp, cookie);

        accessTokenCookie.setMaxAge(accessTokenCookie.getMaxAge() + userSessionResponse.getTokenLifeTime());
        CookieManager.setCookie(resp, accessTokenCookie);
    }

    @Override
    public void init(final FilterConfig arg0) throws ServletException {
        logger.debug("init");
    }
}