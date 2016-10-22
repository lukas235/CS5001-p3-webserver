import java.io.File;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
 private String request;
 private String requestType;
 private File resourceName;
 private String protocolVersion;
 private boolean isValidHttp = false;
 private Pattern p;
 private Matcher m;

 public Request(String request) {
  this.request = request;
  p = Pattern.compile("^(GET|HEAD)[ *]([^ ]*)[ *](HTTP*.+$)");
  m = p.matcher(request);

  if (!m.matches()) {
   isValidHttp = false;
  }
  else {
   isValidHttp = true;
   System.out.println("Match");

   requestType = m.group(1);
   resourceName = new File(m.group(2));
   protocolVersion = m.group(3);
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

 public boolean isValidHttp() {
  return isValidHttp;
 }

 public boolean isValidRequest(String request) {
  return isValidHttp;
 }

}
