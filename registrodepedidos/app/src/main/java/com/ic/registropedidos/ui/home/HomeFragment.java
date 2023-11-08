package com.ic.registropedidos.ui.home;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ic.registropedidos.ProductDetails;
import com.ic.registropedidos.R;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Entidades.Cliente;
import Entidades.Pedido;
import Model.DataBaseSQLHelper;
import Model.Estructura_BBDD;

public class HomeFragment extends Fragment  {


    private DataBaseSQLHelper conn;
    List<String> listaClientes = new ArrayList<>();
    List<Cliente> list = new ArrayList<>();
    ListView listaClientesPedido;
    TextView totalView;
    boolean isExito;
    int total, precioProducto, embalaje;
    private boolean exisRegistros;
    View root;
    ImageButton btnActualiza;
    Button btnElimina;
    public  ArrayAdapter adapter= null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try{
            root = inflater.inflate(R.layout.fragment_home, container, false);

            exisRegistros = false;
            listaClientesPedido = root.findViewById(R.id.listClientes );
            totalView =  root.findViewById(R.id.lblTotal);

            btnActualiza = root.findViewById(R.id.btnActualizar);
            btnActualiza.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    actualizar();
                }
            });

            btnElimina= root.findViewById(R.id.btnEliminar);
            btnElimina.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    delete();
                }
            });

            agregarClientes();
            total=0;
            isExito =false;
            adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listaClientes);
            listaClientesPedido.setAdapter(adapter);
           // ft =  getActivity().getSupportFragmentManager().beginTransaction();
            //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            if(exisRegistros){
                listaClientesPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                        Cliente cliente = list.get(pos);

                        Intent intent = new Intent(getContext(), ProductDetails.class );
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

        }catch (Exception e){
            Log.d(HomeFragment.class.getName(),"Error al crearFragment " + e.getMessage());
        }

        return root;
    }



    public List<Cliente> consultaClientes(){

        try{

            conn = new DataBaseSQLHelper(getContext());
            SQLiteDatabase db = conn.getReadableDatabase();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            // Date date = new Date();
            //String systemDate = dateFormat.format(date);
            //String[] parametro = {systemDate};

            String sql="SELECT DISTINCT " + Estructura_BBDD.CLIENTE_O +" FROM " + Estructura_BBDD.TABLE_ORDEN_O +
                    "ORDER BY "+ Estructura_BBDD.CLIENTE_O + " ASC";


            Cursor cursor = db.rawQuery(sql,null);

            while(cursor.moveToNext()){
                Cliente cli = new Cliente();
                cli.setNombre(cursor.getString(0));
                cli.setEmblaje(embalaje);
                list.add(cli);
            }

        }catch (Exception e){
            Toast.makeText(getContext(), "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
            Log.d(HomeFragment.class.getName(),"Error al consultar Clientes() " + e.getMessage());
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
            Toast.makeText(getContext(), "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
            Log.d(HomeFragment.class.getName(),"Error al consultar agregarClientes() " + e.getMessage());
        }
    }

    public void consultaPedido(){
        try{

            conn = new DataBaseSQLHelper(getContext());
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
            Toast.makeText(getContext(), "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
            Log.d(HomeFragment.class.getName(),"Error al consultar consultarPedidos() " + e.getMessage());
        }
    }

    public void delete(){

        EliminarPedidos(false,null,0);
    }

    public void EliminarPedidos(final boolean isByClient, final Cliente cliente, final int posicion){

        try{
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("Desea borrar los pedidos registrados");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    conn = new DataBaseSQLHelper(getContext());
                    SQLiteDatabase db = conn.getWritableDatabase();

                    if(isByClient){
                        String[] client = {cliente.getNombre()};
                        db.delete(Estructura_BBDD.TABLE_ORDEN_O,"cliente =?", client);
                        db.close();
                        listaClientes.remove(posicion);
                        adapter.notifyDataSetChanged();
                        actualizarTabla();
                        Toast.makeText(getContext(), "Se han eliminado El pedido de " + cliente.getNombre(),
                                Toast.LENGTH_SHORT).show();
                    }else{
                        db.delete(Estructura_BBDD.TABLE_ORDEN_O,null,null);
                        db.close();

                        if(ProductDetails.listaProductos != null){
                            ProductDetails.listaProductos.clear();
                        }

                        listaClientes.clear();
                        adapter.notifyDataSetChanged();
                        actualizarTabla();
                        totalView.setText("Total: 0");

                        Toast.makeText(getContext(), "Se han eliminado los pedidos. ",
                                Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizar(){

        actualizarTabla();
        if (isExito) {
            Toast.makeText(getContext(), "Se han actualizado los pedidos.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No se han podido actualizar los pedidos.",
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
            Toast.makeText(getContext(), "Ha ocurrido un error al obtenerProductosOrden " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            Log.d(HomeFragment.class.getName(),"Error al obtenerProductosOrden() " + e.getMessage());
        }
        return listPedidos;
    }

    public List<Pedido> consultaPedido(String cliente){
        List<Pedido> list = new ArrayList<>();

        try{
            conn = new DataBaseSQLHelper(getContext());
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
            Toast.makeText(getContext(), "Ha ocurrido un error ",
                    Toast.LENGTH_SHORT).show();
            Log.d(HomeFragment.class.getName(),"Error al consultarPedido() " + e.getMessage());
        }
        return  list;
    }

    public List<String> consultarProductoPorCod(String codProducto){
        List<String> list = new ArrayList<>();
        try{
            conn = new DataBaseSQLHelper(getContext());
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
            Toast.makeText(getContext(), "Ha ocurrido un error al consultarProductoPorCod ",
                    Toast.LENGTH_SHORT).show();
            Log.d(HomeFragment.class.getName(),"Error al consultarProductoPorCod() " + e.getMessage());
        }

        return  list;
    }

    public void actualizarTabla(){
        try {
            total=0;
            conn = new DataBaseSQLHelper(getContext());
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
                totalView.setText("Total: " + str);
                isExito=true;
                db.close();
            }else{
                isExito=false;
                db.close();
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "Ha ocurrido un error al actualizarTabla " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            Log.d(HomeFragment.class.getName(),"Error al actualizarTabla() " + e.getMessage());
        }
    }

}