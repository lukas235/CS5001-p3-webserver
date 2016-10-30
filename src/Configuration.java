/**
 * The Configuration class contains various parameters in order to configure the server.
 */
public abstract class Configuration {

 /** Name of the server. */
 public static final String serverName = "Lukas' HTTP Server";

 /** The filename for the logfile that stores valid requests. */
 public static final String logFileValid = "valid_requests.log";

 /** The filename for the logfile that stores invalid requests. */
 public static final String logFileInvalid = "invalid_requests.log";

 /** The code for requests, which are no valid HTTP. */
 public static final int isNoHttp = 0;

 /** The code for requests, which are not implemented on the server. */
 public static final int isNotImplemented = 501;

 /** The code for requests, which are valid HTTP, but demand a resource, that does not exist. */
 public static final int isNotFound = 404;

 /** The code for requests, which are valid HTTP and demand an existing resource. */
 public static final int isOk = 200;

}
