/**
 * 
 */
package org.core.connector.rest.api;

/**
 * @author piotrek
 *
 */
public interface IServiceInvocationAdapter<U, V> {
	public V execute(U input);
}
