package etsiitdevs.com.palioblanco;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.HashMap;

import etsiitdevs.com.palioblanco.api.Api;
import etsiitdevs.com.palioblanco.api.Lista;
import etsiitdevs.com.palioblanco.api.Marcha;
import etsiitdevs.com.palioblanco.api.UsoApi;
import etsiitdevs.com.palioblanco.player.AudioPlayer;


public class BibliotecaTab extends Fragment implements UsoApi<ArrayList>, PopupMenu.OnMenuItemClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    private ImageView user_image;
    private ImageView settings;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.biblioteca, container, false);


        HashMap<String,String> data = new SessionManager(getContext()).getUserDetails();
        user_image = (ImageView) view.findViewById(R.id.user_image);
        settings = (ImageView) view.findViewById(R.id.settings);
        new Api.DownloadImageTask(user_image).execute(data.get(SessionManager.KEY_IMG));

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });


        listView = (ListView) view.findViewById(R.id.listView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Api.getLibrary(new SessionManager(getContext()).getUserDetails().get(SessionManager.KEY_ID), BibliotecaTab.this);
            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BiblioItem biblioItem = items.get(position);
                if(biblioItem.tipo == BiblioItem.SONG) {
                    final Marcha marcha = biblioItem.marcha;
                    final Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
                    // layout to display
                    dialog.setContentView(R.layout.dialog_marcha);
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                    // set color transpartent
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
                    dialog.show();


                    RelativeLayout back = (RelativeLayout) dialog.findViewById(R.id.back);
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    TextView song_name = (TextView) dialog.findViewById(R.id.song_name);
                    song_name.setText(marcha.nombre);
                    TextView song_autor = (TextView) dialog.findViewById(R.id.song_autor);
                    song_autor.setText(marcha.autor);

                    TextView play = (TextView) dialog.findViewById(R.id.play);
                    TextView addPlayList = (TextView) dialog.findViewById(R.id.add_playlist);
                    TextView addCola = (TextView) dialog.findViewById(R.id.add_cola);

                    addCola.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AudioPlayer.getInstance(getContext()).addMarcha(marcha);
                            Toast.makeText(getContext(), marcha.nombre + " añadida a la cola de reproducción", Toast.LENGTH_SHORT).show();
                        }
                    });

                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AudioPlayer.getInstance(getContext()).addMarcha(marcha);
                            AudioPlayer.getInstance(getContext()).next();
                            Toast.makeText(getContext(), marcha.nombre + " se está reproduciendo.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    addPlayList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAddPlaylistDialog(marcha);
                        }
                    });
                }
                else if (biblioItem.tipo == BiblioItem.PLAYLIST)
                {
                    Lista lista = biblioItem.lista;
                    Intent intent = new Intent(getContext(), PlaylistActivity.class);
                    intent.putExtra("LISTA", lista.id);
                    intent.putExtra("NOMBRE", lista.nombre);
                    intent.putExtra("CREADOR", lista.creador);
                    startActivity(intent);
                }
            }
        });


        return view;
    }


    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrar:
                LoginManager.getInstance().logOut();
                new SessionManager(getContext()).logoutUser();
                AudioPlayer.getInstance(getContext()).stop();
                startActivity(new Intent(getContext(), LogInActivity.class));
                getActivity().finish();
                return true;
            default:
                return false;
        }
    }



    private Dialog dialogAddPlaylist;
    private ListView playlistview;
    private void showAddPlaylistDialog(final Marcha marcha)
    {
        dialogAddPlaylist = new Dialog(getContext(), R.style.DialogTheme);
        // layout to display
        dialogAddPlaylist.setContentView(R.layout.dialog_add_playlist);
        dialogAddPlaylist.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // set color transpartent
        dialogAddPlaylist.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
        dialogAddPlaylist.show();


        RelativeLayout back = (RelativeLayout) dialogAddPlaylist.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddPlaylist.dismiss();
            }
        });

        playlistview = (ListView) dialogAddPlaylist.findViewById(R.id.listView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Api.getListas(BibliotecaTab.this, new SessionManager(getContext()).getUserDetails().get(SessionManager.KEY_ID));
            }
        }).start();

        playlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listas.get(position).tipo == BibliotecaTab.BiblioItem.MORE)
                {
                    final Dialog dialog = new Dialog(getContext(), R.style.Theme_AppCompat_Light_Dialog);
                    // layout to display
                    dialog.setContentView(R.layout.dialog_new_playlist);
                    dialog.show();

                    final EditText playlist_name = (EditText) dialog.findViewById(R.id.playlist_name);
                    playlist_name.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    TextView aceptar = (TextView) dialog.findViewById(R.id.aceptar);
                    aceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!playlist_name.getText().toString().equals("")) {
                                final String nombre = playlist_name.getText().toString();
                                dialogAddPlaylist.dismiss();
                                playlistview = null;
                                dialog.dismiss();
                                Toast.makeText(getContext(), marcha.nombre + " añadida a la lista de reproducción " + nombre, Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Api.lista_add_new(new SessionManager(getContext()).getUserDetails().get(SessionManager.KEY_ID), nombre, marcha.id);
                                    }
                                }).start();
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Introduce un nombre para la lista de reproducción", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    TextView cancelar = (TextView) dialog.findViewById(R.id.cancelar);
                    cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
                else if (listas.get(position).tipo == BibliotecaTab.BiblioItem.PLAYLIST)
                {
                    final String lista = listas.get(position).lista.id;
                    Toast.makeText(getContext(), marcha.nombre + " añadida a la lista de reproducción " + listas.get(position).lista.nombre, Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Api.addLista(new SessionManager(getContext()).getUserDetails().get(SessionManager.KEY_ID), lista, marcha.id);
                        }
                    }).start();
                    dialogAddPlaylist.dismiss();
                    playlistview = null;
                }
            }
        });
    }

    private ArrayList<BiblioItem> items;
    private ArrayList<BibliotecaTab.BiblioItem> listas;
    @Override
    public void result(ArrayList result) {

        if(playlistview != null) {
            listas = new ArrayList<>();
            ArrayList<Lista> tmp = result;
            BibliotecaTab.BiblioItem title1 = new BibliotecaTab.BiblioItem();
            title1.tipo = BibliotecaTab.BiblioItem.MORE;
            title1.title = "Nueva lista de reproducción";
            listas.add(title1);
            for (Lista lista : tmp)
            {
                BibliotecaTab.BiblioItem biblioItem = new BibliotecaTab.BiblioItem();
                biblioItem.tipo = BibliotecaTab.BiblioItem.PLAYLIST;
                biblioItem.lista = lista;
                listas.add(biblioItem);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playlistview.setAdapter(new BibliotecaAdapter(getContext(), R.layout.biblio_item_playlist, listas));
                }
            });
        }
        else if(result.size()>0 && result.get(0) instanceof BiblioItem) {
            items = result;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(new BibliotecaAdapter(getContext(), R.layout.biblio_item_title, items));
                }
            });
        }
    }


    public static class BiblioItem
    {
        public static final int PLAYLIST = 0;
        public static final int SONG = 1;
        public static final int TITLE = 2;
        public static final int NOTHING = 3;
        public static final int MORE = 4;

        public int tipo;
        public String title;
        public Marcha marcha;
        public Lista lista;
    }
}

