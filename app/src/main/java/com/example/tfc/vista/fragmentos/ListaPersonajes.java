package com.example.tfc.vista.fragmentos;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.PersonajeDAO;
import com.example.tfc.bbdd.entidades.Personaje;
import com.example.tfc.vista.adaptadores.PersonajeAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaPersonajes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaPersonajes extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IDUSER = "iduser";
    private static final String ARG_IDCAMPANA = "idCampana";

    // TODO: Rename and change types of parameters
    private String iduser;
    private int idCampana;

    private PersonajeAdapter personajeAdapter;
    private PersonajeDAO personajeDAO;
    private ArrayList<Personaje> listaPersonajes;
    private ListaPersonajes.OnPersonajeSelectedListener listener;

    public ListaPersonajes() {
        // Required empty public constructor
    }
    public interface OnPersonajeSelectedListener {
        void onPersonajeSelected(Personaje personaje);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param iduser Parameter 1.
     * @param idCampana Parameter 2.
     * @return A new instance of fragment ListaPersonajes.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaPersonajes newInstance(String iduser, int idCampana) {
        ListaPersonajes fragment = new ListaPersonajes();
        Bundle args = new Bundle();
        args.putString(ARG_IDUSER, iduser);
        args.putInt(ARG_IDCAMPANA, idCampana);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            iduser = getArguments().getString(ARG_IDUSER);
            idCampana = getArguments().getInt(ARG_IDCAMPANA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_personajes, container, false);

        personajeDAO = new PersonajeDAO(getContext());

        listaPersonajes = new ArrayList<>(personajeDAO.obtenerPersonajesPorCampanaYUsuario(idCampana, iduser));

        ListView listView = view.findViewById(R.id.listViewPersonajes);
        personajeAdapter = new PersonajeAdapter(getContext(), R.layout.personajes_element,R.id.textViewNombrePersonaje, listaPersonajes);
        listView.setAdapter(personajeAdapter);

        listView.setOnItemClickListener((adapterView, v, position, id) -> {
            Personaje personajeSeleccionado = (Personaje) adapterView.getItemAtPosition(position);
            listener.onPersonajeSelected(personajeSeleccionado);
        });

        listView.setOnItemLongClickListener((adapterView, v, position, id) -> {
            Personaje personajeSeleccionado = listaPersonajes.get(position);

            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminar personaje")
                    .setMessage("¿Seguro que deseas eliminar \"" + personajeSeleccionado.getNombre()+ "\"?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        personajeDAO.borrarPersonaje(personajeSeleccionado.getNombre());
                        recargarPersonajes();
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });
        return view;
    }

    private void recargarPersonajes() {
        listaPersonajes.clear();
        listaPersonajes.addAll(personajeDAO.obtenerPersonajesPorCampanaYUsuario(idCampana, iduser));
        personajeAdapter.notifyDataSetChanged();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPersonajeSelectedListener) {
            listener = (OnPersonajeSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " debe implementar OnPersonajeSelectedListener");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        recargarPersonajes();
    }
}