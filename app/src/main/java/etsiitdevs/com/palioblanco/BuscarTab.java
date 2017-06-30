package etsiitdevs.com.palioblanco;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import etsiitdevs.com.palioblanco.api.Api;
import etsiitdevs.com.palioblanco.api.Lista;
import etsiitdevs.com.palioblanco.api.Marcha;
import etsiitdevs.com.palioblanco.api.ResultadoFiltro;
import etsiitdevs.com.palioblanco.api.UsoApi;
import etsiitdevs.com.palioblanco.player.AudioPlayer;

public class BuscarTab extends Fragment implements UsoApi<ArrayList>
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    private CustomEditText editText;
    private ListView listView;
    private ImageView buttonSearch;
    private boolean tecladoAbierto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.buscar, container, false);

        editText = (CustomEditText) view.findViewById(R.id.editText);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.addTextChangedListener(textWatcher);
        editText.setHandleDismissingKeyboard(new CustomEditText.handleDismissingKeyboard() {
            @Override
            public void dismissKeyboard() {
                buttonSearch.setImageResource(R.drawable.ic_magnify_black_48dp);
            }
        });

        buttonSearch = (ImageView) view.findViewById(R.id.button_search);
        buttonSearch.setImageResource(R.drawable.ic_chevron_down_black_48dp);
        tecladoAbierto = true;

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tecladoAbierto) {
                    tecladoAbierto = false;
                    buttonSearch.setImageResource(R.drawable.ic_magnify_black_48dp);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
                else
                {
                    tecladoAbierto = true;
                    buttonSearch.setImageResource(R.drawable.ic_chevron_down_black_48dp);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    editText.requestFocus();
                }
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tecladoAbierto) {
                    tecladoAbierto = false;
                    buttonSearch.setImageResource(R.drawable.ic_magnify_black_48dp);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
                else
                {
                    tecladoAbierto = true;
                    buttonSearch.setImageResource(R.drawable.ic_chevron_down_black_48dp);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    editText.requestFocus();
                }
            }
        });



        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ResultadoFiltro rf = resultadoFiltro.get(position);
                if(rf.tipo == ResultadoFiltro.MARCHA) {
                    final Marcha marcha = rf.marcha;
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
                            Toast.makeText(getContext(), marcha.nombre + " se está reproduciendo", Toast.LENGTH_SHORT).show();
                        }
                    });

                    addPlayList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAddPlaylistDialog(marcha);
                        }
                    });
                }
                else if (rf.tipo == ResultadoFiltro.LISTA)
                {
                    Lista lista = rf.lista;
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
                Api.getListas(BuscarTab.this, new SessionManager(getContext()).getUserDetails().get(SessionManager.KEY_ID));
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
                }
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        private Timer timer=new Timer();
        private final long DELAY = 350; // milliseconds

        @Override
        public void afterTextChanged(final Editable s) {
            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            if(s.length() >= 3) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Api.filtro(s.toString(), BuscarTab.this);
                                    }
                                }).start();
                            }

                            // TODO: do what you need here (refresh list)
                            // you will probably need to use runOnUiThread(Runnable action) for some specific actions

                        }
                    },
                    DELAY
            );
        }
    };

    private ArrayList<ResultadoFiltro> resultadoFiltro;
    private ArrayList<BibliotecaTab.BiblioItem> listas;
    @Override
    public void result(ArrayList result)
    {
        if(result.size()>0 && result.get(0) instanceof ResultadoFiltro) {
            resultadoFiltro = result;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(new SearchAdapter(getContext(), R.layout.list_item_search, resultadoFiltro));
                }
            });
        }
        else if(playlistview != null) {
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(getContext(), "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(getContext(), "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
    }
}
