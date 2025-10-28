package com.example.tfc.vista.fragmentos;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.InventarioDAO;
import com.example.tfc.bbdd.dao.PersonajeDAO;
import com.example.tfc.bbdd.entidades.Inventario;
import com.example.tfc.bbdd.entidades.Personaje;
import com.example.tfc.vista.adaptadores.InventarioAdapter;
import com.example.tfc.vista.adaptadores.PersonajeAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaInventario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaInventario extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IDPERSONAJE = "idPersonaje";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int idPersonaje;
    private String mParam2;

    private InventarioAdapter inventarioAdapter;
    private InventarioDAO inventarioDAO;
    private ArrayList<Inventario> listaInventario;
    private ListaInventario.OnInventarioSelectedListener listener;

    public ListaInventario() {
        // Required empty public constructor
    }
    public interface OnInventarioSelectedListener {
        void OnInventarioSelectedListener(Inventario inventario);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idPersonaje Parameter 1.
     * @return A new instance of fragment ListaInventario.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaInventario newInstance(int idPersonaje) {
        ListaInventario fragment = new ListaInventario();
        Bundle args = new Bundle();
        args.putInt(ARG_IDPERSONAJE, idPersonaje);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idPersonaje = getArguments().getInt(ARG_IDPERSONAJE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_inventario, container, false);

        inventarioDAO = new InventarioDAO(getContext());

        listaInventario = new ArrayList<>(inventarioDAO.obtenerItemsPorPersonaje(idPersonaje));

        ListView listView = view.findViewById(R.id.listViewInventario);
        inventarioAdapter = new InventarioAdapter(getContext(), R.layout.inventario_element,R.id.textViewNombreItem, listaInventario);
        listView.setAdapter(inventarioAdapter);

        listView.setOnItemClickListener((adapterView, v, position, id) -> {
            Inventario itemSelecionado = (Inventario) adapterView.getItemAtPosition(position);
            listener.OnInventarioSelectedListener(itemSelecionado);
        });

        listView.setOnItemLongClickListener((adapterView, v, position, id) -> {
            Inventario itemSeleccionado = listaInventario.get(position);

            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminar item")
                    .setMessage("¿Seguro que deseas eliminar \"" + itemSeleccionado.getProducto()+ "\"?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        inventarioDAO.borrarItem(itemSeleccionado.getIdPersonaje(), itemSeleccionado.getProducto());
                        recargarInventario();
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });
        return view;
    }

    private void recargarInventario() {
        listaInventario.clear();
        listaInventario.addAll(inventarioDAO.obtenerItemsPorPersonaje(idPersonaje));
        inventarioAdapter.notifyDataSetChanged();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ListaInventario.OnInventarioSelectedListener) {
            listener = (ListaInventario.OnInventarioSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " debe implementar OnInventarioSelectedListener");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        recargarInventario();
    }
}