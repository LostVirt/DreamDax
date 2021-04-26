package org.dreambot.walker.dax.models.exceptions;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}
