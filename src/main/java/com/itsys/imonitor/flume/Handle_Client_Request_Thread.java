package com.itsys.imonitor.flume;

import com.itsys.imonitor.JDBC.JdbcConection;
import com.itsys.imonitor.flume.entities.Models;
import com.itsys.imonitor.flume.entities.QueryRelationGroup;
import java.net.*;
import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handle_Client_Request_Thread extends Thread {

    private static final Logger LOG = Logger.getLogger(Handle_Client_Request_Thread.class.getName());

    private Socket socket = null;
    private MyRpcClientFacade client = null;
    private QueryRelationGroup q = null;
    private JdbcConection jdbc = null;

    public Handle_Client_Request_Thread(Socket s, MyRpcClientFacade c) {
        super("HandleClientRequestThread");
        socket = s;
        client = c;
        q = new QueryRelationGroup();
        jdbc = new JdbcConection();
    }

    public static int bytesToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
        // "|="按位或赋值�?  
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }

//  public static int byteArrayToInt(byte[] b) 
//{
//    if(b.length==2){
//           return   b[1] & 0xFF |
//            (b[0] & 0xFF) << 8 ;
//    }else{
//    return   b[3] & 0xFF |
//            (b[2] & 0xFF) << 8 |
//            (b[1] & 0xFF) << 16 |
//            (b[0] & 0xFF) << 24;
//    }
//}
    @Override
    public void run() {
        try {
//	    ObjectInputStream iso = new ObjectInputStream(socket.getInputStream());

//read string from bufferedreader from socket
//            BufferedReader in = new BufferedReader( 
//            new InputStreamReader( socket.getInputStream())); 
//                    String log =  in.readLine();
//                    in.close();
            InputStream stream = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(stream);

            long startTime = System.currentTimeMillis();

//            byte offset = (byte) bis.read();
//            int type = (int) offset;
//            byte[] t = new byte[4];
//            bis.read(t);
//            int length = bytesToInt(t);
//                System.out.printf("length:%d\n", length);
            long endTime1 = System.currentTimeMillis();
            long totalTime1 = endTime1 - startTime;
            System.out.println("convert time: " + totalTime1);
//            
            byte[] content = new byte[1024];
            bis.read(content);
//            

            bis.close();
            stream.close();

//                    iso.close();
//               
            socket.close();

//            String type = offset.toString().split(" ")[0];
//            int len = Integer.valueOf(offset.toString().split(" ")[1]).intValue();
//            String soffset = new String(offset,"UTF-8");
//            String type = soffset.split(" ")[0];
//            int len = Integer.valueOf(soffset.split(" ")[1]).intValue();
            //BufferedReader b = new BufferedReader(new InputStreamReader(stream));
            //int a = stream.read(stream);
            String raw = new String(content, "UTF-8");
            String log = raw.trim();

            System.out.println("message from client: " + log+"\n Thead num: " + Thread.currentThread().getId());

            String localIp = null;
            String processId = null;
            int relationId = 0;
            int sourcePort = 0;
            String timeraw = null;
            String userName = null;
            String consoleId = null;
            String loginDate = null;
            String loginTime = null;
            String remoteIp = null;
//            String processId = null;
            String cmd = null;

            //TRY CATCH TO VALIDATE INTERGRITY OF LOG MESSAGE
            try {
                processId = log.split(" ")[0];
//            relationId= "1";
                localIp = log.split(" ")[1];
                timeraw = log.split(" ")[2];
                userName = log.split(" ")[3];
                consoleId = log.split(" ")[4];
                loginDate = log.split(" ")[5];
                loginTime = log.split(" ")[6];
                String remoteIpt = log.split(" ")[7];
                remoteIp = remoteIpt.substring(1, remoteIpt.length() - 1);
//            processId = log.split(" ")[8];
                cmd = log.split("\\%::\\%")[1];
            } catch (Exception e) {

            }

            if (localIp != null && processId != null && timeraw != null && userName != null && consoleId != null && loginDate != null && loginTime != null && remoteIp != null && cmd != null) {

                String time = timeraw.split(":")[0] + ":" + timeraw.split(":")[1] + ":" + timeraw.split(":")[2];
                String cmdTime = time.split("-")[0] + "-" + time.split("-")[1] + "-" + time.split("-")[2] + " " + time.split("-")[3];
                Date a = Timestamp.valueOf(cmdTime);
//            SimpleDateFormat b = new SimpleDateFormat("yyyy-mm-dd HH:MM:SS");
//                try {
//                    a = b.parse(cmdTime);
//                } catch (ParseException ex) {
//                    Logger.getLogger(Handle_Client_Request_Thread.class.getName()).log(Level.SEVERE, null, ex);
//                }

                LOG.log(Level.INFO, "CMD is : {0}", cmd);

                //create queryRelationGroup for http post query
                q.setLocalIp(localIp);
                q.setProcessId(processId);
                q.setRemoteIp(remoteIp);

                Thread.sleep(10000);

                Models m = jdbc.findRelation(q);

                if (m != null) {

                    relationId = m.getId();
                    sourcePort = m.getPort();

                } else {
                    System.out.println("error ID return from database ");
                }

//            String formatedLog = time + "\t" + userName + "\t" + consoleId + "\t" + loginDate + "\t" + loginTime + "\t" +remoteIp + "\t" + cmd;
                String formatedLog = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", localIp, relationId, time, userName, consoleId, loginDate, loginTime, remoteIp, cmd);
                System.out.println("formated message: " + formatedLog);
                String formatedOrder = String.format("%s\t%s\t%s\t%s\t%s", localIp, cmd, cmdTime, sourcePort, relationId);
                System.out.println("formated order message: " + formatedOrder);

                long endTime = System.currentTimeMillis();
                long processTime = endTime - startTime; 
                System.out.println("process time: " + processTime);

                try {
                    client.sendDataToFlume(formatedOrder);
                } catch (Exception e) {
                }
            } else {
                System.out.println("message include null message");
            }

            // }
            //client.cleanUp();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(Handle_Client_Request_Thread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Handle_Client_Request_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
