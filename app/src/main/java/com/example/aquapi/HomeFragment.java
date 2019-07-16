package com.example.aquapi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    String CHANNEL_ID = "AquaPi";  // Canal de las notificaciones
    int NOTIFICATION_ID = 1; //Identificador de las notificaciones
    float pHFlag  = 0; // Variable flag para el pH para las notificaciones relacionadas al pH inicializado en 0

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();  // Variable database para realizar las operaciones a la base en tiempo real en firebase
        database.getReference().child("AguaConsumida").addValueEventListener(new ValueEventListener(){ //Obtener la referencia a la BD al hijo AguaConsumida
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //Funcion propia de Firebase que se efectua cada ves que hay cambios en la BD
                if (dataSnapshot.exists()){
                    String Aguaa = dataSnapshot.child("Agua").getValue().toString(); //Obtener el valor del agua consumida y volverlo un String
                    String pH = dataSnapshot.child("pH").getValue().toString(); //Obtener el valor del pH y convertilo en String
                    String dateBD = dataSnapshot.child("Fecha").getValue().toString(); //Obtener la Fecha y convertila en String
                    LocalDateTime myDateObj = LocalDateTime.now();
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedDate = myDateObj.format(myFormatObj);
                    float pHact = Float.parseFloat(pH); // Asignar al pHact el valor el pH actual en la BD

                    float aguiconsu =  Float.parseFloat(Aguaa)*100/2500; //Variable para calcular el porcentaje de agua consumida y se muestre en el CirculeProgesive
                    setTextAgua(Aguaa); //Llamado funcion para actualizar el textView del agua consumida
                    setTextpH(pH); //Llamado funcion para actualizar el textView del pH actual
                    if(pHFlag != pHact) { //Verificacion de si hay cambios en el pH para mostrar las notificaiones comparando el pHFlag con el pHact
                        notificacionpH(pHact); //Llamado a la funcion para mostrar la notificaion debida
                        pHFlag = pHact; //Acualizar el pHFlag para que solo se musetre cuando cambie el pH en la BD
                    }
                    if(Float.parseFloat(Aguaa) >= 2500.0){ //Verificador si se llego al consumo debido durante el dia establecido en 2500 ml
                        LocalDateTime locaDate = LocalDateTime.now();
                        int hours  = locaDate.getHour();
                        int min  = locaDate.getMinute();
                        notificacionAGUA(hours,min); //Llamado a la funcion para mostrar la notificacion establecida mostrando la Hora y minutos
                    }
                    if(formattedDate.compareTo(dateBD) == 1) {
                        database.getReference().child("AguaConsumida").child("Fecha").setValue(formattedDate);
                        LocalDateTime locaDate = LocalDateTime.now();
                        int hours = locaDate.getHour();
                        int min = locaDate.getMinute();
                        notificacionAGUAI(hours, min); //Llamado a la funcion para mostrar la notificacion establecida mostrando la Hora y minutos
                        final String Aqua = Aguaa;
                        final String pH2 = pH;
                        database.getReference().child("Estadisticas").addListenerForSingleValueEvent(new ValueEventListener() { //Obtener la referencia a la BD al hijo AguaConsumida
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) { //Funcion propia de Firebase que se efectua cada ves que hay cambios en la BD
                                if (dataSnapshot.exists()) {
                                    String AguaPromedio = dataSnapshot.child("AguaPromedio").getValue().toString(); //Obtener el valor del agua consumida y volverlo un String
                                    String AguaMax = dataSnapshot.child("MaxAgua").getValue().toString(); //Obtener el valor del agua consumida y volverlo un String
                                    String AguaMin = dataSnapshot.child("MinAgua").getValue().toString(); //Obtener el valor del agua consumida y volverlo un String
                                    String pHPromedio = dataSnapshot.child("pHPromedio").getValue().toString(); //Obtener el valor del agua consumida y volverlo un String
                                    String pHMax = dataSnapshot.child("MaxpH").getValue().toString(); //Obtener el valor del agua consumida y volverlo un String
                                    String pHMin = dataSnapshot.child("MinpH").getValue().toString(); //Obtener el valor del agua consumida y volverlo un String
                                    double AguaPromediof = Double.parseDouble(AguaPromedio);
                                    double AguaMaxf = Double.parseDouble(AguaMax);
                                    double AguaMinf = Double.parseDouble(AguaMin);
                                    double pHPromediof = Double.parseDouble(pHPromedio);
                                    double pHMaxf = Double.parseDouble(pHMax);
                                    double pHMinf = Double.parseDouble(pHMin);
                                    setAguaEstadisticas(AguaPromediof,AguaMaxf,AguaMinf);
                                    setpHEstadisticas(pHPromediof,pHMaxf,pHMinf);
                                }
                            }

                            public  void setAguaEstadisticas(double AguaPromediof,double AguaMaxf,double AguaMinf){
                                if (AguaPromediof == 0.0){
                                    database.getReference().child("Estadisticas").child("AguaPromedio").setValue(Double.parseDouble(Aqua));
                                }else{
                                    double aguapromf = (AguaPromediof+Double.parseDouble(Aqua))/2;
                                    database.getReference().child("Estadisticas").child("AguaPromedio").setValue(aguapromf);
                                }
                                if (AguaMaxf == 0.0){
                                    database.getReference().child("Estadisticas").child("MaxAgua").setValue(Double.parseDouble(Aqua));
                                }else{
                                    if(Double.parseDouble(Aqua) > AguaMaxf){
                                        database.getReference().child("Estadisticas").child("MaxAgua").setValue(Double.parseDouble(Aqua));
                                    }
                                }
                                if (AguaMinf == 0.0){
                                    database.getReference().child("Estadisticas").child("MinAgua").setValue(Double.parseDouble(Aqua));
                                }else{
                                    if(Double.parseDouble(Aqua) < AguaMinf){
                                        database.getReference().child("Estadisticas").child("MinAgua").setValue(Double.parseDouble(Aqua));
                                    }
                                }
                            }
                            public void setpHEstadisticas(double pHPromediof,double pHMaxf,double pHMinf){
                                if (pHPromediof == 0.0){
                                    database.getReference().child("Estadisticas").child("pHPromedio").setValue(Double.parseDouble(pH2));
                                }else{
                                    double phpromf =  (pHPromediof+Double.parseDouble(pH2))/2;
                                    database.getReference().child("Estadisticas").child("pHPromedio").setValue(phpromf);
                                }
                                if (pHMaxf == 0.0){
                                    database.getReference().child("Estadisticas").child("MaxpH").setValue(Double.parseDouble(pH2));
                                }else{
                                    if(Double.parseDouble(pH2) > pHMaxf){
                                        database.getReference().child("Estadisticas").child("MaxpH").setValue(Double.parseDouble(pH2));
                                    }
                                }
                                if (pHMinf == 0.0){
                                    database.getReference().child("Estadisticas").child("MinpH").setValue(Double.parseDouble(pH2));
                                }else{
                                    if(Double.parseDouble(pH2) < pHMinf){
                                        database.getReference().child("Estadisticas").child("MinpH").setValue(Double.parseDouble(pH2));
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        database.getReference().child("AguaConsumida").child("Agua").setValue(0);
                        database.getReference().child("AguaConsumida").child("pH").setValue(0);

                    }
                    setProAgua(aguiconsu);
                }
            }
            public void setTextAgua(String text) {
                try{
                    TextView t = (TextView) getActivity().findViewById(R.id.Agua);  //UPDATE
                    t.setText(text);
                }catch (NullPointerException ignored){ }

            }
            public void setTextpH(String text) {
                try {
                    TextView t = (TextView) getActivity().findViewById(R.id.pH);  //UPDATE
                    t.setText(text);
                }catch (NullPointerException ignored){}
            }
            public void setProAgua(float valor) {
                try{
                    ProgressBar t = (ProgressBar) getActivity().findViewById(R.id.determinateBar);  //UPDATE
                    int a = Math.round(valor);
                    t.setProgress(a);
                    TextView p = (TextView) getActivity().findViewById(R.id.porcent);  //UPDATE
                    String porcenta = "Porcentaje: "+Integer.toString(a)+"%";
                    p.setText(porcenta);}
                    catch (NullPointerException ignored){}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home2, container, false);
    }

    public void notificacionpH(float pHNoti){
        try{
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"AquaPi",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),CHANNEL_ID);
            if(pHNoti > 8) {
                builder.setSmallIcon(R.drawable.ic_warning_black_24dp);
                builder.setContentTitle("pH MUY ALTO FUERA DE LO NORMAL");
                builder.setContentText("El pH del agua que va a consumir indica que el agua es Alcalina, podria perjudicar su salud");
                builder.setColor(Color.RED);
            }else if(pHNoti < 6.5){
                builder.setSmallIcon(R.drawable.ic_warning_black_24dp);
                builder.setContentTitle("pH MUY BAJO FUERA DE LO NORMAL");
                builder.setContentText("El pH del agua que va a consumir indica que el agua es Acida, podria perjudicar su salud");
                builder.setColor(Color.RED);
            }else {
                builder.setSmallIcon(R.drawable.ic_check_black_24dp);
                builder.setContentTitle("El pH ES NEUTRO");
                builder.setContentText("El pH del agua que va a consumir indica que el agua es Neutra y buena para el consumo");
                builder.setColor(Color.GREEN);
            }
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
            notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());}
            catch (NullPointerException ignored){}
    }
    public void notificacionAGUA(int hora,int min) {
        try{
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "AquaPi", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_check_black_24dp);
            builder.setContentTitle("FELICITACIONES!!");
            String dianoche = "am";
            if(hora > 12){
                hora = hora -12;
                dianoche = "pm";
            }
            builder.setContentText("Haz alcanzado la meta del día a las: "+hora+":"+min+" "+dianoche);
            builder.setColor(Color.GREEN);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());}
            catch (NullPointerException ignored){}
    }
    public void notificacionAGUAI(int hora,int min) {
        try{
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "AquaPi", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_check_black_24dp);
            builder.setContentTitle("NUEVO INICIO!!");
            String dianoche = "am";
            if(hora > 12){
                hora = hora -12;
                dianoche = "pm";
            }
            builder.setContentText("A iniciado a consumir agua a las: "+hora+":"+min+" "+dianoche);
            builder.setColor(Color.GREEN);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());}
            catch (NullPointerException ignored){}
    }
}
