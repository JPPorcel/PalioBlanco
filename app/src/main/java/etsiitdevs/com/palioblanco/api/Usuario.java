package etsiitdevs.com.palioblanco.api;

import com.google.gson.JsonObject;

/**
 * Created by jpblo on 17/06/17.
 */

public class Usuario {
    public String id;
    public String nombre;
    public String apellidos;
    public String localidad;
    public String idLocalidad;
    public String imagen;

    public Usuario(JsonObject m)
    {
        id = m.get("idFacebook").getAsString();
        nombre = m.get("nombre").getAsString();
        apellidos = m.get("apellidos").getAsString();
        localidad = m.get("localidad").getAsString();
        if(m.has("imagen") && !m.get("imagen").isJsonNull()) imagen = m.get("imagen").getAsString();
        else imagen = "";
        if(m.has("idLocalidad")) idLocalidad = m.get("idLocalidad").getAsString();
    }
}
