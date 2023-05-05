package com.example.avideochatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avideochatapp.Models.Users;
import com.example.avideochatapp.databinding.ActivitySettingsBinding;
import com.example.avideochatapp.databinding.ActivityVoicecallBinding;

import com.example.avideochatapp.media.RtcTokenBuilder2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.ChannelMediaOptions;


public class voicecall extends AppCompatActivity {

    private static final int PERMISSION_REQ_ID = 22;
    // Fill the App ID of your project generated on Agora Console.
    private final String appId = "0764474f3b79414aad7e17436d9a9c6d";
    String appCertificate = "50a677024e4a4da2b681a3002c2cbb24";
    // Fill the channel name.
    private String channelName = "";
    // Fill the temp token generated on Agora Console.
    private String token = "";
    int expirationTimeInSeconds = 3600;
    // An integer that identifies the local user.
    String senderId =" ";
    String receiverId = null;
    String userName;

    //val to pass
    int sendval=0, receiveval=0, channelval=0;
    // Track the status of your connection
    private boolean isJoined = false;

    // Agora engine instance
    private RtcEngine agoraEngine;
    // UI elements
    private TextView infoText;
    private TextView NameText;
    private Button joinLeaveButton;

    ActivityVoicecallBinding binding;
    FirebaseDatabase database;


    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO
            };

    private boolean checkSelfPermission()
    {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) !=  PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        return true;
    }

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void setupVoiceSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote user joining the channel.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(()->infoText.setText("Remote user joined: " + uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            // Successfully joined a channel
            isJoined = true;
            showMessage("Joined Channel " + channel);
            runOnUiThread(()->infoText.setText("Waiting for a remote user to join"));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            // Listen for remote users leaving the channel
            showMessage("Remote user offline " + uid + " " + reason);
            if (isJoined) runOnUiThread(()->infoText.setText("Waiting for a remote user to join"));
        }

        @Override
        public void onLeaveChannel(RtcStats 	stats) {
            // Listen for the local user leaving the channel
            runOnUiThread(()->infoText.setText("Press the button to join a channel"));
            isJoined = false;
        }
    };

    private void joinChannel() {
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.autoSubscribeAudio = true;
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine.joinChannel(token, channelName, sendval, options);
    }


    public void joinLeaveChannel(View view) {
            agoraEngine.leaveChannel();
            joinLeaveButton.setText("Join");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoicecallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();


        Bundle extras = getIntent().getExtras();
        String value1 = null;
        if (extras != null) {
            value1 = extras.getString("userId");
        }
        senderId=value1;

        String value2 = null;
        if (extras != null) {
            value2 = extras.getString("receiverId");
        }
        receiverId=value2;

        String value3 = null;
        if (extras != null) {
            value3 = extras.getString("userName");
        }
        userName=value3;

        for(int i = 0; i < senderId.length(); i++){
            sendval+=(int)senderId.charAt(i);
        }

        for(int i = 0; i < receiverId.length(); i++){
            receiveval+=(int)receiverId .charAt(i);
        }

        channelval=sendval+receiveval;
        channelName=Integer.toString(channelval);
        RtcTokenBuilder2 tokenBuilder = new RtcTokenBuilder2();
        // Calculate the time expiry timestamp
        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result = tokenBuilder.buildTokenWithUid(appId, appCertificate,
                channelName,  sendval, RtcTokenBuilder2.Role.ROLE_PUBLISHER, timestamp, timestamp);
        token=result;
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }

        setupVoiceSDKEngine();

        // Set up access to the UI elements
        joinLeaveButton = findViewById(R.id.joinLeaveButton);

        infoText = findViewById(R.id.infoText);
        NameText = findViewById(R.id.Nametext);
        joinChannel();

        database.getReference().child("Users").child(receiverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        Picasso.get()
                                .load(users.getProfilepic())
                                .placeholder(R.drawable.avatar)
                                .into(binding.userImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        runOnUiThread(()->NameText.setText(userName));
    }

    protected void onDestroy() {
        super.onDestroy();
        agoraEngine.leaveChannel();

        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }


}