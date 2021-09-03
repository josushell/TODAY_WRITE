package com.example.writediary;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import lib.kingja.switchbutton.SwitchMultiButton;

public class list_fragment extends Fragment {
    RecyclerView recyclerView;
    NoteAdapter adapter;

    Context context;
    onTabItemSelectedListener listener;
    ArrayList<Note> items=new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;

        if(context instanceof onTabItemSelectedListener){
            listener=(onTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(context!=null){
            context=null;
            listener=null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.list_fragment,container,false);
        initUI(rootview);
        loadNoteListData();
        return rootview;
    }
    public int loadNoteListData(){
        String sql="select _id, WEATHER, ADDRESS, LOCATION_X, LOCATION_Y, CONTENTS, TITLE, MOOD, " +
                "PICTURE, CREATE_DATE, MODIFY_DATE from "+NoteDatabase.TABLE_NOTE+
                " order by CREATE_DATE desc";

        NoteDatabase DB=NoteDatabase.getInstance(context);
        int recordCount=-1;
        if(DB!=null){
            Cursor outcursor=DB.rawQuery(sql);

            ArrayList<Note> items=new ArrayList<>();
            for(int i=0;i<outcursor.getCount();i++){
                outcursor.moveToNext();
                int _id=outcursor.getInt(0);
                String _weather= outcursor.getString(1);
                String _address= outcursor.getString(2);
                String _locationx= outcursor.getString(3);
                String _locationy= outcursor.getString(4);
                String _contents= outcursor.getString(5);
                String _title= outcursor.getString(6);
                String _mood= outcursor.getString(7);
                String _picture= outcursor.getString(8);
                String _create_date= outcursor.getString(9);
                String createStr=null;

                if(_create_date!=null&&_create_date.length()>10){
                    try{
                        Date indate=AppConstants.dateFormat4.parse(_create_date);
                        createStr=AppConstants.dateFormat3.format(indate);
                    }catch (ParseException E){
                        E.printStackTrace();
                    }
                }
                else{
                    createStr="";
                }
                items.add(new Note(_id, _weather, _address, _locationx,_locationy,_contents,_title,_mood,_picture,createStr));

            }
            recordCount= outcursor.getCount();
            outcursor.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
        return recordCount;
    }
    private void initUI(ViewGroup view) {
        recyclerView = view.findViewById(R.id.recycler);
        adapter = new NoteAdapter(items,getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);

        Button writebutton = view.findViewById(R.id.writeButton);
        writebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onTabSelected(1);
                }
            }
        });

        SwitchMultiButton switchbutton = view.findViewById(R.id.switchButton);
        switchbutton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                adapter.switchLayout(position);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setOnItemClickListener(new onNoteItemClickListener(){
            @Override
            public void onNoteClick(NoteAdapter.ViewHolder holder, View view, int position) {
                Note item= adapter.getItem(position);
                if(listener!=null){
                    listener.showWritable(item);
                }
            }
        });
    }
}
