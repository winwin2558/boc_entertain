package com.sinocham.harry.expandablelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALAnimatedSpeech;
import com.aldebaran.qi.helper.proxies.ALAudioDevice;
import com.aldebaran.qi.helper.proxies.ALBehaviorManager;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.Object;

public class MainActivity extends Activity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ALTextToSpeech alTextToSpeech;
    ALBehaviorManager alBehaviorManager;
    ALAnimatedSpeech alAnimatedSpeech;
    ALAudioDevice alAudioDevice;
    ALMotion alMotion;
    ALRobotPosture alRobotPosture;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public int lang = 2;
    public Session session = new Session();

    public ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button langBtn = (Button) findViewById(R.id.langBtn);
        final Button pauseBtn = (Button) findViewById(R.id.pauseBtn);
        final Button loopBtn = (Button) findViewById(R.id.loopBtn);
        final Button okBtn = (Button) findViewById(R.id.okBtn);
        final EditText iptext = (EditText) findViewById(R.id.iptext);
        final Button softerBtn = (Button) findViewById(R.id.softerBtn);
        final Button louderBtn = (Button) findViewById(R.id.louderBtn);
        final Button sitBtn = (Button) findViewById(R.id.sitBtn);
        final Button standBtn = (Button) findViewById(R.id.standBtn);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        try {
            session.connect("tcp://" + Constant.ROBOT_J + ":9559").sync(1000, TimeUnit.MILLISECONDS);
            alBehaviorManager = new ALBehaviorManager(session);
            alAnimatedSpeech = new ALAnimatedSpeech(session);
            alAudioDevice = new ALAudioDevice(session);
            alRobotPosture = new ALRobotPosture(session);
            alMotion = new ALMotion(session);
            alTextToSpeech = new ALTextToSpeech(session);
            alTextToSpeech.setLanguage("English");
            try {
                alRobotPosture.goToPosture("Stand", 0.5f);
                alTextToSpeech.say("Hello");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        startPollingThread();
        // preparing list data
        sitBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {
                     alMotion.rest();
                 } catch (CallError callError) {
                     callError.printStackTrace();
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }
         });
        standBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    alRobotPosture.goToPosture("Stand",0.5f);
                } catch (CallError callError) {
                    callError.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        prepareListData();
        softerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.isConnected()){
                    try{
                        int vol = alAudioDevice.getOutputVolume();
                        vol-=10f;
                        if(vol<=0){vol=0;}
                        alAudioDevice.setOutputVolume(vol);
                        Toast.makeText(getApplicationContext(),(vol), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        louderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.isConnected()){
                    try{
                        int vol = alAudioDevice.getOutputVolume();
                        vol+=10f;
                        if(vol>=100){vol=100;}
                        alAudioDevice.setOutputVolume(vol);
                        Toast.makeText(getApplicationContext(),(vol), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        loopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.isConnected()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 1; i < 3; i++) {
                                for (int j = 0; j < Application.speech.get(i).get(lang).length; j++) {
                                    final String a;
                                    a = Application.speech.get(i).get(lang)[j];
                                    try {
                                        alMotion.setStiffnesses("Body", 1f);
                                        alAnimatedSpeech.setBodyLanguageMode(2);
                                        alAnimatedSpeech.say(a);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();
                }
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    session.close();
//                    session.onDisconnected("testOnDisconnect");
                    session.connect("tcp://" + iptext.getText() + ":9559").sync(1000, TimeUnit.MILLISECONDS);
                    alBehaviorManager = new ALBehaviorManager(session);
                    alAnimatedSpeech = new ALAnimatedSpeech(session);
                    alMotion = new ALMotion(session);
                    alTextToSpeech = new ALTextToSpeech(session);
                    alTextToSpeech.setLanguage("CantoneseHK");
                    startPollingThread();
                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(
                            getApplicationContext(),
                            "Cannot connect to " + iptext.getText(), Toast.LENGTH_SHORT)
                            .show();
                }
                try {
                    alTextToSpeech.say("Hello");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        langBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           if (session.isConnected()) {
                                               pauseBtn.callOnClick();
                                               lang += 1;
                                               if (lang > 2) {
                                                   lang = 0;
                                               }
                                               switch (lang) {
                                                   case 0:
                                                       langBtn.setText("繁");
                                                       loopBtn.setText("循環播放");
                                                       pauseBtn.setText("暫停");
                                                       upadateListData();
                                                       loading = ProgressDialog.show(MainActivity.this, "", "NAO正在轉語言", true);
                                                       new Thread(new Runnable() {
                                                           @Override
                                                           public void run() {
                                                               try {
                                                                   alTextToSpeech.setLanguage("CantoneseHK");
                                                                   alTextToSpeech.say("廣東話");
                                                                   runOnUiThread(new Runnable() {
                                                                       @Override
                                                                       public void run() {
                                                                           loading.dismiss();
                                                                       }
                                                                   });
                                                               } catch (CallError callError) {
                                                                   callError.printStackTrace();
                                                               } catch (InterruptedException e) {
                                                                   e.printStackTrace();
                                                               }
                                                           }
                                                       }).start();
                                                       break;
                                                   case 1:
                                                       langBtn.setText("簡");
                                                       loopBtn.setText("循环播放");
                                                       pauseBtn.setText("暂停");
                                                       upadateListData();
                                                       loading = ProgressDialog.show(MainActivity.this, "", "NAO正在轉語言", true);
                                                       new Thread(new Runnable() {
                                                           @Override
                                                           public void run() {
                                                               try {
                                                                   alTextToSpeech.setLanguage("Chinese");
                                                                   alTextToSpeech.say("普通話");
                                                                   runOnUiThread(new Runnable() {
                                                                       @Override
                                                                       public void run() {
                                                                           loading.dismiss();
                                                                       }
                                                                   });
                                                               } catch (CallError callError) {
                                                                   callError.printStackTrace();
                                                               } catch (InterruptedException e) {
                                                                   e.printStackTrace();
                                                               }
                                                           }
                                                       }).start();
                                                       break;
                                                   case 2:
                                                       langBtn.setText("ENG");
                                                       loopBtn.setText("Loop");
                                                       pauseBtn.setText("Pause");
                                                       upadateListData();
                                                       loading = ProgressDialog.show(MainActivity.this, "", "NAO is changing language", true);
                                                       new Thread(new Runnable() {
                                                           @Override
                                                           public void run() {
                                                               try {
                                                                   alTextToSpeech.setLanguage("English");
                                                                   alTextToSpeech.say("English");
                                                                   runOnUiThread(new Runnable() {
                                                                       @Override
                                                                       public void run() {
                                                                           loading.dismiss();
                                                                       }
                                                                   });
                                                               } catch (CallError callError) {
                                                                   callError.printStackTrace();
                                                               } catch (InterruptedException e) {
                                                                   e.printStackTrace();
                                                               }
                                                           }
                                                       }).start();
                                                       break;

                                               }

                                           }
                                       }
                                   }

        );
        pauseBtn.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            if (session.isConnected()) {
                                                try {
                                                    alTextToSpeech.stopAll();
                                                    alBehaviorManager.stopAllBehaviors();
                                                } catch (CallError callError) {
                                                    callError.printStackTrace();
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }

        );

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).

                addApi(AppIndex.API).build();

    }

    /*
     * Preparing the list data
     */
    private void upadateListData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDataHeader.clear();
                if (lang == 0) {
                    listDataHeader.add("趣怪動作");

                    listDataHeader.add("理財教育");
                    listDataHeader.add("最新消息");
                } else if (lang == 1) {
                    listDataHeader.add("趣怪动作");
                    listDataHeader.add("理财教育");
                    listDataHeader.add("最新消息");
                } else if (lang == 2) {
                    listDataHeader.add("fun movement");
                    listDataHeader.add("education");
                    listDataHeader.add("news");
                }
                // Adding child data
                List<String> fun = new ArrayList<String>();

                for (int i = 0; i < Constant.jokes.length; i++) {
                    fun.add((i+1)+". "+Application.speech.get(0).get(lang)[i]);
                }
                List<String> edu = new ArrayList<String>();
                for (int i = 0; i < Constant.educations.length; i++) {
                    edu.add("edu" + (i + 1));
                }


                List<String> news = new ArrayList<String>();
                for (int i = 0; i < Constant.commercial.length; i++) {
                    news.add("news" + (i + 1));
                }


                listDataChild.put(listDataHeader.get(0), fun); // Header, Child data
                listDataChild.put(listDataHeader.get(1), edu);
                listDataChild.put(listDataHeader.get(2), news);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listDataChild);
                        // setting list adapter
                        expListView.setAdapter(listAdapter);
                    }
                });

            }
        }).start();
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        upadateListData();


        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (session.isConnected()) {
                    final String a;
                    a = Application.speech.get(groupPosition).get(lang)[childPosition];
                    Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();
                    if (groupPosition != 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    alTextToSpeech.stopAll();
                                    alMotion.setStiffnesses("Body", 1f);
                                    alRobotPosture.goToPosture("Stand", 0.5f);
                                    alAnimatedSpeech.setBodyLanguageMode(2);
                                    alAnimatedSpeech.say(a);
                                    //alMotion.setStiffnesses("Body", 0f);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        try {
                            alTextToSpeech.stopAll();
                            alMotion.setStiffnesses("Body", 1f);
                            alTextToSpeech.say(a);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    alBehaviorManager.stopAllBehaviors();
                                    alTextToSpeech.stopAll();
                                    alBehaviorManager.runBehavior("boc/" + a);
                                    alMotion.setStiffnesses("Body", 0f);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                return false;
            }
        });
    }

    public void startPollingThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!session.isConnected()) {

                        break;
                    }
                }
            }
        }).start();
    }

    public void sayDisconnected() {
        Toast.makeText(
                getApplicationContext(),
                "Cannot connect to NAO", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.sinocham.harry.expandablelist/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.sinocham.harry.expandablelist/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}