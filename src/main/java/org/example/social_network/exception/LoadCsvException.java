package org.example.social_network.exception;

public class LoadCsvException extends Exception {
    private final ErrorCsv code;
    private final int lineNumber;

    public LoadCsvException(ErrorCsv code, int lineNumber, String message) {
        super(message);
        this.code = code;
        this.lineNumber = lineNumber;
    }

    public LoadCsvException(ErrorCsv code, int lineNumber, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.lineNumber = lineNumber;
    }

    public ErrorCsv getCode() {
        return code;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
