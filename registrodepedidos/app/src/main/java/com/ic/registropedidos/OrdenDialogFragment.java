package com.ic.registropedidos;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ic.registropedidos.ui.home.HomeFragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import Entidades.Pedido;
import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class OrdenDialogFragment extends BottomSheetDialogFragment {

    EditText descripcion;
    TextView cantidad;
    DataBaseSQLHelper conn;
    Button btndelete;
    Button btnactualizar;
    int precioProd;
    public static boolean isEdit;
    int iva, embalaje;
    boolean isExito;
    Pedido cod;
    View root ;
   // private BottomSheetListener mListener;

    public OrdenDialogFragment() {
        // Required empty public constructor
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_orden_list_dialog, container, false);
        try {
            descripcion =  root.findViewById(R.id.txtDesccripcionEdit );
            cantidad = root.findViewById(R.id.editNumber );

            precioProd=0;
            iva=0;
            isExito=false;

            Bundle obj = getArguments();
            if(obj != null){
                cod= (Pedido) obj.getSerializable("codigo");
                BuscarProduco(cod.getCodigoProd());

            }else{
                Toast.makeText(getContext(), "Ha ocurrido un error ",
                        Toast.LENGTH_SHORT).show();
            }
            btnactualizar = root.findViewById(R.id.btnAct);
            btndelete =  root.findViewById(R.id.btnDelete);
            btnactualizar.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                  // mListener.onButtonClicked(Actualizar());
                    Actualizar();
                }
            });
            btndelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Delete();
                }
            });

        }catch (Exception e){
            Toast.makeText(getContext(), "Ha ocurrido un error "+ e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        return  root;
    }

    private void BuscarProduco(String codigo) {

        try{

            conn= new DataBaseSQLHelper(getContext());
            SQLiteDatabase db=conn.getReadableDatabase();

            String[] parametro = {codigo};
            String query ="SELECT " + Estructura_BBDD.DESCRIPCION_P + Estructura_BBDD.COMMA_SEP +
                    Estructura_BBDD.PRECIO_BASE_P + Estructura_BBDD.COMMA_SEP +
                    Estructura_BBDD.EMBALAJE_P + Estructura_BBDD.COMMA_SEP +
                    Estructura_BBDD.IVA_P + " FROM " + Estructura_BBDD.TABLE_PRODUCTO + " WHERE " +
                    Estructura_BBDD.CODIGOPRODUCTO_P + " = ?";

            Cursor cursor= db.rawQuery(query, parametro);

            if(cursor.getCount() >0){

                cursor.moveToFirst();
                descripcion.setText(cursor.getString(0));
                precioProd = cursor.getInt(1);
                cantidad.setText(String.valueOf(cod.getCantidad()));
                iva=cursor.getInt(3);
                embalaje = cursor.getInt(2);
            }

            db.close();

        }catch (Exception e)  {
            Toast.makeText(getContext(), "Ha ocurrido un error BuscarProduco: " +e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void Actualizar(){
        if (cantidad.getText().toString().equals("") || Integer.parseInt(cantidad.getText().toString()) == cod.getCantidad()) {
            Toast.makeText(getContext(), "Debe colocar una cantidad", Toast.LENGTH_SHORT).show();
        }else {
            BuscarPedido();
            if(isExito){
                getDialog().hide();
            }
        }
    }

    private void BuscarPedido() {

        try{
          conn= new DataBaseSQLHelper(getContext());
            SQLiteDatabase db=conn.getWritableDatabase();
            String[] parametro = {cod.getCodigoProd(), cod.getCliente()};
            int precio;
            ContentValues values = new ContentValues();

            values.put(Estructura_BBDD.CANTIDAD_O, cantidad.getText().toString());

            int calcularDescuento = (precioProd * 10) / 100;
            precioProd = precioProd - calcularDescuento;
            int calculaIVA = (precioProd * iva) / 100;
            precioProd = precioProd + calculaIVA;

            if(cod.getPorPaquete()==1){
                precio = precioProd * (Integer.parseInt(cantidad.getText().toString())* embalaje);
            }else{
                precio = precioProd * Integer.parseInt(cantidad.getText().toString());
            }

            values.put(Estructura_BBDD.TOTALPRODUCTO_O, (int) precio);
            int res = db.update(Estructura_BBDD.TABLE_ORDEN_O, values, Estructura_BBDD.CODPRODUCTO_O + " =?"
                    + " AND " + Estructura_BBDD.CLIENTE_O + " =?", parametro);

            Toast.makeText(getContext(), "Se actualizo la cantidad de producto. " + res, Toast.LENGTH_SHORT).show();
            isExito=true;
            db.close();
        }catch (Exception e)  {
            Toast.makeText(getContext(), "Ha ocurrido un error BuscarPedido " +e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void Delete(){

        try{
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("Desea eliminar este producto del pedido actual");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                    conn= new DataBaseSQLHelper(getContext());
                    SQLiteDatabase db=conn.getWritableDatabase();
                    String[] parametro = {cod.getCodigoProd(), cod.getCliente()};

                    db.delete(Estructura_BBDD.TABLE_ORDEN_O, Estructura_BBDD.CODPRODUCTO_O + " =?"
                            + " AND " + Estructura_BBDD.CLIENTE_O + " =?", parametro);
                    db.close();
                    Toast.makeText(getContext(), "Se ha eliminado el producto: "+ cod.getCodigoProd(),
                            Toast.LENGTH_SHORT).show();
                    getDialog().hide();
                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                }
            });
            dialogo1.show();


        }catch (Exception e){
            Toast.makeText(getContext(), "Ha ocurrido un error Delete " +e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }

    public void onAttach(Context context){
        super.onAttach(context);
        try {
           // mListener = (BottomSheetListener) context;
        }catch (ClassCastException ex){
            Toast.makeText(getContext(), "Ha ocurrido un error "+ ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
            throw new  ClassCastException(context.toString() + " "+ ex.getMessage());
        }

    }



}
