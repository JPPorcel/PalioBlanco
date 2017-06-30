package etsiitdevs.com.palioblanco.api;

import com.google.gson.JsonObject;


public class Marcha {
    public String nombre, autor, tipo;
    public int duration;
    public String id;
    public String path;
    public String title;
    public String imagen;


    public Marcha(JsonObject m)
    {
        nombre = m.get("titulo").getAsString();
        autor = m.get("autor").getAsString();
        tipo = m.get("tipo").getAsString();
        id = m.get("id").getAsString();
        if(m.has("duration")) duration = m.get("duration").getAsInt();
        path = Api.host+"/marcha/"+id;
        title = nombre + " - " + autor;
        if(m.has("imagen") && !m.get("imagen").isJsonNull()) imagen = m.get("imagen").getAsString();
        else imagen = "";
    }


    public Marcha()
    {
        path = "http://52.56.206.110/marcha/JwgtZvgQdLE";
        nombre = "A esta es";
        autor = "Manuel no se qué";
        tipo = "CT";
        duration = 164;
        title = nombre + " - " + autor;
    }

}

