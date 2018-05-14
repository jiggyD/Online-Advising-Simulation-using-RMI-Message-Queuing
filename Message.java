/*
	References :
	1)Starting and stopping thread: https://stackoverflow.com/questions/11917714/stopping-a-thread-by-a-swing-button
	2)Student advisor queue: https://code.google.com/archive/p/student-advisor-mq/source/default/source?page=2
	3)RMI application: https://www.javatpoint.com/RMI
	4)Serialize object: //https://stackoverflow.com/questions/2374436/when-should-i-implement-java-io-serializable-in-rmi

*/
import java.io.Serializable;


/**
 * structure to hold the information of the student request
 * for course clearance from advisor
*/

public class Message implements Serializable{

    private static final long serialVersionUID = 227L;

        String source;      //source= student/advisor
        String student_name;
        String course_name;
        int clearance;      //stores advisors decision clear=2 not clear=1

        public Message(){

        }
        public Message (String source,String student_name,String course_name,int clearance){
            this.source=source;
            this.student_name=student_name;
            this.course_name=course_name;
            this.clearance=clearance;

        }
        //getter and setter methods to serialize object
        public int getClearance() {
                return clearance;
        }

        public void setClearance(int clearance) {
                this.clearance = clearance;
        }

        public String getCourse_name() {
                return course_name;
        }

        public void setCourse_name(String course_name) {
                this.course_name = course_name;
        }

        public String getSource() {
                return source;
        }

        public void setSource(String source) {
                this.source = source;
        }

        public String getStudent_name() {
                return student_name;
        }

        public void setStudent_name(String student_name) {
                this.student_name = student_name;
        }
}
