package com.tarun.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    int correctAns = 0;
    int incorrectAns;
    String[] options = new String[4];

    ImageView imageView;
    Button button1;
    Button button2;
    Button button3;
    Button button4;

    public void ChosenCeleb(View view){
        if(view.getTag().toString().equals(Integer.toString(correctAns))){
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"InCorrect!",Toast.LENGTH_LONG).show();
        }
        newQuestion();
    }



    public class DownloadImage extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap res;
            URL url;
            HttpURLConnection httpURLConnection;

            try{

                url=new URL(urls[0]);
                httpURLConnection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadWeb extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String res="";
            URL url;
            HttpURLConnection httpURLConnection;

            try{

                url=new URL(urls[0]);
                httpURLConnection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while(data!=-1){
                    char cur = (char) data;
                    res+=cur;
                    data=inputStreamReader.read();
                }
                return res;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadWeb downloadWeb = new DownloadWeb();
        String result;

        try {

            result=downloadWeb.execute("http://www.posh24.se/kandisar").get();

            String[] splitres = result.split("<div class=\"sidebarContainer\">");
            Pattern pattern = Pattern.compile("<img src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(splitres[0]);

            while(matcher.find()){
                celebURLs.add(matcher.group(1));
            }

            pattern = Pattern.compile("alt=\"(.*?)\"");
            matcher = pattern.matcher(splitres[0]);

            while(matcher.find()){
                celebNames.add(matcher.group(1));
            }
            button1 = (Button)findViewById(R.id.button1);
            button2 = (Button)findViewById(R.id.button2);
            button3 = (Button)findViewById(R.id.button3);
            button4 = (Button)findViewById(R.id.button4);


        } catch (Exception e) {
            e.printStackTrace();
        }
        newQuestion();
    }
    public void newQuestion(){
        Random random = new Random();
        chosenCeleb = random.nextInt(celebURLs.size());

        DownloadImage downloadImage = new DownloadImage();
        Bitmap image = null;
        try {
            image = downloadImage.execute(celebURLs.get(chosenCeleb)).get();

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(image);

        correctAns = random.nextInt(4);

        for(int i=0;i<4;i++){
            if(i==correctAns){
                options[i] = celebNames.get(chosenCeleb);
            }
            else{
                incorrectAns = random.nextInt(celebNames.size());
                while(correctAns==incorrectAns){
                    incorrectAns = random.nextInt(celebNames.size());
                }
                options[i] = celebNames.get(incorrectAns);
            }
        }
        button1.setText(options[0]);
        button2.setText(options[1]);
        button3.setText(options[2]);
        button4.setText(options[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
