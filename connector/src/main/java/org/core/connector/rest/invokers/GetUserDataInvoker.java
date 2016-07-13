/**
 * 
 */
package org.core.connector.rest.invokers;

import org.api.model.UserDataResponse;
import org.core.connector.rest.AbstractServiceInvoker;
import org.core.connector.rest.adapters.GetRestServiceInvocationAdapter;
import org.core.connector.rest.api.AccessTokenWrapper;

/**
 * @author piotrek
 */
public class GetUserDataInvoker<I extends AccessTokenWrapper<String>> extends
		AbstractServiceInvoker<I, UserDataResponse, String, UserDataResponse> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.core.connector.rest.AbstractServiceInvoker#preprocessing(java.lang
	 * .Object)
	 */
	@Override
	protected String preprocessing(final I input) {
    invocationAdapter = new GetRestServiceInvocationAdapter<String, UserDataResponse>("user/",
        input.getAccessToken(), UserDataResponse.class);
		return input.getContent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.core.connector.rest.AbstractServiceInvoker#postrocessing(java.lang
	 * .Object)
	 */
	@Override
	protected UserDataResponse postprocessing(final UserDataResponse input) {
		return input;
	}
}