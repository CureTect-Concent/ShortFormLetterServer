package com.shotFormLetter.sFL.ExceptionHandler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "entity not found")
public class DataNotMatchException extends RuntimeException{
    private static final long serialVersionUID=1L;
    public DataNotMatchException(String message){
        super(message);
    }
}
