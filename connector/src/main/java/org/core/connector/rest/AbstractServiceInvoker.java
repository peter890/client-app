/**
 * 
 */
package org.core.connector.rest;

import org.core.connector.rest.api.IServiceInvocationAdapter;
import org.core.connector.rest.api.IServiceInvoker;

/**
 * 
 * @author piotrek
 *
 * @param <I>
 *            - typ wejsciowy dla connectora
 * @param <O>
 *            - typ zwracany przez connector
 * @param <U>
 *            - typ wejsciowy dla WS
 * @param <V>
 *            - Typ zwracany przez WS
 */
public abstract class AbstractServiceInvoker<I, O, U, V> implements IServiceInvoker<I, O> {
  /**
   * Invocation Adapter.
   */
	protected IServiceInvocationAdapter<U, V> invocationAdapter;
	
	/**
   * Rest Url.
   */
  //protected String restUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.core.connector.rest.api.IServiceInvoker#invoke(java.lang.Object)
	 */
	public O invoke(final I input) {
		U wsInput = preprocessing(input);
		V wsResponse = invocationAdapter.execute(wsInput);
		O output = postprocessing(wsResponse);
		return output;
	}

	/**
	 * Procesowanie danych przed wywo³anie WS
	 * 
	 * @param input
	 *            dane wejœciowe dla WS
	 * @return
	 */
	abstract protected U preprocessing(I input);

	/**
	 * Procesowanie danych zwróconych przez WS
	 * 
	 * @param input
	 *            dane zwrócone przed WS
	 * @return
	 */
	abstract protected O postprocessing(V input);

}
