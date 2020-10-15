package com.ic.registropedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import Entidades.Producto;
import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class SearchOrders extends AppCompatActivity {


    private  DataBaseSQLHelper conn;
    List<String> listaClientes = new ArrayList<>();
    List<Cliente> list = new ArrayList<>();
    ListView listaClientesPedido;
    TextView totalView;
    boolean isExito;
    int total, precioProducto, isPorPaquete, embalaje;
    private boolean exisRegistros;
    public static ArrayAdapter adapter= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_orders);

        //CODIGO BANNER
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest2);
        //FIN CODIGO BANNER

        exisRegistros = false;
        listaClientesPedido = findViewById(R.id.listClientes );
        totalView =  findViewById(R.id.lblTotal);
        agregarClientes();
        total=0;
        isExito =false;
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaClientes);
        listaClientesPedido.setAdapter(adapter);

        if(exisRegistros){
            listaClientesPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    Cliente cliente = list.get(pos);

                    Intent intent = new Intent(SearchOrders.this, ProductDetails.class );
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("nombre", cliente);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            listaClientesPedido.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long l) {
                    Cliente cliente = list.get(pos);
                    EliminarPedidos(true, cliente, pos);
                    return true;
                }
            });
        }
    }

    public void onBackPressed() {
        finish();
    }

    public List<Cliente> consultaClientes(){

        try{

            conn = new DataBaseSQLHelper(getApplicationContext());
            SQLiteDatabase db = conn.getReadableDatabase();

           // SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
           // Date date = new Date();
            //String systemDate = dateFormat.format(date);
            //String[] parametro = {systemDate};

            String sql="SELECT DISTINCT " +Estructura_BBDD.CLIENTE_O +" FROM " + Estructura_BBDD.TABLE_ORDEN_O +
                        "ORDER BY "+ Estructura_BBDD.CLIENTE_O + " ASC";


            Cursor cursor = db.rawQuery(sql,null);

            while(cursor.moveToNext()){
              Cliente cli = new Cliente();
              cli.setNombre(cursor.getString(0));
              cli.setEmblaje(embalaje);
              list.add(cli);
            }

        }catch (Exception e){
            Toast.makeText(SearchOrders.this, "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
        }
        return  list;
    }

    public void agregarClientes(){

            try{
                List<Cliente>  cliente = consultaClientes();

                if(!cliente.isEmpty()) {
                    for (Cliente res : cliente) {
                        listaClientes.add("Cliente: " + res.getNombre().toUpperCase());
                    }
                    consultaPedido();
                    String str = String.format("%,d", total);
                    totalView.setText("Total " + str);
                    exisRegistros=true;
                }else{
                    listaClientes.add("No hay pedidos guardados ");
                    totalView.setText("Total " + total);
                    exisRegistros=false;
                }
            }catch (Exception e){
            Toast.makeText(SearchOrders.this, "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
            }
    }

    public void consultaPedido(){
        try{

            conn = new DataBaseSQLHelper(getApplicationContext());
            SQLiteDatabase db = conn.getReadableDatabase();

            //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
           // Date date = new Date();
           // String systemDate = dateFormat.format(date);
            //String[] parametro = {systemDate};

            String sql="SELECT " + Estructura_BBDD.TOTALPRODUCTO_O+ " FROM " + Estructura_BBDD.TABLE_ORDEN_O;

            Cursor cursor = db.rawQuery(sql,null);

            while(cursor.moveToNext()){
                total += cursor.getInt(0);
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(SearchOrders.this, "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void delete(View view){
        EliminarPedidos(false,null,0);
    }

    public void EliminarPedidos(final boolean isByClient, final Cliente cliente, final int posicion){

        try{
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("Desea borrar los pedidos registrados");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    conn = new DataBaseSQLHelper(getApplicationContext());
                    SQLiteDatabase db = conn.getWritableDatabase();

                    if(isByClient){
                        String[] client = {cliente.getNombre()};
                        db.delete(Estructura_BBDD.TABLE_ORDEN_O,"cliente =?", client);
                        db.close();
                        listaClientes.remove(posicion);
                        adapter.notifyDataSetChanged();
                        actualizarTabla();
                        Toast.makeText(SearchOrders.this, "Se han eliminado El pedido de " + cliente.getNombre(),
                                Toast.LENGTH_SHORT).show();
                    }else{
                        db.delete(Estructura_BBDD.TABLE_ORDEN_O,null,null);
                        db.close();
                        Toast.makeText(SearchOrders.this, "Se han eliminado los pedidos. ",
                                Toast.LENGTH_SHORT).show();

                        ProductDetails.listaProductos.clear();
                        listaClientes.clear();
                        Intent search = new Intent(getApplicationContext(), MainActivityBase.class);
                        startActivity(search);
                        finish();
                    }
                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                }
            });
            dialogo1.show();


        }catch (Exception e){
            Toast.makeText(SearchOrders.this, "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizar(View view){

            actualizarTabla();
            if (isExito) {
                Toast.makeText(SearchOrders.this, "Se han actualizado los pedidos.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SearchOrders.this, "No se han podido actualizar los pedidos.",
                        Toast.LENGTH_SHORT).show();
            }

    }

    public List<Pedido> obtenerProductosOrden(){
        List<Pedido> listPedidos = new ArrayList<>();
        try{
            List<Cliente> clientes = list;
            for (Cliente var  : clientes){
                List<Pedido> pedidos = consultaPedido(var.getNombre());
                    if(!pedidos.isEmpty() || pedidos != null){
                        for (Pedido ped : pedidos){
                            Pedido pedido = new Pedido();

                            pedido.setCliente(ped.getCliente());
                            pedido.setCodigoProd(ped.getCodigoProd());
                            pedido.setFecha(ped.getFecha());
                            pedido.setCantidad(ped.getCantidad());
                            pedido.setEmbalaje(ped.getEmbalaje());
                            pedido.setPorPaquete(ped.getPorPaquete());
                            pedido.setTotalProducto(ped.getTotalProducto());
                            listPedidos.add(pedido);
                        }
                    }
            }
        }catch (Exception e){
            Toast.makeText(SearchOrders.this, "Ha ocurrido un error al obtenerProductosOrden " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        return listPedidos;
    }

    public List<Pedido> consultaPedido(String cliente){
        List<Pedido> list = new ArrayList<>();

        try{
            conn = new DataBaseSQLHelper(getApplicationContext());
            SQLiteDatabase db = conn.getReadableDatabase();
            String cli = cliente;
           /* SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            String systemDate = dateFormat.format(date);*/
            String[] parametro = {cli};

            String sql="SELECT * FROM " + Estructura_BBDD.TABLE_ORDEN_O + " WHERE "+  Estructura_BBDD.CLIENTE_O + " = ?" ;

            Cursor cursor = db.rawQuery(sql,parametro);

            while(cursor.moveToNext()){
                Pedido pedido = new Pedido();

                pedido.setCliente(cursor.getString(1));
                pedido.setCodigoProd(cursor.getString(2));
                pedido.setFecha(cursor.getString(3));
                pedido.setCantidad(cursor.getInt(4));
                pedido.setTotalProducto(cursor.getInt(5));
                pedido.setPorPaquete(cursor.getInt(7));
                list.add(pedido);
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(SearchOrders.this, "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
        }
        return  list;
    }

    public List<String> consultarProductoPorCod(String codProducto){
        List<String> list = new ArrayList<>();
        try{
            conn = new DataBaseSQLHelper(getApplicationContext());
            SQLiteDatabase db = conn.getReadableDatabase();
            String[] parametro = {codProducto};

            String query ="SELECT " + Estructura_BBDD.PRECIO_BASE_P + ", " +Estructura_BBDD.IVA_P +","+
                           Estructura_BBDD.EMBALAJE_P +
                           " FROM " + Estructura_BBDD.TABLE_PRODUCTO + " WHERE " +
                           Estructura_BBDD.CODIGOPRODUCTO_P + " = ?";

            Cursor cursor = db.rawQuery(query,parametro);
            cursor.moveToFirst();
            if(cursor.getCount() >0){
                list.add(cursor.getString(0));
                list.add(cursor.getString(1));
                list.add(cursor.getString(2));
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(SearchOrders.this, "Ha ocurrido un error al consultarProductoPorCod ",
                    Toast.LENGTH_SHORT).show();
        }

        return  list;
    }

    public void actualizarTabla(){
        try {
            total=0;
            conn = new DataBaseSQLHelper(getApplicationContext());
            SQLiteDatabase db = conn.getWritableDatabase();
            int precio;
            List<Pedido> listOrders = obtenerProductosOrden();
            if(!listOrders.isEmpty()) {
                for (Pedido ped : listOrders) {
                    List<String> prod = consultarProductoPorCod(ped.getCodigoProd());
                     if(!prod.isEmpty()) {

                        String parametro[] = new String[]{ped.getCodigoProd(), ped.getCliente()};
                        ContentValues values = new ContentValues();

                        precioProducto = Integer.parseInt(prod.get(0));
                        int iva = Integer.parseInt(prod.get(1));
                        int calcularDescuento = (precioProducto * 10) / 100;
                        precioProducto = precioProducto - calcularDescuento;
                        int calculaIVA = (precioProducto * iva) / 100;
                        precioProducto = precioProducto + calculaIVA;
                        if(ped.getPorPaquete()==1){
                             precio = precioProducto * (ped.getCantidad()*Integer.parseInt(prod.get(2)));
                        }else{
                             precio = precioProducto * ped.getCantidad();
                        }
                        total += precio;
                        values.put(Estructura_BBDD.TOTALPRODUCTO_O, precio);
                        values.put(Estructura_BBDD.PRECIOPRODUCTO_O, precioProducto);
                        db.update(Estructura_BBDD.TABLE_ORDEN_O, values, Estructura_BBDD.CODPRODUCTO_O + " =? AND " +
                                  Estructura_BBDD.CLIENTE_O +  "=?", parametro);
                    }
                }
                String str = String.format("%,d", total);
                totalView.setText("Total " + str);
                isExito=true;
                db.close();
            }else{
                isExito=false;
                db.close();
            }
        }catch (Exception e){
            Toast.makeText(SearchOrders.this, "Ha ocurrido un error al actualizarTabla " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }


}



