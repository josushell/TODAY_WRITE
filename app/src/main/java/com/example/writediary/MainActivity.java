package com.example.writediary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        onTabItemSelectedListener, onRequestListener, AutoPermissionsListener{
    list_fragment list_frag;
    write_fragment write_frag;
    statistics_fragment statistic_frag;

    BottomNavigationView bottomNavi;

    Location currentLoc;
    GPSListener gpsListener;

    // get weather 변수
    Date currentDate;
    String currentDateString;
    int locationCount=0;

    // getaddress 변수
    String currentAddress;

    // 데이터 베이스
    public static NoteDatabase mDatabase = null;

    // 아이템 클릭
    boolean writetab=true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDatabase!=null){
            mDatabase.close();
            mDatabase=null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_frag=new list_fragment();
        write_frag=new write_fragment();
        statistic_frag=new statistics_fragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,list_frag).commit();


        bottomNavi=findViewById(R.id.bottom_navi);
        bottomNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.tab1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,list_frag).commit();
                        return true;
                    case R.id.tab2:
                        writetab=false;
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,write_frag).commit();
                        return true;
                    case R.id.tab3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,statistic_frag).commit();
                        return true;
                }
                return false;
            }
        });
        AutoPermissions.Companion.loadAllPermissions(this, 1);
        setPicturePath();
        openDatabase();

    }
    public void setPicturePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        AppConstants.FOLDER_PHOTO = sdcardPath + File.separator + "photo";
    }
    public void openDatabase(){
        if(mDatabase!=null){
            mDatabase.close();
            mDatabase=null;
        }
        mDatabase=NoteDatabase.getInstance(this);
        boolean isOpen=mDatabase.open();
        if(isOpen){
            System.out.println("db is opened");
        }
        else{
            System.out.println("db is not opened");
        }
    }
    public void onTabSelected(int position){
        if(position==0){
            bottomNavi.setSelectedItemId(R.id.tab1);
        }else if(position==1){
            writetab=true;
            write_frag=new write_fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,write_frag).commit();
        }
        else{
            bottomNavi.setSelectedItemId(R.id.tab3);
        }
    }
    @Override
    public void showWritable(Note item){
        write_frag=new write_fragment();
        write_frag.setItem(item);

        //bottomNavi.setSelectedItemId(R.id.tab2);
        writetab=false;
        getSupportFragmentManager().beginTransaction().replace(R.id.container,write_frag).commit();
    }
    public void onRequest(String command){
        if(command!=null){
            if(command.equals("getCurrentLocation")&&writetab==true){
                writetab=true;
                System.out.println("onRequest() 에서 getCurrentLocation 호출됨 and writetab: "+writetab);
                getCurrentLocation();
            }
        }
    }
    public void getCurrentLocation(){
        currentDate=new Date();
        currentDateString=AppConstants.dateFormat3.format(currentDate);
        if(write_frag!=null){
            write_frag.setDateString(currentDateString);
        }

        LocationManager manager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try{
            currentLoc=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLoc!=null){
                double latitude=currentLoc.getLatitude();
                double longitude=currentLoc.getLongitude();

                String message="latitude: "+latitude+", longitude: "+longitude+"\n";
                System.out.println("위치: "+message);

                // 현재 위치로 날씨 확인
                //getCurrentWeather();
                // 현재 위치로 주소 확인
                //getCurrentAddress();
            }
            gpsListener=new GPSListener();
            long mintime=10000;
            int mindistance=0;
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,mintime,mindistance,gpsListener);
        } catch (SecurityException e){
            e.printStackTrace();
            System.out.println("getCurrentLocation()에서 SecurityException 발생");
        }
    }
    public void stopLocationService(){
        LocationManager manager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
            manager.removeUpdates(gpsListener);
            System.out.println("stopLocationService() called");
        } catch(SecurityException e){
            e.printStackTrace();
        }
    }
    public void getCurrentAddress(){
        Geocoder geocoder=new Geocoder(this, Locale.getDefault());
        List<Address> addresses=null;
        try{
            addresses=geocoder.getFromLocation(currentLoc.getLatitude(),currentLoc.getLongitude(),1);
        } catch (IOException E){
            E.printStackTrace();
        }
        if(addresses!=null&&addresses.size()>0){
            Address address=addresses.get(0);
            currentAddress=address.getLocality()+" "+address.getSubLocality();
            if(write_frag!=null){
                write_frag.setAddress(currentAddress);
            }
        }
    }
    public void getCurrentWeather(){
        Map<String, Double> gridMap=GridUtil.getGrid(currentLoc.getLatitude(),currentLoc.getLongitude());
        double gridX=gridMap.get("x");
        double gridY=gridMap.get("y");
        System.out.println("getCurrentWeather() called -> ("+gridX+", "+gridY+")\n");

        xmlparsing(gridX,gridY);
    }
    public void xmlparsing(double x, double y){
        String url="http://www.kma.go.kr/wid/queryDFS.jsp";
        url+="?gridx="+Math.round(x);
        url+="&gridy="+Math.round(y);

        try{
            URL myurl=new URL(url);

            WeatherThread task=new WeatherThread();
            task.execute(myurl);
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
    }
    class GPSListener implements LocationListener{
        @Override
        public void onLocationChanged(@NonNull Location location) {
            currentLoc=location;
            locationCount++;

            double latitude=currentLoc.getLatitude();
            double longitude=currentLoc.getLongitude();

            String message="latitude: "+latitude+", longitude: "+longitude+"\n";
            System.out.println("위치: "+message);

            getCurrentWeather();
            getCurrentAddress();

            stopLocationService();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }
    class WeatherThread extends AsyncTask<URL, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("parsing 완료: "+s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL myUrl=urls[0];
            try{
                InputStream IS= myUrl.openStream();
                XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                XmlPullParser parser= factory.newPullParser();

                parser.setInput(IS,"UTF8");
                int eventType= parser.getEventType();

                while(eventType!=XmlPullParser.END_DOCUMENT){
                    if(eventType==XmlPullParser.START_TAG){
                        String tagname=parser.getName();
                        if(tagname.equals("tm")){
                            parser.next();
                            try{
                                Date time=AppConstants.dateFormat.parse(parser.getText());
                                String message=AppConstants.dateFormat2.format(time);
                                System.out.println("기준 시간: "+message);
                            } catch (ParseException e){
                                System.out.println("doInBackground() 에서 오류 발생 ");
                                e.printStackTrace();
                            }
                        }
                        else if(tagname.equals("wfKor")){
                            if(write_frag!=null){
                                parser.next();
                                write_frag.setWeather(parser.getText());
                                break;
                            }
                        }
                    }
                    eventType=parser.next();
                }
                IS.close();
            } catch (IOException E){
                System.out.println("날씨에서 IOException 발생");
                E.printStackTrace();
            } catch (XmlPullParserException e){
                System.out.println("날씨에서 XmlPullParserException 발생");
                e.printStackTrace();
            }
            return " doInBackground() 호출 완료";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, String[] strings) {

    }

    @Override
    public void onGranted(int i, String[] strings) {
        System.out.println("onGranted() 호출됨: 권한 부여됨");

    }
}