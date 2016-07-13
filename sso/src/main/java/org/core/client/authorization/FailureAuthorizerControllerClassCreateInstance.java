/**
 * 
 */
package org.core.client.authorization;


/**
 * @author piotrek
 * Wyj¹tek rzucany jeœli w konfiguracji zabranie parametru "authorizeControllerClassName"
 */
public class FailureAuthorizerControllerClassCreateInstance extends RuntimeException{
	public FailureAuthorizerControllerClassCreateInstance() {
		super("Error while creating authorizationController instance!");
	}
}
