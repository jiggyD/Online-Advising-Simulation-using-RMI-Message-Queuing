/*
	References :
	1)Starting and stopping thread: https://stackoverflow.com/questions/11917714/stopping-a-thread-by-a-swing-button
	2)Student advisor queue: https://code.google.com/archive/p/student-advisor-mq/source/default/source?page=2
	3)RMI application: https://www.javatpoint.com/RMI
	4)Serialize object: //https://stackoverflow.com/questions/2374436/when-should-i-implement-java-io-serializable-in-rmi

*/

import java.io.Serializable;

/**
 * class to hold the messages in the queue fashion
 * implemented as a linked list
 */

public  class Msg_Node_t implements Serializable {

     Message request;   //instance of message that holds request info
     Msg_Node_t prev;   //previous and next pointers
     Msg_Node_t next;
    private static final long serialVersionUID = 225L;

    //constructor
    public Msg_Node_t(){}
    public Msg_Node_t(Message request,Msg_Node_t prev,Msg_Node_t next)
    {
        this.request=request;
        this.prev=prev;
        this.next=next;
    }

    //getter and setter methods to serialize object
    public Message getRequest() {
        return request;
    }

    public Msg_Node_t getNext() {
        return next;
    }

    public Msg_Node_t getPrev() {
        return prev;
    }

    public void setNext(Msg_Node_t next) {
        this.next = next;
    }

    public void setPrev(Msg_Node_t prev) {
        this.prev = prev;
    }

    public void setRequest(Message request) {
        this.request = request;
    }

    @Override
    public String toString(){
        return "MsgNode [Message=" + request + ", prev=" + prev + ", next=" + next + "]";
    }

}

