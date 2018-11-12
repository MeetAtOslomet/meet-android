package no.oslomet.meet.core;

public class Strings
{
    public static final String Domain = "https://meet.vlab.cs.hioa.no";
    public static String ApiUrl()
    {
        return Domain + "/api.php";
    }

    public static String Heartbeat()
    {
        return Domain + "/api.php?request=heartbeat";
    }
    public static String Languages() {return  Domain + "/api.php?request=get_language";}
    public static String Hobbies() { return ApiUrl() + "?request=get_hobbies"; }
    public static String Campus() { return ApiUrl() + "?request=get_campus"; }

    public static String ProfileExists(String Username)
    {
        if (Username == null || Username.length() <= 0) {return null; }
        return Domain + "/api.php?request=chk_user&username="+Username;
    }

    public static String Request_GetIdUser(String username, String token)
    {
        return ApiUrl() + "?request=get_id_user&username="+username+"&authenticationToken="+token;
    }
    public static String Request_GetMe(String token, String data)
    {
        return ApiUrl() + "?request=get_me&authenticationToken="+token+"&data="+data;
    }
    public static String Request_UserLanguages(String userId)
    {
        return ApiUrl() + "?request=get_user_language&id_user="+userId;
    }
    public static String Request_UserHobbies(String userId)
    {
        return ApiUrl() + "?request=get_user_hobbies&id_user=" + userId;
    }
    public static String Request_Get_TANDEM(String userId)
    {
        return ApiUrl() + "?request=get_tandem&id_user="+userId;
    }




    public static String testPost = "{\"username\":\"Admin\",\"password\":\"c0aba0c3f3fdc55050e59b7eb596605d000d41e0625328b7e87c65336b000a15\",\"type\":\"Student\"}";
}
