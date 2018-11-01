# Meet @ Oslomet

A project for improving OsloMet students language skills


### GET request with API
```java

/// Creating the API object containing API methods
Api api = new Api();

/// Gets string as output from  the API call, url is provided as paramter for request
String response = api.GET("https://meet.vlab.cs.hioa.no/api.php?request=heartbeat");

// Getting the data from JSON into corresponding object
Heartbeat h = new JsonHandler().getHeartbeat(response);
```
