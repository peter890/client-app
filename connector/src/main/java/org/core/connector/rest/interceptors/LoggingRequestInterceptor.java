/**
 * 
 */
package org.core.connector.rest.interceptors;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * @author piotrek
 *
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
  final static Logger logger = LoggerFactory.getLogger(LoggingRequestInterceptor.class);
  /**
   * Czy logowaæ dane wejœciowe.
   */
  private boolean logInput;
  /**
   * Czy logowaæ dane wyjœciowe.
   */
  private boolean logOutput;

  public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
      final ClientHttpRequestExecution execution) throws IOException {

    if (logInput) {
      traceRequest(request, body);
    }
    ClientHttpResponse response = execution.execute(request, body);
    if (logOutput) {
      traceResponse(response);
    }

    return response;
  }

  private void traceRequest(final HttpRequest request, final byte[] body) throws IOException {
    logger
        .debug("===========================request begin================================================");

    logger.debug("URI : " + request.getURI());
    logger.debug("Method : " + request.getMethod());
    logger.debug("Request Body : " + new String(body, "UTF-8"));
    logger
        .debug("==========================request end================================================");
  }

  private void traceResponse(final ClientHttpResponse response) throws IOException {
    logger
        .debug("============================response begin==========================================");
    try {
      logger.debug("Response Body : " + IOUtils.toString(response.getBody(), "UTF-8"));
    } catch (IOException e) {
      logger.error("traceResponse", e);
    } finally {
      logger.debug("status code: " + response.getStatusCode());
      logger.debug("status text: " + response.getStatusText());
      logger
          .debug("=======================response end=================================================");
    }
  }
  /**
   * Konstruktor.
   */
  public LoggingRequestInterceptor() {
    this(true, true);
  }
  /**
   * 
   * @param logInput czy logowaæ dane wejœciowe?
   * @param logOutput czy logowaæ dane wyjœciowe?
   */
  public LoggingRequestInterceptor(final boolean logInput, final boolean logOutput) {
    this.logInput = logInput;
    this.logOutput = logOutput;
  }
}
