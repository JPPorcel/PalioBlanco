package etsiitdevs.com.palioblanco.api;

import com.google.gson.JsonObject;

/**
 * Created by jpblo on 12/06/17.
 */

public class Autor
{
    public String id;
    public String nombre;
    public String imagen;

    public Autor(JsonObject m)
    {
        nombre = m.get("nombre").getAsString();
        id = m.get("id").getAsString();
        if(m.has("imagen") && !m.get("imagen").isJsonNull()) imagen = m.get("imagen").getAsString();
        else imagen = "";
    }

}