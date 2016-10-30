
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class RequestChecker analyzes a client request and returns .
 */
public class RequestChecker {

 /** Request string that came from the client. */
 private String request;

 /** Name of the requested Resource (File or Path). */
 private File resourceName;

 /** Root directory of the server. */
 private File documentRoot;

 /** Valid http regex pattern. */
 private Pattern httpPattern;

 /** Valid regex get pattern. */
 private Pattern getPattern;

 /** Http matcher. */
 private Matcher httpMatcher;

 /** Get matcher. */
 private Matcher getMatcher;

 /**
  * Instantiates a new request.
  *
  * @param request request that is being checked
  * @param documentRoot root directory of the server
  */
 public RequestChecker(String request, File documentRoot) {
  this.request = request;
  this.documentRoot = documentRoot;
 }

 /**
  * Gets the appropriate response type to the request.
  *
  * @return 0, if the request could not be recognized as a valid HTTP request.
  * 501, if the request has the HTTP structure, but is not implemented on the server.
  * 404, if the request is valid GET, but the requested resource (File/Path) was not found.
  * 200, if valid GET and the resource could be found.
  */
 public int getResponseType() {

  if ((request == null) || request.equals(null)) {
   return Configuration.IS_BAD_REQUEST;
  }

  httpPattern = Pattern.compile("^([A-Z]+)\\s(\\S+)\\s(HTTP.*)$");
  getPattern = Pattern.compile("^(GET)\\s(\\S+)\\s(HTTP.*)$");
  httpMatcher = httpPattern.matcher(request);
  getMatcher = getPattern.matcher(request);

  if (!httpMatcher.matches()) { // No HTTP request
   return Configuration.IS_BAD_REQUEST;
  }

  else {
   resourceName = new File(httpMatcher.group(2));

   if (!getMatcher.matches()) { // No GET request / Unknown HTTP request
    return Configuration.IS_NOT_IMPLEMENTED;
   }
   else {
    File filePath = new File(documentRoot.toString() + getMatcher.group(2).toString());
    if (filePath.isFile()) { // File found
     resourceName = filePath;
     return Configuration.IS_OK;
    }
    else if (filePath.isDirectory()) { // Try to find an index.html, if only the directory was requested
     filePath = new File(filePath.toString() + "/index.html");
     if (filePath.isFile()) {
      resourceName = filePath;
      return Configuration.IS_OK;
     }
     else { // No index.html was not found in the directory
      return Configuration.IS_NOT_FOUND;
     }
    }
    else { // No File and no directory were found
     return Configuration.IS_NOT_FOUND;
    }
   }
  }
 }

 /**
  * Gets the requested resource name (Valid or invalid File-/Pathname).
  *
  * @return the requested resource name
  */
 public File getResourceName() {
  return resourceName;
 }

 /**
  * Overrides the toString() method in order to return the original request as string.
  *
  * @see java.lang.Object#toString()
  */
 @Override
 public String toString() {
  return request;
 }

}
