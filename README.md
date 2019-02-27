# Simple Java Http Server should:
• Support HTTP 1.1 protocol; <br/>
• Be able to return static resources via GET requests (both text and binary); <br/>
• Display all files in a direactory, if the directory was requested; <br/>
• Be able to apply specific handler for any user request; <br/>
• Support GET, POST and HEAD requests, for any other method - return Status = 405 (Not Allowed); <br/>
• Support only application/x-www-form-urlencoded MIME type for form data; <br/>
• Support query parameters extraction from GET and POST requests; <br/>
• Support below status codes: <br/>
1)200=OK – Successful request; <br/>
2)400=Bad Request – when request parameters are invalid; <br/>
3)404=Not Found – when requested resource does not exist; <br/>
4)405=Method Not Allowed – when request method is not supported; <br/>
5)500=Internal Server Error – when there is insternal server error of some kind; <br/>
6)505=HTTP Version Not Supported – when HTTP protocol version from request is not equal HTPP 1.1; <br/>
• Work in blocking mode only, i.e. each connection is processed in separate execution thread. Execution threads should be requested from the thread pool. <br/>
• Close connection witn client browser after each request; <br/>
• Support case-insensitive request headers with wrap support; <br/>
• Be abple to inform client browser about static resource caching; <br/>
• Support all mime-types.
• Be able to display error pages with error codes; <br/>
• Support connection pool to relation database; <br/>
• Support configuration via property files; <br/>
• Have CLI for start and stop operation; <br/>
• Be abple to correctly releasy all resources when stopped. <br/>
