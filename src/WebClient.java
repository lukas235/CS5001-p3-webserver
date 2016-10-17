import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class WebClient extends Thread {

 private Socket con; // socket representing TCP/IP connection to Client
 private InputStream is; // get data from client on this input stream 
 private OutputStream os; // can send data back to the client on this output stream
 private BufferedReader br; // use buffered reader to read client data
 private File file;

 public WebClient(Socket con) {
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
   String line = br.readLine(); // get data from client over socket

   // if readLine fails we can deduce here that the connection to the client is broken
   // and shut down the connection on this side cleanly by throwing a DisconnectedException
   // which will be passed up the call stack to the nearest handler (catch block)
   // in the run method
   if (line == null || line.equals("null") || line.equals("exit")) {
    System.out.println("puff");
    closeClientConnection();
   }
   
//   if (line.substring(0, 3) == "GET "){
//    try{
//     file = new File(line.substring(4, line.length()-1));
//     
//     if (!file.isFile() || !file.isDirectory()){
//      Exception e = new Exception();
//      throw e;
//     }
//     
//    }
//    catch (Exception e){
//     os.write("Wrong Path".getBytes());
//    }
//    
//   }

   // in this simple setup all the server does in response to messages from the client is to send
   // a single ACK byte back to client - the client uses this ACK byte to test whether the 
   // connection to this server is still live, if not the client shuts down cleanly //TODO sendalive
   Byte isAlive = 1;
   os.write(isAlive);

   //String string = "Halt's Maul\n";

   //byte[] b = string.getBytes();
   //byte[] b = string.getBytes(Charset.forName("UTF-8"));
   //byte[] b = string.getBytes(StandardCharsets.UTF_8); // Java 7+ only

   //os.write(b);
   System.out.println("WebClient: " + line); // assuming no exception, print out line received from client
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
 
 private void handleGET(){
  
 }

}
