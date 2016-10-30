import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.corba.se.impl.ior.ByteBuffer;

/**
 * The ClientHandler is responsible for handling the client's requests.
 * It supports and responds to correct GET requests.
 * It returns HTML documents requested by a client.
 * It also returns binary files and images such as GIF, JPEG and PNG.
 * It responds with appropriate error messages when non-existent services or resources are requested.
 * It is multi-threaded and therefore supports more than one client connection at a time.
 * Each time a request is made it calls the logger in order to log it to a file.
 * If an invalid request is made it is logged to a separate file.
 */
public class ClientHandler extends Thread {

 /** Client Socket. */
 private Socket con;

 /** InputStream reads the client's requests. */
 private InputStream is;

 /** OutputStream responds to the client. */
 private OutputStream os;

 /** A BufferedReader is needed for reading the requested files. */
 private BufferedReader br;

 /** Root directory of the server. */
 private File documentRoot;

 /** Logs valid and invalid requests. */
 private Logger logger;

 /**
  * Instantiates a new client handler.
  *
  * @param documentRoot te root directory of the server
  * @param con Client Socket
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
  * Handles the client's connection. 
  *
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void handle() throws IOException {
  String msg = br.readLine();

  if ((msg != null) && !msg.equals(null)) {

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

 /** 
  * Run method
  * @see java.lang.Thread#run()
  */
 @Override
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
  StringBuffer sb = new StringBuffer();

  File file = request.getResourceName();
  byte[] webPage = Files.readAllBytes(Paths.get(file.toString()));
  //  String webPageSize = "Content-Length: " + webPage.length + "\r\n";
  //  String contentType = "Content-Type: " + Files.probeContentType(Paths.get(file.toString())) + "\r\n";

  //Write HTTP Header
  sb.append(Configuration.httpOk + "\r\n");
  sb.append(Configuration.serverName + "\r\n");
  sb.append("Content-Length: " + webPage.length + "\r\n");
  sb.append("Content-Type: " + Files.probeContentType(Paths.get(file.toString())) + "\r\n");
  //Write CRLF
  sb.append("\r\n");
  
  byte [] header = sb.toString().getBytes();
  
  byte[] response = new byte[header.length+webPage.length];
  
  for (int i = 0; i < response.length; ++i)
  {
      response[i] = i < header.length ? header[i] : webPage[i - header.length];
  }
  
  os.write(response);
  

  // Write HTTP Header
  //  os.write((Configuration.httpOk + "\r\n").getBytes()); // <protocol> <responseCode> <cr><lf>
  //  os.write((Configuration.serverName + "\r\n").getBytes()); // <server>
  //  os.write(contentType.getBytes()); //
  //  os.write(webPageSize.getBytes());

  // Write CRLF
//  os.write("\r\n".getBytes());

  // Write 200 Body
//  os.write(webPage);
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
