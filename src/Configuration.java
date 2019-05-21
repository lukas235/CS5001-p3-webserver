/**
 * The Configuration class contains various parameters in order to configure the server.
 */
public abstract class Configuration {

 /** Name of the server. */
 public static final String SERVER_NAME = "Lukas' HTTP Server";

 /** The filename for the logfile that stores valid requests. */
 public static final String LOGFILE_VALID = "valid_requests.log";

 /** The filename for the logfile that stores invalid requests. */
 public static final String LOGFILE_INVALID = "invalid_requests.log";

 /** The code for requests, which are no valid HTTP. */
 public static final int IS_BAD_REQUEST = 400;

 /** The code for requests, which are not implemented on the server. */
 public static final int IS_NOT_IMPLEMENTED = 501;

 /** The code for requests, which are valid HTTP, but demand a resource, that does not exist. */
 public static final int IS_NOT_FOUND = 404;

 /** The code for requests, which are valid HTTP and demand an existing resource. */
 public static final int IS_OK = 200;

 /** Max lines the server accepts in a request. */
 public static final int REQUEST_LENGTH = 100;

 /** Connection time-out in milliseconds. */
 public static final int TIME_OUT = 10000;

}
