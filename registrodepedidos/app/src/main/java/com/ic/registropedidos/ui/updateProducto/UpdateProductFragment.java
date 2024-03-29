package com.ic.registropedidos.ui.updateProducto;

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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ic.registropedidos.R;

import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class UpdateProductFragment extends Fragment {

    EditText codProd, descripcion, precioBase, iva;
    DataBaseSQLHelper conn;
    String[] embalaje ={"1","6","8","12","24","40"};
    private Spinner spin;
    View root;
    Button btnActualizar;
    private ArrayAdapter aa;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         root = inflater.inflate(R.layout.fragment_update_product, container, false);



        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spin = root.findViewById(R.id.spinSelectEmbaje);
        // spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        btnActualizar= root.findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Actualizar();
            }
        });

        aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,embalaje);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        codProd = root.findViewById(R.id.txtCod);
        descripcion = root.findViewById(R.id.txtDescripcion);
        precioBase = root.findViewById(R.id.txtPrecio_base);
        iva = root.findViewById(R.id.txtIvaUp);

        codProd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    BuscarProducto();
                }
            }
        });

        return root;
    }


    public void BuscarProducto (){
        String[] parametro = {codProd.getText().toString()};
        //SELECT descripcion FROM productos WHERE codigo = ?
        String query ="SELECT * FROM " + Estructura_BBDD.TABLE_PRODUCTO + " WHERE " +
                Estructura_BBDD.CODIGOPRODUCTO_P + " = ?";
        try {

            conn = new DataBaseSQLHelper(getContext());
            SQLiteDatabase db = conn.getReadableDatabase();

            Cursor cursor = db.rawQuery(query,parametro);
            cursor.moveToFirst();

            if(cursor.getCount() >0){
                descripcion.setText(cursor.getString(1));
                precioBase.setText(cursor.getString(2));
                int posicion= consultaPosicionEmbalaje(cursor.getInt(3));
                spin.setSelection(posicion);
                iva.setText(cursor.getString(4));

            }else{
                Toast.makeText(getContext(), "No existe el producto ",
                        Toast.LENGTH_SHORT).show();
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(getContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public int consultaPosicionEmbalaje(int valor){
        int posicion=0;

        switch (valor){
            case 1:
                posicion=0;
                break;
            case 6:
                posicion=1;
                break;
            case 8:
                posicion=2;
                break;
            case 12:
                posicion=3;
                break;
            case 24:
                posicion=4;
                break;
            case 40:
                posicion=5;
                break;
        }
        return posicion;
    }

    public void  Actualizar(){
        try {

            conn = new DataBaseSQLHelper(getContext());
            SQLiteDatabase db = conn.getWritableDatabase();
            if(codProd.getText().toString().equals("") || descripcion.getText().toString().equals("") ||
                iva.getText().toString().equals("") || spin.getSelectedItem().toString().equals("0")){
                Toast.makeText(getContext(), "Todos los datos deben ser validos. ", Toast.LENGTH_SHORT).show();
            }else{
                String[] parametro = {codProd.getText().toString()};

                ContentValues values = new ContentValues();
                values.put(Estructura_BBDD.DESCRIPCION_P,descripcion.getText().toString().toUpperCase());
                values.put(Estructura_BBDD.PRECIO_BASE_P,precioBase.getText().toString());
                int embalajeSelect = Integer.parseInt(spin.getSelectedItem().toString());
                values.put(Estructura_BBDD.EMBALAJE_P, embalajeSelect);
                values.put(Estructura_BBDD.IVA_P,iva.getText().toString());

                int res= db.update(Estructura_BBDD.TABLE_PRODUCTO, values,Estructura_BBDD.CODIGOPRODUCTO_P + " =? ", parametro);

                Toast.makeText(getContext(), "Se actualizo el producto " + res, Toast.LENGTH_SHORT).show();
                db.close();
                limpiarCampos();
                codProd.requestFocus();

            }


        }catch (Exception e){
            Toast.makeText(getContext(), "Ha surgido un error al guardar"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        codProd.setText("");
        descripcion.setText("");
        precioBase.setText("");
        iva.setText("");
        spin.setSelection(0);
    }
}