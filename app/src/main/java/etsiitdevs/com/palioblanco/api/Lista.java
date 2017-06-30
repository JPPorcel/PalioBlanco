package etsiitdevs.com.palioblanco.api;

import com.google.gson.JsonObject;

/**
 * Created by jpblo on 17/06/17.
 */

public class Lista {

    public String id;
    public String nombre;
    public String creador;
    public String idCreador;
    public String imagen;


    public Lista(JsonObject m)
    {
        id = m.get("id").getAsString();
        nombre = m.get("nombre").getAsString();
        creador = m.get("creador").getAsString();
        if(m.has("imagen") && !m.get("imagen").isJsonNull()) imagen = m.get("imagen").getAsString();
        else imagen = "";
        if(m.has("idCreador")) idCreador = m.get("idCreador").getAsString();
    }
}
