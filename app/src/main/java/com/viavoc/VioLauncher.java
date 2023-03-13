package com.viavoc;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.viavoc.lexpars.parser.Beautifier;
import com.viavoc.lexpars.parser.Lexer;
import com.viavoc.lexpars.parser.Parser;
import com.viavoc.lexpars.parser.Token;
import com.viavoc.lexpars.parser.ast.ClassStatement;
import com.viavoc.lexpars.parser.ast.Statement;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import br.tiagohm.codeview.CodeView;
import br.tiagohm.codeview.Language;
import br.tiagohm.codeview.Theme;

import static android.content.pm.PackageManager.GET_META_DATA;

public class VioLauncher extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WordsAdapter.WordOnClickListener {

    private DrawerLayout drawer;
    private RecyclerView wordsView;
    private FrameLayout bottomContainer;
    FrameLayout contentLauncher;
    private NavigationView navigationView;
    private FloatingActionButton fab;

    private List<String> words;
    private boolean isBottomBarShown = false;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int REQ_CODE_FILE_NAME = 88;
    private int indexToChange = -1;

    private WordsAdapter adapter;
    public Button convertButton;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    CodeView codeView;

    private Dialog newFileDialog;
    private EditText newFileEditText, newFilePackageName;

    private String fileName = "";
    private String packageName = "";
    private static boolean showDebug = false;
    private File primaryStorage = null;
    public Resources resources;

    private Parser parser;

    private View.OnClickListener onWordClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Snackbar.make(v, "onWordClick", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    };

    private Handler handler = new Handler();

    private Runnable bottomWatcher = new Runnable() {
        @Override
        public void run() {
            if (words.isEmpty()) hideBottomContainer();
            handler.postDelayed(this, 300);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vio_launcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpRecognizer();

        contentLauncher = (FrameLayout) findViewById(R.id.content_launcher);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                onRecordClick();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        wordsView = findViewById(R.id.words_container);
        words = new ArrayList<>(); //Arrays.asList("public", "listener")

        bottomContainer = findViewById(R.id.bottom_container);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        wordsView.setLayoutManager(layoutManager);

        adapter = new WordsAdapter(this, words, this);
        wordsView.setAdapter(adapter);

        convertButton = (Button) findViewById(R.id.convert_button);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertWords();
            }
        });

        codeView = findViewById(R.id.code_view);
        String code = "// hi and welcome to viavoc\n" +
                "// wishing you to enjoy\n" +
                "// working with our app\n" +
                "public class Life {\n" +
                "  public final boolean enjoy = true;\n" +
                "  public boolean do() {\n" +
                "     return enjoy;\n" +
                "  }\n" +
                "}\n";

        codeView.setTheme(Theme.ANDROIDSTUDIO)
                .setCode(code)
                .setLanguage(Language.JAVA)
                .setShowLineNumber(true)
                .apply();

        checkPermission();

        handler.post(bottomWatcher);

        navigationView.getMenu().getItem(0).setChecked(true);
        parser = new Parser(this);

        WebView.setWebContentsDebuggingEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!fileName.isEmpty()) {
            setTitle(fileName);
        } else {
            setTitle(R.string.app_name);
        }
    }

    private void convertWords(){
        StringBuilder builder = new StringBuilder();
        if (words.size() < 2) {
            Snackbar.make(wordsView, R.string.cannot_convert, Snackbar.LENGTH_LONG).show();
        } else {
            for (String word : words) {
                builder.append(word).append(" ");
            }
            try {
                Lexer lexer = new Lexer(builder.toString());
                List<Token> tokens = lexer.tokenize();
                if (showDebug) System.out.println(tokens);
                parser.addTokens(tokens);

                Statement result;
                if (parser.isFirst()) {
                    result = parser.parse();
                } else {
                    result = parser.continueParsing();
                }
                codeView.setCode(Beautifier.beautify(result.toString())).apply();
                hideBottomContainer();
                words.clear();
            } catch (Exception ex) {
                Snackbar.make(wordsView, R.string.error_in_command, Snackbar.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
    }

    private void onRecordClick() {
        changeWord(-1);
    }

    private void setUpRecognizer(){
        /**
         * Setting up the recognizer
         * */
        Locale locale = Locale.getDefault() == Locale.UK ? Locale.UK : Locale.US;
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                locale);

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                hideBottomContainer();
            }

            @Override
            public void onBeginningOfSpeech() {
                System.out.println("speech started");
            }

            @Override
            public void onRmsChanged(float v) { }

            @Override
            public void onBufferReceived(byte[] bytes) { }

            @Override
            public void onEndOfSpeech() {
                onRecordClick();
            }

            @Override
            public void onError(int i) { }

            @Override
            public void onResults(Bundle bundle) {
                System.out.println("results");
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null) {
                    System.out.println("on recognition results");
                    words.addAll(Arrays.asList(matches.get(0).toLowerCase().split("\\s+")));
                    System.out.println("words: " + words);
                    adapter.notifyDataSetChanged();
                    showBottomContainer();
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if (!fab.isShown()) {
            getSupportFragmentManager().popBackStack();
            fab.show();
        }
        setTitle(R.string.app_name);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void showNewFileDialog() {
        newFileDialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.new_file_window_layout, null, false);
        newFileEditText = (EditText) view.findViewById(R.id.new_file_edit);
        newFileEditText = (EditText) view.findViewById(R.id.new_file_package);

        ImageView recordView = (ImageView) view.findViewById(R.id.new_file_record);
        recordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        getString(R.string.say_file_name));
                try {
                    startActivityForResult(intent, REQ_CODE_FILE_NAME);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.speech_not_supported),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button finishButton = (Button) view.findViewById(R.id.new_file_finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = newFileEditText.getText().toString();
                String pName = newFilePackageName.getText().toString();
                if (!fName.isEmpty()) fileName = fName;
                if (!pName.isEmpty()) packageName = pName;
                newFileDialog.hide();
                updateName();
            }
        });

        newFileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newFileDialog.setContentView(view);
        newFileDialog.show();
    }

    private void updateName(){
        if (!fileName.isEmpty()) {
            setTitle(fileName);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class current = null;
        if (id == R.id.nav_home) {
            item.setChecked(true);
            onBackPressed();
            return true;
        } else if (id == R.id.nav_save_file) {
            drawer.closeDrawer(Gravity.START);
            saveFile();
            return false;
        } else if (id == R.id.nav_new_file) {
            drawer.closeDrawer(Gravity.START);
            saveFile();
            showNewFileDialog();
            return true;
        } else if (id == R.id.nav_guides) {
            current = TutorialsFragment.class;
        } else if (id == R.id.nav_share) {
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setChooserTitle(getResources().getString(R.string.choose_app))
                    .setText(getResources().getString(R.string.i_use_viavoc) +
                            "http://play.google.com/store/apps/details?id=" + this.getPackageName())
                    .startChooser();
            return false;
        } else if (id == R.id.nav_feedback) {
            openMarket();
            return true;
        } else if (id == R.id.nav_report) {
            current = ReportFragment.class;
        }
        item.setChecked(true);
        if (current != null) {
            try {
                onBackPressed();
                fragment = (Fragment) current.newInstance();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.content_launcher, fragment).addToBackStack(ReportFragment.TAG).commit();
                setTitle(item.getTitle());
                fab.hide();

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean saveFile() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            System.out.println(parser.getParsedStatement());
            if (parser.getParsedStatement() != null){
                ClassStatement main = parser.getParsedStatement();

                if (packageName != null && main.packageName == null) {
                    main.packageName = packageName;
                }
                String name;
                if (fileName == null || (fileName != null & fileName.isEmpty())) {
                    main.name.suppressTemplatingTypes = true;
                    name = main.name.toString();
                    main.name.suppressTemplatingTypes = false;
                } else name = fileName;
                name = name + ".java";

                if (primaryStorage != null) {
                    File file = new File(primaryStorage.getAbsolutePath() + "/" + name);
                    FileOutputStream outputStream;

                    boolean written = false;
                    try {
                        outputStream = new FileOutputStream(file);
                        outputStream.write(main.toString().getBytes());
                        outputStream.close();
                        written = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (written) {
                        Snackbar.make(wordsView,
                                getString(R.string.file) + "  " + name + "  " + getString(R.string.was_saved),
                                Snackbar.LENGTH_LONG).show();
                        return true;
                    }
                } else {
                    checkPermission();
                    return false;
                }
            }
            return false;
        } else {
            checkPermission();
            return false;
        }
    }

    private void openMarket(){
        Context context = getApplicationContext();
        String appId = context.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;
            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+appId));
            context.startActivity(webIntent);
        }
    }

    private void showBottomContainer() {
        if (!isBottomBarShown) {
            isBottomBarShown = true;

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bottomContainer.getLayoutParams();
            params.bottomMargin = 0;
            bottomContainer.setLayoutParams(params);

            FrameLayout.LayoutParams fabParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
            fabParams.bottomMargin += getResources().getDimensionPixelOffset(R.dimen.words_container_height);
            fab.setLayoutParams(fabParams);

            ViewGroup parent = (ViewGroup) findViewById(R.id.content_launcher);

            //TransitionManager.beginDelayedTransition(bottomContainer);
            TransitionManager.beginDelayedTransition(parent);
        }
    }

    private void hideBottomContainer() {
        if (isBottomBarShown) {
            isBottomBarShown = false;

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bottomContainer.getLayoutParams();
            params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.words_container_height_hidden);
            bottomContainer.setLayoutParams(params);

            FrameLayout.LayoutParams fabParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
            fabParams.bottomMargin -= getResources().getDimensionPixelOffset(R.dimen.words_container_height);
            fab.setLayoutParams(fabParams);

            ViewGroup parent = (ViewGroup) findViewById(R.id.content_launcher);

            //TransitionManager.beginDelayedTransition(bottomContainer);
            TransitionManager.beginDelayedTransition(parent);
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 0);
            }
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                primaryStorage =  new File(Environment.getExternalStorageDirectory(), "Viavoc");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String[] newWords = result.get(0).toLowerCase().split("\\s+");
                    if (indexToChange != -1 && newWords.length != 0) {
                        words.remove(indexToChange);
                        words.addAll(indexToChange, Arrays.asList(newWords));
                        indexToChange = -1;
                        adapter.notifyDataSetChanged();
                    } else {
                        words.addAll(Arrays.asList(newWords));
                        adapter.notifyDataSetChanged();
                    }
                    showBottomContainer();
                }
                break;
            }
            case REQ_CODE_FILE_NAME : {
                if (newFileEditText != null) {
                    try {
                        ArrayList<String> result = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String[] newWords = result.get(0).split("\\s+");
                        StringBuilder finalName = new StringBuilder();
                        StringBuilder builder;
                        for (String word : newWords) {
                            builder = new StringBuilder(word);
                            builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
                            finalName.append(builder);
                        }
                        newFileEditText.setText(finalName);
                    } catch (Exception ex) {

                    }
                }
            }
        }
    }

    @Override
    public void changeWord(int position) {
        indexToChange = position;

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                primaryStorage = new File(Environment.getExternalStorageDirectory(), "Viavoc");
                if (!primaryStorage.exists()) {
                    primaryStorage.mkdirs();
                }
            }
        } catch (Exception e) {

        }
    }
}
