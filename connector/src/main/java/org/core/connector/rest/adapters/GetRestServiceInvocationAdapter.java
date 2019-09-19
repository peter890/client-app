package org.core.connector.rest.adapters;

import org.springframework.http.ResponseEntity;


public class GetRestServiceInvocationAdapter<I, O> extends RestServiceInvocationAdapter<I, O> {
    /**
     * @param restAction
     * @param accessToken
     */
    public GetRestServiceInvocationAdapter(final String restAction, final String accessToken, final Class<O> output) {
        super(restAction, accessToken);
        outputClass = output;
    }

    /* (non-Javadoc)
     * @see org.core.connector.rest.api.IServiceInvocationAdapter#execute(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public O execute(final I input) {
        ResponseEntity<O> result = restTemplate.getForEntity(restUrl + restAction + input, outputClass);
        return result.getBody();
    }
}