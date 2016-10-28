

import java.io.File;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
 private String request;
 private String requestType;
 private File resourceName;
 private String protocolVersion;
 private boolean isImplemented;
 private boolean isFound;

 private Pattern p;
 private Matcher m;

 public Request(String request, File documentRoot) {
  this.request = request;
  p = Pattern.compile("^(GET|get)\\s+(\\S+)\\s+(HTTP.*)$");
  m = p.matcher(request);

  if (!m.matches()) {
   isImplemented = false;
  }
  else {
   isImplemented = true;

   requestType = m.group(1);
   resourceName = new File(m.group(2));
   protocolVersion = m.group(3);

   File filepath = new File(documentRoot.toString() + getResourceName().toString());

   if (filepath.isFile()) {
    isFound = true;
    resourceName = filepath;
   }
   else if (documentRoot.isDirectory()) {
    filepath = new File(filepath.toString() + "/index.html");

    if (filepath.isFile()) {
     isFound = true;
     resourceName = filepath;
    }
    else {
     isFound=false;
    }
   }
   else {
    isFound = false;
   }
  }
 }

 public String getRequest() {
  return request;
 }

 public String getRequestType() {
  return requestType;
 }

 public File getResourceName() {
  return resourceName;
 }

 public String getProtocolVersion() {
  return protocolVersion;
 }

 public boolean isImplemented() {
  return isImplemented;
 }

 public boolean isFound() {
  return isFound;
 }

}
