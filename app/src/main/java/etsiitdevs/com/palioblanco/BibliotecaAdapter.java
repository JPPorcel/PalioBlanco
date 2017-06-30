package etsiitdevs.com.palioblanco;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BibliotecaAdapter extends ArrayAdapter<BibliotecaTab.BiblioItem>
{


    public BibliotecaAdapter(Context context, int resource, List<BibliotecaTab.BiblioItem> items) {
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
        if(getItem(position).tipo == BibliotecaTab.BiblioItem.TITLE || getItem(position).tipo == BibliotecaTab.BiblioItem.NOTHING)
            return false;
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        BibliotecaTab.BiblioItem item = getItem(position);
        int type = getItemViewType(position);

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());

        if(type == BibliotecaTab.BiblioItem.TITLE || type == BibliotecaTab.BiblioItem.NOTHING || type == BibliotecaTab.BiblioItem.MORE)
        {
            v = vi.inflate(R.layout.biblio_item_title, null);
            TextView title = (TextView) v.findViewById(R.id.title);
            title.setText(item.title);
            if(type == BibliotecaTab.BiblioItem.NOTHING) {
                title.setTextSize(14);
                title.setTextColor(Color.rgb(136,136,136));
                title.setGravity(View.TEXT_ALIGNMENT_CENTER);
            }
        }
        else if(type == BibliotecaTab.BiblioItem.PLAYLIST) {
            v = vi.inflate(R.layout.biblio_item_playlist, null);
            TextView title = (TextView) v.findViewById(R.id.title);
            TextView subtitle = (TextView) v.findViewById(R.id.subtitle);

            title.setText(item.lista.nombre);
            subtitle.setText("por " + item.lista.creador);
        }
        else if(type == BibliotecaTab.BiblioItem.SONG) {
            v = vi.inflate(R.layout.biblio_item_song, null);
            TextView title = (TextView) v.findViewById(R.id.title);
            TextView subtitle = (TextView) v.findViewById(R.id.subtitle);

            title.setText(item.marcha.nombre);
            subtitle.setText(item.marcha.autor);
        }

        return v;
    }

}