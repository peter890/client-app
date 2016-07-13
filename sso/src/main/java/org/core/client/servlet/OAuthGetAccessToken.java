package org.core.client.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.api.model.UserDataResponse;
import org.client.common.ConfigProperties;
import org.core.client.authorization.FailureAuthorizerControllerClassCreateInstance;
import org.core.client.authorization.IAuthorizer;
import org.core.common.enums.CookiesName;
import org.core.common.utils.CookieManager;
import org.core.common.utils.StringUtils;
import org.core.connector.ServiceInvocationException;
import org.core.connector.rest.api.AccessTokenWrapper;
import org.core.connector.rest.api.IServiceInvoker;
import org.core.connector.rest.invokers.GetUserDataInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Servlet implementation class OAuthGetAccessToken
 */
public class OAuthGetAccessToken extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(OAuthGetAccessToken.class);
	private IAuthorizer authorizer;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OAuthGetAccessToken() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);

		try {
			String authorizeControllerClassName = config.getInitParameter("authorizeControllerClassName");
			logger.debug("authorizeControllerClassName: {}", authorizeControllerClassName);
			Class<?> clazz = Class.forName(authorizeControllerClassName);
			authorizer = (IAuthorizer) clazz.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new FailureAuthorizerControllerClassCreateInstance();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse response) throws ServletException,
			IOException {
		logger.debug("doGet|START");
		String code = req.getParameter("code");
		OAuthClientRequest request;
		try {
			request = OAuthClientRequest.tokenProvider(OAuthProviderType.MYAUTH)
					.setGrantType(GrantType.AUTHORIZATION_CODE).setClientId(ConfigProperties.ClientId.getValue())
					.setClientSecret(ConfigProperties.ClientSecret.getValue())
					.setRedirectURI("http://oauthgate.com:8080/client/index").setCode(code).buildBodyMessage();
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

			// GitHubTokenResponse oAuthResponse =
			// oAuthClient.accessToken(request, GitHubTokenResponse.class);
			OAuthJSONAccessTokenResponse resp = oAuthClient.accessToken(request);

			System.out.println("Access Token: " + resp.getAccessToken() + ", Expires in: " + resp.getExpiresIn());

			IServiceInvoker<AccessTokenWrapper<String>, UserDataResponse> getUserDataInvoker = new GetUserDataInvoker<AccessTokenWrapper<String>>();
			UserDataResponse userData = getUserDataInvoker.invoke(new AccessTokenWrapper<String>(resp.getAccessToken(),
					StringUtils.encodeBase64(resp.getAccessToken())));

			Cookie cookie = new Cookie(CookiesName.OAuthUserId.getValue(), userData.getUserId());
			cookie.setMaxAge(resp.getExpiresIn().intValue());
			cookie.setHttpOnly(true);
			CookieManager.setCookie(response, cookie);

			Cookie accessTokenCookie = new Cookie(CookiesName.AccessToken.getValue(), resp.getAccessToken());
			accessTokenCookie.setMaxAge(2 * resp.getExpiresIn().intValue());
			accessTokenCookie.setHttpOnly(true);
			CookieManager.setCookie(response, accessTokenCookie);

			// response.sendRedirect(req.getContextPath());
			if (authorizer.doLogin(userData.getEmail())) {
				logger.info("Przekierowanie do: " + req.getContextPath());
				response.sendRedirect(req.getContextPath() + ConfigProperties.ClientWelcomePage.getValue());
			}

		} catch (OAuthSystemException e) {
			logger.debug("doGet", e);
		} catch (OAuthProblemException e) {
			logger.debug("doGet", e);
		} catch (final ServiceInvocationException e) {
			logger.error("GetUserDataInvoker", e);
		} catch (Exception e) {
			logger.error("", e);
			if (e instanceof AuthenticationException) {
				response.sendRedirect(req.getContextPath());
			}
		}
		logger.debug("doGet|STOP");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("doPost|START");
		logger.error("Nie powinno tutaj wejœæ!");
	}

	private static List<HttpMessageConverter<?>> getMessageConverters() {
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new MappingJackson2HttpMessageConverter());
		// converters.add(new MappingJackson2XmlHttpMessageConverter());
		return converters;
	}

}
