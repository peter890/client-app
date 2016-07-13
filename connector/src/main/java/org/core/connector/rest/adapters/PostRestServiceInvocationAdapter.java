/**
 * 
 */
package org.core.connector.rest.adapters;

/**
 * @author piotrek
 *
 */
public class PostRestServiceInvocationAdapter<I, O> extends RestServiceInvocationAdapter<I, O> {

	/**
	 * @param restUrl
	 * @param restAction
	 * @param accessToken
	 */
	public PostRestServiceInvocationAdapter(final String restAction, final String accessToken) {
		super(restAction, accessToken);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.core.connector.rest.api.IServiceInvocationAdapter#execute(java.lang
	 * .Object)
	 */
	public O execute(final I input) {
		// TODO Auto-generated method stub
		return null;
	}

}
