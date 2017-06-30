package etsiitdevs.com.palioblanco.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.InputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import etsiitdevs.com.palioblanco.BibliotecaTab;


public class Api
{
    // IP del servidor, escuchando en el puerto 80
    public static String host = "http://35.176.131.61";

    // registro de usuario
    public static void logUp(UsoApi<Response> needResult, String id, String nombre, String apellidos, String img, int localidad)
    {
        final String[] response = new String[1];

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);
        RequestParams params = new RequestParams();

        params.put("idFacebook", id);
        params.put("nombre", nombre);
        params.put("apellidos", apellidos);
        params.put("localidad", localidad);
        params.put("imagen", img);

        client.post(host + "/users/logup", params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );

        JsonObject root = new JsonParser().parse(response[0]).getAsJsonObject();
        needResult.result(new Response(root));
    }

    // login de usuario
    public static void logIn(UsoApi<Response> needResult, String id)
    {
        final String[] response = new String[1];

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);
        RequestParams params = new RequestParams();

        params.put("idFacebook", id);

        client.post(host + "/users/login", params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );

        JsonObject root = new JsonParser().parse(response[0]).getAsJsonObject();
        needResult.result(new Response(root));
    }

    // filtro, devuelve marchas, autores, usuarios y playlists
    public static void filtro(String filtro, UsoApi<ArrayList> needResult)
    {
        final String[] response = new String[1];
        final SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);


        String api = host + "/filtro/" + filtro;

        client.get(api,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, final String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );

        ArrayList<ResultadoFiltro> resultadoFiltro = new ArrayList<>();

        JsonArray marchas = new JsonParser().parse(response[0]).getAsJsonObject().get("marchas").getAsJsonArray();
        if(marchas.size() > 0)
        {
            ResultadoFiltro rf = new ResultadoFiltro();
            rf.tipo = ResultadoFiltro.TITLE;
            rf.title = "Marchas";
            resultadoFiltro.add(rf);
        }
        for(int i=0; i<marchas.size(); i++) {
            ResultadoFiltro rf = new ResultadoFiltro();
            rf.tipo = ResultadoFiltro.MARCHA;
            rf.marcha = new Marcha(marchas.get(i).getAsJsonObject());
            resultadoFiltro.add(rf);
        }

        JsonArray autores = new JsonParser().parse(response[0]).getAsJsonObject().get("autores").getAsJsonArray();
        if(autores.size() > 0)
        {
            ResultadoFiltro rf = new ResultadoFiltro();
            rf.tipo = ResultadoFiltro.TITLE;
            rf.title = "Autores";
            resultadoFiltro.add(rf);
        }
        for(int i=0; i<autores.size(); i++) {
            ResultadoFiltro rf = new ResultadoFiltro();
            rf.tipo = ResultadoFiltro.AUTOR;
            rf.autor = new Autor(autores.get(i).getAsJsonObject());
            resultadoFiltro.add(rf);
        }

        JsonArray usuarios = new JsonParser().parse(response[0]).getAsJsonObject().get("usuarios").getAsJsonArray();
        if(usuarios.size() > 0)
        {
            ResultadoFiltro rf = new ResultadoFiltro();
            rf.tipo = ResultadoFiltro.TITLE;
            rf.title = "Usuarios";
            resultadoFiltro.add(rf);
        }
        for(int i=0; i<usuarios.size(); i++) {
            ResultadoFiltro rf = new ResultadoFiltro();
            rf.tipo = ResultadoFiltro.USUARIO;
            rf.usuario = new Usuario(usuarios.get(i).getAsJsonObject());
            resultadoFiltro.add(rf);
        }

        JsonArray listas = new JsonParser().parse(response[0]).getAsJsonObject().get("listas").getAsJsonArray();
        if(listas.size() > 0)
        {
            ResultadoFiltro rf = new ResultadoFiltro();
            rf.tipo = ResultadoFiltro.TITLE;
            rf.title = "Listas de reproducción";
            resultadoFiltro.add(rf);
        }
        for(int i=0; i<listas.size(); i++) {
            ResultadoFiltro rf = new ResultadoFiltro();
            rf.tipo = ResultadoFiltro.LISTA;
            rf.lista = new Lista(listas.get(i).getAsJsonObject());
            resultadoFiltro.add(rf);
        }

        needResult.result(resultadoFiltro);
    }

    // registra el historial del usuario
    public static void nuevaEscucha(String user, String marcha)
    {
        final String[] response = new String[1];

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);
        RequestParams params = new RequestParams();

        params.put("user", user);
        params.put("marcha", marcha);

        client.post(host + "/historial/nuevo", params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );
    }

    // devuelve las listas de reproducción del usaurio
    public static void getListas(UsoApi<ArrayList> needResult, String user)
    {
        final String[] response = new String[1];
        final SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);


        String api = host + "/playlists/" + user;

        client.get(api,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, final String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );

        ArrayList<Lista> listas = new ArrayList<>();

        JsonArray array = new JsonParser().parse(response[0]).getAsJsonArray();
        for(int i=0; i<array.size(); i++) {
            Lista lista = new Lista(array.get(i).getAsJsonObject());
            listas.add(lista);
        }
        needResult.result(listas);
    }

    // devuelve las marchas de una playlist identificada por lista
    public static void getLista(UsoApi<ArrayList> needResult, String lista)
    {
        final String[] response = new String[1];
        final SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);


        String api = host + "/playlist/" + lista;

        client.get(api,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, final String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );

        ArrayList<Marcha> marchas = new ArrayList<>();

        JsonArray array = new JsonParser().parse(response[0]).getAsJsonArray();
        for(int i=0; i<array.size(); i++) {
            Marcha marcha = new Marcha(array.get(i).getAsJsonObject());
            marchas.add(marcha);
        }
        needResult.result(marchas);
    }

    // devuelve la biblioteca del usuario
    public static void getLibrary(String user, UsoApi<ArrayList> needResult)
    {
        final String[] response = new String[1];
        final SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);


        String api = host + "/library/" + user;

        client.get(api,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, final String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );

        ArrayList<BibliotecaTab.BiblioItem> items = new ArrayList<>();

        JsonArray listas = new JsonParser().parse(response[0]).getAsJsonObject().get("listas").getAsJsonArray();
        BibliotecaTab.BiblioItem title1 = new BibliotecaTab.BiblioItem();
        title1.tipo = BibliotecaTab.BiblioItem.TITLE;
        title1.title = "Tus listas de reproducción";
        items.add(title1);

        if(listas.size() == 0)
        {
            BibliotecaTab.BiblioItem title3 = new BibliotecaTab.BiblioItem();
            title3.tipo = BibliotecaTab.BiblioItem.NOTHING;
            title3.title = "No tienes ninguna lista de reproducción";
            items.add(title3);
        }
        for(int i=0; i<listas.size(); i++) {
            BibliotecaTab.BiblioItem biblioItem = new BibliotecaTab.BiblioItem();
            biblioItem.tipo = BibliotecaTab.BiblioItem.PLAYLIST;
            Lista lista = new Lista(listas.get(i).getAsJsonObject());
            biblioItem.lista = lista;
            items.add(biblioItem);
        }

        JsonArray marchas = new JsonParser().parse(response[0]).getAsJsonObject().get("marchas").getAsJsonArray();
        BibliotecaTab.BiblioItem title2 = new BibliotecaTab.BiblioItem();
        title2.tipo = BibliotecaTab.BiblioItem.TITLE;
        title2.title = "Tus marchas más escuchadas";
        items.add(title2);
        if(marchas.size() == 0)
        {
            BibliotecaTab.BiblioItem title3 = new BibliotecaTab.BiblioItem();
            title3.tipo = BibliotecaTab.BiblioItem.NOTHING;
            title3.title = "Aún no has escuchado ninguna marcha :(";
            items.add(title3);
        }
        for(int i=0; i<marchas.size(); i++) {
            BibliotecaTab.BiblioItem biblioItem = new BibliotecaTab.BiblioItem();
            biblioItem.tipo = BibliotecaTab.BiblioItem.SONG;
            Marcha marcha = new Marcha(marchas.get(i).getAsJsonObject());
            biblioItem.marcha = marcha;
            items.add(biblioItem);
        }

        needResult.result(items);
    }

    // devuelve el top de marchas más escuchadas
    public static void getTop(UsoApi<ArrayList> needResult)
    {
        final String[] response = new String[1];
        final SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);


        String api = host + "/marchas/top";

        client.get(api,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, final String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );

        ArrayList<BibliotecaTab.BiblioItem> items = new ArrayList<>();

        JsonArray marchas = new JsonParser().parse(response[0]).getAsJsonArray();
        BibliotecaTab.BiblioItem title2 = new BibliotecaTab.BiblioItem();
        title2.tipo = BibliotecaTab.BiblioItem.TITLE;
        title2.title = "Marchas más escuchadas";
        items.add(title2);
        if(marchas.size() == 0)
        {
            BibliotecaTab.BiblioItem title3 = new BibliotecaTab.BiblioItem();
            title3.tipo = BibliotecaTab.BiblioItem.NOTHING;
            title3.title = "Esta lista no debería de estar vacía :(";
            items.add(title3);
        }
        for(int i=0; i<marchas.size(); i++) {
            BibliotecaTab.BiblioItem biblioItem = new BibliotecaTab.BiblioItem();
            biblioItem.tipo = BibliotecaTab.BiblioItem.SONG;
            Marcha marcha = new Marcha(marchas.get(i).getAsJsonObject());
            biblioItem.marcha = marcha;
            items.add(biblioItem);
        }

        needResult.result(items);
    }

    // nueva lista de reproducción
    public static void nuevaLista(String user, String nombre)
    {
        final String[] response = new String[1];

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);
        RequestParams params = new RequestParams();

        params.put("user", user);
        params.put("nombre", nombre);

        client.post(host + "/playlist/new", params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );
    }

    // crea una lista y añade una marcha
    public static void lista_add_new(String user, String nombre, String marcha)
    {
        final String[] response = new String[1];

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);
        RequestParams params = new RequestParams();

        params.put("user", user);
        params.put("nombre", nombre);
        params.put("marcha", marcha);

        client.post(host + "/playlist/new_add", params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );
    }

    // añade una marcha a una lista de reproducción
    public static void addLista(String user, String lista, String marcha)
    {
        final String[] response = new String[1];

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(5000);
        RequestParams params = new RequestParams();

        params.put("user", user);
        params.put("lista", lista);
        params.put("marcha", marcha);

        client.post(host + "/playlist/add", params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        response[0] = res;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        response[0] = res;
                    }
                }
        );
    }

    // devuelve una imagen de una url
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls)
        {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}


