package com.shotFormLetter.sFL.ExceptionHandler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "you can't access about data")
public class DataNotAccessException  extends  RuntimeException{
    private static final long serialVersionUID=1L;
    public DataNotAccessException(String message) {
        super(message);
    }
}
