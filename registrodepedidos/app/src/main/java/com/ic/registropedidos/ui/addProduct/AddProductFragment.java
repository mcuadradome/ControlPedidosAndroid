package com.ic.registropedidos.ui.addProduct;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ic.registropedidos.R;

import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class AddProductFragment extends Fragment {

    DataBaseSQLHelper conn;
    EditText codProd, descripcion, precioBase, iva;
    String[] embalaje ={"1","6","8","12","24","40"};
    private Spinner spin;
    View root;
    private Button btnIngresar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         root = inflater.inflate(R.layout.fragment_add_product, container, false);


        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spin = root.findViewById(R.id.spinSelectEmbaje);
        // spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        btnIngresar= root.findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ingresar();
            }
        });

        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,embalaje);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        codProd =  root.findViewById(R.id.txtCodProd);
        descripcion = root.findViewById(R.id.txtDesc);
        precioBase = root.findViewById(R.id.txtprecio);
        iva= root.findViewById(R.id.txtIva);

        return root;
    }


    public void ingresar(){
        try{
            conn = new DataBaseSQLHelper(getContext());
            SQLiteDatabase db= conn.getWritableDatabase();

            //obtiene los valores para insertarlos en bd

            if(codProd.getText().toString().equals("") || precioBase.getText().toString().equals("") || iva.getText().toString().equals("") ){
                Toast.makeText(getContext(), "No pueden haber datos vacios.", Toast.LENGTH_SHORT).show();
            }else{
                boolean existPeoduct = existProduct();

                if (existPeoduct){
                    Toast.makeText(getContext(), "El producto con codigo "+codProd.getText()+ " ya existe.", Toast.LENGTH_SHORT).show();
                }else{
                    ContentValues values = new ContentValues();
                    values.put(Estructura_BBDD.CODIGOPRODUCTO_P, codProd.getText().toString());
                    values.put(Estructura_BBDD.DESCRIPCION_P, descripcion.getText().toString().toUpperCase());
                    values.put(Estructura_BBDD.PRECIO_BASE_P, Integer.parseInt(precioBase.getText().toString()));
                    int embalajeSelect = Integer.parseInt(spin.getSelectedItem().toString());
                    values.put(Estructura_BBDD.EMBALAJE_P, embalajeSelect);
                    values.put(Estructura_BBDD.IVA_P, Integer.parseInt(iva.getText().toString()));

                    Long idResul = db.insert(Estructura_BBDD.TABLE_PRODUCTO, Estructura_BBDD.CODIGOPRODUCTO_P, values);
                    limpiarCampos();
                    Toast.makeText(getContext(), "Se registro el producto "+ idResul + " - " + codProd.getText(), Toast.LENGTH_SHORT).show();
                }

            }

            db.close();

        }catch (Exception e){
            Toast.makeText(getContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean existProduct(){
        try{
            conn = new DataBaseSQLHelper(getContext());
            SQLiteDatabase db= conn.getReadableDatabase();

            String[] parametro = {codProd.getText().toString()};
            String sql="SELECT * FROM "+ Estructura_BBDD.TABLE_PRODUCTO + " WHERE "+ Estructura_BBDD.CODIGOPRODUCTO_P + " =?";

            Cursor cursor = db.rawQuery(sql,parametro);
            cursor.moveToFirst();

            if(cursor.getCount() >0) {
                db.close();
                return true;
            }else{
                db.close();
                return false;
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void limpiarCampos(){
        codProd.setText("");
        descripcion.setText("");
        precioBase.setText("");
        iva.setText("");
    }
}