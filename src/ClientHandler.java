import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

  // Set time-out
  try {
   con.setSoTimeout(Configuration.TIME_OUT);
  }
  catch (SocketException e) {
   System.out.println("ClientHandler: Socket timeout.");
  }

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
  ArrayList<String> request = new ArrayList<String>();

  while (true) {
   String msg = br.readLine();

   if (msg == null || msg.equals(null)) {
    break;
   }

   request.add(msg);
   System.out.println(msg);

   // Check for CRLF and request size
   if (msg.length() == 0 || request.size() >= Configuration.REQUEST_LENGTH) {
    break;
   }
  }

  if (request.size() > 0 && request.get(0) != null && !request.get(0).equals(null) && request.get(0).length() > 0) {
   RequestChecker requestChecker = new RequestChecker(request.get(0), documentRoot);

   switch (requestChecker.getResponseType()) {
    case Configuration.IS_OK: // Send 200
     respondOk(requestChecker);
     logger.logValid(con.getInetAddress() + ": " + requestChecker.toString());
     break;

    case Configuration.IS_NOT_FOUND: // Send 404
     respondNotFound(requestChecker);
     logger.logValid(con.getInetAddress() + ": " + requestChecker.toString());
     break;

    case Configuration.IS_NOT_IMPLEMENTED: // Send 501
     respondNotImplemented();
     logger.logInvalid(con.getInetAddress() + ": " + requestChecker.toString());
     break;

    default: // Send nothing, but log
     respondBadRequest();
     logger.logInvalid(con.getInetAddress() + ": " + requestChecker.toString());
     break;
   }
  }
  // Close the connection after the request has been sent!
  closeClientConnection();
 }

 /**
  * Run method for multi-threading. Executes the handle() method and closes the client connection on error.
  *
  * @see java.lang.Thread#run()
  */
 @Override
 public void run() {
  System.out.println("New ClientHandler thread started.");
  try {
   handle();
  }
  catch (Exception e) {
   System.out.println("ClientHandler: " + e.getMessage());
   closeClientConnection();
  }
 }

 /**
  * Close client connection for clean-up purposes.
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
   System.out.println("ClientHandler: " + ioe.getMessage());
  }
 }

 /**
  * Create HTTP OK (200) response and send it to the client.
  * The message consists of:
  * <header>
  * <cr><lf>
  * <content>
  *
  * @param request the request from the client
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void respondOk(RequestChecker request) throws IOException {
  File file = request.getResourceName();
  byte[] content = Files.readAllBytes(Paths.get(file.toString()));
  StringBuffer sb = new StringBuffer();

  // Append HTTP Header
  sb.append("HTTP/1.1 200 OK" + "\r\n");
  sb.append(Configuration.SERVER_NAME + "\r\n");
  sb.append("Content-Length: " + content.length + "\r\n");
  sb.append("Content-Type: " + Files.probeContentType(Paths.get(file.toString())) + "\r\n");

  // Append CRLF
  sb.append("\r\n");

  byte[] header = sb.toString().getBytes();

  byte[] response = new byte[header.length + content.length];

  for (int i = 0; i < response.length; ++i) {
   response[i] = i < header.length ? header[i] : content[i - header.length];
  }

  // Write to output stream
  os.write(response);
 }

 /**
  * Create HTTP Not Found (404) response and send it to the client.
  * The message consists of:
  * <header>
  * <cr><lf>
  * <content>
  *
  * @param request the request
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void respondNotFound(RequestChecker request) throws IOException {
  String errorMessage = "<!doctype html><html lang = \"en\"><head><meta charset=\"utf-8\"><title>Titel</title></head><body><h1>ERROR: 404</h1><p>Path "
    + request.getResourceName().toString() + " not found.</p></body></html>";
  StringBuffer sb = new StringBuffer();

  // Append HTTP Header
  sb.append("HTTP/1.1 404 Not Found" + "\r\n");
  sb.append(Configuration.SERVER_NAME + "\r\n");
  sb.append("Content-Type: text/html\r\n");
  sb.append("Content-Length: " + errorMessage.length() + "\r\n");

  // Append CRLF
  sb.append("\r\n");

  // Append 404 Content
  sb.append(errorMessage);

  // Write to output stream
  os.write(sb.toString().getBytes());
 }

 /**
  * Create HTTP Not Implemented (501) response and send it to the client.
  * The message consists of:
  * <header>
  * <cr><lf>
  *
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void respondNotImplemented() throws IOException {
  StringBuffer sb = new StringBuffer();

  // Append HTTP Header
  sb.append("HTTP/1.1 501 Not Implemented" + "\r\n");
  sb.append(Configuration.SERVER_NAME + "\r\n");

  // Append CRLF
  sb.append("\r\n");

  // Write to output stream
  os.write(sb.toString().getBytes());
 }

 /**
  * Create HTTP Bad Request (400) response and send it to the client.
  * The message consists of:
  * <header>
  * <cr><lf>
  *
  * @throws IOException Signals that an I/O exception has occurred.
  */
 private void respondBadRequest() throws IOException {
  StringBuffer sb = new StringBuffer();

  // Append HTTP Header
  sb.append("HTTP/1.1 400 Bad Request" + "\r\n");
  sb.append(Configuration.SERVER_NAME + "\r\n");

  // Append CRLF
  sb.append("\r\n");

  // Write to output stream
  os.write(sb.toString().getBytes());
 }
}
