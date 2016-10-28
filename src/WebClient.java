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

 private Socket con;
 private InputStream is;
 private OutputStream os;
 private BufferedReader br;
 private File documentRoot;

 public WebClient(File documentRoot, Socket con) {
  this.documentRoot = documentRoot;
  this.con = con;

  try {
   is = con.getInputStream();
   os = con.getOutputStream();
   br = new BufferedReader(new InputStreamReader(is));

  }
  catch (IOException ioe) {
   System.out.println("WebClient x: " + ioe.getMessage());
  }
 }

 private void handle() throws DisconnectedException, IOException {
  while (true) {
   String msg = br.readLine();

   if (msg == null || msg.equals(null)) {
    throw new DisconnectedException("Client closed the connection");
   }
   else {

    Request request = new Request(msg, documentRoot);

    if (request.isImplemented()) {
     if (request.isFound()) {
      writeOK(request.getResourceName());
     }
     else {
      writeNotFound(request);
     }
    }
    else {
     writeNotImplemented(request);
    }
   }
   throw new DisconnectedException("Server closed the connection");
  }
 }

 public void run() {
  System.out.println("-- New WebClient Thread started --");
  try {
   handle();
  }
  catch (Exception e) {
   System.out.println("WebClient: " + e.getMessage());
   closeClientConnection();
  }
 }

 public void closeClientConnection() {
  System.out.println("Closing WebClient connection.");
  try {
   br.close();
   os.close();
   is.close();
   con.close();
   System.out.println("WebClient connection successfully closed.");
  }
  catch (IOException ioe) {
   System.out.println("Error while closing WebClient connection: " + ioe.getMessage());
  }
 }

 private void writeOK(File file) throws IOException {
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

 private void writeNotFound(Request request) throws IOException {
  String errorMessage = "<html><body><h1>ERROR: 404</h1><p>Path " + request.getResourceName().toString()
    + " not found.</p></body></html>";
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

 private void writeNotImplemented(Request request) throws IOException {
  System.out.println("Request " + request.getResourceName() + " not implemented");

  // Write HTTP Header
  os.write((Configuration.httpNotImplemented + "\r\n").getBytes());
  os.write((Configuration.serverName + "\r\n").getBytes());

  // Write CRLF
  os.write("\r\n".getBytes());
 }

}
