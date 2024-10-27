package com.example.lab5_20180951;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ReinicioCaloriasReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.resetearCalorias();

        // Opcional: enviar una notificación indicando que las calorías se han reiniciado
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("REINICIO_CALORIAS", "Reinicio de Calorías", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "REINICIO_CALORIAS")
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("Calorías Reiniciadas")
                .setContentText("El contador de calorías ha sido reiniciado automáticamente.")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}

