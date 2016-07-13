/**
 * 
 */
package org.core.client.authorization;

/**
 * @author piotrek
 *
 */
public interface IAuthorizer {
  Boolean doLogin(String userEmail);

}
