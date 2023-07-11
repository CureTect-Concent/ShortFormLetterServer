package com.shotFormLetter.sFL.ExceptionHandler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "can't upload file")
public class DataNotUploadException extends RuntimeException{
    private static final long serialVersionUID=1L;
    public DataNotUploadException (String message){
        super(message);
    }
}
