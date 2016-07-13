/**
 * 
 */
package org.core.connector;

/**
 * @author piotrek
 *
 */
public class ServiceInvocationException extends RuntimeException {
  /**
   * UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * Kod zakoñczenia b³êdu.
   */
  private String errorCode;

  /**
   * Opis b³êdu.
   */
  private String errorDescription;

  /**
   * @param errorCode
   * @param errorDescription
   */
  public ServiceInvocationException(final String errorCode, final String errorDescription) {
    super("[ErrorCode: " + errorCode + ", ErrorDescription: " + errorDescription + "]");
    this.errorCode = errorCode;
    this.errorDescription = errorDescription;
  }

  /**
   * @return Zwraca errorDescription
   */
  public String getErrorDescription() {
    return errorDescription;
  }

  /**
   * @return Zwraca errorCode
   */
  public String getErrorCode() {
    return errorCode;
  }

}
