import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

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
  * Depending of the request type that was sent, the server returns different responses.
  *
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void handle() throws IOException {
  String msg = br.readLine();

  if ((msg != null) && !msg.equals(null)) {

   Request request = new Request(msg, documentRoot);

   switch (request.getType()) {
    case Configuration.isOk: { // Send 200
     logger.logValid(con.getInetAddress() + ": " + request.toString());
     respondOk(request);
     break;
    }
    case Configuration.isNotFound: { // Send 404
     logger.logValid(con.getInetAddress() + ": " + request.toString());
     respondNotFound(request);
     break;
    }
    case Configuration.isNotImplemented: { // Send 501
     logger.logInvalid(con.getInetAddress() + ": " + request.toString());
     respondNotImplemented(request);
     break;
    }
    default: // Send nothing, but log
     logger.logInvalid(con.getInetAddress() + ": " + request.toString());
     break;
   }
  }
  
  // Close the connection!
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
 private void respondOk(Request request) throws IOException {
  File file = request.getResourceName();
  byte[] body = Files.readAllBytes(Paths.get(file.toString()));
  StringBuffer sb = new StringBuffer();

  // Append HTTP Header
  sb.append(Configuration.httpOk + "\r\n");
  sb.append(Configuration.serverName + "\r\n");
  sb.append("Content-Length: " + body.length + "\r\n");
  sb.append("Content-Type: " + Files.probeContentType(Paths.get(file.toString())) + "\r\n");
  
  // Append CRLF
  sb.append("\r\n");

  byte[] header = sb.toString().getBytes();

  byte[] response = new byte[header.length + body.length];

  for (int i = 0; i < response.length; ++i) {
   response[i] = i < header.length ? header[i] : body[i - header.length];
  }
  os.write(response);
 }

 /**
  * Write not found.
  *
  * @param request the request
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void respondNotFound(Request request) throws IOException {
  String errorMessage = "<!doctype html><html lang = \"en\"><head><meta charset=\"utf-8\"><title>Titel</title></head><body><h1>ERROR: 404</h1><p>Path "
    + request.getResourceName().toString() + " not found.</p></body></html>";
  StringBuffer sb = new StringBuffer();

  // Append HTTP Header
  sb.append(Configuration.httpNotFound + "\r\n");
  sb.append(Configuration.serverName + "\r\n");
  sb.append("Content-Type: text/html\r\n");
  sb.append("Content-Length: " + errorMessage.length() + "\r\n");

  // Append CRLF
  sb.append("\r\n");

  // Append 404 Body
  sb.append(errorMessage);

  os.write(sb.toString().getBytes());
 }

 /**
  * Write not implemented.
  *
  * @param request the request
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void respondNotImplemented(Request request) throws IOException {
  StringBuffer sb = new StringBuffer();

  // Append HTTP Header
  sb.append(Configuration.httpNotImplemented + "\r\n");
  sb.append(Configuration.serverName + "\r\n");

  // Append CRLF
  sb.append("\r\n");

  os.write(sb.toString().getBytes());
 }

}
