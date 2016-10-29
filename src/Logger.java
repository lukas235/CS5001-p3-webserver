import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class Logger.
 */
public class Logger {

 /**
  * Log valid.
  *
  * @param event the event
  */
 public synchronized void logValid(String event) {
  System.out.println("log valid");
  try {
   File logFile = new File(Configuration.logFileValid);
   
   if (!logFile.exists()) {
    System.out.println("File does not exist. Creating new File.");
    logFile.createNewFile();
   }

   Date date = new Date();
   SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
   
   event = sdf.format(date) + ": " + event;

   BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
   bw.append(event);
   bw.newLine();
   bw.close();
   System.out.println("CLosed valid");
  }
  catch (IOException e) {
   System.out.println("Logger: " + e.getMessage());
  }
 }
 
 /**
  * Log invalid.
  *
  * @param event the event
  */
 public synchronized void logInvalid(String event) {
  System.out.println("log invalid");
  try {
   File logFile = new File(Configuration.logFileInvalid);
   
   if (!logFile.exists()) {
    System.out.println("File does not exist. Creating new File.");
    logFile.createNewFile();
   }

   Date date = new Date();
   SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
   
   event = sdf.format(date) + ": " + event;

   BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
   bw.append(event);
   bw.newLine();
   bw.close();
   System.out.println("CLosed invalid");

  }
  catch (IOException e) {
   System.out.println("Logger: " + e.getMessage());
  }
 }
 
}
