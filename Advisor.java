/*
	References :
	1)Starting and stopping thread: https://stackoverflow.com/questions/11917714/stopping-a-thread-by-a-swing-button
	2)Student advisor queue: https://code.google.com/archive/p/student-advisor-mq/source/default/source?page=2
	3)RMI application: https://www.javatpoint.com/RMI
	4)Serialize object: //https://stackoverflow.com/questions/2374436/when-should-i-implement-java-io-serializable-in-rmi

*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/*
-Advisor class searches the message queue server for any requests from student
-Start and stop buttons on GUI will prompt the advisor process to start/end searching
-If request are not found advisor sleeps for 3 secs and searches queue again
 */
public class Advisor extends JFrame implements ActionListener, WindowListener
{
  //  private static final long serialVersionUID = 1L;
    private JButton start, stop; 		// Stop and Start Button
    private JTextArea log; 			    //to log events
    private static final int PORT_NUMBER = 1099;    //port number for rmi
    static Registry registry = null;    //rmi registry
    MQService queueService;            //implementatioj of service
    static String host="localhost";    //server for rmi connection
    Message msg = new Message();       //instance of message class
    Thread p = null;                   //thread for listening from server
    public volatile boolean flag;      //to start/stop thread

    //initialize the GUI
    Advisor()
    {
        //North panel for 'Start/Stop'
        JPanel north = new JPanel();
        start = new JButton("Start");
        start.addActionListener(this);
        north.add(start);
        stop = new JButton("Stop");
        stop.addActionListener(this);
        north.add(stop);
        add(north, BorderLayout.NORTH);

        //Panels for log text area
        JPanel center = new JPanel(new GridLayout(1, 2));
        log = new JTextArea(100, 100);
        log.setEditable(false);
        appendMsg("Log \n");
        center.add(new JScrollPane(log));
        add(center);

        //add listener for  events
        addWindowListener(this);
        setSize(500, 500);
        setTitle("Advisor");
        setVisible(true);
    }

    //this method appends msg to log area
    public void appendMsg(String str)
    {
        log.append(str);  				//to append message to  log
        log.setCaretPosition(log.getText().length() - 1);
    }

    //tracks button clicked on GUI and performs action accordingly
    public void actionPerformed(ActionEvent e)
    {
        Object o = e.getSource();  //retrieving the event object
        if(o==start){
            if(p==null){
                start.setEnabled(false);
                flag=true;
                p=new ListenFromServer();
                p.start();
            }
        }
        else {
            try {
                flag=false;
                p=null;
                stop.setEnabled(false);
                start.setEnabled(false);

            }catch (Exception e2){
                System.out.println("Exception:"+e2);
            }
        }

    }


    //creating Advisor instance
    public static void main(String[] arg)
    {
        new Advisor();
    }

    public void windowClosing(WindowEvent e)
    {
        System.exit(0);
    }
    public void windowClosed(WindowEvent e) {	}
    public void windowOpened(WindowEvent e) {	}
    public void windowIconified(WindowEvent e) {	}
    public void windowDeiconified(WindowEvent e) {	}
    public void windowActivated(WindowEvent e) {	}
    public void windowDeactivated(WindowEvent e) {	}

    /*
    Thread to run continuously and check for request from students
    */
    class ListenFromServer extends Thread
    {

        public void run()
        {
            while (flag)
            {
                Msg_Node_t msg_node = new Msg_Node_t();
                try {
                    //locate registry
                    registry = LocateRegistry.getRegistry(1099);
                    String MQserverURL = "rmi://" + host + "/queueService";
                    MQService MQSinf = (MQService) registry.lookup("queueService");

                    System.out.println("Searching for request from student:  ");
                    appendMsg("\n->Searching for request from student: ");
                    msg = MQSinf.fetchMsg("student");
                    sleep(1000);

                    //if request is found
                    if(msg!=null){

                        int tmp = (int) (Math.random() * 2 + 1);         //random function for advisor decision
                        msg.setClearance(tmp);                           //set advisor decision
                        msg.setSource("advisor");                        //change the source for notification process

                        appendMsg("\n Request found");                //append decision in logs
                        appendMsg("\nADVISOR DECISION: " + msg.getStudent_name() + " for " + msg.getCourse_name());
                        if (tmp == 2)
                            appendMsg(" Cleared");
                        else if (tmp==1)
                            appendMsg(" Not Cleared");

                        msg_node.setRequest(msg);
                        msg_node.setNext(null);
                        msg_node.setPrev(null);
                        MQSinf.addMsg(msg_node);                            //add advisor decision in queue
                        MQSinf.displayMsges();
                    }
                    else{
                        appendMsg("\nNo Request from students found!");
                        sleep(3000);                                   //sleep for 3 sec and search again

                    }


                } catch (Exception e1) {
                    System.out.println("Exception:" + e1);
                }

            }
        }

    }


}
