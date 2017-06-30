package etsiitdevs.com.palioblanco;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import etsiitdevs.com.palioblanco.player.PlayerViewMin;

public class MainActivity extends FragmentActivity {

    private FragmentTabHost tabHost;
    private int indexTabSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(MainActivity.this, getSupportFragmentManager(), android.R.id.tabcontent);

        // construimos las tres pestañas
        tabHost.addTab(tabHost.newTabSpec("buscar").setIndicator(createTabView(tabHost.getContext(),
                "Buscar", R.drawable.ic_magnify_grey600_48dp, false)), BuscarTab.class, null);
        tabHost.addTab(tabHost.newTabSpec("inicio").setIndicator(createTabView(tabHost.getContext(),
                "Inicio", R.drawable.ic_home_grey600_48dp, true)), InicioTab.class, null);
        indexTabSelected = 1;
        tabHost.addTab(tabHost.newTabSpec("biblioteca").setIndicator(createTabView(tabHost.getContext(),
                "Biblioteca", R.drawable.ic_music_box_outline_grey600_48dp, false)), BibliotecaTab.class, null);

        tabHost.setCurrentTab(indexTabSelected);

        // cambio de pestaña
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int index = 0;
                if (tabId.equals("inicio")) index = 1;
                if (tabId.equals("biblioteca")) index = 2;
                View view = tabHost.getTabWidget().getChildTabViewAt(index);
                selectTab(tabHost.getTabWidget().getChildTabViewAt(indexTabSelected), view);
                indexTabSelected = index;
            }
        });

        // inicializamos el reproductor
        initPlayerMin();
    }

    private View createTabView(Context context, String text, int icon, boolean selected)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.indicator_tab_text);
        tv.setText(text);
        ImageView iv = (ImageView) view.findViewById(R.id.indicator_tab_icon);
        iv.setImageResource(icon);

        if(selected) {
            iv.setColorFilter(Color.BLACK);
            tv.setTextColor(Color.BLACK);
        }
        return view;
    }

    private void selectTab(View oldSelected, View newSelected)
    {
        TextView tv = (TextView) oldSelected.findViewById(R.id.indicator_tab_text);
        ImageView iv = (ImageView) oldSelected.findViewById(R.id.indicator_tab_icon);
        iv.setColorFilter(Color.rgb(136, 136, 136));
        tv.setTextColor(Color.rgb(136, 136, 136));
        tv = (TextView) newSelected.findViewById(R.id.indicator_tab_text);
        iv = (ImageView) newSelected.findViewById(R.id.indicator_tab_icon);
        iv.setColorFilter(Color.BLACK);
        tv.setTextColor(Color.BLACK);
    }


    /********************************************************************************/
    // PlayerView min
    private PlayerViewMin player;
    private void initPlayerMin()
    {
        player = (PlayerViewMin) findViewById(R.id.player);
    }

    public PlayerViewMin getPlayer()
    {
        return player;
    }
}