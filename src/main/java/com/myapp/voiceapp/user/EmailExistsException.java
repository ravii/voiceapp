package com.myapp.voiceapp.user;

public class EmailExistsException extends Exception {

    public EmailExistsException(String message) {
        super(message);
    }

}
