/**
 * 
 */
package org.core.connector.rest.api;

import org.core.connector.ServiceInvocationException;

/**
 * @author piotrek
 *
 */
public interface IServiceInvoker<I, O> {
  /**
   * Wywo³uje us³ugê.
   * @param input obiekt wejœciowy connectora do us³ugi.
   * @return Zwraca obiekt connectora zwrócony przez us³ugê.
   */
	O invoke(I input) throws ServiceInvocationException;
}
