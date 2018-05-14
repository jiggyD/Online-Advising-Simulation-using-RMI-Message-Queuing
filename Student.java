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
 * Student class allows student to request for enrollment
 * user can enter student name and course name in GUI to send request
 *
 */

public class Student extends JFrame implements ActionListener, WindowListener
{
    private static final long serialVersionUID = 1L;
    private JButton start, stop; 		// Stop and Start Button, Button to create client
    private JTextArea log; 			//log area
    private JTextField sname,cname;
    private static final int PORT_NUMBER = 1099;
    static Registry registry = null;
    MQService queueService;
    Message msg_1;
    Msg_Node_t msg_node;
    //initialize the GUI
    Student()
    {
        //North panel for 'Start/Stop'
        JPanel info = new JPanel();
        info.add(new JLabel("Please enter details: "));
        info.setSize(500,300);
        info.setVisible(true);
        add(info, BorderLayout.NORTH);

        JPanel north = new JPanel(new GridLayout(4, 2));
        north.setSize(300,300);
        north.add(new JLabel("Student name: "));
        sname = new JTextField("  ");
        sname.setSize(200,100);
        north.add(sname);
        north.add(new JLabel("Course name: "));
        cname = new JTextField("  ");
        north.add(cname);
        add(north, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setSize(500,100);
        start = new JButton("Send Request");
        start.addActionListener(this);
        south.add(start);

        add(south, BorderLayout.SOUTH);
        //add listener for client events
        addWindowListener(this);
        setSize(500, 300);
        setTitle("Student");
        setVisible(true);
    }

    //this method appends msg to log area
    public void appendRoom(String str)
    {
        log.append(str);  				//to append message to  log
        log.setCaretPosition(log.getText().length() - 1);
    }

    //tracks button clicked on GUI and performs action accordingly
    public void actionPerformed(ActionEvent e)
    {
        Object o = e.getSource();  //retrieving the event object
        if(o==start){
            try {
                registry = LocateRegistry.getRegistry(1099);
                String MQserverURL="rmi://localhost/queueService";
                MQService MQSinf=(MQService)registry.lookup("queueService");
                try
                {
                    String name = sname.getText().trim();
                    String course=cname.getText().trim();
                    sname.setText(" ");
                    cname.setText(" ");
                    msg_1= new Message();
                    msg_1.setSource("student");
                    msg_1.setStudent_name(name);
                    msg_1.setCourse_name(course);
                    msg_1.setClearance(-1);

                    msg_node= new Msg_Node_t();
                    msg_node.setRequest(msg_1);
                    msg_node.setNext(null);
                    msg_node.setPrev(null);

                    MQSinf.addMsg(msg_node);
                    MQSinf.displayMsges();

                } catch (Exception er) {
                    appendRoom("Invalid name");
                    return;
                }



            }catch (Exception e1){
                System.out.println("Exception:"+e1);
            }
        }


    }


    //creating serverGUI instance
    public static void main(String[] arg)
    {
        new Student();
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
