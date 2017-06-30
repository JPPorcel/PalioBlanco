package etsiitdevs.com.palioblanco;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import etsiitdevs.com.palioblanco.api.Api;
import etsiitdevs.com.palioblanco.api.Autor;
import etsiitdevs.com.palioblanco.api.Lista;
import etsiitdevs.com.palioblanco.api.Marcha;
import etsiitdevs.com.palioblanco.api.ResultadoFiltro;
import etsiitdevs.com.palioblanco.api.Usuario;

public class SearchAdapter extends ArrayAdapter<ResultadoFiltro> {

    public SearchAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public SearchAdapter(Context context, int resource, List<ResultadoFiltro> items) {
        super(context, resource, items);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).tipo;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public boolean isEnabled(int position) {
        if(getItem(position).tipo == ResultadoFiltro.TITLE)
            return false;
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        int type = getItemViewType(position);

        ResultadoFiltro rf = getItem(position);

        if (rf != null) {
            if(type == ResultadoFiltro.MARCHA) {
                v = vi.inflate(R.layout.search_item_marcha, null);
                Marcha m = rf.marcha;
                ImageView imageView = (ImageView) v.findViewById(R.id.img_marcha);
                TextView nombre = (TextView) v.findViewById(R.id.title);
                TextView autor = (TextView) v.findViewById(R.id.subtitle);

                nombre.setText(m.nombre);
                autor.setText(m.autor);
                //if(!m.imagen.equals("")) imageView
            }
            else if(type == ResultadoFiltro.AUTOR) {
                v = vi.inflate(R.layout.search_item_autor, null);
                Autor a = rf.autor;
                TextView nombre = (TextView) v.findViewById(R.id.nombre);

                nombre.setText(a.nombre);
            }
            else if(type == ResultadoFiltro.USUARIO) {
                v = vi.inflate(R.layout.search_item_usuario, null);
                Usuario u = rf.usuario;
                ImageView imageView = (ImageView) v.findViewById(R.id.img_user);
                TextView nombre = (TextView) v.findViewById(R.id.nombre);
                TextView localidad = (TextView) v.findViewById(R.id.localidad);

                nombre.setText(u.nombre);
                localidad.setText(u.localidad);
                new Api.DownloadImageTask(imageView).execute(u.imagen);
            }
            else if(type == ResultadoFiltro.LISTA) {
                v = vi.inflate(R.layout.search_item_playlist, null);
                Lista l = rf.lista;
                ImageView imageView = (ImageView) v.findViewById(R.id.img_user);
                TextView title = (TextView) v.findViewById(R.id.title);
                TextView subtitle = (TextView) v.findViewById(R.id.subtitle);

                title.setText(l.nombre);
                subtitle.setText("por " + l.creador);
                //new Api.DownloadImageTask(imageView).execute(u.imagen);
            }
            else if(type == ResultadoFiltro.TITLE) {
                v = vi.inflate(R.layout.search_item_title, null);
                String t = rf.title;
                TextView title = (TextView) v.findViewById(R.id.title);

                title.setText(t);
            }
        }

        return v;
    }

}
