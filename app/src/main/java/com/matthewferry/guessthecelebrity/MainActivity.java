package com.matthewferry.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList <String> celUrls = new ArrayList<>();
    ArrayList <String> celNames = new ArrayList<>();
    int chosenCel = 0;
    ImageView imageView;
    String [] answers = new String[4];
    int correctAnswer = 0;
    int incorrectAnswer;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button restart;
    TextView score;
    int i=0;
    int j=0;


    public void ChosenCeleb(View view){
        if(view.getTag().toString().equals(Integer.toString(correctAnswer))){
            Toast.makeText(getApplicationContext(),"Great job!", 10).show();
            i++;
            j++;
            score.setText(Integer.toString(i) + " / " + Integer.toString(j));
        }
        else {
            Toast.makeText(getApplicationContext(), "Wrong! It was " + celNames.get(chosenCel), 10).show();
            j++;
            score.setText(Integer.toString(i) + " / " + Integer.toString(j));
        }
        restart.setVisibility(View.VISIBLE);
        Question();
    }
    public void Question(){

        try {
            Random rand = new Random();
            chosenCel = rand.nextInt(celUrls.size());
            ImageDownload taskImage = new ImageDownload();
            Bitmap celImage = taskImage.execute(celUrls.get(chosenCel)).get();
            imageView.setImageBitmap(celImage);

            correctAnswer = rand.nextInt(4);

            for (int i = 0; i < 4; i++) {
                if (i == correctAnswer) {
                    answers[i] = celNames.get(chosenCel);
                } else {
                    incorrectAnswer = rand.nextInt(celUrls.size());

                    while (incorrectAnswer == chosenCel)
                        incorrectAnswer = rand.nextInt(celUrls.size());

                    answers[i] = celNames.get(incorrectAnswer);
                }
            }

            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void Restart(View view){

        restart.setVisibility(View.INVISIBLE);
        i = 0;
        j = 0;
        score.setText("");
        Question();

    }

    public class ImageDownload extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try{
                URL url = new URL (urls[0]);
                HttpURLConnection UrlConnection = (HttpURLConnection) url.openConnection();
                UrlConnection.connect();
                InputStream in = UrlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    public class TaskDownloader extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            URL url;
            HttpURLConnection urlConnection = null;
            try{

                url = new URL(urls[0]);
                urlConnection  = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1) {
                    char cur = (char) data;
                    result+= cur;
                    data = reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        restart = findViewById(R.id.button);
        score = findViewById(R.id.score);

        restart.setVisibility(View.INVISIBLE);

        TaskDownloader task = new TaskDownloader();
        String result = null;

        try{
            result = task.execute("http://www.posh24.se/kandisar").get();

            String [] splitR = result.split("<div class=\"listedArticles\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitR[0]);

            while(m.find()){
                celUrls.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitR[0]);

            while(m.find()){
                celNames.add(m.group(1));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        Question();

    }
}
