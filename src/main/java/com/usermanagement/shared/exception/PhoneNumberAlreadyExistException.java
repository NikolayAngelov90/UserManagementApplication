package com.usermanagement.shared.exception;

public class PhoneNumberAlreadyExistException extends RuntimeException {
    public PhoneNumberAlreadyExistException(String message) {
        super(message);
    }
}
