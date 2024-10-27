package com.example.lab5_20180951;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText editPeso, editAltura, editEdad;
    private Spinner spinnerGenero, spinnerNivelActividad, spinnerObjetivo;
    private TextView textCaloriasRecomendadas, textCaloriasConsumidas;
    private DatabaseHelper databaseHelper;
    private int caloriasRecomendadas;

    private EditText editIntervaloObjetivo;
    private Button btnProgramarObjetivo;
    private Button btnReiniciarCalorias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editPeso = findViewById(R.id.editPeso);
        editAltura = findViewById(R.id.editAltura);
        editEdad = findViewById(R.id.editEdad);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        spinnerNivelActividad = findViewById(R.id.spinnerNivelActividad);
        spinnerObjetivo = findViewById(R.id.spinnerObjetivo);
        textCaloriasRecomendadas = findViewById(R.id.textCaloriasRecomendadas);
        textCaloriasConsumidas = findViewById(R.id.textCaloriasConsumidas);
        databaseHelper = new DatabaseHelper(this);


        editIntervaloObjetivo = findViewById(R.id.editIntervaloObjetivo);
        btnProgramarObjetivo = findViewById(R.id.btnProgramarObjetivo);

        btnReiniciarCalorias = findViewById(R.id.btnReiniciarCalorias);



        findViewById(R.id.btnCalcularCalorias).setOnClickListener(view -> calcularCalorias());
        findViewById(R.id.btnAgregarComida).setOnClickListener(view -> abrirAddFoodActivity());

        btnProgramarObjetivo.setOnClickListener(view -> {
            String intervaloTexto = editIntervaloObjetivo.getText().toString();
            if (!intervaloTexto.isEmpty()) {
                int intervaloMinutos = Integer.parseInt(intervaloTexto);
                programarNotificacionObjetivo(intervaloMinutos);
                Toast.makeText(this, "Notificación de objetivo programada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Por favor, ingresa un intervalo", Toast.LENGTH_SHORT).show();
            }
        });

        btnReiniciarCalorias.setOnClickListener(view -> reiniciarCalorias());
        programarReinicioDiario();
        actualizarCaloriasConsumidas();

    }

    private void programarNotificacionObjetivo(int intervaloMinutos) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long intervaloMillis = intervaloMinutos * 60 * 1000; // convertir minutos a milisegundos

        Intent intent = new Intent(this, ObjetivoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 104, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervaloMillis, pendingIntent);
    }

    private void calcularCalorias() {
        // Verificar si los campos están vacíos antes de intentar parsear
        if (editPeso.getText().toString().isEmpty() ||
                editAltura.getText().toString().isEmpty() ||
                editEdad.getText().toString().isEmpty()) {

            // Mostrar mensaje al usuario
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return; // Detener la ejecución del método si falta algún dato
        }

        // Si todos los campos están llenos, proceder con el cálculo
        double peso = Double.parseDouble(editPeso.getText().toString());
        double altura = Double.parseDouble(editAltura.getText().toString());
        int edad = Integer.parseInt(editEdad.getText().toString());
        String genero = spinnerGenero.getSelectedItem().toString();
        String objetivo = spinnerObjetivo.getSelectedItem().toString();

        // Cálculo de TMB (Tasa Metabólica Basal)
        double tmb = (genero.equals("Masculino"))
                ? 88.36 + (13.4 * peso) + (4.8 * altura) - (5.7 * edad)
                : 447.6 + (9.2 * peso) + (3.1 * altura) - (4.3 * edad);

        String nivelActividad = spinnerNivelActividad.getSelectedItem().toString();
        double factorActividad = getFactorActividad(nivelActividad);

        caloriasRecomendadas = (int)(tmb * factorActividad);
        caloriasRecomendadas += (objetivo.equals("Subir de peso")) ? 500 : (objetivo.equals("Bajar de peso")) ? -300 : 0;

        textCaloriasRecomendadas.setText("Calorías recomendadas: " + caloriasRecomendadas);
    }


    private double getFactorActividad(String nivelActividad) {
        switch (nivelActividad) {
            case "Sedentario":
                return 1.2;
            case "Actividad ligera":
                return 1.375;
            case "Moderadamente activo":
                return 1.55;
            case "Activo":
                return 1.725;
            default:
                return 1.2;
        }
    }
    private void abrirAddFoodActivity() {
        Intent intent = new Intent(this, AddFoodActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarCaloriasConsumidas(); // Actualizar cada vez que se regresa a esta actividad
    }

    private void actualizarCaloriasConsumidas() {
        int totalCalorias = databaseHelper.getTotalCalorias(); // Método que suma las calorías de todas las comidas del día
        textCaloriasConsumidas.setText("Calorías consumidas hoy: " + totalCalorias);
        verificarExcesoCalorias(totalCalorias);
    }


    private void verificarExcesoCalorias(int caloriasConsumidas) {
        if (caloriasConsumidas > caloriasRecomendadas) {
            // Configurar el NotificationManager y NotificationChannel
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("EXCESO_CALORIAS", "Exceso de Calorías", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Notificación de exceso de calorías");
                notificationManager.createNotificationChannel(channel);
            }

            // Crear la notificación
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "EXCESO_CALORIAS")
                    .setSmallIcon(R.drawable.ic_warning)
                    .setContentTitle("Alerta de Exceso de Calorías")
                    .setContentText("Has consumido más calorías de las recomendadas. Considera hacer ejercicio o reducir tu siguiente comida.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            notificationManager.notify(1, builder.build());
        }
    }


    private void programarRecordatorios() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar desayuno = Calendar.getInstance();
        desayuno.set(Calendar.HOUR_OF_DAY, 8);
        desayuno.set(Calendar.MINUTE, 0);

        Calendar almuerzo = Calendar.getInstance();
        almuerzo.set(Calendar.HOUR_OF_DAY, 12);
        almuerzo.set(Calendar.MINUTE, 0);

        Calendar cena = Calendar.getInstance();
        cena.set(Calendar.HOUR_OF_DAY, 20);
        cena.set(Calendar.MINUTE, 0);

        programarAlarma(alarmManager, desayuno, 101);
        programarAlarma(alarmManager, almuerzo, 102);
        programarAlarma(alarmManager, cena, 103);
    }

    private void programarAlarma(AlarmManager alarmManager, Calendar hora, int requestCode) {
        Intent intent = new Intent(this, AlarmaReceiver.class);
        intent.putExtra("mensaje", "Recuerda registrar tu comida.");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, hora.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void reiniciarCalorias() {
        databaseHelper.resetearCalorias(); // Llama al método que borra las calorías en la base de datos
        actualizarCaloriasConsumidas();
        Toast.makeText(this, "Calorías reiniciadas", Toast.LENGTH_SHORT).show();
    }

    private void programarReinicioDiario() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Configuración de la alarma para las 12:00 AM cada día
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, ReinicioCaloriasReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 105, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }







}



