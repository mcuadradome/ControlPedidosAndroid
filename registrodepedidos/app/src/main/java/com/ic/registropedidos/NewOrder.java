package com.ic.registropedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class NewOrder extends AppCompatActivity {

    DataBaseSQLHelper conn;
    EditText cliente, codProd, cantidad;
    ListView listProduct;
    Switch isPorPaquete;
    String descripcion;
    int precioProducto;
    int iva, embalaje;
    public ArrayAdapter adapter = null;
    List<String> listaProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        //CODIGO BANNER
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest2);
        //FIN CODIGO BANNER

        cliente = findViewById(R.id.txtClient);
        codProd = findViewById(R.id.nCod);
        cantidad = findViewById(R.id.nCantidad);
        isPorPaquete = findViewById(R.id.switchPaquete);
        isPorPaquete.setChecked(true);
        listProduct = findViewById(R.id.list_products);

        listaProducts = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, listaProducts);
        listProduct.setAdapter(adapter);

        listaProducts.add("prueba");

        codProd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    boolean existeProduct = buscar();
                    if(existeProduct){
                        SaveOrder();
                    }else{
                        Toast.makeText(getApplicationContext(), "No se puede registrar pedido con codigo " + codProd.getText().toString(), Toast.LENGTH_SHORT).show();
                        codProd.setText("");
                    }
                }
            }
        });
        
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

            conn = new DataBaseSQLHelper(getApplicationContext());
            SQLiteDatabase db = conn.getReadableDatabase();

            Cursor cursor = db.rawQuery(query,parametro);
            cursor.moveToFirst();
            if(!codProd.getText().toString().equals("")){

                if(cursor.getCount() >0){
                    descripcion = cursor.getString(0);
                    precioProducto = Integer.parseInt(cursor.getString(1));
                    iva = cursor.getInt(2);
                    embalaje = cursor.getInt(3);
                    return  true;
                }else{
                    Toast.makeText(NewOrder.this, "No existe el producto ",
                            Toast.LENGTH_SHORT).show();
                    codProd.setText("");
                    return false;
                }
            }

            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    public void SaveOrder(){
        try{

            conn = new DataBaseSQLHelper(getApplicationContext());
            SQLiteDatabase db= conn.getWritableDatabase();
            int cantidadPaquete;
            int precio;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            String systemDate = dateFormat.format(date);

            if(cliente.getText().toString().equals("") || codProd.getText().toString().equals("") || cantidad.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), "Todos los datos deben ser validos. ", Toast.LENGTH_SHORT).show();
            }else {

                if(ExistProduct(codProd.getText().toString(), cliente.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Este producto ya ha sido agregado al pedido ", Toast.LENGTH_SHORT).show();
                }else {

                        ContentValues values = new ContentValues();
                        values.put(Estructura_BBDD.CLIENTE_O, cliente.getText().toString().toLowerCase().trim());
                        values.put(Estructura_BBDD.CODPRODUCTO_O, codProd.getText().toString());
                        values.put(Estructura_BBDD.FECHA_O, systemDate);
                        values.put(Estructura_BBDD.CANTIDAD_O, cantidad.getText().toString());

                        int calculateDescent = (precioProducto * 10) / 100;
                        precioProducto = precioProducto - calculateDescent;
                        int calculateIVA = (precioProducto * iva) / 100;
                        precioProducto = Math.round(precioProducto + calculateIVA) ;

                        if(isPorPaquete.isChecked()){
                            cantidadPaquete= Integer.parseInt(cantidad.getText().toString()) * embalaje;
                            values.put(Estructura_BBDD.PORPAQUETE_O, 1);
                        }else {
                            cantidadPaquete= Integer.parseInt(cantidad.getText().toString());
                            values.put(Estructura_BBDD.PORPAQUETE_O, 0);
                        }

                        precio = Math.round(precioProducto * cantidadPaquete);
                        values.put(Estructura_BBDD.TOTALPRODUCTO_O, precio);
                        values.put(Estructura_BBDD.PRECIOPRODUCTO_O, precioProducto);

                        db.insert(Estructura_BBDD.TABLE_ORDEN_O, Estructura_BBDD.CODPRODUCTO_O, values);
                        precioProducto=0;
                        listaProducts.add(codProd.getText().toString()+ ":" + descripcion);
                        Toast.makeText(getApplicationContext(), "Se registro el producto " + descripcion, Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cantidad.requestFocus();

                }
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public boolean ExistProduct(String cod, String cliente){
        try{
            conn = new DataBaseSQLHelper(getApplicationContext());
            SQLiteDatabase db = conn.getReadableDatabase();

            String[] parametro = {cod, cliente};
            String sql ="SELECT cliente, codProducto FROM orden WHERE  codProducto= ? AND cliente= ?";

            Cursor cursor = db.rawQuery(sql,parametro);
            if (cursor.moveToFirst()){
              return true;
            }else{
               return false;
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            return  false;
        }
    }

    private void limpiarCampos() {
        codProd.setText("");
        cantidad.setText("");
    }

    public void onBackPressed() {
        finish();
    }

}
