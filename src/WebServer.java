import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

// TODO: Auto-generated Javadoc
/**
 * The Class WebServer.
 */
public class WebServer {

 /** The ss. */
 private ServerSocket ss;

 /**
  * Instantiates a new web server.
  *
  * @param documentRoot the document root
  * @param port the port
  */
 public WebServer(File documentRoot, int port) {
  try {
   ss = new ServerSocket(port);
   System.out.println("WebServer started. Listening on port " + port + ". Root directory is " + documentRoot.toString());

   while (true) {
    Socket con = ss.accept();
    System.out.println("WebServer received a new connection request from " + con.getInetAddress());

    ClientHandler ch = new ClientHandler(documentRoot, con);

    ch.start();
   }
  }
  catch (IOException ioe) {
   System.out.println("WebServer IOException: " + ioe.getMessage());
  }
 }
}
