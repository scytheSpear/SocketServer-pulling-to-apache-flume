/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itsys.imonitor.flume.entities;

//import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author user
 */
public class QueryRelationGroup implements Serializable {

    private String localIp;
    private String remoteIp;
    private String processId;
//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
//    private Date cmdTime;
    
    

    public String getLocalIp() {
        return localIp;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public String getProcessId() {
        return processId;
    }

//    public Date getCmdTime() {
//        return cmdTime;
//    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

//    public void setCmdTime(Date cmdTime) {
//        this.cmdTime = cmdTime;
//    }

}
