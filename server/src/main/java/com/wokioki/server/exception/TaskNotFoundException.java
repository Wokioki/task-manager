package com.wokioki.server.exception;

public class TaskNotFoundException extends RuntimeException{
    private final long id;


    public TaskNotFoundException(Long id){
        super("Task not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
