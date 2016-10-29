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

// TODO: Auto-generated Javadoc
/**
 * The Class ClientHandler.
 */
public class ClientHandler extends Thread {

 /** The con. */
 private Socket con;
 
 /** The is. */
 private InputStream is;
 
 /** The os. */
 private OutputStream os;
 
 /** The br. */
 private BufferedReader br;
 
 /** The document root. */
 private File documentRoot;
 
 /** The logger. */
 private Logger logger;

 /**
  * Instantiates a new client handler.
  *
  * @param documentRoot the document root
  * @param con the con
  */
 public ClientHandler(File documentRoot, Socket con) {
  this.documentRoot = documentRoot;
  this.con = con;

  logger = new Logger();

  try {
   is = con.getInputStream();
   os = con.getOutputStream();
   br = new BufferedReader(new InputStreamReader(is));

  }
  catch (IOException ioe) {
   System.out.println("ClientHandler: " + ioe.getMessage());
  }
 }

 /**
  * Handle.
  *
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void handle() throws IOException {
  String msg = br.readLine();

  if (msg != null && !msg.equals(null)) {

   Request request = new Request(msg, documentRoot);

   switch (request.getType()) {
    case Configuration.isOk: {
     logger.logValid(con.getInetAddress() + ": " + request.toString());
     writeOK(request);
     break;
    }
    case Configuration.isNotFound: {
     logger.logValid(con.getInetAddress() + ": " + request.toString());
     writeNotFound(request);
     break;
    }
    case Configuration.isNotImplemented: {
     logger.logValid(con.getInetAddress() + ": " + request.toString());
     writeNotImplemented(request);
     break;
    }
    default:
     logger.logInvalid(con.getInetAddress() + ": " + request.toString());
     break;
   }
  }
  closeClientConnection();
 }

 /* (non-Javadoc)
  * @see java.lang.Thread#run()
  */
 public void run() {
  System.out.println("New ClientHandler Thread started");
  try {
   handle();
  }
  catch (Exception e) {
   System.out.println("ClientHandler: " + e.getMessage());
   closeClientConnection();
  }
 }

 /**
  * Close client connection.
  */
 public void closeClientConnection() {
  System.out.println("Closing ClientHandler connection.");
  try {
   br.close();
   os.close();
   is.close();
   con.close();
   System.out.println("ClientHandler connection successfully closed.");
  }
  catch (IOException ioe) {
   System.out.println("Error while closing ClientHandler connection: " + ioe.getMessage());
  }
 }

 /**
  * Write OK.
  *
  * @param request the request
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void writeOK(Request request) throws IOException {
  File file = request.getResourceName();
  byte[] webPage = Files.readAllBytes(Paths.get(file.toString()));
  String webPageSize = "Content-Length: " + webPage.length + "\r\n";
  String contentType = "Content-Type: " + Files.probeContentType(Paths.get(file.toString())) + "\r\n";

  // Write HTTP Header
  os.write((Configuration.httpOk + "\r\n").getBytes()); // <protocol> <responseCode> <cr><lf>
  os.write((Configuration.serverName + "\r\n").getBytes()); // <server>
  os.write(contentType.getBytes()); // 
  os.write(webPageSize.getBytes());

  // Write CRLF
  os.write("\r\n".getBytes());

  // Write 200 Body
  os.write(webPage);
 }

 /**
  * Write not found.
  *
  * @param request the request
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void writeNotFound(Request request) throws IOException {
  String errorMessage = "<!doctype html><html lang = \"en\"><head><meta charset=\"utf-8\"><title>Titel</title></head><body><h1>ERROR: 404</h1><p>Path "
    + request.getResourceName().toString() + " not found.</p></body></html>";
  String errorLength = "Content-Length: " + errorMessage.length();

  System.out.println("Path " + request.getResourceName() + " not found");

  // Write HTTP Header
  os.write((Configuration.httpNotFound + "\r\n").getBytes());
  os.write((Configuration.serverName + "\r\n").getBytes());
  os.write("Content-Type: text/html\r\n".getBytes());
  os.write((errorLength + "\r\n").getBytes());

  // Write CRLF
  os.write("\r\n".getBytes());

  // Write 404 Body
  os.write("\r\n".getBytes());
 }

 /**
  * Write not implemented.
  *
  * @param request the request
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void writeNotImplemented(Request request) throws IOException {
  System.out.println("Request " + request.getResourceName() + " not implemented");

  // Write HTTP Header
  os.write((Configuration.httpNotImplemented + "\r\n").getBytes());
  os.write((Configuration.serverName + "\r\n").getBytes());

  // Write CRLF
  os.write("\r\n".getBytes());
 }

}
