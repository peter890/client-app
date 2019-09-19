package org.core.connector;

public class ServiceInvocationException extends RuntimeException {
    /**
     * UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Kod zako�czenia b��du.
     */
    private String errorCode;

    /**
     * Opis b��du.
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
