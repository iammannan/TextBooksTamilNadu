package mannan.textbookstamilnadu;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.ayz4sci.androidfactory.DownloadProgressView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.File;

public class ViewActivity extends AppCompatActivity {

    private static final String TAG = "ViewActivity";
    String pdfLink,pdfName;
    ProgressBar progressBar;
    File targetFile;
    PDFView pdfView;
    DownloadProgressView downloadProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        progressBar = findViewById(R.id.webProgress);

        downloadProgressView = findViewById(R.id.downloadProgressView);

        Intent getID = getIntent();
        if (getID.hasExtra("bookID")) {
            pdfLink = getID.getStringExtra("bookID");
        }
        if (getID.hasExtra("bookIDName")) {
            pdfName = getID.getStringExtra("bookIDName");
        }

        Log.d(TAG, "onCreate: BookLink"+pdfLink);
        Log.d(TAG, "onCreate: BookName"+pdfName);

        targetFile = new File(Environment.getExternalStorageDirectory()+"/Android/data/mannan.textbookstamilnadu/files/",pdfName);
        String fold = Environment.getExternalStorageDirectory().toString();
        File rootFold = new File(fold,"/Android/data/mannan.textbookstamilnadu/files/");
        if(!rootFold.exists()){
           rootFold.mkdirs();
        }
        //! add
        if(targetFile.exists()){
            viewPDF();
        }else
        {

            final long downloadID;
            downloadProgressView.setVisibility(View.VISIBLE);
            final DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfLink));
            request.setTitle("Tamilnadu Textbook app");
            request.setDescription("Your book is downloading...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setDestinationInExternalFilesDir(getApplicationContext(),null, pdfName);
            request.allowScanningByMediaScanner();
            downloadID = downloadManager.enqueue(request);

            downloadProgressView.show(downloadID, new DownloadProgressView.DownloadStatusListener() {
                @Override
                public void downloadFailed(int reason) {
                    finish();
                    //Action to perform when download fails, reason as returned by DownloadManager.COLUMN_REASON
                }

                @Override
                public void downloadSuccessful() {
                        viewPDF();
                        downloadProgressView.setVisibility(View.GONE);
                    //Action to perform on success
                }

                @Override
                public void downloadCancelled() {
                    finish();
                    //Action to perform when user press the cancel button
                }
            });
        }


    }

    void viewPDF(){
        pdfView = findViewById(R.id.pdfView);
        pdfView.setVisibility(View.VISIBLE);
        pdfView.fromFile(targetFile)
                .defaultPage(0)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        Log.d(TAG, "loadComplete: ");
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError: "+t.getLocalizedMessage());
                    }
                })
                .load();
    }

}

