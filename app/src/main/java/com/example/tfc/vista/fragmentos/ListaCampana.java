package com.example.tfc.vista.fragmentos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.CampanaDAO;
import com.example.tfc.bbdd.dao.UsuarioCampanasDAO;
import com.example.tfc.bbdd.entidades.Campana;
import com.example.tfc.vista.adaptadores.CampanaAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaCampana#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaCampana extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CampanaDAO campanaDAO;
    private UsuarioCampanasDAO usuarioCampanasDAO;
    private CampanaAdapter campanaAdapter;
    private ArrayList<Campana> listaCampanas;
    private OnCampanaSelectedListener listener;

    public ListaCampana() {
        // Required empty public constructor
    }
    public interface OnCampanaSelectedListener {
        void onCampanaSelected(Campana campana);
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaCampana.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaCampana newInstance(String param1, String param2) {
        ListaCampana fragment = new ListaCampana();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_campana, container, false);

        campanaDAO = new CampanaDAO(getContext());
        listaCampanas = new ArrayList<>(campanaDAO.obtenerTodasCampanas());

        ListView listView = view.findViewById(R.id.listViewCampanas);
        campanaAdapter = new CampanaAdapter(getContext(), R.layout.campana_element,R.id.textViewNombre, listaCampanas);
        listView.setAdapter(campanaAdapter);

        listView.setOnItemClickListener((adapterView, v, position, id) -> {
            Campana campanaSeleccionada = (Campana) adapterView.getItemAtPosition(position);
        });

        listView.setOnItemLongClickListener((adapterView, v, position, id) -> {
            Campana campanaSeleccionada = listaCampanas.get(position);

            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminar campaña")
                    .setMessage("¿Seguro que deseas eliminar \"" + campanaSeleccionada.getNombreCampanha() + "\"?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        campanaDAO.borrarCampana(campanaSeleccionada.getNombreCampanha());
                        recargarCampanas();
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true; // Consumimos el evento
        });
        return view;
    }

    private void recargarCampanas() {
        listaCampanas.clear();
        listaCampanas.addAll(campanaDAO.obtenerTodasCampanas());
        campanaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        recargarCampanas();
    }
}