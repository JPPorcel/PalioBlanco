package etsiitdevs.com.palioblanco;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import etsiitdevs.com.palioblanco.api.Localidad;

public class LocalidadesAdapter extends ArrayAdapter<Localidad>
{

    private Context context;
    private List<Localidad> items;
    private ArrayList<Localidad> suggestions;
    private Filter nameFilter;

    public LocalidadesAdapter(@NonNull Context c, @LayoutRes int resource, @NonNull List<Localidad> objects) {
        super(c, resource, objects);
        context = c;
        items = new ArrayList<>(objects);
        suggestions = new ArrayList<>();
        nameFilter = new Filter()
        {
            @Override
            public String convertResultToString(Object resultValue) {
                String str = ((Localidad)(resultValue)).getNombre() + ", " + ((Localidad)(resultValue)).getNombre();
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence prefix)
            {
                final FilterResults results = new FilterResults();

                if (prefix != null) {
                    final String prefixString = prefix.toString().toLowerCase();

                    final ArrayList<Localidad> values= new ArrayList<>(items);

                    final int count = values.size();
                    final ArrayList<Localidad> newValues = new ArrayList<>();

                    for (int i = 0; i < count; i++) {
                        final Localidad value = values.get(i);
                        final String valueText = value.getNombre().toLowerCase();

                        // First match against the whole, non-splitted value
                        if (valueText.startsWith(prefixString))
                            newValues.add(value);
                    }

                    results.values = newValues;
                    results.count = newValues.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                suggestions = (ArrayList<Localidad>) results.values;
                if(results.count > 0) {
                    clear();
                    addAll(suggestions);
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }


    @Override
    public int getCount() {
        return this.suggestions.size();
    }

    @Override
    public Localidad getItem(int position) {
        return suggestions.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        // Create a new view into the list.
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView nombre = (TextView) rowView.findViewById(android.R.id.text1);

        final Localidad item = this.suggestions.get(position);
        nombre.setText(item.getNombre() + ", " + item.getProvincia());

        return rowView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }
}