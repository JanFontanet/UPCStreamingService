package pbe.upcstreamingservice.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import pbe.upcstreamingservice.R;

/**
 * Created by Roger on 19/11/2014.
 */

public class Notifications{

}
public class Notificatio {

    NotificationManager notificationManager = (NotificationManager);
    getSystemService()

    /* afegir icone, text, moment */

    int icono = R.drawable.ic_launcher;
    CharSequence tickerText = "Barra de notifiacio";
    long moment = System.currentTimeMillis();

    Notification notification = new Notificatio(icono, tickerText, moment);

    Context context = getApplicationContext();
    CharSequence contentTitle = "UPC Streaming Service";
    CharSequence contentText =  "Has recibido un nuevo archivo. Pulsa para leer";

    //afegir so
    notification.defaults = Notification.DEFAULT_SOUND;
    //afegir vibració
    notification.defaults = Notification.DEFAULT_VIBRATE;

    Intent notificationIntent  new Intent(this, Notifi);
    PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
    private Context applicationContext;

    public Context getApplicationContext() {
        return applicationContext;
    }

    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

    notification.notify(HELLO_ID, notification);

}
