package Model;

import android.provider.BaseColumns;

 public class Estructura_BBDD {

     public static String consulta;
     public static final String TEXT_TYPE = " TEXT ";
     public static final String COMMA_SEP = ",";

    public Estructura_BBDD(){}

        //Estructure table user
        public static final String ID_U = "id";
        public static final String TABLE_USUARIO = "usuario";
        public static final String USER_U = "user";
        public static final String NOMBRE_U = "nombre";
        public static final String APELLIDO_U = "apellido";
        public static final String PASSWORD_U = "password";

        //Estructure table product
        public static final String TABLE_PRODUCTO = " productos ";
        public static final String CODIGOPRODUCTO_P = " codigo ";
        public static final String DESCRIPCION_P = " descripcion ";
        public static final String PRECIO_BASE_P = " precio_base ";
        public static final String IVA_P = " iva ";
        public static final String VALOR_IVA_P = " valor_iva ";
        public static final String PRECIO_FINAL_P = " precio_final ";
        public static final String EMBALAJE_P = " embalaje ";

        //Estructure table order
        public static final String TABLE_ORDEN_O = " orden ";
        public static final String ID_O = "id";
        public static final String CLIENTE_O = "cliente";
        public static final String CODPRODUCTO_O = "codProducto";
        public static final String CANTIDAD_O = "cantidad";
        public static final String TOTALPRODUCTO_O = "total_producto";
        public static final String PRECIOPRODUCTO_O = "precio_producto";
        public static final String PORPAQUETE_O = "por_paquete";
        public static final String FECHA_O = "fecha";

        //Estructure table client
        public static final String TABLE_CLIENT = " cliente ";
        public static final String NOMBRE = "nombre_cliente";
        public static final String ZONA = "zona_cliente";


        public static  final String CREARTABLAUSUARIO = "CREATE TABLE " + TABLE_USUARIO + " (" +
                                                        ID_U + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                                                        NOMBRE_U +  TEXT_TYPE +  COMMA_SEP +
                                                        APELLIDO_U +  TEXT_TYPE + COMMA_SEP +
                                                        USER_U +  TEXT_TYPE + COMMA_SEP +
                                                        PASSWORD_U +  TEXT_TYPE + " ); ";


        public static  final String CREARTABLAPRODUCTO="CREATE TABLE " + TABLE_PRODUCTO + " (" +
                                                        CODIGOPRODUCTO_P +  TEXT_TYPE  +" PRIMARY KEY "+ COMMA_SEP +
                                                        DESCRIPCION_P +  TEXT_TYPE + COMMA_SEP +
                                                        PRECIO_BASE_P +  " INTEGER " + COMMA_SEP +
                                                        EMBALAJE_P +  " INTEGER " + COMMA_SEP +
                                                        IVA_P +  " INTEGER ); ";


        public static  final String CREARTABLACLIENTE = "CREATE TABLE " + TABLE_CLIENT + " (" +
                                                        ID_U + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                                                        NOMBRE +  TEXT_TYPE + COMMA_SEP +
                                                        ZONA +  TEXT_TYPE + " ); ";


        public static  final String CREARTABLAORDER = "CREATE TABLE " + TABLE_ORDEN_O + " (" +
                                                        ID_O + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                                                        CLIENTE_O +  TEXT_TYPE + COMMA_SEP +
                                                        CODPRODUCTO_O +  TEXT_TYPE + COMMA_SEP +
                                                        FECHA_O +  TEXT_TYPE + COMMA_SEP +
                                                        CANTIDAD_O +  " INTEGER , " +
                                                        TOTALPRODUCTO_O + " INTEGER ," +
                                                        PRECIOPRODUCTO_O + " INTEGER ," +
                                                        PORPAQUETE_O + " INTEGER ," +
                                                        "FOREIGN KEY " + "(" +CLIENTE_O + ")"+ " REFERENCES " + TABLE_CLIENT + "(" + ID_U  + "),"+
                                                        "FOREIGN KEY " + "(" +CODPRODUCTO_O + ")"+ " REFERENCES " + TABLE_PRODUCTO + "(" + CODIGOPRODUCTO_P  + "));";

}
