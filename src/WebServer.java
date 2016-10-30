import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Class WebServer holds a ServerSocket, that listens on the determined port.
 * If a new connection is requested by a client, it opens up a new threaded Socket, that handles the client's connection.
 */
public class WebServer {

 /** Socket of the server. */
 private ServerSocket ss;

 /**
  * Instantiates a new web server.
  *
  * @param documentRoot the specified root directory, that contains the web site.
  * @param port the port, where the server listen on with its socket.
  */
 public WebServer(File documentRoot, int port) {
  try {
   ss = new ServerSocket(port);
   System.out.println(Configuration.SERVER_NAME + " started. Listening on port " + port + ". Root directory is "
     + documentRoot.toString() + ".");

   while (true) {
    Socket con = ss.accept();
    System.out
      .println(Configuration.SERVER_NAME + " received a new connection request from " + con.getInetAddress() + ".");

    ClientHandler ch = new ClientHandler(documentRoot, con);

    ch.start();
   }
  }
  catch (IOException ioe) {
   System.out.println("WebServer IOException: " + ioe.getMessage());
  }
 }
}
