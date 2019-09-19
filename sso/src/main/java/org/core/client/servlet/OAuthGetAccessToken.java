package org.core.client.servlet;

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

import javax.net.ssl.*;
import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            final String authorizeControllerClassName = config.getInitParameter("authorizeControllerClassName");
            logger.debug("authorizeControllerClassName: {}", authorizeControllerClassName);
            final Class<?> clazz = Class.forName(authorizeControllerClassName);
            this.authorizer = (IAuthorizer) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new FailureAuthorizerControllerClassCreateInstance();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse response) throws ServletException,
            IOException {
        logger.debug("doGet|START");
        final String code = req.getParameter("code");

        try {
            fixUntrustCertificate();
            final OAuthClientRequest request = OAuthClientRequest.tokenProvider(OAuthProviderType.MYAUTH)
                    .setGrantType(GrantType.AUTHORIZATION_CODE).setClientId(ConfigProperties.CLIENT_ID.getValue())
                    .setClientSecret(ConfigProperties.CLIENT_SECRET.getValue())
                    .setRedirectURI(ConfigProperties.CLIENT_CALLBACK_URL.getValue()).setCode(code).buildBodyMessage();
            final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

            // GitHubTokenResponse oAuthResponse =
            // oAuthClient.accessToken(request, GitHubTokenResponse.class);
            final OAuthJSONAccessTokenResponse resp = oAuthClient.accessToken(request);

            System.out.println("Access Token: " + resp.getAccessToken() + ", Expires in: " + resp.getExpiresIn());

            final IServiceInvoker<AccessTokenWrapper<String>, UserDataResponse> getUserDataInvoker = new GetUserDataInvoker<>();
            final UserDataResponse userData = getUserDataInvoker.invoke(new AccessTokenWrapper<>(resp.getAccessToken(),
                    StringUtils.encodeBase64(resp.getAccessToken())));

            final Cookie cookie = new Cookie(CookiesName.OAuthUserId.getValue(), userData.getUserId());
            cookie.setMaxAge(resp.getExpiresIn().intValue());
            cookie.setHttpOnly(true);
            CookieManager.setCookie(response, cookie);

            final Cookie accessTokenCookie = new Cookie(CookiesName.AccessToken.getValue(), resp.getAccessToken());
            accessTokenCookie.setMaxAge(2 * resp.getExpiresIn().intValue());
            accessTokenCookie.setHttpOnly(true);
            CookieManager.setCookie(response, accessTokenCookie);

            // response.sendRedirect(req.getContextPath());
            if (this.authorizer.doLogin(userData.getEmail())) {
                logger.info("Przekierowanie do: " + req.getContextPath());
                response.sendRedirect(req.getContextPath() + ConfigProperties.CLIENT_WELCOME_PAGE.getValue());
            }

        } catch (final OAuthSystemException | OAuthProblemException e) {
            logger.debug("doGet", e);
        } catch (final ServiceInvocationException e) {
            logger.error("GetUserDataInvoker", e);
        } catch (final Exception e) {
            logger.error("", e);
            if (e instanceof AuthenticationException) {
                response.sendRedirect(req.getContextPath());
            }
        }
        logger.debug("doGet|STOP");
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("doPost|START");
        logger.error("Nie powinno tutaj wejsc!");
    }

    private static List<HttpMessageConverter<?>> getMessageConverters() {
        final List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        // converters.add(new MappingJackson2XmlHttpMessageConverter());
        return converters;
    }

    private void fixUntrustCertificate() throws KeyManagementException, NoSuchAlgorithmException {
        final List<String> allowedHostname = Arrays.asList("oauthgate.com");

        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {

                    }
                }
        };

        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        final HostnameVerifier allHostsValid = (hostname, session) -> allowedHostname.contains(hostname);

        // set the  allTrusting verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

}
