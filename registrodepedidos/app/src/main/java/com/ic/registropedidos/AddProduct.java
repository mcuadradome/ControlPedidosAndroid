package com.ic.registropedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class AddProduct extends AppCompatActivity {

    DataBaseSQLHelper conn;
    EditText codProd, descripcion, precioBase, iva;
    String[] embalaje ={"1","6","8","12","24","40"};
    private Spinner spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //CODIGO BANNER
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest2);
        //FIN CODIGO BANNER

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spin = findViewById(R.id.spinSelectEmbaje);
       // spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,embalaje);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        codProd =  findViewById(R.id.txtCodProd);
           descripcion = findViewById(R.id.txtDesc);
           precioBase = findViewById(R.id.txtprecio);
           iva= findViewById(R.id.txtIva);

    }


    public void onBackPressed() {
        finish();
    }

    public void onClick(View view){
        try{
            conn = new DataBaseSQLHelper(this);
            SQLiteDatabase db= conn.getWritableDatabase();

            //obtiene los valores para insertarlos en bd

            if(codProd.getText().toString().equals("") || precioBase.getText().toString().equals("") || iva.getText().toString().equals("") ){

                Toast.makeText(getApplicationContext(), "No pueden haber datos vacios.", Toast.LENGTH_SHORT).show();
            }else{
               boolean existPeoduct = existProduct();

                if (existPeoduct){
                    Toast.makeText(getApplicationContext(), "El producto con codigo "+codProd.getText()+ " ya existe.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Se registro el producto "+ idResul, Toast.LENGTH_SHORT).show();
                }

            }

            db.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean existProduct(){
        try{
            conn = new DataBaseSQLHelper(this);
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
            Toast.makeText(getApplicationContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
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
