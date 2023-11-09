package com.ic.registropedidos.ui.newOrder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ic.registropedidos.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class NewOrderFragment extends Fragment {

    View root;
    DataBaseSQLHelper conn;
    EditText cliente, codProd, cantidad;
    String descripcion;
    Switch isPorPaquete;
    int precioProducto;
    int iva, embalaje;
    ListView listaProductos;
    public  ArrayAdapter adapter= null;
    List<String> addProducts = new ArrayList<>();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_new_order, container, false);

        listaProductos = root.findViewById(R.id.list_products);

        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, addProducts);
        listaProductos.setAdapter(adapter);

        cliente = root.findViewById(R.id.txtClient);
        codProd = root.findViewById(R.id.nCod);
        cantidad = root.findViewById(R.id.nCantidad);
        isPorPaquete = root.findViewById(R.id.switchPaquete);
        isPorPaquete.setChecked(true);

        codProd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                   boolean existeProduct = buscar();
                   if(existeProduct){
                       Agregar();
                       cantidad.requestFocus();
                   }else{
                       Toast.makeText(getContext(), "No se puede registrar pedido con codigo " + codProd.getText().toString(), Toast.LENGTH_SHORT).show();
                       codProd.setText("");
                       //codProd.requestFocusFromTouch();
                        }
                }
            }
        });

        return  root;
    }

    private void Agregar() {

        try{

            conn = new DataBaseSQLHelper(getContext());
            SQLiteDatabase db= conn.getWritableDatabase();
            int cantidadPaquete;
            int precio;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            String systemDate = dateFormat.format(date);

            if(cliente.getText().toString().equals("") || codProd.getText().toString().equals("") || cantidad.getText().toString().equals("")){
                Toast.makeText(getContext(), "Todos los datos deben ser validos. ", Toast.LENGTH_SHORT).show();

            }else {

                if(ExistProductInOrden(codProd.getText().toString(), cliente.getText().toString())){
                    Toast.makeText(getContext(), "Este producto ya ha sido agregado al pedido ", Toast.LENGTH_SHORT).show();
                }else {

                    ContentValues values = new ContentValues();
                    values.put(Estructura_BBDD.CLIENTE_O, cliente.getText().toString().toLowerCase().trim());
                    values.put(Estructura_BBDD.CODPRODUCTO_O, codProd.getText().toString());
                    values.put(Estructura_BBDD.FECHA_O, systemDate);
                    values.put(Estructura_BBDD.CANTIDAD_O, cantidad.getText().toString());

                    int calculateDescent = (precioProducto * 10) / 100;
                    precioProducto = precioProducto - calculateDescent;
                    int calculateIVA = (precioProducto * iva) / 100;
                    precioProducto = precioProducto + calculateIVA ;

                    if(isPorPaquete.isChecked()){
                        cantidadPaquete= Integer.parseInt(cantidad.getText().toString()) * embalaje;
                        values.put(Estructura_BBDD.PORPAQUETE_O, 1);
                    }else {
                        cantidadPaquete= Integer.parseInt(cantidad.getText().toString());
                        values.put(Estructura_BBDD.PORPAQUETE_O, 0);
                    }

                    precio = precioProducto * cantidadPaquete;
                    values.put(Estructura_BBDD.TOTALPRODUCTO_O, precio);
                    values.put(Estructura_BBDD.PRECIOPRODUCTO_O, precioProducto);

                    db.insert(Estructura_BBDD.TABLE_ORDEN_O, Estructura_BBDD.CODPRODUCTO_O, values);
                    precioProducto=0;
                    Toast.makeText(getContext(), "Se registro el Producto " + descripcion, Toast.LENGTH_SHORT).show();

                    String str = String.format("%,d", precio);
                    addProducts.add("PROD:  "+codProd.getText().toString() + "   CANT: " + cantidad.getText().toString() + " - $" + str);

                    limpiarCampos();
                }
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(getContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public Boolean buscar(){
        String[] parametro = {codProd.getText().toString()};
        //SELECT descripcion FROM productos WHERE codigo = ?
        String query ="SELECT " + Estructura_BBDD.DESCRIPCION_P + Estructura_BBDD.COMMA_SEP +
                Estructura_BBDD.PRECIO_BASE_P + Estructura_BBDD.COMMA_SEP +
                Estructura_BBDD.IVA_P + Estructura_BBDD.COMMA_SEP +
                Estructura_BBDD.EMBALAJE_P +
                " FROM " + Estructura_BBDD.TABLE_PRODUCTO + " WHERE " +
                Estructura_BBDD.CODIGOPRODUCTO_P + " = ?";
        try {

            conn = new DataBaseSQLHelper(getContext());
            SQLiteDatabase db = conn.getReadableDatabase();

            Cursor cursor = db.rawQuery(query,parametro);
            cursor.moveToFirst();
            if(!codProd.getText().toString().equals("")){

                if(cursor.getCount() >0){
                    descripcion = cursor.getString(0);
                    precioProducto = Integer.parseInt(cursor.getString(1));
                    iva = cursor.getInt(2);
                    embalaje = cursor.getInt(3);
                    return true;
                }else{
                    return false;
                }
            }

            db.close();
        }catch (Exception e){
            Toast.makeText(getContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean ExistProductInOrden(String cod, String cliente){
        try{
            conn = new DataBaseSQLHelper(getContext());
            SQLiteDatabase db = conn.getReadableDatabase();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            String systemDate = dateFormat.format(date);

            String[] parametro = {cod, cliente, systemDate};
            String sql ="SELECT cliente, codProducto FROM orden WHERE  codProducto=? AND cliente=? AND fecha=?";

            Cursor cursor = db.rawQuery(sql,parametro);
            if (cursor.moveToFirst()){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            return  false;
        }
    }

    private void limpiarCampos() {
        codProd.setText("");
        cantidad.setText("");
    }

    }
