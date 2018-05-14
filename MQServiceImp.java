/*
	References :
	1)Starting and stopping thread: https://stackoverflow.com/questions/11917714/stopping-a-thread-by-a-swing-button
	2)Student advisor queue: https://code.google.com/archive/p/student-advisor-mq/source/default/source?page=2
	3)RMI application: https://www.javatpoint.com/RMI
	4)Serialize object: //https://stackoverflow.com/questions/2374436/when-should-i-implement-java-io-serializable-in-rmi

*/

import java.io.*;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;


/**
 *  MQServiceImp provides implementation of the remote interface
 */
public class MQServiceImp extends UnicastRemoteObject implements MQService {

    public MQServiceImp() throws RemoteException {
        super();
    }


    Msg_Node_t head=null;   //head pointer for Message queue
    int msgCount=0;         //msgCount for Message Queue
    File inFile = null;		//file handler
    static Message ret=null;



    /**
     * adds a new message to the front of the queue
     */
    public int addMsg(Msg_Node_t newMsg){
        Msg_Node_t temp;

        if(newMsg==null){
            System.out.println("newMsg is null, insert failed\n");
            return 0;
        }

        if(head==null){
            head=newMsg;
            msgCount++;
            return 1;
        }

        temp=head;
        while(temp.next!=null){
            temp=temp.next;
        }
        temp.next=newMsg;
        newMsg.prev=temp;
        msgCount++;
        return 1;
    }

    //compares the message based on student & course name
    public int compareMsgRequest(Message req1,Message req2){
        if(req1==null || req2==null){
            System.out.println("compare failed, null message");
            return 0;
        }
        if( req1.student_name==req2.student_name && req1.course_name==req2.course_name )
            return 1;
        else
            return 0;
    }


    /**
     * removes a message from the queue
     */
    public int removeMsg(Msg_Node_t msg){
        Msg_Node_t temp;
        if(msg==null){
            System.out.println("remove failed, newMsg is null\n");
            return 0;
        }
        temp=head;
        if(temp!=null  && msgCount>0) {
            if(compareMsgRequest(msg.request,temp.request)==1){
                temp=temp.next;
                head=temp;
                msgCount--;
                return 1;
            }
            while(temp.next!=null){
                if(compareMsgRequest(msg.request,temp.request)==1){
                    temp.prev.next=temp.next;
                    temp.next.prev=temp.prev;
                    msgCount--;
                    return 1;
                }
                temp=temp.next;
            }
            if(temp!=null){
                if(compareMsgRequest(msg.request,temp.request)==1){
                    Msg_Node_t rel=temp;
                    temp=temp.prev;
                    temp.next=null;
                    msgCount--;

                    return 1;
                }
            }
        }
        System.out.println("message not found\n");
        return 0;

    }

    /**
     * copies message from one structure var to another
     */
    public void copy_message(Message msg1, Message msg2){
          msg1.source=msg2.source;
        msg1.student_name=msg2.student_name;
        msg1.course_name=msg2.course_name;
        msg1.clearance=msg2.clearance;
 }



    /**
     * this method fetch the first message of the type source
     */
    public  Message fetchMsg(String source){
        Msg_Node_t temp;
        Message ret= new Message() ;

        if(source==null){
            System.out.println("fetch failed, source is null\n");
            //return 0;
            return null;
        }
        System.out.println("\nlooking for messages ");
        temp=head;
        if(temp!=null && msgCount>0){
            while(temp!=null){
                if(temp.request.source.equals(source)){
                    copy_message(ret,temp.request);
                    System.out.println("Message found:");
                    System.out.println("Source    : "+ret.source+
                            ", Student Name : "+ret.student_name+
                            ", Course Name : "+ret.course_name+
                            ", clearance : "+ret.clearance);
                    removeMsg(temp);
                    return ret;
                }
                temp=temp.next;
            }
         }
        else {
            System.out.println("No Messages \n");
            return null;
        }
        return null;
    }



    /**
     * displays all the messages in the queue
     */
    public String displayMsges(){
        Msg_Node_t temp=head;
        String disp="";

        if(temp==null){
            disp="\nMessage queue is empty \n";
            System.out.println("Message queue is empty \n");
            //return ;
        }
        while(temp!=null){
            System.out.print("\nSource : "+temp.request.source);
            System.out.print(", Student Name : "+temp.request.student_name);
            System.out.print(", Course Name : "+temp.request.course_name);
            System.out.print(", Clearance : "+temp.request.clearance);
            disp=disp+"\nSource : "+temp.request.source+
                      ",Student Name : "+temp.request.student_name+
                      ",Course Name : "+temp.request.course_name+
                      ",Clearance : "+temp.request.clearance;


            temp=temp.next;
        }
        return disp;
    }

    //saves pending messages to text file
    public  void saveMsges(){

        try
        {


            URL url = getClass().getResource("Msg_File_Persist.txt");
            //File file = new File(url.getPath());
            BufferedWriter writer = new BufferedWriter(new FileWriter(url.getPath(), false));
          
            if(msgCount>0 && head!=null){
                int i=0;
                Msg_Node_t temp=head;
                writer.append(""+msgCount);

                for(i=0;i<msgCount && temp!=null ;i++,temp=temp.next){
                    writer.append("\n"+temp.request.source);
                    writer.append(","+temp.request.student_name);
                    writer.append(","+temp.request.course_name);
                    writer.append(","+temp.request.clearance);
                    //writer.append(" \n");

                }
                System.out.println("Messages persisted\n");
            }
            else
                System.out.println("No Messages persisted \n");
            //fclose(msgFile);
            writer.close();
        }
        catch(IOException ex)
        {
            System.err.println("Exception: " + ex.getMessage());
        }
    }

    /**
     * Restoring messages from file to linked list
     */
    public String restoreMsges(){
        int readMsgCount=0;
        //FILE msgFile=NULL;
        int student_id, course_id,clearance;
        String  source,student_name,course_name;
        String line = null;
        Message msg =null;
        Msg_Node_t msg_node =null;
        Scanner s1,s;
        String result="";

        try
        {
            URL url = getClass().getResource("Msg_File_Persist.txt");
            inFile = new File(url.getPath());
            //inFile = new File(FILE_NAME); 							// The dictionary file
            FileInputStream fis = new FileInputStream(inFile);		//open the file
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));  //to read contents of file
            s1 = new Scanner(br);
            //readMsgCount=Integer.parseInt(br.readLine());
            readMsgCount=Integer.parseInt(s1.nextLine());
            System.out.println("No of messages in file : "+readMsgCount);
            result="\n No of messages in file : "+readMsgCount;

            if(readMsgCount>0 ){

                int i = 0;
                for (i = 0; i < readMsgCount; i++) {
                    s = new Scanner(s1.nextLine()).useDelimiter(",");
                    msg = new Message();
                    msg_node = new Msg_Node_t();
                    msg.source = s.next();
                    msg.student_name  = s.next();
                    msg.course_name = s.next();
                    msg.clearance = Integer.parseInt((s.next()).trim());

                    msg_node.request = msg;
                    msg_node.next = null;
                    msg_node.prev = null;
                    addMsg(msg_node);
                 }
                    result=result+"\nMessages restored successfully \n";
                    System.out.println("Messages restored successfully \n");
                }
                else
                {   result=result+"\nMessages restore failed \n";
                    System.out.println("Messages restore failed \n");
                }

            br.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static void main(String args[]) throws RemoteException {

        try {
            MQServiceImp  mqs = new MQServiceImp();
            Naming.rebind("MQServiceImp", mqs);
            System.out.println("Message queue is ready");
            mqs.restoreMsges();
            mqs.displayMsges();
        }catch(Exception e){
            System.out.println("Exception :"+e);
        }
    }

}//MQServiceImp


