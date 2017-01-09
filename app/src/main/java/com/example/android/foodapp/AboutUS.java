package com.example.android.foodapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Akash on 09-11-2016.
 */

public class AboutUS extends AppCompatActivity {

    private TextView h1;
    private TextView h2;
    private TextView h1_text;
    private TextView h2_text;
    private TextView footer;
    private ImageButton play_video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about_us);
        h1=(TextView)findViewById(R.id.what_we_do);
        Typeface myFont=Typeface.createFromAsset(getAssets(),"fonts/FrederickatheGreat-Regular.ttf");
        h1.setTypeface(myFont);
        h1_text=(TextView)findViewById(R.id.what_we_do_text);
        String str_h1_text="Indians waste as much as food as the whole of United Kingdom consumes- a statistic that may not so much be indictive of our love of surfeit, as it is of our population.\n"+
                "Still, food wastage is an alarming issue in India ansd this app is a small and progressive initiative to reduce the food wastage foodprint.";
        h1_text.setText(str_h1_text);
        h2=(TextView)findViewById(R.id.how_it_works);
        h2.setTypeface(myFont);
        h2_text=(TextView)findViewById(R.id.how_it_works_text);
        String str="On one end we will have hotels/wedding functions/parties, which have left over food and on the other end we will have NGOs/companies running initiatives/interested individuals willing to help the needy.\n"+
                "The Providers will be able to broadcast their locations along with the required details(contact no. etc)\n" +
                "and the all the Receivers will get an instant notification and will be able to see the location in real time\n" +
                "and thus can contact the Providers regarding food quantity and other necessary details. \n" +
                "The main aim here is to connect the provider and the receiver as the provider doesn\'t have any clue as to whom should be contacted or approached so that they can prevent the food from ending up in the trash and on the other hand the NGOs or other organizations willing to help, struggle to find appropriate collection locations.\n" +
                "I have used Google Maps Api to display the locations and handle other stuff and Firebase as cloud-hosted NoSQL database.\n"+
                "\n" +
                "        QUICK FACT:- \" Mumbai has 2 lakh kids who go hungry each day :( \"\n" +
                "\n" +
                "        This model can also be extended to provide for other needs and spread awareness.\n" +
                "\n" +
                "        Let us fight hunger and fill everyone's tummy :P\n"+
                "Here is a Youtube video you would like to watch:\n";
        h2_text.setText(str);
        footer=(TextView)findViewById(R.id.akash);
        String set="Developed by Akash";
        footer.setText(set);
        play_video=(ImageButton)findViewById(R.id.play);
        play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=7QbHlWNoyp4")));
                Log.i("Playing video:","successful");
            }
        });
    }
}
