package com.example.voicetranslation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class MainMenu extends AppCompatActivity {

    Button done_button;
    FloatingActionButton record;
    BottomNavigationView bottomNavigationView;
    public TextToSpeech mtts;
    String [] languages = {"kannada","english","telugu","tamil","malay","marathi","hindi","punjabi","gujrathi","spanish","french","dutch","vietnamese","indonesian","russian","portuguese","german","japanese","chinese","italian","korean","turkish","greek","arabic"};
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompleteTextView2;
    ArrayAdapter<String> adapteritems;
//see iff locale.getdefault works down there below
public static Locale locale_language_from = Locale.getDefault();
    public static  Locale locale_language_to= Locale.getDefault();

    EditText speech;
    SpeechRecognizer mSpeechRecongnizer;
    Intent mSpeechRecongnizerIntent;
    //speech_said contains the words said by the user
    String speech_said,language2;
    TextView ex;
    String target_lang,source_lang;
   public static  String Language_selected_to = null;
    public static  String Language_selected_from = null;
      String speech_recog ;
    ArrayList<ExampleItem> exampleList = new ArrayList<>();
    ImageView im;
    int count = 1;
private boolean checkEnabled;
     Dialog dialog ;
    private int PERMISSION_RECORD_AUDIO= 1;
    private AnimatorSet mPulseAnimatorSet;
   private LinearLayout overlayLayout;
    BottomAppBar bottomAppBar;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //checkPermission();

        ex=findViewById(R.id.ex);
        im=findViewById(R.id.im);

         bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setTranslationY(0);

        bottomAppBar= findViewById(R.id.bottomAppBar);

        record = (FloatingActionButton)  findViewById(R.id.voice_record_btn);
        record.bringToFront();
        if (ContextCompat.checkSelfPermission(MainMenu.this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED){}


        else requestPermission();



        record.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (Language_selected_to!=null&&Language_selected_from!=null) {


                    switch (motionEvent.getAction()) {


                        case MotionEvent.ACTION_DOWN:
                            ex.setText("Listening.....");
                            mSpeechRecongnizer.startListening(mSpeechRecongnizerIntent);
                            Animation pulseAnimation = AnimationUtils.loadAnimation(MainMenu.this, R.anim.pulse_animation);
                            record.startAnimation(pulseAnimation);


                            bottomAppBar.animate()
                                    .translationY(bottomAppBar.getHeight())
                                    .setDuration(300)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            bottomAppBar.setVisibility(View.GONE);
                                        }
                                    })
                                    .start();

                            break;

                        case MotionEvent.ACTION_UP:
                            ex.setText("You can translate from "+Language_selected_from+" to "+Language_selected_to);
                            mSpeechRecongnizer.stopListening();
                            record.clearAnimation();



                            bottomAppBar.animate()
                                    .translationY(0)
                                    .setDuration(300)
                                    .withStartAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            bottomAppBar.setVisibility(View.VISIBLE);
                                        }
                                    })
                                    .start();
                            break;

                    }
                }

                else
                    Toast.makeText(MainMenu.this, "please select a language first", Toast.LENGTH_SHORT).show();


                return false;
            }
        });


        dialog=new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);


         done_button = dialog.findViewById(R.id.lang_btn_done);
        // use dialog.findviewbyid to acess the spinner and button in the bottom dialog

        autoCompleteTextView =dialog.findViewById(R.id.textinputfromlang);
        adapteritems = new ArrayAdapter<>(this, R.layout.dropdown_item, languages);
        autoCompleteTextView.setAdapter(adapteritems);





        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Language_selected_from = adapterView.getItemAtPosition(i).toString();
                speechRecognition();

                if (Language_selected_from!=null&&Language_selected_to!=null)
                {
                    done_button.setEnabled(true);
                }



                //Toast.makeText(MainMenu.this, "Item "+Language_selected_from, Toast.LENGTH_SHORT).show();

                //string item contains the language selected
            }
        });

        autoCompleteTextView2 = dialog.findViewById(R.id.textinputtolang);
        adapteritems = new ArrayAdapter<>(this, R.layout.dropdown_item, languages);
        autoCompleteTextView2.setAdapter(adapteritems);

        autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Language_selected_to = adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(MainMenu.this, "Item "+Language_selected_to, Toast.LENGTH_SHORT).show();\

                if (Language_selected_from!=null&&Language_selected_to!=null)
                {
                    done_button.setEnabled(true);
                }

                count++;
            }

        });






        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Language_selected_from.equals(Language_selected_to))
                {
                    Toast.makeText(MainMenu.this, "You have selected the same languages", Toast.LENGTH_SHORT).show();
                }
                else
                dialog.dismiss();

            }
        });



        mSpeechRecongnizer=SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecongnizerIntent   = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);



        bottomNavigationView.setOnItemSelectedListener(item -> {


            switch (item.getItemId()){

                case R.id.btn_language:
                   // Toast.makeText(this, "lang", Toast.LENGTH_SHORT).show();
                    showDialog();
                    break;


                case R.id.btn_scan:
                    Toast.makeText(this, "scan", Toast.LENGTH_SHORT).show();
                    break;

            }


            return false;
        });

        mtts = new TextToSpeech(MainMenu.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS)
                {
                   tts();

                }
                else {
                    Log.e("tts","Initialization failed");

                }
            }
        });


    }

    private void checkEnabled() {

        if (Language_selected_to.equals(Language_selected_from))
        {
            done_button.setEnabled(false);
        }
    }


    private void showDialog() {

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations= R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    private void checkPermission(){

        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)){

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
            startActivity(intent);
            finish();

        }
     }

     private void requestPermission()
     {

             ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},PERMISSION_RECORD_AUDIO);

     }


    private void translation(String speech_said2) {
        TranslatorOptions options;

        String source_text = speech_said2;

if (Language_selected_from!=null) {

    switch (Language_selected_from) {

        case "kannada":
            source_lang = "kn";
            speech_recog = "kn_IN";
            locale_language_from = new Locale("kn", "IN");
            break;

        case "malay":
            source_lang = "ms";
            speech_recog = "ms_MY";
            locale_language_from = new Locale("ms", "MY");
            break;
        case "portuguese":
            source_lang = "pt";
            speech_recog = "pt_BR";
            locale_language_from = new Locale("pt");
            break;

        case "arabic":
            source_lang = "ar";
            speech_recog = "ar_SA";
            locale_language_from = new Locale("ar", "SA");
            break;

        case "tamil":
            source_lang = "ta";
            speech_recog = "ta_IN";
            locale_language_from = new Locale("ta", "IN");

            break;

        case "telugu":
            source_lang = "te";
            speech_recog = "te_IN";
            locale_language_from = new Locale("te", "IN");

            break;

        case "marathi":
            source_lang = "mr";
            speech_recog = "mr_IN";
            locale_language_from = new Locale("mr", "IN");

            break;

        case "hindi":
            source_lang = "hi";
            speech_recog = "hi_IN";
            locale_language_from = new Locale("hi", "IN");

            break;

        case "gujrathi":
            source_lang = "gu";
            speech_recog = "gu_IN";
            locale_language_from = new Locale("gu", "IN");

            break;

        case "japanese":
            source_lang = "ja";
            speech_recog = "ja_JP";
            locale_language_from = new Locale("ja");
            break;

        case "vietnamese":
            source_lang = "vi";
            speech_recog = "vi_VN";
            locale_language_from = new Locale("vi");
            break;

        case "spanish":
            source_lang = "es";
            speech_recog = "es_BR";
            locale_language_from = new Locale("es");
            break;

        case "dutch":
            source_lang = "nl";
            speech_recog = "nl_NL";
            locale_language_from = new Locale("nl");
            break;

        case "german":
            source_lang = "de";
            speech_recog = "de_DE";
            locale_language_from = new Locale("de");
            break;

        case "russian":
            source_lang = "ru";
            speech_recog = "ru_RU";
            locale_language_from = new Locale("ru");
            break;

        case "french":
            source_lang = "fr";
            speech_recog = "fr_FR";
            locale_language_from = new Locale("fr");
            break;

        case "chinese":
            source_lang = "zh";
            speech_recog = "zh_HK";
            locale_language_from = new Locale("zh");
            break;

        case "korean":
            source_lang = "ko";
            speech_recog = "ko_KR";
            locale_language_from = new Locale("ko");
            break;

        case "english":
            source_lang = "en";
            speech_recog = "en_US";
            locale_language_from = new Locale("en", "US");
            break;

        case "indonesian":
            source_lang = "id";
            speech_recog = "id_ID";
            locale_language_from = new Locale("id");
            break;

        case "turkish":
            source_lang = "tr";
            speech_recog = "tr_TR";
            locale_language_from = new Locale("tr");
            break;

        case "greek":
            source_lang = "el";
            speech_recog = "el_GR";
            locale_language_from = new Locale("el");
            break;
        case "italian":
            source_lang = "it";
            speech_recog = "it_IT";
            locale_language_from = new Locale("it");
            break;


        default:
            throw new IllegalStateException("Unexpected value: " + Language_selected_from);
    }
}
else Toast.makeText(this, "select a language first", Toast.LENGTH_SHORT).show();


        if (Language_selected_to==null)
        {
            Toast.makeText(this, "Say something", Toast.LENGTH_SHORT).show();
        }

        else {

            switch (Language_selected_to){

                case "english":
                    target_lang = "en";
                    locale_language_to = new Locale("en","US");
                    break;
                case "malay":
                    target_lang = "ms";
                    locale_language_to = new Locale("ms","MY");
                    break;
                case "portuguese":
                    target_lang = "pt";
                    locale_language_to = new Locale("pt","BR");
                    break;
                case "arabic":
                    target_lang = "ar";
                    locale_language_to = new Locale("ar","SA");
                    break;
                case "kannada":
                    target_lang = "kn";
                     locale_language_to = new Locale("kn","IN");
                     break;

                case "tamil":
                    target_lang = "ta";
                    locale_language_to = new Locale("ta","IN");

                    break;

                case "telugu":
                    target_lang = "te";
                    locale_language_to = new Locale("te","IN");

                    break;

                case "marathi":
                    target_lang = "mr";
                    locale_language_to = new Locale("mr","IN");

                    break;

                case "hindi":
                    target_lang = "hi";
                    locale_language_to = new Locale("hi","IN");

                    break;

                case "gujrathi":
                    target_lang = "gu";
                    locale_language_to = new Locale("gu","IN");

                    break;

                case "japanese":
                    target_lang = "ja";
                    locale_language_to = new Locale("ja","JP");
                    break;

                case "vietnamese":
                    target_lang = "vi";
                    locale_language_to = new Locale("vi","VN");
                    break;

                case "spanish":
                    target_lang = "es";
                    locale_language_to = new Locale("es","BR");
                    break;

                case "dutch":
                    target_lang = "nl";
                    locale_language_to = new Locale("nl","BE");
                    break;

                case "german":
                    target_lang = "de";
                    locale_language_to = new Locale("de","DE");
                    break;

                case "russian":
                    target_lang = "ru";
                    locale_language_to = new Locale("ru","RU");
                    break;

                case "french":
                    target_lang = "fr";
                    locale_language_to = new Locale("fr","FR");
                    break;

                case "chinese":
                    target_lang = "zh";
                    locale_language_to = new Locale("zh","HK");
                    break;

                case "korean":
                    target_lang = "ko";
                    locale_language_to = new Locale("ko","KR");
                    break;



                case "indonesian":
                    target_lang = "id";
                    locale_language_to = new Locale("id");
                    break;

                case "turkish":
                    target_lang = "tr";
                    locale_language_to = new Locale("tr","TR");
                    break;

                case "greek":
                    target_lang = "el";
                    locale_language_to = new Locale("el","GR");
                    break;
                case "italian":
                    target_lang = "it";
                    locale_language_to = new Locale("it","IT");
                    break;


                default:
                    throw new IllegalStateException("Unexpected value: " + Language_selected_to);
            }

            options = new TranslatorOptions.Builder()
                    .setSourceLanguage(source_lang)
                    .setTargetLanguage(target_lang).build();

            Translator translator = Translation.getClient(options);


            ProgressDialog progressDialog  = new ProgressDialog(MainMenu.this);
            progressDialog.setTitle("This Message is only shown once");
            progressDialog.setMessage("Please wait till the files are  downloaded this takes only a few seconds");
            progressDialog.setCancelable(false);
            progressDialog.show();

            translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Task<String> result = translator.translate(source_text).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            speak(s);
                           recyclerView(source_text,s);
                           // ex.setText(s);
                            //s contains the translated text
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull  Exception e) {
                            //Toast.makeText(MainMenu.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(MainMenu.this, "Task Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (mtts!=null)
        {
            mtts.stop();
            mtts.shutdown();
        }
        super.onDestroy();
    }

    private void speechRecognition()
    {


            switch (Language_selected_from) {

                case "kannada":
                    //Toast.makeText(this, "it is supposed to be working", Toast.LENGTH_SHORT).show();
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "kn_IN");
                    break;

                case "malay":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ms_MY");
                    break;
                case "portuguese":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt_BR");
                    break;

                case "arabic":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar_SA");
                    break;

                case "tamil":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta_IN");
                    break;

                case "telugu":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "te_IN");
                    break;

                case "marathi":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "mr_IN");
                    break;

                case "hindi":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi_IN");
                    break;

                case "gujrathi":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "gu_IN");
                    break;

                case "japanese":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja_JP");
                    break;

                case "vietnamese":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi_VN");
                    break;

                case "spanish":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es_BR");
                    break;

                case "dutch":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "nl_NL");
                    break;

                case "german":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de_DE");
                    break;

                case "russian":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru_RU");
                    break;

                case "french":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr_FR");
                    break;

                case "chinese":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh_HK");
                    break;

                case "korean":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko_KR");
                    break;

                case "english":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");
                    break;

                case "indonesian":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "in_ID");
                    break;

                case "turkish":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr_TR");
                    break;

                case "greek":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "el_GR");
                    break;
                case "italian":
                    mSpeechRecongnizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "it_IT");
                    break;


                default:
                    Toast.makeText(this, "erroooooooooooor", Toast.LENGTH_SHORT).show();
            }









        mSpeechRecongnizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches!=null) {
                    speech_said=(matches.get(0));
                  //  speech.setText(speech_said);
                    // speech_said contains the text said by the user
                    translation(speech_said);

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

    private void tts()
    {
      //  Locale locale2 =new Locale("hi","IN");

        // char c = '0C80–0CFF';

        //u can set language below for the bot to speak
        //see languages codes in google
        int result= mtts.setLanguage(locale_language_to);

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
        {
            Toast.makeText(MainMenu.this, "Language not supported", Toast.LENGTH_SHORT).show();
            Log.e("TTS","Language not supported");

        }
    }

   private void speak(String s)
    {

        //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        mtts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"uniqueId");
    }

    @SuppressLint("ClickableViewAccessibility")
    public void recyclerView(String lang_from, String lang_to)
    {

        RecyclerView mRecyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        exampleList.add(new ExampleItem(R.drawable.ic_baseline_swap_vert_24,lang_from,lang_to));
        RecyclerView.Adapter mAdapter = new ExampleAdapter(exampleList);
        mRecyclerView.setAdapter(mAdapter);


        im.setVisibility(View.GONE);
        ex.setVisibility(View.INVISIBLE);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // Scrolling up
                    // Hide the BottomAppBar with animation
                    hideBottomAppBar();
                } else {
                    // Scrolling down
                    // Show the BottomAppBar with animation
                    showBottomAppBar();
                }
            }
        });

    }

    private void hideBottomAppBar() {
        if (bottomAppBar.getTranslationY() == 0) {
            // The BottomAppBar is visible, so hide it with animation
            bottomAppBar.animate().translationY(bottomAppBar.getHeight()).setDuration(200);
        }
    }

    private void showBottomAppBar() {
        if (bottomAppBar.getTranslationY() >= bottomAppBar.getHeight()) {
            // The BottomAppBar is hidden, so show it with animation
            bottomAppBar.animate().translationY(0).setDuration(200);
        }
    }


}