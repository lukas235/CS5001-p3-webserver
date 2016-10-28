import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
 BufferedWriter bw;
 PrintWriter pw;
 File logFile;

 public void log(String event) {
  try {
   logFile = new File(Configuration.logFile);
   
   if (!logFile.exists()) {
    System.out.println("File does not exist!");
    logFile.createNewFile();
   }

   Date date = new Date();
   SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
   
   event = sdf.format(date) + ": " + event;

   bw = new BufferedWriter(new FileWriter(logFile, true));
   bw.append(event);
   bw.newLine();
   bw.close();
   System.out.println(event);
  }
  catch (IOException e) {
   System.out.println("Logger: " + e.getMessage());
  }
 }
}
