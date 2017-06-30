package etsiitdevs.com.palioblanco.api;

import com.google.gson.JsonObject;


public class Localidad
{
    private int id;
    private String nombre;
    private String provincia;

    public Localidad(JsonObject m)
    {
        nombre = m.get("nombre").getAsString();
        id = m.get("id").getAsInt();
    }

    public Localidad(int i, String n, String p)
    {
        id = i;
        nombre = n;
        provincia = p;
    }

    public int getId()
    {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getProvincia() {
        return provincia;
    }
}