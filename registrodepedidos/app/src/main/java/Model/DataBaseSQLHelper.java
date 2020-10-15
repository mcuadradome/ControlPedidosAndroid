package Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseSQLHelper extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME = "Pedidos.db";
    public static final int VERSION = 1;
    public DataBaseSQLHelper(Context context){
        super(context, DATA_BASE_NAME, null ,VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(Estructura_BBDD.CREARTABLAPRODUCTO);
        sqLiteDatabase.execSQL(Estructura_BBDD.CREARTABLACLIENTE);
        sqLiteDatabase.execSQL(Estructura_BBDD.CREARTABLAUSUARIO);
        sqLiteDatabase.execSQL(Estructura_BBDD.CREARTABLAORDER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Estructura_BBDD.TABLE_USUARIO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Estructura_BBDD.TABLE_PRODUCTO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Estructura_BBDD.TABLE_CLIENT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Estructura_BBDD.TABLE_ORDEN_O);
        onCreate(sqLiteDatabase);

    }



}
