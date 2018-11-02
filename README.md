# Meet @ Oslomet

A project for improving OsloMet students language skills


### Required stuff to do api calls
```java

/// To run the api calls the requests needs to be run on a different thread
/// calls thats performed on the main thread will cause the app to get killed
/// An example to run the api code use AsyncTask
AsyncTask.execute(new Runnable()
{
  @Override
  public void run()
  {
    /// place your code here ..
  }
});

/// To access the main thread again (to perform changes or access views)
runOnUiThread(new Runnable()
{
  @Override
  public void run()
  {
    /// place your code here..
  }
});


```


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
/// URL to the api can be obtained by writing: Strings.ApiUrl(); // To reduce workload.
String response = api.POST("https://meet.vlab.cs.hioa.no/api.php", sParams);

/// Then handling the response according to the API.


```

### POST request with API (JSON how to)
```java

/// Creating the API object containing API methods
Api api = new Api();

/// Creating the JSON object with values
JSONObject root = new JSONObject();
try
{
  root.put("username", "<username string here>");
  root.put("password", "<hashed password here>"); /// Password passed should and will always be hashed
}
catch (JSONException e)
{
  e.printStackTrace();
}

/// Getting JSON as string
String jString = root.toString();

/// Creating the ArrayList that will contain all of our POST Paramters to the API
ArrayList<PostParam> params = new ArrayList<>();

/// Example of how paramters are constructed
PostParam pp = new PostParam("request", "login_user");
PostParam pp1 = new PostParam("data", jString);

/// Adding the paramters to the ArrayList
params.add(pp);
params.add(pp1);

/// Getting the paramets as a string by passing the ArrayList params
String sParams = api.POST_DATA(params);

/// Sending POST request and getting data
/// URL to the api can be obtained by writing: Strings.ApiUrl(); // To reduce workload.
String response = api.POST("https://meet.vlab.cs.hioa.no/api.php", sParams);

/// Then handling the response according to the API.


```
