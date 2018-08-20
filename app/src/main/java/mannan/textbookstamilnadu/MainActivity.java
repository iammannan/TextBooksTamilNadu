package mannan.textbookstamilnadu;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.euzee.permission.PermissionUtil;
import com.github.euzee.permission.ShortPerCallback;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    String requestUrl = "http://35.200.173.188/~ttapp/tnscert/";
    Integer[] hIdSub,hidTBkId ;
    String[] stdList = {"Tamil Medium","English Medium","Kannada Medium","Urdu Medium","Malayala Medium","Arabic Medium",
                        "Sanskrit Medium","Hindi Medium","Telugu Medium","Gujarati Medium"};
    String[] stdList2 = {"Standard I","Standard VI","Standard IX","Standard XI"};
    String[]  txtTitle,pdffname,size,taDesp;

    NiceSpinner langSelect, stdSelect, subSelect;
    List<String> dataset3;

    ListView availList;

    ProgressBar progressBar;

    static final String KEY_BOOKNAME = "bookName";
    static final String KEY_BOOKLINK = "bookLink";
    static final String KEY_BOOKSIZE = "bookSize";

    ArrayList<HashMap<String, String>> bookList;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hIdSub = new Integer[]{01,02};
        hidTBkId = new Integer[]{001, 002};

        //Dummy to avoid null
        txtTitle = new String[]{"Server Error", "Server Error"};

        langSelect = (NiceSpinner) findViewById(R.id.Main_langSelect);
        stdSelect = (NiceSpinner) findViewById(R.id.Main_stdSelect);
        subSelect = (NiceSpinner) findViewById(R.id.Main_subSelect);

        availList = findViewById(R.id.availist);
        progressBar=findViewById(R.id.progressLoading);
        showGuide();
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();

        //Lang and Std Assign
        List<String> dataset = new LinkedList<>(Arrays.asList(stdList));
        langSelect.attachDataSource(dataset);
        List<String> dataset2 = new LinkedList<>(Arrays.asList(stdList2));
        stdSelect.attachDataSource(dataset2);


        langSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bookList = new ArrayList<HashMap<String, String>>();
                bookList.clear();
                adapter=new LazyAdapter(MainActivity.this, bookList);
                availList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                availList.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        stdSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", "onItemSelected: "+i);
                bookList = new ArrayList<HashMap<String, String>>();
                bookList.clear();
                availList.setVisibility(View.INVISIBLE);
                adapter=new LazyAdapter(MainActivity.this, bookList);
                availList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                switch (i){
                    case 0:
                        fetchSubject(1);
//                        Log.d( "onItemSelected: ","01 Exe");
                        break;
                    case 1:
                        fetchSubject(6);
//                        Log.d( "onItemSelected: ","06 Exe");
                        break;
                    case 2:
                        fetchSubject(9);
//                        Log.d( "onItemSelected: ","09 Exe");
                        break;
                    case 3:
                        fetchSubject(11);
//                        Log.d( "onItemSelected: ","011 Exe");
                        break;
                    default:
                        Log.d("onItemSelected: ","DefaultCaseStatement");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        subSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                bookList = new ArrayList<HashMap<String, String>>();
                bookList.clear();
                availList.setVisibility(View.INVISIBLE);
                adapter=new LazyAdapter(MainActivity.this, bookList);
                availList.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                switch (stdSelect.getSelectedIndex()) {
                    case 0:
                        fetchBookLink(
                                langSelect.getSelectedIndex()+1,
                                1,
                                hIdSub[i]
                        );
                        break;
                    case 1:
                        fetchBookLink(
                                langSelect.getSelectedIndex()+1,
                                6,
                                hIdSub[i]
                        );
                        break;
                    case 2:
                        fetchBookLink(
                                langSelect.getSelectedIndex()+1,
                                9,
                                hIdSub[i]
                        );
                        break;
                    case 3:
                        fetchBookLink(
                                langSelect.getSelectedIndex()+1,
                                11,
                                hIdSub[i]
                        );
                        break;
                    default:
                        Log.d("onItemSelected: ", "DefaultCaseStatement");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        callPermission();
    }

    private void callPermission() {
        PermissionUtil.storageRW(this, new ShortPerCallback() {
            @Override
            public void onPermissionGranted() {}

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });
    }
    public void displaypdf(String pdfLink,Integer pos) {

        String saveAs = langSelect.getSelectedIndex()+"_"+stdSelect.getSelectedIndex()+"_"+subSelect.getSelectedIndex()+"_"+pos+".pdf";

        File saveFolder = new File(Environment.getExternalStorageDirectory()+"/Mannan Softworks/TN Textbooks/");
        if(!saveFolder.exists()){
            saveFolder.mkdir();
        }
//            viewPDF(pdfLink);

        Intent passInt = new Intent(MainActivity.this, ViewActivity.class);
        passInt.putExtra("bookID",pdfLink);
        passInt.putExtra("bookIDName",saveAs);
        Log.d("DisplayPDF",pdfLink +"\n"+saveAs);
        MainActivity.this.startActivity(passInt);
    }
    public  void toastie(String text) {
        Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
    }
    private void fetchSubject(final Integer clsCode) {
        final String tempReq = "include/php/admin/list/subject?selval=";

        new AsyncTask<Void,Void,Void>()
        {
            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... params) {
                try {
                        OkHttpClient client = new OkHttpClient();
//                        Log.d("doInBackground:"," ClassCode "+clsCode);
                        Request request = new Request.Builder()
                                .url(requestUrl+tempReq+clsCode)
                                .build();
                        Response response = client.newCall(request).execute();
//                        Log.d("TAG", "doInBackground: "+request.url().toString());
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Boolean isErrorInSub = jsonObject.getBoolean("error");
                        if(!isErrorInSub){
                            JSONArray jsonArray = jsonObject.getJSONArray("msg");
                            hIdSub = new Integer[jsonArray.length()];
                            txtTitle = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tmpObject = jsonArray.getJSONObject(i);
                                hIdSub[i] = tmpObject.getInt("hidSub");
                                txtTitle[i] = tmpObject.getString("txtTitle");

                                if(txtTitle[i].contains("&amp;")){
                                    txtTitle[i].replace("&amp;","&");
                                }
                                Log.d("doInBackground: ",hIdSub[i].toString());
                                Log.d("doInBackground: ",txtTitle[i]);
                            }

                        }else{
                            Log.d("Error in BgThread", "doInBackground: ");
                        }

                    } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {

                progressBar.setVisibility(View.INVISIBLE);
                dataset3= new LinkedList<>(Arrays.asList(txtTitle));
                subSelect.attachDataSource(dataset3);
                super.onPostExecute(aVoid);
            }
        }.execute();
    }
    private void fetchBookLink(Integer langCode, Integer clsCode, Integer subjectCode) {

        final String tempReq = "include/php/admin/list/text-book?lang="+langCode+"&cls="+clsCode+"&sub="+subjectCode;
        Log.d("TAG", "fetchBookLink: "+langCode+"-"+clsCode+"-"+subjectCode);
        Log.d("TAG", "fetchBookLink: "+requestUrl+tempReq);
        new AsyncTask<Void,Void,Void>()
        {
            @Override
            protected void onPreExecute(){
                progressBar.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    bookList = new ArrayList<HashMap<String, String>>();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(requestUrl+tempReq)
                            .build();
                    Response response = client.newCall(request).execute();
                    String str = response.body().string();
//                    Log.d("JSONARRAY", "doInBackground: "+str);
                    JSONObject jsonObject2 = new JSONObject(str);
                    isSubFetch = jsonObject2.getBoolean("error");
                    if(!isSubFetch){
                        JSONArray jsonArray = jsonObject2.getJSONArray("msg");
                        hidTBkId = new Integer[jsonArray.length()];
                        pdffname = new String[jsonArray.length()];
                        size = new String[jsonArray.length()];
                        taDesp = new String[jsonArray.length()];
//                        Log.d("ArrayLength", "doInBackground: "+jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject tmpObject = jsonArray.getJSONObject(i);
                            if(tmpObject.getString("hidTBkId").length()>0)
                            {
                                hidTBkId[i] = tmpObject.getInt("hidTBkId");
                            }else {hidTBkId[i] =00;}
                            if(tmpObject.getString("pdffname").length()>0)
                            {
                                pdffname[i] = tmpObject.getString("pdffname");
                            }else {pdffname[i] ="null";}
                            if(tmpObject.getString("size").length()>0)
                            {
                                size[i] = tmpObject.getString("size");
                            }else {size[i] ="-";}
                            if(tmpObject.getString("taDesp").length()>0)
                            {
                                taDesp[i] = tmpObject.getString("taDesp");
                            }else {taDesp[i] ="Not Available";}

                            Log.d("doInBackground: ",hidTBkId[i].toString());
                            Log.d("doInBackground: ",pdffname[i]);
                            Log.d("doInBackground: ",size[i]);
                            Log.d("doInBackground: ",taDesp[i]);


                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put(KEY_BOOKNAME, taDesp[i]);
                            map.put(KEY_BOOKSIZE,size[i]);
                            map.put(KEY_BOOKLINK, pdffname[i]);

                            // adding HashList to ArrayList
                            bookList.add(map);
                        }
                    }else{
                        Log.d("Error in BookLinkFetch", "doInBackground: ");
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {

                progressBar.setVisibility(View.INVISIBLE);
                if(isSubFetch==null){
                    toastie("Please check the Internet Connection");
                }
                if(isSubFetch){
                     toastie("Book not available");
                }else
                {
                    searchWholeBook();
                }
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    public void viewPDF(String uri)
    {

        Uri path = Uri.parse(uri);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try{
            startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(MainActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }

    Boolean isSubFetch;
    LazyAdapter adapter;
    private void searchWholeBook()
    {
        adapter=new LazyAdapter(this, bookList);
        availList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        availList.setVisibility(View.VISIBLE);
        availList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(pdffname[i].contains("null")){
                    toastie("Sorry, this Book was not uploaded by Govt of TN");
                } else
                    displaypdf(pdffname[i],i);
            }
        });
    }

//    public void viewPDF(String Id)
//    {
//        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Softworks/TN_TextBooks/" +Id);  // -> filename = maven.pdf
//        Uri path = Uri.fromFile(pdfFile);
//        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//        pdfIntent.setDataAndType(path, "application/pdf");
//        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        try{
//            startActivity(pdfIntent);
//        }catch(ActivityNotFoundException e){
//            Toast.makeText(MainActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
//        }
//    }


    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {

        }
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_menu_int));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                showInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
//                goToNextLevel();
            }
        });
        return interstitialAd;
    }

    void showGuide(){
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Guide");
        alertDialog.setMessage("1.Select Medium -> Class -> Subject -> Book \n\n2.For language books, select both \"Medium\"" +
                " and \"Subject\" as same \n\n if you found this app useful, please share and rate our app  thanks  :-)");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

}
