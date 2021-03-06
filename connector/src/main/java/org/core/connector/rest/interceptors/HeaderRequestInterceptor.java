package org.core.connector.rest.interceptors;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;

public class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {
    private final String headerName;
    private final String headerValue;

    /**
     * @param headerName
     * @param headerValue
     */
    public HeaderRequestInterceptor(final String headerName, final String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    /* (non-Javadoc)
     * @see org.springframework.http.client.ClientHttpRequestInterceptor#intercept(org.springframework.http.HttpRequest, byte[], org.springframework.http.client.ClientHttpRequestExecution)
     */
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
            throws IOException {
        HttpRequest wrapper = new HttpRequestWrapper(request);
        wrapper.getHeaders().set(headerName, headerValue);
        return execution.execute(wrapper, body);
    }
}