package com.ic.registropedidos;

import androidx.appcompat.app.AppCompatActivity;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Entidades.Cliente;
import Entidades.Pedido;
import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class ProductDetails extends AppCompatActivity {

    DataBaseSQLHelper conn;
    ListView listaDetallesPedido;
    public static List<String> listaProductos;
    List<Pedido> ordenPedido = new ArrayList<>();
    List<Pedido> list = new ArrayList<>();
    TextView totalPedido, titulo;
    int total, embalaje;
    Cliente cliente;
    String clienteAdd="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        listaDetallesPedido = findViewById(R.id.listProduct );
        totalPedido =  findViewById(R.id.lblTotalDetails);
        listaProductos = new ArrayList<>();
        titulo = findViewById(R.id.txtTitleDetails);
        Bundle objeto = getIntent().getExtras();

        //Envia como objeto el nombre del cliente a la activity ProductDetails

        if (objeto != null){
            cliente =(Cliente) objeto.getSerializable("nombre");
            clienteAdd= cliente.getNombre();
            embalaje=cliente.getEmblaje();
            agregarPedido(cliente.getNombre());
            titulo.setText("Detalles del pedido de:\n" + cliente.getNombre().toUpperCase());
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaProductos);
            listaDetallesPedido.setAdapter(adapter);
        }else{
            Toast.makeText(ProductDetails.this, "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
        }

        // envia como objeto el codigo del producto a la activity EditOrden
        listaDetallesPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //consultaPedido(clienteAdd);
                Pedido orden = ordenPedido.get(pos);

                Intent intent = new Intent(ProductDetails.this, EditOrden.class );
                Bundle bundle = new Bundle();
                bundle.putSerializable("codigo", orden);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public void onBackPressed() {
        finish();
    }


    public List<Pedido> consultaPedido(String cliente){


        try{

            conn = new DataBaseSQLHelper(getApplicationContext());
            SQLiteDatabase db = conn.getReadableDatabase();
            String cli = cliente;
           // SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            //Date date = new Date();
            //String systemDate = dateFormat.format(date);
            String[] parametro = {cli};

            String sql="SELECT * FROM " + Estructura_BBDD.TABLE_ORDEN_O + " WHERE "+
                        Estructura_BBDD.CLIENTE_O + " = ? ORDER BY codProducto" ;

            Cursor cursor = db.rawQuery(sql,parametro);

            while(cursor.moveToNext()){
               Pedido pedido = new Pedido();
                pedido.setId(cursor.getInt(0));
                pedido.setCliente(cursor.getString(1));
                pedido.setCodigoProd(cursor.getString(2));
                pedido.setFecha(cursor.getString(3));
                pedido.setCantidad(cursor.getInt(4));
                pedido.setTotalProducto(cursor.getInt(5));
                pedido.setPrecioProducto(cursor.getInt(6));
                pedido.setPorPaquete(cursor.getInt(7));
                pedido.setEmbalaje(embalaje);
                list.add(pedido);
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(ProductDetails.this, "Ha ocurrido un error "+ e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        return  list;
    }

    public boolean agregarPedido(String cliente){

        listaProductos.clear();
        try{
            List<Pedido>  pedido = consultaPedido(cliente);
            //listaProductos.add(" Codigo - Cantidad - Total Venta - Precio/U");
            if(!pedido.isEmpty()) {
                for (Pedido res : pedido) {
                    Pedido ped = new Pedido();
                    ped.setCliente(res.getCliente());
                    ped.setCodigoProd(res.getCodigoProd());
                    ped.setCantidad(res.getCantidad());
                    ped.setTotalProducto(res.getTotalProducto());
                    ped.setPrecioProducto(res.getPrecioProducto());
                    ped.setPorPaquete(res.getPorPaquete());
                    total += ped.getTotalProducto();
                    ordenPedido.add(ped);
                    if(ped.getPorPaquete()==1){
                        listaProductos.add(" * " + ped.getCantidad() + "  -  "  +
                                ped.getCodigoProd()  + "  -   "  + ped.getTotalProducto()
                                + "   -   " + ped.getPrecioProducto());
                    }else{
                        listaProductos.add("  " + ped.getCantidad() + "  -  "  +
                                ped.getCodigoProd()  + "  -   "  + ped.getTotalProducto()
                                + "   -   " + ped.getPrecioProducto());
                    }


                    String str = String.format("%,d", total);
                    totalPedido.setText("Total: "+str);
                }
                return  true;
            }else{
                listaProductos.add("No hay pedidos guardados ");
                return true;
            }
        }catch (Exception e){
            Toast.makeText(ProductDetails.this, "Ha surgido un error",
                    Toast.LENGTH_SHORT).show();
            return  false;
        }
    }

}
