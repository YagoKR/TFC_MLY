package com.example.tfc.vista.fragmentos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

public class ListaCampana extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private CampanaDAO campanaDAO;
    private UsuarioCampanasDAO usuarioCampanasDAO;
    private CampanaAdapter campanaAdapter;
    private ArrayList<Campana> listaCampanas;
    private OnCampanaSelectedListener listener;
    private SharedPreferences sp;

    private String idUsuario;

    public ListaCampana() {
    }

    public interface OnCampanaSelectedListener {
        void onCampanaSelected(Campana campana);
    }

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

        usuarioCampanasDAO = new UsuarioCampanasDAO(getContext());

        sp = getActivity().getSharedPreferences("datosUsuario", Context.MODE_PRIVATE);
        idUsuario = sp.getString("usuario", null);

        listaCampanas = new ArrayList<>();
        if (idUsuario != null) {
            listaCampanas.addAll(usuarioCampanasDAO.obtenerCampanasDeUsuario(idUsuario));
        }

        ListView listView = view.findViewById(R.id.listViewCampanas);
        campanaAdapter = new CampanaAdapter(getContext(), R.layout.campana_element, R.id.textViewNombre, listaCampanas);
        listView.setAdapter(campanaAdapter);

        listView.setOnItemClickListener((adapterView, v, position, id) -> {
            Campana campanaSeleccionada = (Campana) adapterView.getItemAtPosition(position);
            listener.onCampanaSelected(campanaSeleccionada);
        });

        listView.setOnItemLongClickListener((adapterView, v, position, id) -> {
            Campana campanaSeleccionada = listaCampanas.get(position);

            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminar campaña")
                    .setMessage("¿Seguro que deseas eliminar \"" + campanaSeleccionada.getNombreCampanha() + "\"?\n Se borrarán todos los personajes e items asociados.")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        CampanaDAO campanaDAO = new CampanaDAO(getContext());
                        campanaDAO.borrarCampana(campanaSeleccionada.getNombreCampanha());

                        usuarioCampanasDAO.borrarUsuarioCampana(idUsuario, String.valueOf(campanaSeleccionada.getId()));

                        recargarCampanas();
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });

        return view;
    }

    private void recargarCampanas() {
        listaCampanas.clear();
        if (idUsuario != null) {
            listaCampanas.addAll(usuarioCampanasDAO.obtenerCampanasDeUsuario(idUsuario));
        }
        campanaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCampanaSelectedListener) {
            listener = (OnCampanaSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " debe implementar OnCampanaSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        recargarCampanas();
    }
}
