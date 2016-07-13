/**
 * 
 */
package org.core.connector.rest.interceptors;

import java.io.IOException;

import org.core.common.utils.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * @author piotrek
 *
 */
public class BearerAuthInterceptor implements ClientHttpRequestInterceptor {
  /**
   * AccessToken.
   */
  private String accessToken;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.http.client.ClientHttpRequestInterceptor#intercept(org.springframework.
   * http.HttpRequest, byte[], org.springframework.http.client.ClientHttpRequestExecution)
   */
  public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
      final ClientHttpRequestExecution execution) throws IOException {
    String authHeader = "Bearer " + StringUtils.encodeBase64(accessToken);
    // Add the auth-header
    request.getHeaders().add("Authorization", authHeader);
    return execution.execute(request, body);
  }

  public BearerAuthInterceptor(final String accessToken) {
    this.accessToken = accessToken;
  }
}