/**
 * 
 */
package org.core.connector.rest.api;


/**
 * @author piotrek
 *
 */
public class AccessTokenWrapper<T> {
	/**
	 * AccessToken
	 */
	private String accessToken;

	/**
	 * Opakowywany obiekt
	 */
	private T content;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(final String accessToken) {
		this.accessToken = accessToken;
	}

	public T getContent() {
		return content;
	}

	public void setContent(final T content) {
		this.content = content;
	}

	/**
	 * Konstruktor.
	 * 
	 * @param accessToken
	 *            access token
	 * @param content
	 *            opakowywany obiekt
	 */
	public AccessTokenWrapper(final String accessToken, final T content) {
		this.accessToken = accessToken;
		this.content = content;
	}

	/**
	 * Konstruktor.
	 */
	public AccessTokenWrapper() {
	}

}
