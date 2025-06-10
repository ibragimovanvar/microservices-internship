package com.epam.training.spring_boot_epam.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String s) {
        super(s);
    }
}
