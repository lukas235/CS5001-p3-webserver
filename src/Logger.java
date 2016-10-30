import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Logger can log valid and invalid requests and write them to separate files.
 * The two files can be specified in the Configuration.java file.
 *
 * The logger also adds a timestamp and the IP adress from where the request came.
 */
public class Logger {

 /**
  * Log valid requests in Configuration.LOGFILE_VALID.
  *
  * @param request the request that shall be logged
  */
 public synchronized void logValid(String request) {
  try {
   File logFile = new File(Configuration.LOGFILE_VALID);

   if (!logFile.exists()) {
    System.out.println("File does not exist. Creating new File.");
    logFile.createNewFile();
   }

   Date date = new Date();
   SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

   request = sdf.format(date) + ": " + request;

   BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
   bw.append(request);
   bw.newLine();
   bw.close();
  }
  catch (IOException e) {
   System.out.println("Logger: " + e.getMessage());
  }
 }

 /**
  * Log invalid requests in Configuration.LOGFILE_INVALID.
  *
  * @param request the request that shall be logged
  */
 public synchronized void logInvalid(String request) {
  try {
   File logFile = new File(Configuration.LOGFILE_INVALID);

   if (!logFile.exists()) {
    System.out.println("File does not exist. Creating new File.");
    logFile.createNewFile();
   }

   Date date = new Date();
   SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

   request = sdf.format(date) + ": " + request;

   BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
   bw.append(request);
   bw.newLine();
   bw.close();

  }
  catch (IOException e) {
   System.out.println("Logger: " + e.getMessage());
  }
 }

}
