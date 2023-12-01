package edu.takming.myapplicationrecyclerview_youbike_new1121;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText edittext;
    private RecyclerView recycler;
    private Spinner spinner1, spinner2;
    private String[] place1 = new String[]{"請選擇區域", "大安區", "大同區", "士林區", "文山區", "中正區", "中山區", "內湖區", "北投區", "松山區", "南港區", "信義區", "萬華區", "臺大專區", "臺大公館校區"};
    private String[] place2 = new String[]{"尋找借/還", "借", "還"};
    private String url, search, msg, type;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edittext = findViewById(R.id.edittext);
        recycler = findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        url = "https://tcgbusfs.blob.core.windows.net/dotapp/youbike/v2/youbike_immediate.json";
        new BikeTask().execute(url);
        findViews();
        setAdapter();
        setListener();
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search = edittext.getText().toString();
                new BikeTask().execute(url);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    private void setAdapter() {
        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, place1);
        adapter1.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        ArrayAdapter<String> adapter2 =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, place2);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
    }

    private void findViews() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
    }

    private void setToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void setListener() {
        spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected
                    (AdapterView<?> adapterView, View view, int position, long l) {
                msg = adapterView.getItemAtPosition(position).toString();
                setToast(msg);
                new BikeTask().execute(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected
                    (AdapterView<?> adapterView, View view, int position, long l) {
                type = adapterView.getItemAtPosition(position).toString();
                setToast(type);
                new BikeTask().execute(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    class BikeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuffer json = new StringBuffer();
            try {
                URL url = new URL(params[0]);
                Log.d(TAG, "AsyncTask doInBackground: " + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                Log.d(TAG, "line: " + line);
                while (line != null) {
                    json.append(line);
                    line = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONArray jonObject = null;
            List<Bike> bikes = new ArrayList<>();
            try {
                jonObject = new JSONArray(result.toString());
                Log.d(TAG, "jonObject: " + jonObject);
                JSONObject resultObj = jonObject.getJSONObject(0);
                Log.d(TAG, "resultObj: " + resultObj);
                //JSONArray recordArray = resultObj.getJSONArray("0001");
                try {
                    //for (int i = 0; i < recordArray.length(); i++) {
                    // JSONObject object = recordArray.getJSONObject(i);
                    for (int i = 0; i < jonObject.length(); i++) {
                        resultObj = jonObject.getJSONObject(i);
                        String name = resultObj.getString("sna").replace("YouBike2.0_", "");
                        String address = resultObj.getString("ar");
                        String totalNumber = resultObj.getString("tot");
                        String lendNumber = resultObj.getString("sbi");
                        String returnNumber = resultObj.getString("bemp");
                        String sarea = resultObj.getString("sarea");
                        Log.d(TAG, "sarea: " + sarea);
                        if (search == null) {
                            if (msg.equals("請選擇區域")) {
                                bikes.add(new Bike(name, address, totalNumber,
                                        lendNumber, returnNumber));
                            } else if (msg.equals(sarea)) {
                                if (type.equals("借")) {
                                    if (Integer.parseInt(lendNumber) > 0) {
                                        bikes.add(new Bike(name, address, totalNumber,
                                                lendNumber, returnNumber));
                                    }
                                } else if (type.equals("還")) {
                                    if (Integer.parseInt(returnNumber) > 0) {
                                        bikes.add(new Bike(name, address, totalNumber,
                                                lendNumber, returnNumber));
                                    }
                                } else {
                                    bikes.add(new Bike(name, address, totalNumber,
                                            lendNumber, returnNumber));
                                }
                            }
                        } else if (name.contains(search)) {
                            if (type.equals("借")) {
                                if (Integer.parseInt(lendNumber) > 0) {
                                    bikes.add(new Bike(name, address, totalNumber,
                                            lendNumber, returnNumber));
                                }
                            } else if (type.equals("還")) {
                                if (Integer.parseInt(returnNumber) > 0) {
                                    bikes.add(new Bike(name, address, totalNumber,
                                            lendNumber, returnNumber));
                                }
                            } else {
                                bikes.add(new Bike(name, address, totalNumber,
                                        lendNumber, returnNumber));
                            }
                        }
                        Log.d(TAG, "AsyncTask onPostExecute: " +
                                name + address + totalNumber +
                                lendNumber + returnNumber);
                    }
                } catch (JSONException e) {
                }
                recycler.setAdapter(new BikeAdapter(bikes));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it = new Intent(Intent.ACTION_VIEW);
        if(item.getItemId()==R.id.time)
        {
            it.setClass(MainActivity.this, Time.class);
            startActivity(it);
            return true;
        }
            else if(item.getItemId()==R.id.home)
        {
            it.setData(Uri.parse("https://www.youbike.com.tw/region/main/"));
            startActivity(it);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}