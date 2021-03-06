package com.linkedlogics.execution;

import com.linkedlogics.LogicContext;
import com.linkedlogics.annotation.*;
import com.linkedlogics.exception.LogicException;
import lombok.extern.java.Log;
import org.apache.tomcat.jni.File;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@LogicConfiguration
public class BasicActions {

    @Logic("add")
    public void add(@ContextParam(value = "list") List<String> list, @InputParam(value = "item") String item) {
        list.add(item) ;
    }

    @Logic(value = "remove", returnAs = "removed")
    public boolean remove(@ContextParam("list") List<String> list, @InputParam("item") String item) {
       return list.remove(item) ;
    }

    @Logic("error")
    public void error(@InputParam("error_code") Long errorCode, @InputParam("error_message") String errorMessage) {
        throw new LogicException(errorCode, errorMessage) ;
    }

    @Logic("request")
    @AsyncLogic(timeout = 5, unit = TimeUnit.SECONDS)
    public String request() {
        return "test" ;
    }

    @Logic("response")
    public void response(@ContextParam("list") List<String> list, @ContextParam("item") String item) {
        list.add(item) ;
    }

    @Logic(value = "retry", returnAs = "retried")
    @RetryLogic(attempts = 3, delay = 100, unit = TimeUnit.MILLISECONDS)
    public void retry(@InputParam("retries") Integer retries, @ContextParam LogicContext context) {
        int retried = (Integer) context.getContextParam("retried", 1) ;
        if (retried < retries) {
            context.setContextParam("retried", retried + 1) ;
            throw LogicException.high(-1, "failed") ;
        }
    }
}
