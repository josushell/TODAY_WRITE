package com.example.writediary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.channguyen.rsv.RangeSliderView;
import com.google.android.material.slider.RangeSlider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class write_fragment extends Fragment {

    Context context;
    onTabItemSelectedListener listener;
    onRequestListener requestListener;

    // 뷰 아이템 id
    ImageView weatherIcon;
    ImageView imagetoset;
    int weatherIndex=0;
    int moodIndex=2;
    TextView locationTextView;
    TextView datetext;
    EditText contentsInput;
    EditText titleInput;
    Note item;
    RangeSliderView moodSlider;

    // 사진 찍기 기능
    boolean isphotoCaptured;
    boolean isPhotoCanceled;
    boolean isphotoFileSaved;
    int selectedPhotoMenu;

    // 사진 파일
    File file;
    Bitmap resultPhotoBitmap;

    // 밑의 버튼 3개
    int mMode=AppConstants.MODE_INSERT;

    public void setTitle(String s){
        titleInput.setText(s);
    }
    public void setContent(String s){ contentsInput.setText(s);}
    public void setDateString(String s){
        datetext.setText(s);
    }
    public void setAddress(String s){
        locationTextView.setText(s);
    }
    public void setContents(String data) {
        contentsInput.setText(data);
    }
    public void setWeather(String data){
        if (data != null) {
            if (data.equals("맑음")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_1);
                weatherIndex = 0;
            } else if (data.equals("구름 조금")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_2);
                weatherIndex = 1;
            } else if (data.equals("구름 많음")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_3);
                weatherIndex = 2;
            } else if (data.equals("흐림")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_4);
                weatherIndex = 3;
            } else if (data.equals("비")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_5);
                weatherIndex = 4;
            } else if (data.equals("눈/비")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_6);
                weatherIndex = 5;
            } else if (data.equals("눈")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_7);
                weatherIndex = 6;
            } else {
                System.out.println("setWeather() 오류 : " + data);
            }
        }

    }
    public void setWeatherIndex(int index) {
        if (index == 0) {
            weatherIcon.setImageResource(R.drawable.weather_icon_1);
            weatherIndex = 0;
        } else if (index == 1) {
            weatherIcon.setImageResource(R.drawable.weather_icon_2);
            weatherIndex = 1;
        } else if (index == 2) {
            weatherIcon.setImageResource(R.drawable.weather_icon_3);
            weatherIndex = 2;
        } else if (index == 3) {
            weatherIcon.setImageResource(R.drawable.weather_icon_4);
            weatherIndex = 3;
        } else if (index == 4) {
            weatherIcon.setImageResource(R.drawable.weather_icon_5);
            weatherIndex = 4;
        } else if (index == 5) {
            weatherIcon.setImageResource(R.drawable.weather_icon_6);
            weatherIndex = 5;
        } else if (index == 6) {
            weatherIcon.setImageResource(R.drawable.weather_icon_7);
            weatherIndex = 6;
        } else {
            System.out.println("setWeatherIndex() 오류 : " + index);
        }

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
        if(context instanceof onTabItemSelectedListener){
            this.listener=(onTabItemSelectedListener) context;
        }
        if(context instanceof onRequestListener){
            this.requestListener=(onRequestListener) context;
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

        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.write_fragment,container,false);
        initUI(rootview);
        if(requestListener!=null){
            requestListener.onRequest("getCurrentLocation");
        }
        applyItem();
        return rootview;
    }

    public void applyItem() {

        if (item != null) {
            mMode = AppConstants.MODE_MODIFY;

            setWeatherIndex(Integer.parseInt(item.getWeather()));
            setAddress(item.getAddress());
            setDateString(item.getCreateDateStr());
            setTitle(item.getTitle());
            setContents(item.getContents());

            String picturePath = item.getPicture();
            if (picturePath == null || picturePath.equals("")) {
                imagetoset.setImageResource(R.drawable.noimagefound);
            } else {
                setPicture(item.getPicture(), 1);
            }

            setMood(item.getMood());
        } else {
            mMode = AppConstants.MODE_INSERT;

            setWeatherIndex(0);
            setAddress("");

            Date currentDate = new Date();
            String currentDateString = AppConstants.dateFormat6.format(currentDate);
            setDateString(currentDateString);

            contentsInput.setText("");
            imagetoset.setImageResource(R.drawable.noimagefound);
            setMood("2");
        }

    }
    public void setPicture(String picturePath, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        resultPhotoBitmap = BitmapFactory.decodeFile(picturePath, options);

        imagetoset.setImageBitmap(resultPhotoBitmap);
    }

    public void setMood(String mood) {
        try {
            moodIndex = Integer.parseInt(mood);
            moodSlider.setInitialIndex(moodIndex);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void showDialog(int command){
        AlertDialog.Builder builder=null;
        switch (command){
            case AppConstants.CONTENT_PHOTO:
                builder=new AlertDialog.Builder(context);
                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo,0, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialogs, int whichButton){
                        selectedPhotoMenu=whichButton;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(selectedPhotoMenu==0){
                            showPhotoCaptureActivity();
                        }
                        else if(selectedPhotoMenu==1){
                            showPhotoSelectionActivity();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      // 취소 시 동작 안함
                    }
                });
                break;

            case AppConstants.CONTENT_PHOTO_EX:
                builder=new AlertDialog.Builder(context);
                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo_EX, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedPhotoMenu=i;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(selectedPhotoMenu==0){
                            showPhotoCaptureActivity();
                        }
                        else if(selectedPhotoMenu==1){
                            showPhotoSelectionActivity();
                        }
                        else if(selectedPhotoMenu==2){
                            isPhotoCanceled=true;
                            isphotoCaptured=false;

                            imagetoset.setImageResource(R.drawable.picture1);
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                });
                break;

            default:
                break;
        }
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public void showPhotoCaptureActivity(){
        if(file==null){
            file=createFile();
        }
        Uri fileUri= FileProvider.getUriForFile(context,"com.example.writediary.fileprovider",file);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        if(intent.resolveActivity(context.getPackageManager())!=null){
            startActivityForResult(intent,AppConstants.REQ_PHOTO_CAPTURE);
        }
    }
    public void showPhotoSelectionActivity(){
        Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,AppConstants.REQ_PHOTO_SELECTION);
    }
    private File createFile(){
        String filename="capture.jpg";
        File storageDR= Environment.getExternalStorageDirectory();
        File outFile=new File(storageDR,filename);
        return outFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(intent!=null){
            switch (requestCode){
                case AppConstants.REQ_PHOTO_CAPTURE:

                    resultPhotoBitmap=decodeSampledBitmapFromResource(file,imagetoset.getWidth(),
                            imagetoset.getHeight());
                    imagetoset.setImageBitmap(resultPhotoBitmap);

                    break;

                case AppConstants.REQ_PHOTO_SELECTION:

                    Uri selectedImg=intent.getData();
                    String[] filepathColumn={MediaStore.Images.Media.DATA};

                    Cursor cursor=context.getContentResolver().query(selectedImg,filepathColumn,null,null,null);
                    cursor.moveToFirst();

                    int columnindex=cursor.getColumnIndex(filepathColumn[0]);
                    String filepath= cursor.getString(columnindex);
                    cursor.close();

                    resultPhotoBitmap=decodeSampledBitmapFromResource(new File(filepath),
                            imagetoset.getWidth(), imagetoset.getHeight());
                    imagetoset.setImageBitmap(resultPhotoBitmap);
                    isphotoCaptured=true;

                    break;
            }
        }
    }
    public static Bitmap decodeSampledBitmapFromResource(File res, int width, int height){
        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(res.getAbsolutePath(),options);

        options.inSampleSize=calculateInSamplesize(options,width,height);
        options.inJustDecodeBounds=false;

        return BitmapFactory.decodeFile(res.getAbsolutePath(),options);
    }
    public static int calculateInSamplesize(BitmapFactory.Options options,int width, int height){
        final int _height= options.outHeight;
        final int _width= options.outWidth;
        int sampleSize=1;

        if(_height>height||_width>width){
            final int halfH=height;
            final int halfW=width;

            while((halfH/sampleSize)>=height&&(halfW/sampleSize)>=width){
                sampleSize*=2;
            }
        }
        return sampleSize;
    }
    private String createFilename(){
        Date curDate=new Date();
        String curDateStr=String.valueOf(curDate.getTime());
        return curDateStr;
    }

    private void initUI(ViewGroup view){
        datetext=view.findViewById(R.id.dateView);
        weatherIcon=view.findViewById(R.id.weather_icon);
        locationTextView=view.findViewById(R.id.locationView);
        contentsInput=view.findViewById(R.id.edit_content);
        titleInput=view.findViewById(R.id.edit_title);

        imagetoset=view.findViewById(R.id.imageToSet);
        imagetoset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isphotoCaptured||isphotoFileSaved){
                    showDialog(AppConstants.CONTENT_PHOTO_EX);
                }
                else{
                    showDialog(AppConstants.CONTENT_PHOTO);
                }
            }
        });

        moodSlider=view.findViewById(R.id.sliderVew);
        Button saveButton=view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMode==AppConstants.MODE_INSERT){
                    saveNote();
                }
                else if(mMode==AppConstants.MODE_MODIFY){
                    ModifyNote();
                }
                if(listener!=null){
                    listener.onTabSelected(0);
                }
            }
        });

        Button deleteButton=view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNote();

                if(listener!=null){
                    listener.onTabSelected(0);
                }
            }
        });

        Button closeButton=view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onTabSelected(0);
                }
            }
        });

        RangeSliderView sliderView=view.findViewById(R.id.sliderVew);
        final RangeSliderView.OnSlideListener listener=new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                moodIndex=index;
            }
        };
        sliderView.setOnSlideListener(listener);
        sliderView.setInitialIndex(2);

    }
    private void saveNote(){
        String address=locationTextView.getText().toString();
        String contents=contentsInput.getText().toString();
        String title=titleInput.getText().toString();

        String picPath=savePicture();

        String sql="insert into "+NoteDatabase.TABLE_NOTE+"(WEATHER, ADDRESS, LOCATION_X, LOCATION_Y, CONTENTS, TITLE, MOOD, PICTURE) values("+
                "'"+weatherIndex+"', "+
                "'"+address+"', "+
                "'"+""+"', "+
                "'"+""+"', "+
                "'"+contents+"', "+
                "'"+title+"', "+
                "'"+moodIndex+"', "+
                "'"+picPath+"')";

        NoteDatabase database=NoteDatabase.getInstance(context);
        database.execSQL(sql);
    }
    private void ModifyNote(){
        if(item!=null){
            String address=locationTextView.getText().toString();
            String contents=contentsInput.getText().toString();
            String title=titleInput.getText().toString();

            String picPath=savePicture();

            String sql = "update " + NoteDatabase.TABLE_NOTE +
                    " set " +
                    "   WEATHER = '" + weatherIndex + "'" +
                    "   ,ADDRESS = '" + address + "'" +
                    "   ,LOCATION_X = '" + "" + "'" +
                    "   ,LOCATION_Y = '" + "" + "'" +
                    "   ,CONTENTS = '" + contents + "'" +
                    "   ,TITLE = '" + title + "'" +
                    "   ,MOOD = '" + moodIndex + "'" +
                    "   ,PICTURE = '" + picPath + "'" +
                    " where " +
                    "   _id = " + item.id;

            NoteDatabase database=NoteDatabase.getInstance(context);
            database.execSQL(sql);
        }
    }
    private void deleteNote(){
        if(item!=null){
            String sql="delete from "+NoteDatabase.TABLE_NOTE+" where _id = "+item.id;

            NoteDatabase database=NoteDatabase.getInstance(context);
            database.execSQL(sql);
        }
    }
    private String savePicture(){
        if(resultPhotoBitmap==null){
            return "";
        }
        File photoFolder=new File(AppConstants.FOLDER_PHOTO);
        if(!photoFolder.isDirectory()){
            photoFolder.mkdirs();
        }
        String photoFilename=createFilename();
        String picPath=photoFolder+File.separator+photoFilename;

        try{
            FileOutputStream outstream=new FileOutputStream(picPath);
            resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG,100,outstream);
            outstream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return picPath;
    }
    public void setItem(Note _item){
        this.item=_item;
    }
}
