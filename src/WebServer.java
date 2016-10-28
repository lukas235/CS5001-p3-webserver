import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class WebServer {

 private ServerSocket ss; // listen for client connection requests on this server socket
 private ExecutorService executor;

 public WebServer(File documentRoot, int port) {
  try {
   ss = new ServerSocket(port);
   System.out.println("-- WebServer started. Listening on port " + port + ". --");
   Logger logger = new Logger();
   logger.log("Server has been started");

   while (true) {
    Socket con = ss.accept(); // wait until client requests a connection, then returns connection (socket)
    System.out.println("WebServer received new connection request from " + con.getInetAddress());

    WebClient ch = new WebClient(documentRoot, con); // create new handler for the connection

    ch.start(); // handle the client request
   }
  }
  catch (IOException ioe) {
   System.out.println("WebServer IOException: " + ioe.getMessage());
  }
 }
}
