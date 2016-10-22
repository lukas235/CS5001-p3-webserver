import java.io.File;

public class WebServerMain {

 public static void main(String[] args) {
  File documentRoot = null;
  int port = 0;

  if (args.length < 2) {
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }

  if (args[0].equals(null)) {
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
  
  // check if port is valid
  if (port < 1 || port > Integer.MAX_VALUE) {
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }
  
  // check if the string is a valid dir
  if (!documentRoot.isDirectory()){
   System.out.println("Usage: java WebServerMain <document_root> <port>"+documentRoot.getPath().toString());
   System.exit(-1);
  }
  
  System.out.println(port);
  System.out.println(documentRoot.getPath());

  WebServer ws = new WebServer(documentRoot, port);

 }


}
