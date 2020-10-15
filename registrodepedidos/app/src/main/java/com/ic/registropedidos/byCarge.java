package com.ic.registropedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Entidades.Cliente;
import Entidades.Pedido;
import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class byCarge extends AppCompatActivity {

    DataBaseSQLHelper conn;
    static List<String> listaCarge;
    ListView listaProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_carge);

        //CODIGO BANNER
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest2);
        //FIN CODIGO BANNER

        listaCarge = new ArrayList<>();
        listaProducto = findViewById(R.id.listCarge );
        agregarCarge();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaCarge);
        listaProducto.setAdapter(adapter);

    }


    public List<Pedido> consultarProductosACargar() {
        List<Pedido> pedido = new ArrayList<>();
        try {

            conn = new DataBaseSQLHelper(this);
            SQLiteDatabase db = conn.getReadableDatabase();

           /* SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            String systemDate = dateFormat.format(date);
            String[] parametro = {systemDate};*/

            //sql por cargar select pr.descripcion, ord.codProducto, SUM(cantidad) as total  from orden ord
            // inner join productos pr on  pr.codigo=ord.codProducto group by ord.codProducto;
            String sql = "SELECT codProducto, SUM(cantidad) as total ,"+
                          "por_paquete FROM orden  GROUP BY codProducto ORDER BY total " ;

            Cursor cursor = db.rawQuery(sql, null);

            if(cursor.getCount() >0) {
                while (cursor.moveToNext()) {
                    Pedido ped = new Pedido();

                    ped.setCodigoProd(cursor.getString(0));
                    ped.setCantidad(cursor.getInt(1));
                    ped.setPorPaquete(cursor.getInt(2));
                    pedido.add(ped);
                }
            }
            db.close();

        } catch (Exception e) {
            Toast.makeText(byCarge.this, "Ha ocurrido un error al consultar " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        return pedido;
    }

    public void agregarCarge() {
        try {

            conn = new DataBaseSQLHelper(this);
            SQLiteDatabase db = conn.getReadableDatabase();
            String desc="";
            int embalaje=0;
            List<Pedido> res = consultarProductosACargar();
            //listaCarge.add("Producto  -  Cantidad   -  Descripcion");
            if (!res.isEmpty()) {
                for (Pedido obj : res) {
                    String[] parametro = {obj.getCodigoProd()};
                    String sql ="SELECT descripcion, embalaje FROM productos WHERE codigo =?";
                    Cursor cursor = db.rawQuery(sql, parametro);
                    if(cursor.moveToFirst()){
                      desc = cursor.getString(0);
                      embalaje= cursor.getInt(1);
                    }

                    if(obj.getPorPaquete()==1){

                        int cantidad= obj.getCantidad()* embalaje;
                        listaCarge.add(" "+ cantidad + "  -  " + obj.getCodigoProd() + "  - " + desc);

                    }else{
                        listaCarge.add(" "+ obj.getCantidad() + "  -  " + obj.getCodigoProd() + "  - " + desc);
                    }

                }
            }else{
                listaCarge.add("No hay pedidos guardados ");
            }

        } catch (Exception e) {
            Toast.makeText(byCarge.this, "Ha ocurrido un error al cargar" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        finish();
    }
}
