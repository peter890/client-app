/**
 *
 */
package org.core.connector.rest.invokers;

import org.api.model.UserSessionDataResponse;
import org.core.connector.rest.AbstractServiceInvoker;
import org.core.connector.rest.adapters.GetRestServiceInvocationAdapter;
import org.core.connector.rest.api.AccessTokenWrapper;

public class CheckUserSessionInvoker<I extends AccessTokenWrapper<String>> extends AbstractServiceInvoker<I, UserSessionDataResponse, String, UserSessionDataResponse> {

    /* (non-Javadoc)
     * @see org.core.connector.rest.AbstractServiceInvoker#preprocessing(java.lang.Object)
     */
    @Override
    protected String preprocessing(final I input) {
        invocationAdapter = new GetRestServiceInvocationAdapter<>(
                "session/", input.getAccessToken(), UserSessionDataResponse.class);
        return input.getContent();
    }

    /* (non-Javadoc)
     * @see org.core.connector.rest.AbstractServiceInvoker#postprocessing(java.lang.Object)
     */
    @Override
    protected UserSessionDataResponse postprocessing(final UserSessionDataResponse input) {
        return input;
    }

}