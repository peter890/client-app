package org.core.client.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

public class ClientSSOFilter implements Filter {
	/**
	 * Logger.
	 */
	final static Logger logger = LoggerFactory.getLogger(ClientSSOFilter.class);

	@Override
	public void destroy() {
		logger.debug("destroy");
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException {
		logger.debug("doFilter");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String path = req.getRequestURI();
		boolean excludedRequest = false;
		logger.debug("doFilter|path: {}", path);
		if (path.contains("accessToken")) {
			excludedRequest = true; 
		}

		if (!excludedRequest) {
//			try {
//				CookieManager.getCookie(req, CookiesName.AppId);
//			} catch (CookieNotFoundException e1) {
//				String clientId = ConfigProperties.ClientId.getValue();
//				CookieManager.setCookie(resp, CookiesName.AppId, clientId);
//			}

			try {
				CookieManager.getCookie(req, CookiesName.OAuthUserId);
			} catch (CookieNotFoundException e) {
				logger.error("doFilter", "Nie odnaleziono Cookie name: OAUTHUSERID");
				// sprawdzamy czy mamy w sesji albo ciasteczku ACCESS_TOKEN.
				// jeœli mamy, to sprawdzamy sesjê za pomoc¹ REST web service.
				// jeœli WS odpowie, ¿e na serwerze istnieje aktywna sesja, to
				// przed³u¿amy t¹ lokaln¹,
				try {
					Cookie accessTokenCookie = CookieManager.getCookie(req, CookiesName.AccessToken);
					if (accessTokenCookie != null) {
					  String accessToken = CookieManager.getCookie(req, CookiesName.AccessToken).getValue();
					  IServiceInvoker<AccessTokenWrapper<String>, UserSessionDataResponse> checkUserSessionInvoker = new CheckUserSessionInvoker<AccessTokenWrapper<String>>();
					  UserSessionDataResponse userSessionResponse = checkUserSessionInvoker.invoke(new AccessTokenWrapper<String>(accessToken, StringUtils.encodeBase64(accessToken)));
						Cookie cookie = new Cookie(CookiesName.OAuthUserId.getValue(), userSessionResponse.userId);
						cookie.setMaxAge(userSessionResponse.tokenLifeTime);
						cookie.setHttpOnly(true);
						CookieManager.setCookie(resp, cookie);
						
						accessTokenCookie.setMaxAge(accessTokenCookie.getMaxAge() + userSessionResponse.tokenLifeTime);
						CookieManager.setCookie(resp, accessTokenCookie);
					  
					} 
				} catch (CookieNotFoundException e2) {
					String clientId = ConfigProperties.ClientId.getValue();
					String redirectURI = ConfigProperties.AccessTokenUrl.getValue();
					try {
						OAuthClientRequest oauthRequest = OAuthClientRequest
								.authorizationProvider(OAuthProviderType.MYAUTH).setClientId(clientId)
								.setRedirectURI(redirectURI).setResponseType(ResponseType.CODE.toString())
								.setScope("user").buildQueryMessage();
						filterChain.doFilter(request, response);
						resp.sendRedirect(oauthRequest.getLocationUri());

						// zapisujemy w sesji url z jakiego weszlimy. Moï¿½e uda
						// siï¿½ pï¿½niej do niego wrï¿½ciï¿½?
						return;
					} catch (OAuthSystemException e1) {
						logger.error("doFilter", e1);
					}
				}

			}
		}
		filterChain.doFilter(request, response);
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {
		logger.debug("init");
	}
}
