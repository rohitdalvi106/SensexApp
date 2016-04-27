package com.example.rohitdalvi.sensexapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import DataBase.SQLiteDB;
import Model.EventStock;
import Util.PrefStock;

public class MainActivity extends AppCompatActivity {

    TextView nametxt;
    TextView tradepricetxt;
    TextView changetxt;
    TextView currencytxt;
    TextView prevclosetxt;
    TextView changeinpercenttxt;
    TextView dayslowtxt;
    TextView dayshightxt;
    ImageView lasttradeImage;
    Button showdbbtn;
    ImageButton addtolistbtn;
    SQLiteDB sqLiteDB = new SQLiteDB(this);
    EventStock eventStock = new EventStock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nametxt = (TextView) findViewById(R.id.companyID);
        tradepricetxt = (TextView) findViewById(R.id.lasttradeID);
        changetxt = (TextView) findViewById(R.id.changeID);
        currencytxt = (TextView) findViewById(R.id.currencyID);
        prevclosetxt = (TextView) findViewById(R.id.previouscloseID);
        changeinpercenttxt = (TextView) findViewById(R.id.changeinPercentID);
        dayslowtxt = (TextView) findViewById(R.id.daysLowID);
        dayshightxt = (TextView) findViewById(R.id.daysHighID);
        lasttradeImage = (ImageView) findViewById(R.id.lasttradeImageID);
        showdbbtn = (Button) findViewById(R.id.showDBID);
        addtolistbtn =(ImageButton)findViewById(R.id.addtoDBID);


        PrefStock stock = new PrefStock(MainActivity.this);



        showdbbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data = sqLiteDB.showDATABASE();

                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("data", data);
                startActivity(intent);

            }
        });


        getStockData(stock.getSymbol());

    }

    public void getStockData(String symbol) {

        // https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22TSLA%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=

        String left = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22";
        String right = "%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        String url = left + symbol + right;

        JsonObjectRequest stockRequest = new JsonObjectRequest(Request.Method.GET,
                url, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject queryObject = response.getJSONObject("query");
                    JSONObject resultsObject = queryObject.getJSONObject("results");
                    JSONObject quoteObject = resultsObject.getJSONObject("quote");
                    String Name = quoteObject.getString("Name");
                    String tradePrice = quoteObject.getString("LastTradePriceOnly");
                    String change = quoteObject.getString("Change");
                    String currency = quoteObject.getString("Currency");
                    String prevClose = quoteObject.getString("PreviousClose");
                    String changeinPercent = quoteObject.getString("ChangeinPercent");
                    String dayslow = quoteObject.getString("DaysLow");
                    String dayshigh = quoteObject.getString("DaysHigh");


                    final EventStock eventStock = new EventStock();

                    eventStock.setName(Name);
                    eventStock.setLastTradePrice(tradePrice);
                    eventStock.setChange(change);
                    eventStock.setCurrency(currency);
                    eventStock.setPreclose(prevClose);
                    eventStock.setChangeinPercent(changeinPercent);
                    eventStock.setDaysLow(dayslow);
                    eventStock.setDaysHigh(dayshigh);

                    nametxt.setText("Name: " + eventStock.getName());
                    tradepricetxt.setText(eventStock.getLastTradePrice());
                    changetxt.setText(eventStock.getChange());
                    currencytxt.setText("Currency: " + eventStock.getCurrency());
                    prevclosetxt.setText("Previous Close: " + eventStock.getPreclose());
                    changeinpercenttxt.setText("Change in Percent: " + eventStock.getChangeinPercent());
                    dayslowtxt.setText("Days Low: " + eventStock.getDaysLow());
                    dayshightxt.setText("Days High: " + eventStock.getDaysHigh());

                    addtolistbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sqLiteDB.addstock(eventStock);
                            Toast.makeText(getApplicationContext(), "Added to your STOCKS list", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (change.startsWith("-")) {
                        lasttradeImage.setImageResource(R.drawable.down);
                    } else if (change.startsWith("+")) {
                        lasttradeImage.setImageResource(R.drawable.up);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(stockRequest);



    }

    public String showDATABASE() {
        SQLiteDatabase sqLiteDatabase = sqLiteDB.getWritableDatabase();
        String[] columns = {sqLiteDB.NAME, sqLiteDB.LASTTRADE, sqLiteDB.CURRENCY};
        Cursor cursor = sqLiteDatabase.query(sqLiteDB.TABLE_NAME, columns, null, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            //int cid = cursor.getInt(0);
            String name = cursor.getString(0);
            String Ltrade = cursor.getString(1);
            String currency = cursor.getString(2);

            buffer.append(name+ " " + Ltrade+ " "+ currency+ "\n");
        }
        return buffer.toString() ;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_stock) {
            changeStock();
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeStock() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter the stock");
        final EditText stockInput = new EditText(MainActivity.this);
        stockInput.setInputType(InputType.TYPE_CLASS_TEXT);
        stockInput.setHint("TSLA");
        builder.setView(stockInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                PrefStock stockpreference = new PrefStock(MainActivity.this);
                stockpreference.setSymbol(stockInput.getText().toString());

                String newStock = stockpreference.getSymbol();

                getStockData(newStock);

            }
        });

        builder.show();

    }
}
