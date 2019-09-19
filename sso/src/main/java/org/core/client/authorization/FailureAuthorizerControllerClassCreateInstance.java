/**
 *
 */
package org.core.client.authorization;


/**
 * Wyjatek rzucany jesli w konfiguracji zabranie parametru "authorizeControllerClassName"
 */
public class FailureAuthorizerControllerClassCreateInstance extends RuntimeException {
    public FailureAuthorizerControllerClassCreateInstance() {
        super("Error while creating authorizationController instance!");
    }
}