package com.ic.registropedidos;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.util.ArrayList;
import java.util.List;

import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;


public class MainActivityBase extends Activity {

    DataBaseSQLHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_base);

        //CODIGO BANNER
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest2);
        //FIN CODIGO BANNER

        insertarProductos();
    }

    public void insertarProductos(){
        try{

            conn = new DataBaseSQLHelper(this);
            SQLiteDatabase db= conn.getWritableDatabase();
            List<String> datos = new ArrayList<>();
            boolean existData = existData();

            if(!existData){

                datos.add("'103', 'FOSFORO GRANDE', 2353, 1, 19");
                datos.add("'161', 'DELIMANI X 50GR',860, 12, 19");
                datos.add("'182', 'FOSFORO  X 35GR',745, 12, 19");
                datos.add("'193','TODORICO CRIOLLO X 55GR',1250,8,19");
                datos.add("'205','NATURAL SUPERGIGANTE X 350GR', 4901,1,19");
                datos.add("'223','PAPA POLLO 115GR',2101,1,19");
                datos.add("'222','PAPA NATURAL 115GR',2101,1,19");
                datos.add("'224','PAPA PICANTE 115 GR ',2101,1,119");
                datos.add("'225','PAPA NATURAL 215GR',3179,1,19");
                datos.add("'226','PAPA POLLO 215GR',3179,1,19");
                datos.add("'305','POLLO SUPERGIGANTE 350GR',4901,1,19");
                datos.add("'350','TODORICO CRIOLLO 180GR',3350,1,19");
                datos.add("'396','MANI CON PASAS X 50GR',950,12,19");
                datos.add("'447','CHIRRICO CARAMELO X 50GR',797,25,19");
                datos.add("'475','DISPLAY DELIMANI X 50GR',15998,1,19");
                datos.add("'640','PAPA SUPER CROKANTE 115GR',2095,1,19");
                datos.add("'703','CHICHARRON GRANDE 50GR',2095,1,5");
                datos.add("'757','CHICHARRON X 10GR',665,24,5");
                datos.add("'760','TODORICO NAT X6 - 45GR',6930,1,19");
                datos.add("'744','TODORICO BBQ X6 - 45GR',6930,1,19");
                datos.add("'783','TODORICO NAT',8085,1,19");
                datos.add("'784','TODORICO BQQ',8085,1,19");
                datos.add("'792','TODORICO BBQ 160GR',2604,1,19");
                datos.add("'814','TROCITOS POLLO X 32GR',420,12,19");
                datos.add("'815','RIQUILLAS X 15GR',643,12,5");
                datos.add("'823','CHIRRICO NATURAL X 55GR',730,40,19");
                datos.add("'828','CHIRRICO X 10GR',200,12,19");
                datos.add("'833','TOCINETA X 12GR',420,8,19");
                datos.add("'855','CHIRRICO PIKANTICO 50GR',730,40,19");
                datos.add("'895','TODORICO NATURAL 45GR X 8',1155,8,19");
                datos.add("'896','TODORICO BBQ 45GR X8',1155,8,19");
                datos.add("'905','TODORICO NATURAL 160GR',2634,1,19");
                datos.add("'919','TODORICO NATURAL SUPERGIGANTE 250GR',3824,1,19");
                datos.add("'932','MIGA DE PAPA 1KL',7155,1,19");
                datos.add("'10022','PAPA SUPER CROKANTE 40GR',988,6,19");
                datos.add("'10039','DELIMANI CON SAL X 25GR',402,12,19");
                datos.add("'10067','SUPER CHEESE 48GR',700,8,19"); //verificar
                datos.add("'10081','TAJAMIEL 40GR',830,12,19");
                datos.add("'10083','TAJADITAS 40GR',830,12,19");
                datos.add("'10087','PAPA SUPER CROKANTE GALLINA 40GR',988,6,19");
                datos.add("'10089','PAPA SUPER CROKANTE CERDO 40GR',988,6,19");
                datos.add("'10122','PAPA SUPER CROKANTE GALLINA 115GR',2185,1,19");
                datos.add("'10123','PAPA SUPER CROKANTE CERDO 115GR',2185,1,19");
                datos.add("'10128','PAPA ONDITAS MEGA LIMON 32.5GR',938,12,19");
                datos.add("'10131','CHIRRICO LIMON X 45GR',797,35,19");
                datos.add("'10132','PAPA ONDITAS LIMON X 115GR',2101,1,19");
                datos.add("'10133','TODORICO LIMON X 45GR',1155,8,19");
                datos.add("'10138','PAPA SUPER PAKETE POLLO X 45GR',1070,6,19");
                datos.add("'10158','PAPA POLLO X 30GR',938,12,19");
                datos.add("'10159','PAPA NATURAL X 30GR',938,12,19");
                datos.add("'10160','PAPA PIKANTE X 30GR',938,12,19");
                datos.add("'10161','PAPA BAR-B-Q X 30GR',938,12,19");
                datos.add("'10162','PAPA LIMON X 30GR',938,12,19");
                datos.add("'10229','SUPER SURTIDA PRACTICA',3900,1,19");
                datos.add("'10268','TODORICO CRIOLLO AJI 55GR',1250,8,19");
                datos.add("'10432','TROCITO POLLO 120GR',1470,1,19");
                datos.add("'10594','CHIRRICOS MAXI QUESO 28GR',630,8,19"); //verificar
                datos.add("'10623','PAPA GOURMET SALSA ARRABIATA 40GR',989,6,19");
                datos.add("'10627','PAPA GOURMET SALSA VINAGRETA 40GR',989,6,19");
                datos.add("'10644','SUPER SURTIDA FANATICA',4800,1,19");
                datos.add("'10648','TODORICO BQQ 250GR',3824,1,19");
                datos.add("'10647','TODO RICO CRIOLLO AJI 180GR',3359,1,19");
                datos.add("'10625','PAPA GOURMET SALSA ARRABIATA 115GR',2185,1,19");
                datos.add("'10629','PAPA GOURMET SALSA VINAGRETA 115GR',2185,1,19");
                datos.add("'10707','TODORICO CRIOLLO CALENTADO',1250,8,19");
                datos.add("'10713','CHICARRON BBQ X 10GR',665,12,5");
                datos.add("'301','PAPA POLLO 25GR',7152,12,19");
                datos.add("'201','PAPA NATURAL 25GR',7152,12,19");
                datos.add("'401','PAPA PICANTE 25GR',7152,12,19");
                datos.add("'511','PAPA BQQ 25GR',7152,12,19");
                datos.add("'532','PAPA LIMON 25GR',7152,12,19");
                datos.add("'911','PAPA NATURAL LIBRA',6710,12,19");

               for (String var : datos){

                   db.execSQL("INSERT INTO " + Estructura_BBDD.TABLE_PRODUCTO + "( "+
                              Estructura_BBDD.CODIGOPRODUCTO_P +","+
                              Estructura_BBDD.DESCRIPCION_P+ ","+
                              Estructura_BBDD.PRECIO_BASE_P + ","+
                              Estructura_BBDD.EMBALAJE_P + ","+
                              Estructura_BBDD.IVA_P +")" +" VALUES (" + var + " );" );
               }
                db.close();
                Toast.makeText(getApplicationContext(), "Se han actualizado los productos ", Toast.LENGTH_SHORT).show();

            }
            db.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean existData(){
        try{
            conn = new DataBaseSQLHelper(this);
            SQLiteDatabase db= conn.getReadableDatabase();

            String sql ="SELECT * FROM "+ Estructura_BBDD.TABLE_PRODUCTO;

            Cursor cursor = db.rawQuery(sql,null);
            if(cursor.getCount() >0){
                db.close();
                return true;
            }else{
                db.close();
                return  false;
            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Ha surgido un error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public void Consultar(View view){
            Intent search = new Intent(getApplicationContext(), SearchOrders.class);
            startActivity(search);
    }


    public void NewOrder(View view) {
        Intent order = new Intent(getApplicationContext(), NewOrder.class);
        startActivity(order);
    }

    public void NewProducto(View view) {
        Intent prod = new Intent(getApplicationContext(), AddProduct.class);
        startActivity(prod);
    }

    public void UpdateProduct(View view) {
        Intent upProd = new Intent(getApplicationContext(), UpdateProduct.class);
        startActivity(upProd);
    }

    public void porCargar(View view) {
        Intent prod = new Intent(getApplicationContext(), byCarge.class);
        startActivity(prod);
    }

}
