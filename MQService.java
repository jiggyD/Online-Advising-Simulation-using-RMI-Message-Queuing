/*
	References :
	1)Starting and stopping thread: https://stackoverflow.com/questions/11917714/stopping-a-thread-by-a-swing-button
	2)Student advisor queue: https://code.google.com/archive/p/student-advisor-mq/source/default/source?page=2
	3)RMI application: https://www.javatpoint.com/RMI
	4)Serialize object: //https://stackoverflow.com/questions/2374436/when-should-i-implement-java-io-serializable-in-rmi

*/
import java.rmi.*;

/**
 * Create remote interface for rmi application
 * All the methods work on queue implemented through link list structure
 */
public interface MQService extends Remote {


    // adds a new message to the front of the queue
    public int addMsg(Msg_Node_t newMsg)throws RemoteException;

    //compares the message based on student & course name
    public int compareMsgRequest(Message req1,Message req2) throws RemoteException;

   //removes a message from the queue
    public int removeMsg(Msg_Node_t msg)throws RemoteException;

   //copies message from one structure var to another
    public void copy_message(Message msg1, Message msg2)throws RemoteException;

    //this method fetch the first message of the type source
    public Message fetchMsg(String source)throws RemoteException;

    //displays all the messages in the queue
    public String displayMsges()throws RemoteException;

    //saves pending messages to text file
    public void saveMsges()throws RemoteException;

    //Restoring messages from file to linked list
    public String restoreMsges()throws RemoteException;

}
