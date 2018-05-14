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


/**
 * Notification class searches for advisor decision and notifies it to student
 *
 */
public class Notification extends JFrame implements ActionListener, WindowListener {
    private static final long serialVersionUID = 1L;
    private JButton start, stop;        // Stop and Start Button, Button to create client
    private JTextArea log;            //log and events
    private static final int PORT_NUMBER = 1099;
    static Registry registry = null;
    MQService queueService;
    static String host = "localhost";
    boolean search = true;
    Message msg = new Message();
    Thread p = null;
    public volatile boolean flag;

    //initialize the GUI
    Notification() {
        //North panel for 'Start/Stop'
        JPanel north = new JPanel();
        start = new JButton("Start");
        start.addActionListener(this);
        north.add(start);
        stop = new JButton("Stop");
        stop.addActionListener(this);
        north.add(stop);
        add(north, BorderLayout.NORTH);

        //Panels for log text areas
        JPanel center = new JPanel(new GridLayout(1, 2));
        log = new JTextArea(100, 100);
        log.setEditable(false);
        appendRoom("Log \n");
        center.add(new JScrollPane(log));
        add(center);

        //add listener for  events
        addWindowListener(this);
        setSize(500, 500);
        setTitle("Notifier");
        setVisible(true);
    }

    //this method appends msg to log area
    public void appendRoom(String str) {
        log.append(str);                //to append message to client log
        log.setCaretPosition(log.getText().length() - 1);
    }

    //tracks button clicked on GUI and performs action accordingly
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();  //retrieving the event object
        if (o == start) {
            if (p == null) {
                start.setEnabled(false);
                flag = true;
                p = new ListenFromAdvisor();
                p.start();
            }

        } else {
            try {
                //search=false;
                flag = false;
                p = null;
                stop.setEnabled(false);
                start.setEnabled(false);

            } catch (Exception e2) {
                System.out.println("Exception:" + e2);
            }
        }

    }


    //creating serverGUI instance
    public static void main(String[] arg) {
        new Notification();
    }

    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    class ListenFromAdvisor extends Thread {

        public void run() {
            while (flag) {
                Msg_Node_t msg_node = new Msg_Node_t();
                try {

                    registry = LocateRegistry.getRegistry(1099);
                    String MQserverURL = "rmi://" + host + "/queueService";
                    MQService MQSinf = (MQService) registry.lookup("queueService");

                    System.out.println("Searching for Advisor Decision  ");
                    appendRoom("\n->Searching for Advisor Decision : ");
                    msg = MQSinf.fetchMsg("advisor");
                    sleep(1000);

                    if (msg != null) {

                        int i = msg.getClearance();
                        appendRoom("\n Decision found");

                        appendRoom("\nADVISOR DECISION: " + msg.getStudent_name() + " for " + msg.getCourse_name());
                        if (i == 2)
                            appendRoom(" Cleared");
                        else if (i == 1)
                            appendRoom(" Not Cleared");

                        MQSinf.displayMsges();
                    } else {
                        appendRoom("\nNo approvals from advisor found!");
                        sleep(7000);

                    }


                } catch (Exception e1) {
                    System.out.println("Exception:" + e1);
                }

            }
        }

    }


}
