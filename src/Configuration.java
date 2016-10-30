import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// TODO: Auto-generated Javadoc
/**
 * The Class Configuration.
 */
public abstract class Configuration {

 /** Name of the server. */
 public static final String serverName = "My HTTP Server";

 /** The Constant httpOk. */
 public static final String httpOk = "HTTP/1.1 200 OK";

 /** The Constant httpNotFound. */
 public static final String httpNotFound = "HTTP/1.1 404 Not Found";

 /** The Constant httpNotImplemented. */
 public static final String httpNotImplemented = "HTTP/1.1 501 Not Implemented";

 /** The Constant logFileValid. */
 public static final String logFileValid = "valid_requests.log";

 /** The Constant logFileInvalid. */
 public static final String logFileInvalid = "invalid_requests.log";

 /** The Constant isNoHttp. */
 public static final int isNoHttp = 0;

 /** The Constant isNotImplemented. */
 public static final int isNotImplemented = 501;

 /** The Constant isNotFound. */
 public static final int isNotFound = 404;

 /** The Constant isOk. */
 public static final int isOk = 200;
 

}
