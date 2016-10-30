import java.io.File;

/**
 * WebServerMain checks for the right command line arguments and starts the server.
 */
public class WebServerMain {

 /**
  * The main method checks the command line arguments and starts the server.
  *
  * @param args arguments from command-line.
  */
 public static void main(String[] args) {
  File documentRoot = null;
  int port = 0;

  // Check number of arguments.
  if (args.length != 2) {
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }

  // Check if not equals 0.
  if (args[0].equals(null) || (args[0] == null) || args[1].equals(null) || (args[1] == null)) {
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }

  try {
   documentRoot = new File(args[0]);
   port = Integer.parseInt(args[1]);
  }
  catch (Exception e) {
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }

  // Check if port is valid.
  if ((port < 1) || (port > Integer.MAX_VALUE)) {
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }

  // Check if the string is a valid directory.
  if (!documentRoot.isDirectory()){
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }

  // Start the webserver.
  WebServer ws = new WebServer(documentRoot, port);



 }


}
