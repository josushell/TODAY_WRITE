package com.example.writediary;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>
        implements onNoteItemClickListener{
    ArrayList<Note> items=new ArrayList<Note>();
    onNoteItemClickListener listener;
    int layoutType=0;
    Context context;

    public NoteAdapter(ArrayList<Note> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        ViewGroup view=(ViewGroup) inflater.inflate(R.layout.note_item,parent,false);
        return new ViewHolder(view,this,layoutType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note item=items.get(position);
        holder.setItem(item);
        holder.setLayoutType(layoutType);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem(Note item){
        items.add(item);
    }
    public void setItems(ArrayList<Note> items){
        this.items=items;
    }
    public Note getItem(int position){
        return items.get(position);
    }
    public void setOnItemClickListener(onNoteItemClickListener listener){
        this.listener=listener;
    }
    @Override
    public void onNoteClick(ViewHolder holder, View view, int position) {
        if(listener!=null){
            listener.onNoteClick(holder,view,position);
        }
    }
    public void switchLayout(int type){
        layoutType=type;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout1;
        LinearLayout layout2;

        ImageView moodImg1;
        ImageView moodImg2;

        ImageView userImg1;
        ImageView userImg2;

        ImageView weatherImg1;
        ImageView weatherImg2;

        TextView title1;
        TextView title2;

        TextView location1;
        TextView location2;

        TextView date1;
        TextView date2;

        public ViewHolder(@NonNull View itemView, final onNoteItemClickListener listener, int type) {
            super(itemView);

            layout1=itemView.findViewById(R.id.layout1);
            layout2=itemView.findViewById(R.id.layout2);

            moodImg1=itemView.findViewById(R.id.mood_img);
            moodImg2=itemView.findViewById(R.id.mood_img2);

            userImg1=itemView.findViewById(R.id.user_img);
            userImg2=itemView.findViewById(R.id.user_img2);

            weatherImg1=itemView.findViewById(R.id.weather_img);
            weatherImg2=itemView.findViewById(R.id.weather_img2);

            title1=itemView.findViewById(R.id.content);
            title2=itemView.findViewById(R.id.content2);

            location1=itemView.findViewById(R.id.location);
            location2=itemView.findViewById(R.id.location2);

            date1=itemView.findViewById(R.id.date);
            date2=itemView.findViewById(R.id.date2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    if(listener!=null){
                        listener.onNoteClick(ViewHolder.this,view,position);
                    }
                }
            });
            setLayoutType(type);
        }

        public void setItem(Note item){
            String mood=item.getMood();
            int moodIndex=Integer.parseInt(mood);
            setMoodImg(moodIndex);

            String picPath=item.getPicture();
            if(picPath!=null&&!picPath.equals("")){
                userImg1.setVisibility(View.VISIBLE);
                userImg2.setVisibility(View.VISIBLE);
                userImg2.setImageURI(Uri.parse("file://"+picPath));
            }
            else{
                userImg1.setVisibility(View.GONE);
                userImg2.setVisibility(View.GONE);
                userImg2.setImageResource(R.drawable.noimagefound);
            }

            String weather=item.getWeather();
            int weatherIdx=Integer.parseInt(weather);
            setWeatherImg(weatherIdx);

            title1.setText(item.getTitle());
            title2.setText(item.getTitle());

            location1.setText(item.getAddress());
            location2.setText(item.getAddress());

            date1.setText(item.getCreateDateStr());
            date2.setText(item.getCreateDateStr());

        }

        public void setMoodImg(int idx){
            switch (idx){
                case 0:
                    moodImg1.setImageResource(R.drawable.smile1_48);
                    moodImg2.setImageResource(R.drawable.smile1_48);
                    break;
                case 1:
                    moodImg1.setImageResource(R.drawable.smile2_48);
                    moodImg2.setImageResource(R.drawable.smile2_48);
                    break;
                case 2:
                    moodImg1.setImageResource(R.drawable.smile3_48);
                    moodImg2.setImageResource(R.drawable.smile3_48);
                    break;
                case 3:
                    moodImg1.setImageResource(R.drawable.smile4_48);
                    moodImg2.setImageResource(R.drawable.smile4_48);
                    break;
                case 4:
                    moodImg1.setImageResource(R.drawable.smile5_48);
                    moodImg2.setImageResource(R.drawable.smile5_48);
                    break;
                default:
                    moodImg1.setImageResource(R.drawable.smile3_48);
                    moodImg2.setImageResource(R.drawable.smile3_48);
                    break;
            }

        }
        public void setWeatherImg(int idx){
            switch (idx){
                case 0:
                    weatherImg1.setImageResource(R.drawable.weather_icon_1);
                    weatherImg2.setImageResource(R.drawable.weather_icon_1);
                    break;
                case 1:
                    weatherImg1.setImageResource(R.drawable.weather_icon_2);
                    weatherImg2.setImageResource(R.drawable.weather_icon_2);
                    break;
                case 2:
                    weatherImg1.setImageResource(R.drawable.weather_icon_3);
                    weatherImg2.setImageResource(R.drawable.weather_icon_3);
                    break;
                case 3:
                    weatherImg1.setImageResource(R.drawable.weather_icon_4);
                    weatherImg2.setImageResource(R.drawable.weather_icon_4);
                    break;
                case 4:
                    weatherImg1.setImageResource(R.drawable.weather_icon_5);
                    weatherImg2.setImageResource(R.drawable.weather_icon_5);
                    break;
                case 5:
                    weatherImg1.setImageResource(R.drawable.weather_icon_6);
                    weatherImg2.setImageResource(R.drawable.weather_icon_6);
                    break;
                case 6:
                    weatherImg1.setImageResource(R.drawable.weather_icon_7);
                    weatherImg2.setImageResource(R.drawable.weather_icon_7);
                    break;
                default:
                    weatherImg1.setImageResource(R.drawable.weather_icon_1);
                    weatherImg2.setImageResource(R.drawable.weather_icon_1);
                    break;
            }
        }
        public void setLayoutType(int type){
            if(type==0){
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
            }
            else if(type==1){
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
            }

        }
    }
}
