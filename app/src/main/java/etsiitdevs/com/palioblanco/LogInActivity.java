package etsiitdevs.com.palioblanco;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;
import java.util.Arrays;

import etsiitdevs.com.palioblanco.api.Api;
import etsiitdevs.com.palioblanco.api.Localidad;
import etsiitdevs.com.palioblanco.api.Response;
import etsiitdevs.com.palioblanco.api.UsoApi;
import etsiitdevs.com.palioblanco.db.DBHelper;

public class LogInActivity extends Activity implements UsoApi<Response>
{

    private TextView title;

    private AutoCompleteTextView autoCompleteTextView;
    private ArrayList<Localidad> localidades;
    private Localidad localidadSeleccionada;


    private Button logupButton;
    private Button loginButton;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private SessionManager session;


    private ViewPager viewPager;
    private SliderAdapter mSliderAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;


    private boolean login;

    private String name;
    private String id;
    private Uri img;
    private String apellidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        title = (TextView) findViewById(R.id.title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Windsong.ttf");
        title.setTypeface(type);
        title.setText(getString(R.string.app_name));


        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.open();
        localidades = dbHelper.getLocalidades();
        dbHelper.close();
        localidadSeleccionada = null;

        autoCompleteTextView.setAdapter(new LocalidadesAdapter(this, R.layout.list_item, localidades));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                localidadSeleccionada = (Localidad) parent.getItemAtPosition(position);
            }
        });


        /******************************************************************************************/
        // Language selector

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, R.layout.spinner_text);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        /******************************************************************************************/
        // Slider

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        // adding bottom dots
        addBottomDots(0);

        mSliderAdapter = new SliderAdapter(this, layouts);
        viewPager.setAdapter(mSliderAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        handler = new Handler();


        /******************************************************************************************/
        // Inicio de sesión con Facebook

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = true;
                fbLogIn();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        logupButton = (Button) findViewById(R.id.logup_button);

        logupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                login = false;
                fbLogUp();
            }
        });

        session = new SessionManager(getApplicationContext());

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                if(currentProfile != null)
                {
                    //logupButton.setText("Salir");

                    name = currentProfile.getName();
                    id = currentProfile.getId();
                    img = currentProfile.getProfilePictureUri(200, 200);
                    apellidos = currentProfile.getLastName();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(login) {
                                Api.logIn(LogInActivity.this, id);
                            }
                            else {
                                // llamada a la Api para registrar un nuevo usuario
                                Api.logUp(LogInActivity.this, id, name, apellidos, img.toString(), localidadSeleccionada.getId());
                            }
                        }
                    }).start();

                }
                else
                {
                    //Toast.makeText(LogInActivity.this, "Sesión Cerrada", Toast.LENGTH_SHORT).show();
                    session.logoutUser();
                }
            }
        };

        if(session.isLoggedIn())
        {
            Intent i = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        /******************************************************************************************/
    }

    public void fbLogUp()
    {
        if(localidadSeleccionada == null)
        {
            Toast.makeText(LogInActivity.this, "Debes de seleccionar tu localidad", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LoginManager.getInstance().logInWithReadPermissions(LogInActivity.this,
                    Arrays.asList("public_profile", "user_friends"));

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>()
                    {
                        @Override
                        public void onSuccess(LoginResult loginResult)
                        {
                            Log.e("GIDM", "Facebook login success");
                        }

                        @Override
                        public void onCancel()
                        {
                            Log.e("GIDM", "Facebook login cancel");
                        }

                        @Override
                        public void onError(FacebookException exception)
                        {
                            Log.e("GIDM", "Facebook login error");
                        }
                    });
        }
    }

    public void fbLogIn()
    {
        LoginManager.getInstance().logInWithReadPermissions(LogInActivity.this,
                    Arrays.asList("public_profile", "user_friends"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        Log.e("GIDM", "Facebook login success");
                    }

                    @Override
                    public void onCancel()
                    {
                        Log.e("GIDM", "Facebook login cancel");
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        Log.e("GIDM", "Facebook login error");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            page = position;

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
            } else {
                // still pages are left
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private Handler handler;
    private int delay = 5000; //milliseconds
    private int page = 0;
    Runnable runnable = new Runnable() {
        public void run() {
            if (mSliderAdapter.getCount() == page) {
                page = 0;
            } else {
                page++;
            }
            viewPager.setCurrentItem(page, true);
            handler.postDelayed(this, delay);
        }
    };


    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.dot_dark_screen));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.dot_light_screen));
    }

    @Override
    public void result(Response response)
    {
        if (response.code.equals(Response.OK)) {
            // creamos la sesión para el usuario
            session.createLoginSession(name, id, img.toString());
            // iniciamos la Activity principal
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }
        else if (response.code.equals(Response.UNREGISTERED_USER)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(LogInActivity.this, "Aún no estás registrado.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (response.code.equals(Response.USER_ALREADY_REGISTERED)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(LogInActivity.this, "Ya estás registrado. Inicia sesión.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
