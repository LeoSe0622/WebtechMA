package de.htwberlin.webtech.korbgeld.common.error;

/** 502: Ein externer Dienst (z. B. Open Food Facts) antwortet nicht oder fehlerhaft. */
public class ExternalServiceException extends RuntimeException {

    private final String source;

    public ExternalServiceException(String source, String message, Throwable cause) {
        super(message, cause);
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
