package com.example.pfuternik.vidyoiodemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Endpoint.LogRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements Connector.IConnect,  Connector.IRegisterLogEventListener {

    private static final String TAG = "MainActivity";
    private final String DEFAULT_LOG_LEVELS_AND_CATEGORIES = "warning info@VidyoClient info@LmiPortalSession info@LmiPortalMembership info@LmiResourceManagerUpdates info@LmiPace info@LmiIce  info@VidyoConnector";
    private final String DEBUG_LOG_LEVELS_AND_CATEGORIES = "warning debug@VidyoClient all@LmiPortalSession all@LmiPortalMembership info@LmiResourceManagerUpdates info@LmiPace info@LmiIce all@LmiSignaling  info@VidyoConnector";
    private final int MAX_REMOTE_PARTICIPANTS = 9;

    private Connector vc;
    private FrameLayout videoFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoFrame = (FrameLayout)findViewById(R.id.videoFrame);

        ConnectorPkg.setApplicationUIContext(this);
        ConnectorPkg.initialize();
    }

    public void Start(View v) {

        vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, MAX_REMOTE_PARTICIPANTS, DEBUG_LOG_LEVELS_AND_CATEGORIES, "", 0);
        vc.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
    }


    public void Connect(View v) {
        Log.d(TAG, "Connect: ");

        //set certificates from custom -> TLS problem, certificate not pass
        //vc.setCertificateAuthorityList(readCertificates());

        //set as default certificates -> no TLS problem, but still have problem
        vc.setCertificateAuthorityList("default");

        vc.connectToRoomAsGuest("mbvideo-portal.taishinbank.com.tw",
                "guest",
                "FgYoAO7Hsm",
                "",
                this);
    }

    public void Disconnect(View v) {
        vc.disconnect();
        Log.d(TAG, "Disconnect: ");
    }

    public void onSuccess() {
        Log.d(TAG, "onSuccess: ");
    }

    public void onFailure(Connector.ConnectorFailReason reason) {
        Log.d(TAG, "onFailure: "+ reason.name()+" || " +reason.toString());
    }

    public void onDisconnected(Connector.ConnectorDisconnectReason reason) {
        Log.d(TAG, "onDisconnected: "+ reason.name()+" || " +reason.toString());
    }

    @Override
    public void onLog(LogRecord logRecord) {
        Log.d(TAG, "onLog: "+logRecord.eventTime+"\n"+logRecord.name+" | "+logRecord.message);
    }




    private String readCertificates() {
        try {
            InputStream caCertStream = getResources().openRawResource(R.raw.mbvideo_portal_taishinbank_com_tw_chain);

            File caCertDirectory;
            try {
                String pathDir = getAndroidInternalMemDir();
                caCertDirectory = new File(pathDir);
            } catch (Exception e) {
                caCertDirectory = getDir("marina",0);
            }
            File cafile = new File(caCertDirectory,"ca_certificates.pem");

            FileOutputStream caCertFile = new FileOutputStream(cafile);
            byte buf[] = new byte[1024];
            int len;
            while ((len = caCertStream.read(buf)) != -1) {
                caCertFile.write(buf, 0, len);
            }
            caCertStream.close();
            caCertFile.close();

            return cafile.getPath();
        }
        catch (Exception e) {
            //set as default certificates
            Log.d(TAG, "writeCaCertificates: "+"set as default certificates");
            return "default";
        }
    }

    private String getAndroidInternalMemDir() throws IOException {
        File fileDir = getFilesDir(); //crashing
        if (fileDir != null) {
            String filedir = fileDir.toString() + "/";
            Log.d(TAG, "file directory = " + filedir);
            return filedir;
        } else {
            Log.e(TAG, "Something went wrong, filesDir is null");
        }
        return null;
    }
}
