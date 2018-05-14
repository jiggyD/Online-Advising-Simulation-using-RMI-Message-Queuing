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
 * MsgQueueServer class creates remote object and bind it to the RMIregistry.
 * Student / Advisor /Notification process will invoke the services
 */

public class MsgQueueServer extends JFrame implements ActionListener, WindowListener
{
    private static final long serialVersionUID = 1L;
    private JButton start, stop; 		// Stop and Start Button, Button to create client
    private JTextArea log, event; 			//log and events
    private static final int PORT_NUMBER = 1099;
    static Registry registry = null;
    MQService queueService;
    //initialize the GUI
    MsgQueueServer()
    {
        //North panel for 'Start/Stop' and 'Create Client'
        JPanel north = new JPanel();
        start = new JButton("Start");
        start.addActionListener(this);
        north.add(start);
        stop = new JButton("Stop");
        stop.addActionListener(this);
        north.add(stop);
        add(north, BorderLayout.NORTH);

        //Panels for log and events  text areas
        JPanel center = new JPanel(new GridLayout(1, 2));
        log = new JTextArea(100, 100);
        log.setEditable(false);
        appendRoom("Log \n");
        center.add(new JScrollPane(log));
        add(center);

        //add listener for client events
        addWindowListener(this);
        setSize(700, 500);
        setTitle("Server");
        setVisible(true);
    }

    //this method appends msg to log area
    public void appendRoom(String str)
    {
        log.append(str);  				//to append message to client log
        log.setCaretPosition(log.getText().length() - 1);
    }

       //tracks button clicked on GUI and performs action accordingly
    public void actionPerformed(ActionEvent e)
    {
        Object o = e.getSource();  //retrieving the event object
        if(o==start){
            try {
                //create instance of service
                queueService= new MQServiceImp();
                registry = LocateRegistry.createRegistry(PORT_NUMBER);
                registry.bind("queueService", queueService);
                appendRoom("MsgQueueServer is ready for remote invocations by client");
                //restore pending msgs from text file
                String result=queueService.restoreMsges();
                start.setEnabled(false);
                appendRoom(result);

            }catch (Exception e1){
                System.out.println("Exception:"+e1);
            }
        }
        else {
            try {

                appendRoom(queueService.displayMsges());
                queueService.saveMsges();
                appendRoom("\nMessages Persisted ");
                stop.setEnabled(false);
                start.setEnabled(false);


            }catch (Exception e2){
                System.out.println("Exception:"+e2);
            }
        }

    }


    //creating serverGUI instance
    public static void main(String[] arg)
    {
        new MsgQueueServer();
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



}
