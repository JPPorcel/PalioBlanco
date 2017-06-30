package etsiitdevs.com.palioblanco.api;

import com.google.gson.JsonObject;

/**
 * Created by jpblo on 15/06/17.
 */

public class Response
{

    public static final String OK = "20";
    public static final String USER_ALREADY_REGISTERED = "41";
    public static final String UNREGISTERED_USER = "42";

    public String code;
    public String message;


    public Response(JsonObject m)
    {
        code = m.get("code").getAsString();
        message = m.get("message").getAsString();
    }
}
