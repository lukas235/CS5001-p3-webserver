1 Returning of binary images (GIF, JPEG and PNG)
	Returning binary images and other files as well is made possible, by returning their specific mime-type. The method probeContentType() of the Java API Class
	java.nio.file.Files returns the mime-type String of files, for files as a parameter. This String was set into the OK response (200) in order to inform the browser (or 
	any other client) about the type of the file.

2 Multithreading - support more than one client connection at a time
	Multithreading was implemented in each ClientHandler by letting it extend the class Thread. This means, that every ClientHandler implements
	a run() method, that is executed on creation. Additionally the Logger implements synchronized methods for file access to avoid deadlocks or conflicts between
	different threads trying to write into the same file.

3 Logging - each time a request is made log it to a file, if an invalid request is made log to a separate file
	This was done in the Logger class. The Class implements the two methods logValid(String request) and logInvalid(String request),
	which are used in the ClientHandler class after the request was analyzed and an appropriate response was sent.

4 If a directory was requested, the server search for an index.html file within the folder and sends it as a response.
	The RequestChecker additionally checks, whether the requested resource was a path (e.g. "/") and looks, if an index.html exists in this path,
	that should be sent as the standard .html file. This is helpful, when only the address, e.g. "localhost" is typed in the browser.