package org.core.connector.rest.interceptors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(LoggingRequestInterceptor.class);
    /**
     * Czy logowa� dane wej�ciowe.
     */
    private final boolean logInput;
    /**
     * Czy logowa� dane wyj�ciowe.
     */
    private final boolean logOutput;

    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                        final ClientHttpRequestExecution execution) throws IOException {

        if (this.logInput) {
            traceRequest(request, body);
        }
        final ClientHttpResponse response = execution.execute(request, body);
        if (this.logOutput) {
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
        } catch (final IOException e) {
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
     * @param logInput  czy logowa� dane wej�ciowe?
     * @param logOutput czy logowa� dane wyj�ciowe?
     */
    public LoggingRequestInterceptor(final boolean logInput, final boolean logOutput) {
        this.logInput = logInput;
        this.logOutput = logOutput;
    }
}