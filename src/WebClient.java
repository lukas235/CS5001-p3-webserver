import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;


public class WebClient extends Thread {

 private Socket con; // socket representing TCP/IP connection to Client
 private InputStream is; // get data from client on this input stream 
 private OutputStream os; // can send data back to the client on this output stream
 private BufferedReader br; // use buffered reader to read client data
 private File documentRoot;

 public WebClient(File documentRoot, Socket con) {
  this.documentRoot = documentRoot;
  this.con = con;
  
  try {
   is = con.getInputStream(); // get data from client on this input stream
   os = con.getOutputStream(); // to send data back to the client on this stream
   br = new BufferedReader(new InputStreamReader(is)); // use buffered reader to read client data
   
  }
  catch (IOException ioe) {
   System.out.println("WebClient x: " + ioe.getMessage());
  }
 }

 private void handle() throws IOException {
  while (true) {
   Request request = new Request(br.readLine());

   //String line = br.readLine(); // get data from client over socket

   // if readLine fails we can deduce here that the connection to the client is broken
   // and shut down the connection on this side cleanly by throwing a DisconnectedException
   // which will be passed up the call stack to the nearest handler (catch block)
   // in the run method
   if (request.getRequest() == null || request.getRequest().equals("null") || request.getRequest().equals("exit")) {
    System.out.println("puff");
    closeClientConnection();
   }

   else if (request.isValidHttp()) {
    System.out.println("Path: "+request.getResourceName());
    File path = new File(documentRoot.toString() + request.getResourceName().toString());
    System.out.println(path.toString());
    
    if (path.exists()) { //.exists()
     System.out.println("Path exists: " + path.toString());
     if (path.isFile()) {
      
      byte[] webPage = getByteFromFile(path);
      
      String webPageSize = "Content-Length: " + webPage.length + "\r\n";
      String contentType = "Content-Type: " + Files.probeContentType(Paths.get(path.toString())) + "\r\n";
      os.write("HTTP/1.1 200 OK\r\n".getBytes());
      os.write("Server: My WebServer\r\n".getBytes());
      os.write(contentType.getBytes());
      os.write(webPageSize.getBytes());
      os.write("\r\n".getBytes());
      
      os.write(webPage);
     }
     
     // see if that's ok
     else if (path.isDirectory()){
      File dirpath = new File(path.toString()+"/index.html");
      System.out.println(dirpath);
      if (dirpath.isFile()){
       byte[] webPage = Files.readAllBytes(Paths.get(dirpath.toString()));
       String webPageSize = "Content-Length: " + webPage.length + "\r\n";
       String contentType = "Content-Type: " + Files.probeContentType(Paths.get(dirpath.toString())) + "\r\n";
       os.write("HTTP/1.1 200 OK\r\n".getBytes());
       os.write("Server: My WebServer\r\n".getBytes());
       os.write(contentType.getBytes());
       os.write(webPageSize.getBytes());
       os.write("\r\n".getBytes());
       
       os.write(webPage);
      }
     }
     

         
    }
    
    else if (!request.isValidHttp()) {
     String errorMessage = "<html><body><h1>ERROR: 404</h1><p>Path "+request.getRequest()+" not implemented.</p></body></html>";
     
     String errorLength = "Content-Length: "+errorMessage.length()+"\r\n";
     System.out.println("Path "+request.getResourceName()+" not found");
     os.write("HTTP/1.1 501 Not Implemented\r\n".getBytes());
     os.write("Server: My WebServer\r\n".getBytes());
     os.write("Content-Type: text/html\r\n".getBytes());
     os.write(errorLength.getBytes());
     os.write("\r\n".getBytes());
     
     os.write(errorMessage.getBytes());
    }
    
    
    else {
     String errorMessage = "<html><body><h1>ERROR: 404</h1><p>Path "+request.getResourceName().toString()+" not found.</p></body></html>";
     
     String errorLength = "Content-Length: "+errorMessage.length()+"\r\n";
     System.out.println("Path "+request.getResourceName()+" not found");
     os.write("HTTP/1.1 404 Not Found\r\n".getBytes());
     os.write("Server: My WebServer\r\n".getBytes());
     os.write("Content-Type: text/html\r\n".getBytes());
     os.write(errorLength.getBytes());
     os.write("\r\n".getBytes());
     
     os.write(errorMessage.getBytes());
    }
   }

   //    
   //   }

   // in this simple setup all the server does in response to messages from the client is to send
   // a single ACK byte back to client - the client uses this ACK byte to test whether the 
   // connection to this server is still live, if not the client shuts down cleanly //TODO sendalive
//   Byte isAlive = 1;
//   os.write(isAlive);

   //String string = "Halt's Maul\n";

   //byte[] b = string.getBytes();
   //byte[] b = string.getBytes(Charset.forName("UTF-8"));
   //byte[] b = string.getBytes(StandardCharsets.UTF_8); // Java 7+ only

   //os.write(b);
   System.out.println("WebClient: " + request.getRequest()); // assuming no exception, print out line received from client
  }
 }

 public void run() { // run method is invoked when the Thread's start method (ch.start(); in Server class) is invoked  
  System.out.println("-- New WebClient Thread started --");
  try {
   handle();
  }
  catch (Exception e) { // exit cleanly for any Exception (including IOException, ClientDisconnectedException)
   System.out.println("WebClient: " + e.getMessage());
   closeClientConnection(); // cleanup and exit
  }
 }

 public void closeClientConnection() {
  System.out.println("Closing WebClient connecton.");
  try {
   br.close();
   os.close(); //??
   is.close();
   con.close();
   System.out.println("WebClient connecton successfully closed.");
  }
  catch (IOException ioe) {
   System.out.println("Error while closing WebClient connection: " + ioe.getMessage());
  }
 }
 
 private byte[] getByteFromFile(File file) {
  byte[] buffer = new byte[(int)file.length()];
  InputStream is = null;
  try{
   is = new FileInputStream(file);
   
   if (is.read(buffer) == -1) {
    throw new IOException("EOF");
   }
  }
   
   finally {
    try {
     if (is != null)
      is.close();
    }
    catch (IOException e) {
     
    }
    return buffer;
   }
  }
  
  
//  BufferedReader fr = new BufferedReader(new FileReader(file));
//  StringWriter sw = new StringWriter();
//  StringBuffer outputString;
//  
//  String line;
//  while ((line = br.readLine() != null)) {
//   sw.write(line);
//   sw.
//  }
  

 


}
