package org.core.connector.rest.api;

import org.core.connector.ServiceInvocationException;

public interface IServiceInvoker<I, O> {
    /**
     * Wywo�uje us�ug�.
     *
     * @param input obiekt wej�ciowy connectora do us�ugi.
     * @return Zwraca obiekt connectora zwr�cony przez us�ug�.
     */
    O invoke(I input) throws ServiceInvocationException;
}