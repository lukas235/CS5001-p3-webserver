
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * The Class Request.
 */
public class Request {

 /** The request. */
 private String request;

 /** The request type. */
 private String requestType;

 /** The resource name. */
 private File resourceName;

 /** The protocol version. */
 private String protocolVersion;

 /** The document root. */
 private File documentRoot;

 /** The type. */
 private int type = -1;

 /** The http pattern. */
 private Pattern httpPattern;

 /** The get pattern. */
 private Pattern getPattern;

 /** The http matcher. */
 private Matcher httpMatcher;

 /** The get matcher. */
 private Matcher getMatcher;

 /**
  * Instantiates a new request.
  *
  * @param request the request
  * @param documentRoot the document root
  */
 public Request(String request, File documentRoot) {
  this.request = request;
  this.documentRoot = documentRoot;
 }

 /**
  * Gets the type.
  *
  * @return the type
  */
 public int getType() {
  if (type == -1) {
   httpPattern = Pattern.compile("^([A-Z]+)\\s+(\\S+)\\s+(HTTP.*)$");
   getPattern = Pattern.compile("^(GET)\\s+(\\S+)\\s+(HTTP.*)$");
   httpMatcher = httpPattern.matcher(request);
   getMatcher = getPattern.matcher(request);

   if (!httpMatcher.matches()) { // No HTTP request
    return Configuration.isNoHttp;
   }

   else {
    requestType = httpMatcher.group(1);
    resourceName = new File(httpMatcher.group(2));
    protocolVersion = httpMatcher.group(3);

    if (!getMatcher.matches()) { // No GET request / Unknown HTTP request
     System.out.println("Is not implemented");
     return Configuration.isNotImplemented;
    }
    else {
     File filePath = new File(documentRoot.toString() + getMatcher.group(2).toString());
     if (filePath.isFile()) { // File found
      System.out.println("Is File");
      resourceName = filePath;
      return Configuration.isOk;
     }
     else if (filePath.isDirectory()) { // Try to find the index.html, if only the directory was requested
      filePath = new File(filePath.toString() + "/index.html");
      if (filePath.isFile()) {
       System.out.println("Is Folder with index");
       resourceName = filePath;
       return Configuration.isOk;
      }
      else { // No index.html was not found
       System.out.println("Folder has no index");
       return Configuration.isNotFound;
      }
     }
     else { // No File and no directory
      System.out.println("Is not found");
      return Configuration.isNotFound;
     }
    }
   }
  }
  else {
   return type;
  }
 }

 /**
  * Gets the request type.
  *
  * @return the request type
  */
 public String getRequestType() {
  return requestType;
 }

 /**
  * Gets the resource name.
  *
  * @return the resource name
  */
 public File getResourceName() {
  return resourceName;
 }

 /**
  * Gets the protocol version.
  *
  * @return the protocol version
  */
 public String getProtocolVersion() {
  return protocolVersion;
 }

 /**
  * @see java.lang.Object#toString()
  */
 @Override
 public String toString() {
  return request;
 }

}
