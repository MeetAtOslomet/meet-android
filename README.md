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

### POST request with API (non JSON params)
```java

/// Creating the API object containing API methods
Api api = new Api();

/// Creating the ArrayList that will contain all of our POST Paramters to the API
ArrayList<PostParam> params = new ArrayList<>();

/// Example of how paramters are constructed
PostParam pp = new PostParam("request", "auth_check");
PostParam pp1 = new PostParam("authenticationToken", "<KEY>");

/// Adding the paramters to the ArrayList
params.add(pp);
params.add(pp1);

/// Getting the paramets as a string by passing the ArrayList params
String sParams = api.POST_DATA(params);

/// Sending POST request and getting data
/// URL to the api can be obtained by writing: Strings.ApiUrl(); // To lessen workload.
String response = api.POST("https://meet.vlab.cs.hioa.no/api.php", sParams);

/// Then handling the response according to the API.


```
