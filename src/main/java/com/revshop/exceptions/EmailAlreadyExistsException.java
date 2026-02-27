package com.revshop.exceptions;


/**
 * Exception: EmailAlreadyExistsException
 */
public class EmailAlreadyExistsException extends RuntimeException{

    /**
     * @param message
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

}
