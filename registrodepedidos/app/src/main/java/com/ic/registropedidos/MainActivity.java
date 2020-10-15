package com.ic.registropedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private  EditText usu, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usu= findViewById(R.id.txtUser);
        pass= findViewById(R.id.txtPass);
        pass.requestFocus();

        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(pass.getWindowToken(), 0);
                }
            }
        });
    }


    public void Ingresar(View view){

        if(usu.getText().toString().equals("jose") && pass.getText().toString().equals("123456")){
            Intent i = new Intent(this, MainActivityBase.class);
            startActivity(i);
            finish();
        }else{
            pass.setText("");
            Toast.makeText(MainActivity.this, "Datos invalidos. ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void Registrar(View view){
        Toast.makeText(MainActivity.this, "PROXIMAMENTE :) ",
                Toast.LENGTH_SHORT).show();
    }

}
