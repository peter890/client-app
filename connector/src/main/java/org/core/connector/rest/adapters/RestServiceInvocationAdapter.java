/**
 * 
 */
package org.core.connector.rest.adapters;

import java.io.IOException;

import org.client.common.ConfigProperties;
import org.core.connector.ServiceInvocationException;
import org.core.connector.rest.api.IServiceInvocationAdapter;
import org.core.connector.rest.interceptors.BearerAuthInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * @author piotrek
 *
 */
public abstract class RestServiceInvocationAdapter<U, V> implements IServiceInvocationAdapter<U, V> {
	protected RestTemplate restTemplate;
	/**
   * Rest Url.
   */
  protected String restUrl;
	protected String restAction;
	protected String accessToken;
	@SuppressWarnings("rawtypes")
	protected Class outputClass;
	@SuppressWarnings("rawtypes")
	protected Class inputClass;

	private void init() {
		restTemplate = new RestTemplate();
		/*
		 * The BufferClientHttpRequestFactory allows the response to be read more than one time.
		 */
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		//restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
		restTemplate.getInterceptors().add(new BearerAuthInterceptor(accessToken));
		restTemplate.setErrorHandler(new ResponseErrorHandler() {

			public boolean hasError(final ClientHttpResponse response) throws IOException {
				return HttpStatus.OK != response.getStatusCode();
			}

			public void handleError(final ClientHttpResponse response) throws IOException {
				throw new ServiceInvocationException(String.valueOf(response.getStatusCode().value()), response.getStatusText());
			}
		});
		restUrl = ConfigProperties.RestUrl.getValue();
	}

	/**
	 * @param restAction
	 * @param accessToken
	 */
	public RestServiceInvocationAdapter(final String restAction, final String accessToken) {
		this.restAction = restAction;
		this.accessToken = accessToken;
		init();
	}

}