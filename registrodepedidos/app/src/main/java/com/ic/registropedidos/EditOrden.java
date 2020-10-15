package com.ic.registropedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.sql.SQLClientInfoException;

import Entidades.Pedido;
import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class EditOrden extends AppCompatActivity {

    EditText descripcion;
    TextView cantidad;
    DataBaseSQLHelper conn;
    int precioProd;
    public static boolean isEdit;
    int iva, embalaje;
    boolean isExito;
    Pedido cod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_orden);

        //CODIGO BANNER
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest2);
        //FIN CODIGO BANNER

        descripcion =  findViewById(R.id.txtDesccripcionEdit );
        cantidad = findViewById(R.id.editNumber );
        precioProd=0;
        iva=0;
        isExito=false;
        Bundle obj = getIntent().getExtras();
        if(obj != null){
            cod= (Pedido) obj.getSerializable("codigo");
            BuscarProduco(cod.getCodigoProd());

        }else{
            Toast.makeText(EditOrden.this, "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        finish();
    }

    private void BuscarProduco(String codigo) {

        try{

            conn= new DataBaseSQLHelper(getApplicationContext());
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
            Toast.makeText(EditOrden.this, "Ha ocurrido un error BuscarProduco: " +e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void Actualizar(View view){
        if (cantidad.getText().toString().equals("") || Integer.parseInt(cantidad.getText().toString()) == cod.getCantidad()) {
            Toast.makeText(getApplicationContext(), "Debe colocar una cantidad", Toast.LENGTH_SHORT).show();
        }else {
            BuscarPedido();
            if(isExito){
                Intent order = new Intent(getBaseContext(), SearchOrders.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);;
                startActivity(order);
                finish();
            }
        }
    }

    private void BuscarPedido() {

        try{

            conn= new DataBaseSQLHelper(getApplicationContext());
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
                     precio = Math.round(precioProd * (Integer.parseInt(cantidad.getText().toString())* embalaje));
                }else{
                     precio = Math.round(precioProd * Integer.parseInt(cantidad.getText().toString()));
                }


                values.put(Estructura_BBDD.TOTALPRODUCTO_O, (int) precio);
                int res = db.update(Estructura_BBDD.TABLE_ORDEN_O, values, Estructura_BBDD.CODPRODUCTO_O + " =?"
                        + " AND " + Estructura_BBDD.CLIENTE_O + " =?", parametro);

                Toast.makeText(getApplicationContext(), "Se actualizo la cantidad de producto. " + res, Toast.LENGTH_SHORT).show();
                isExito=true;

            db.close();
        }catch (Exception e)  {
            Toast.makeText(EditOrden.this, "Ha ocurrido un error BuscarPedido " +e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void Delete(View view){

        try{
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("Desea eliminar este producto del pedido actual");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                conn= new DataBaseSQLHelper(getApplicationContext());
                SQLiteDatabase db=conn.getWritableDatabase();
                String[] parametro = {cod.getCodigoProd(), cod.getCliente()};

                db.delete(Estructura_BBDD.TABLE_ORDEN_O, Estructura_BBDD.CODPRODUCTO_O + " =?"
                        + " AND " + Estructura_BBDD.CLIENTE_O + " =?", parametro);
                db.close();
                Toast.makeText(EditOrden.this, "Se ha eliminado el producto: "+ cod.getCodigoProd(),
                        Toast.LENGTH_SHORT).show();


                Intent search =new Intent(getBaseContext(), SearchOrders.class);
                startActivity(search);
                finish();

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                }
            });
            dialogo1.show();


        }catch (Exception e){
            Toast.makeText(EditOrden.this, "Ha ocurrido un error Delete " +e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }

}
