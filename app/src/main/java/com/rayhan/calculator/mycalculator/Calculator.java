package com.rayhan.calculator.mycalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Calculator extends AppCompatActivity {
    private TextView _screen;
    private String display = "";
    private String currentOperator = "";
    private String result = "";

    private SharedPreferences sharedPreferences;
    private String PreferencesName = "tempMemory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Display display1 = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if(display1.getRotation() == Surface.ROTATION_0){
            setContentView(R.layout.activity_calculator);
        }
        if(display1.getRotation() == Surface.ROTATION_90 || display1.getRotation() == Surface.ROTATION_270){
            setContentView(R.layout.horizontal_calculator);
        }

        _screen = (TextView)findViewById(R.id.textView);
        _screen.setText(display);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String saveState = savedInstanceState.getString("saveState");

        _screen = (TextView)findViewById(R.id.textView);
        _screen.setText(saveState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String stateToSave = _screen.getText().toString();
        outState.putString("saveState", stateToSave);
    }

    private void updateScreen(){
        _screen.setText(display);
    }

    public void onClickNumber(View v){
        if(result != ""){
            clear();
            updateScreen();
        }
        Button b = (Button) v;
        display += b.getText();
        updateScreen();
    }

    private boolean isOperator(char op){
        switch (op){
            case '+':return true;
            case '-':return true;
            case 'x':return true;
            case '/':return true;
            default: return false;
        }
    }

    public void onClickOperator(View v){
        if(display == "") return; // if display blank then no operator show

        Button b = (Button)v;

        if(result != ""){
            //result store in _display , then clear screen , then result set result as display
            String _display = result;
            clear();
            display = _display;
        }

        if(currentOperator != ""){
            //Log.d("CalcX", ""+display.charAt(display.length()-1));

            if(isOperator(display.charAt(display.length()-1))){
                // if last charecter is a operator then keep exesting text and put operator after it
                display = display.replace(display.charAt(display.length()-1), b.getText().charAt(0));
                updateScreen();
                return;
            }else{
                // if last charecter is not a operator then generate result
                getResult();
                display = result;
                result = "";
            }
            currentOperator = b.getText().toString();
        }
        display += b.getText(); // put operator after text
        currentOperator = b.getText().toString();
        updateScreen();
    }

    private void clear(){
        display = "";
        currentOperator = "";
        result = "";
    }

    public void onClickClear(View v){ // for clear button
        clear();
        updateScreen();
    }

    private double operate(String a, String b, String op){
        switch (op){
            case "+": return Double.valueOf(a) + Double.valueOf(b);
            case "-": return Double.valueOf(a) - Double.valueOf(b);
            case "x": return Double.valueOf(a) * Double.valueOf(b);
            case "/": try{
                return Double.valueOf(a) / Double.valueOf(b);
            }catch (Exception e){
                Log.d("error", "div error");
            }
            default: return -1;
        }
    }

    String lastDigit;
    String firstDigit;
    String newDisplay;

    private boolean getResult(){

        if(result != "" && lastDigit != ""){ /// execute if equal press multiple time

            result = String.valueOf(operate(result, lastDigit, currentOperator));

            newDisplay = firstDigit+ currentOperator +lastDigit + "\n " + result;
            firstDigit = result;
            return  true;

        }else {  // execute for normal operation

            if (currentOperator == "") return false;
            String[] operation = display.split(Pattern.quote(currentOperator));
            if (operation.length < 2) return false;

            result = String.valueOf(operate(operation[0], operation[1], currentOperator));
            lastDigit = operation[1];
            firstDigit = result;
            newDisplay = operation[0]+ currentOperator +operation[1] + "\n" + result;
            return true;

        }

    }

    public void onClickEqual(View v){
        if(display == "") return;
        if(!getResult()) return;

        _screen.setText(newDisplay );
       // _screen.setText(display + "\n" + String.valueOf(result));

        // For save history
        sharedPreferences = getSharedPreferences(PreferencesName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String finalHistory;

        String[] resultPart = newDisplay.split(Pattern.quote("\n"));
        newDisplay = resultPart[0] + " =" + resultPart[1];
        if(sharedPreferences.getString("history", "") != ""){ // if sharedPreferences is not null

            String previousHistory = sharedPreferences.getString("history", "0");

            finalHistory = previousHistory + "\n" +  newDisplay ;

        }
        else { // if sharedPreferences is null
            finalHistory = newDisplay ;
        }


        editor.putString("history", finalHistory);
        editor.commit();
    }

    //Method for MC

    public void MC(View v)
    {
        sharedPreferences = getSharedPreferences(PreferencesName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Memory", "");
        editor.commit();
        _screen.setText("");
        Toast.makeText(getApplicationContext(), "Memory Clear", Toast.LENGTH_SHORT).show();
    }

    //Method for MPlus

    public void MPlus(View v)
    {
        if(isOperator(display.charAt(display.length()-1))) return;

        sharedPreferences = getSharedPreferences(PreferencesName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

       // this.getResult();
        this.onClickEqual(v);

        String memory;
        if(sharedPreferences.getString("Memory", "") != ""){
            String previousMemory = sharedPreferences.getString("Memory", "0");
            String currentMemory;
            if(result == ""){
                currentMemory = _screen.getText().toString();
            }else {
                currentMemory = result;
            }

            Double sumMemory = Double.valueOf(previousMemory) + Double.valueOf(currentMemory);
            memory = String.valueOf(sumMemory);
        }
        else {
            if (result != ""){
                memory = result;
            }else {
               memory = _screen.getText().toString();
            }

        }

        editor.putString("Memory",String.valueOf(memory));
        editor.commit();

        Toast.makeText(getApplicationContext(), "Memory Saved", Toast.LENGTH_SHORT).show();
    }

    public void MMinus(View v)
    {
        if(isOperator(display.charAt(display.length()-1))) return;

        sharedPreferences = getSharedPreferences(PreferencesName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //this.getResult();
        this.onClickEqual(v);
        String memory;
        if(sharedPreferences.getString("Memory", "") != ""){
            String previousMemory = sharedPreferences.getString("Memory", "0");
            String currentMemory;
            if(result == ""){
                currentMemory = _screen.getText().toString();
            }else {
                currentMemory = result;
            }

            Double sumMemory = Double.valueOf(previousMemory) - Double.valueOf(currentMemory);
            memory = String.valueOf(sumMemory);
        }
        else {
            if (result != ""){
                memory = result;
            }else {
                memory = _screen.getText().toString();
            }

        }

        editor.putString("Memory",String.valueOf(memory));
        editor.commit();

        Toast.makeText(getApplicationContext(), "Memory Saved", Toast.LENGTH_SHORT).show();
    }

    //Method for MR

    public void MR(View v)
    {
        sharedPreferences = getSharedPreferences(PreferencesName, 0);
        String s = sharedPreferences.getString("Memory", "");
        _screen.setText(s);
    }

    public void loadHistory(View v)
    {
        Intent i = new Intent(Calculator.this,History.class);
        startActivity(i);
    }

}
