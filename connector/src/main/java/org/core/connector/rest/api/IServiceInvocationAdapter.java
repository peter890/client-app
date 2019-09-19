package org.core.connector.rest.api;


public interface IServiceInvocationAdapter<U, V> {
    V execute(U input);
}
