package com.revshop.exceptions;

public class ReviewRestrictedException extends RuntimeException{

    /**
     *
     * @param message
     */
    public ReviewRestrictedException(String message) {
        super(message);
    }
}
