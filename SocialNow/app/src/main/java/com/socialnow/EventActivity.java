package com.socialnow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class EventActivity extends AppCompatActivity {
    ListView listView;

    String title;
    Date date;
    Bitmap photo;
    String location;
    String hostName;
    String event_title;

    //Dummy Comment List
    int [] ivParti={R.drawable.host,R.drawable.profilpic,R.drawable.profilpic,R.drawable.profilpic,R.drawable.profilpic,R.drawable.profilpic,R.drawable.profilpic};
    String[] tvParti={"User 1","User 2","User 3","User 4","User 5","User 6","User 7"};

    String[] tvComment={"Let me comment on this event.",
            "Let me comment on this event.", "Let me comment on this event.",
            "Let me comment on this event.","Let me comment on this event.",
            "Let me comment on this event.","Let me comment on this event."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("Title");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO make it to be a floating action menu for users to share event, make comments and so on......
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.Parti);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewParti = new Intent(getApplicationContext(), PartiActivity.class);
                startActivity(viewParti);
            }
        });

        listView=(ListView)findViewById(R.id.lvComment);
        ListAdapter mAdapter = new MyAdapter(this,R.layout.item_comment,tvParti);
        listView.setAdapter(mAdapter);


        Bundle extras = getIntent().getExtras();
            if(extras == null) {
                event_title= null;
            } else {
                event_title= extras.getString("event_title");
            }

        getData();

        TextView eventname = (TextView) findViewById(R.id.tEname);
        eventname.setText(title);
        ImageView img = (ImageView) findViewById(R.id.ivEvent);
        img.setImageBitmap(photo);
        TextView eventdate = (TextView) findViewById(R.id.tEdate);
        eventdate.setText(date.toString());
        TextView eventlocation = (TextView) findViewById(R.id.tElocation);
        eventlocation.setText(location);
        TextView eventhost = (TextView) findViewById(R.id.tHostName);
        eventhost.setText(hostName);


    }
    class MyAdapter extends ArrayAdapter<String> {
        public MyAdapter(Context context, int resource, String[] tvParti) {
            super(context, R.layout.item_comment, tvParti);
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent){
            LayoutInflater mInflater = LayoutInflater.from(getContext());
            View customView = mInflater.inflate(R.layout.item_comment, parent, false);

            TextView mText = (TextView) customView.findViewById(R.id.tvParti);
            ImageView mImg = (ImageView) customView.findViewById(R.id.ivAuthor);
            TextView mComment = (TextView) customView.findViewById(R.id.tvComment);
            String item = getItem(position);
            mText.setText(item);
            mImg.setImageResource(ivParti[position]);
            mComment.setText(tvComment[position]);
            return customView;

        }

    }

    void getData() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("title", event_title);
        query.findInBackground(new FindCallback() {
                                   @Override
                                   public void done(List objects, com.parse.ParseException e) {
                                   }

                                   @Override
                                   public void done(Object o, Throwable throwable) {
                                       List<ParseObject> myObject = (List<ParseObject>) o;

                                       if (throwable == null) {
                                           int count = 0;
                                           ParseFile fileObject;
                                           byte[] data;

                                           title = myObject.get(0).getString("title");
                                           date = myObject.get(0).getDate("event_date");
                                           location = myObject.get(0).getString("event_location");
                                           hostName = myObject.get(0).getParseObject("event_host").getString("Name") + " " + myObject.get(0).getParseObject("event_host").getString("Surname");
                                           fileObject = (ParseFile) myObject.get(0).getParseFile("event_photo");
                                           if (fileObject != null) {
                                               try {
                                                   data = fileObject.getData();
                                                   Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                   photo = bMap;

                                                  //writeToList();

                                               } catch (com.parse.ParseException e1) {
                                                   e1.printStackTrace();
                                               }
                                           }

                                       } else {
                                           Log.d("post", "error retriving posts");
                                       }
                                   }
                               }
        );
    }

 /* void writeToList(){
      listView.setAdapter(new MyAdapter(getActivity(), R.layout.item_event, title));
    }*/
}