package org.core.connector.rest;

import org.core.connector.rest.api.IServiceInvocationAdapter;
import org.core.connector.rest.api.IServiceInvoker;

/**
 * @param <I> - typ wejsciowy dla connectora
 * @param <O> - typ zwracany przez connector
 * @param <U> - typ wejsciowy dla WS
 * @param <V> - Typ zwracany przez WS
 */
public abstract class AbstractServiceInvoker<I, O, U, V> implements IServiceInvoker<I, O> {
    /**
     * Invocation Adapter.
     */
    protected IServiceInvocationAdapter<U, V> invocationAdapter;

    /*
     * (non-Javadoc)
     *
     * @see org.core.connector.rest.api.IServiceInvoker#invoke(java.lang.Object)
     */
    public O invoke(final I input) {
        final U wsInput = preprocessing(input);
        final V wsResponse = this.invocationAdapter.execute(wsInput);
        return postprocessing(wsResponse);
    }

    /**
     * Procesowanie danych przed wywolanie WS
     *
     * @param input dane wejsciowe dla WS
     */
    abstract protected U preprocessing(I input);

    /**
     * Procesowanie danych zwroconych przez WS
     *
     * @param input dane zwrocone przed WS
     */
    abstract protected O postprocessing(V input);
}