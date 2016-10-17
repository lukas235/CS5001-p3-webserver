import java.net.ServerSocket;

public class WebServerMain implements Runnable{
 
 

 public static void main(String[] args) {
  String documentRoot = null;
  int port = 0;
  ServerSocket serverSocket = null;

  if (args.length < 2) {
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }

  if (args[0].equals(null)) {
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }

  try {
   documentRoot = args[0];
   port = Integer.parseInt(args[1]);
  }
  catch (Exception e) {
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }
  
  if (port < 1){
   System.out.println("Usage: java WebServerMain <document_root> <port>");
   System.exit(-1);
  }
  
  try {
   serverSocket = new ServerSocket(port);
   serverSocket.setSoTimeout(60000);
  }
  catch (Exception e){
   
  }

 }

 @Override
 public void run() {
  System.out.println("mooin");
 }

}
