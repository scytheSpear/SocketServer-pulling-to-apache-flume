package com.itsys.imonitor.flume;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class SocketFlumeClient {
    
    private static final Logger LOG = Logger.getLogger(SocketFlumeClient.class.getName());

    public static void main(String[] args) throws IOException {
        
        MyRpcClientFacade client = new MyRpcClientFacade();
        // Initialize client with the remote Flume agent's host and port
        client.init("172.16.16.236", 44444);

        // MyRpcClientFacade client2 = new MyRpcClientFacade();
        //client2.init("tba", 44444);
       
        ServerSocket serverSocket = null;
        Socket socketclient;
        boolean listening = true;
        
        try {
            serverSocket = new ServerSocket(5100);
        } catch (IOException e) {
            
            System.err.println("Could not listen on port: 5100");
            System.exit(-1);
        }
        
        System.out.println("Server is ready ");
        while (listening) {
            socketclient = serverSocket.accept();
            LOG.info("接收到请求");
            Handle_Client_Request_Thread t = new Handle_Client_Request_Thread(socketclient, client);
            t.start();
            
        }
        
        serverSocket.close();

//    String sampleData = String.format("%s\t%s\t%s\t%s\t%s", "172.16.16.235", "test", "2016-06-03 11:50:09", "test", "0");
//    for (int i = 0; i < 10; i++) {
//      client.sendDataToFlume(sampleData);
//    }
//    
//        client.cleanUp();
    }
}
