package com.creative.share.apps.e_branchdriver.notification;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.e_branchdriver.models.OrderModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.tags.Tags;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class FireBaseMessaging extends FirebaseMessagingService {

    Preferences preferences = Preferences.newInstance();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String,String> map = remoteMessage.getData();

        for (String key:map.keySet())
        {
            Log.e("keyss",key+"    value "+map.get(key));
        }

        if (getSession().equals(Tags.session_login))
        {
//            if (map.get("to_user_id")!=null)
//            {
//                int to_id = Integer.parseInt(map.get("to_user_id"));
//
//                if (getCurrentUser_id()==to_id)
//                {
//                    manageNotification(map);
//                }
//            }
//            else
            if(map.get("id")!=null){
                manageNotification(map);


            }
        }
        }





    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void manageNotification(Map<String, String> map) {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createNewNotificationVersion(map);
        }else
            {
                createOldNotificationVersion(map);

            }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createNewNotificationVersion(Map<String, String> map) {

        String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;

        String not_type = map.get("notification_type");

    if(not_type.equals("order"))
        {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            String current_class =activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            int status=Integer.parseInt(map.get("status"));
            int order_id=Integer.parseInt(map.get("id"));
            String title = null;
            title=getResources().getString(R.string.order_num)+" "+order_id;


            OrderModel orderModel=new OrderModel(status+"");
            String content=null;
            if(status==1){
                content=getResources().getString(R.string.market_send_order)+ " "+order_id;
            }

            if (current_class.equals("com.creative.share.apps.e_branchdriver.activities_fragments.activity_home")){
                EventBus.getDefault().post(orderModel);


            }
            sendNotification_VersionNew(content,title,orderModel,sound_Path);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createOldNotificationVersion(Map<String, String> map)
    {


        String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;

        String not_type = map.get("notification_type");


        if(not_type!=null&&not_type.equals("order")) {
            String content = null;
            String title = null;

            int status=Integer.parseInt(map.get("status"));
            int order_id=Integer.parseInt(map.get("id"));
            title=getResources().getString(R.string.order_num)+" "+order_id;
            OrderModel orderModel=new OrderModel(status+"");
            if(status==1){
                content=getResources().getString(R.string.market_send_order)+ " "+order_id;
            }

            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            String current_class =activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            if (current_class.equals("com.creative.share.apps.e_branchdriver.activities_fragments.activity_home")){
                EventBus.getDefault().post(orderModel);


            }
                sendNotification_VersionOld(content,title,orderModel,sound_Path);
        }
    }










    private String getSession()
    {
        return preferences.getSession(this);
    }

    private void sendNotification_VersionOld(String content, String title, OrderModel orderModel, String sound_path) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSound(Uri.parse(sound_path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.drawable.ic_nav_notification);
        builder.setAutoCancel(true);
         builder.setContentTitle(title);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("not",true);
intent.putExtra("data",orderModel);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        builder.setContentText(content);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(12352, builder.build());

        }
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //    Log.e("mange","mang");
                if (manager != null) {

                    builder.setLargeIcon(bitmap);

                    manager.notify(new Random().nextInt(200), builder.build());
                    //  Log.e("mange","mang");

                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        new Handler(Looper.getMainLooper())
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Picasso.with(FireBaseMessaging.this).load(R.drawable.ic_nav_notification).into(target);



                    }
                }, 1);



    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification_VersionNew(String content, String title, OrderModel orderModel, String sound_path) {

        String CHANNEL_ID = "my_channel_02";
        CharSequence CHANNEL_NAME = "my_channel_name";
        int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);
        channel.setShowBadge(true);
        channel.setSound(Uri.parse(sound_path), new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        );
        builder.setChannelId(CHANNEL_ID);
        builder.setSound(Uri.parse(sound_path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.drawable.ic_nav_notification);
        builder.setAutoCancel(true);

          builder.setContentTitle(title);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_nav_notification);
        builder.setLargeIcon(bitmap);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("not",true);
        intent.putExtra("data",orderModel);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        builder.setContentText(content);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
            manager.notify(12352, builder.build());
        }

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                if (manager != null) {
                    builder.setLargeIcon(bitmap);
                    manager.createNotificationChannel(channel);
                    manager.notify(new Random().nextInt(200), builder.build());
                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };


        new Handler(Looper.getMainLooper())
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Picasso.with(FireBaseMessaging.this).load(R.drawable.ic_nav_notification).into(target);

                    }
                }, 1);

    }

}
