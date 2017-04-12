package com.itsys.imonitor.flume.entities;

import java.io.Serializable;

public class Models implements Serializable {

    private int id;
    private int port;

    public int getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
