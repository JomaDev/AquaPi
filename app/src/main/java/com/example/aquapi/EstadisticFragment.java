package com.example.aquapi;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class EstadisticFragment extends Fragment {

    public EstadisticFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("Estadisticas").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String AguaPromedio = dataSnapshot.child("AguaPromedio").getValue().toString();
                    String MaxAgua = dataSnapshot.child("MaxAgua").getValue().toString();
                    String MinAgua = dataSnapshot.child("MinAgua").getValue().toString();
                    String pHPromedio = dataSnapshot.child("pHPromedio").getValue().toString();
                    String MaxpH = dataSnapshot.child("MaxpH").getValue().toString();
                    String MinpH = dataSnapshot.child("MinpH").getValue().toString();
                    setTextAgua(AguaPromedio,MaxAgua,MinAgua);
                    setTextpH(pHPromedio,MaxpH,MinpH);

                }
            }
            public void setTextAgua(String prom,String max,String min) {
                try {
                    TextView p = (TextView) getActivity().findViewById(R.id.aguaPromedio);  //UPDATE
                    p.setText("Agua Promedio: " + prom + " ml");
                    TextView m = (TextView) getActivity().findViewById(R.id.aguaMax);  //UPDATE
                    m.setText("Maximo Agua: " + max + " ml");
                    TextView mi = (TextView) getActivity().findViewById(R.id.aguaMin);  //UPDATE
                    mi.setText("Minimo Agua: " + min + " ml");
                }catch (NullPointerException ignored){

                }
            }
            public void setTextpH(String prom,String max,String min) {
                try {
                    TextView p = (TextView) getActivity().findViewById(R.id.pHPromedio);  //UPDATE
                    p.setText("pH Agua Promedio: " + prom);
                    TextView m = (TextView) getActivity().findViewById(R.id.pHMax);  //UPDATE
                    m.setText("Maximo pH Agua: " + max);
                    TextView mi = (TextView) getActivity().findViewById(R.id.pHMin);  //UPDATE
                    mi.setText("Minimo pH Agua: " + min);
                }catch (NullPointerException ignored){

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_estadistic, container, false);
    }

}
