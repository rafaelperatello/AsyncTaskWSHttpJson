package br.edu.ifspsaocarlos.sdm.asynctaskws;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar mProgress;
    private EditText editAddress;
    private TextView tvTexto;
    private TextView tvData;
    private TextView tvCustomTexto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editAddress = (EditText) findViewById(R.id.edit_address);
        tvTexto = ((TextView) findViewById(R.id.tv_texto));
        tvData = ((TextView) findViewById(R.id.tv_data));
        tvCustomTexto = ((TextView) findViewById(R.id.tv_texto_custom_ws));

        Button btAcessarWs = (Button) findViewById(R.id.bt_acessar_ws);
        Button btAcessarCustomWs = (Button) findViewById(R.id.bt_acessar_custom_ws);

        btAcessarWs.setOnClickListener(this);
        btAcessarCustomWs.setOnClickListener(this);

        mProgress = (ProgressBar) findViewById(R.id.pb_carregando);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.bt_acessar_ws) {
            buscarTexto("http://www.nobile.pro.br/sdm/texto.php");
            buscarData("http://www.nobile.pro.br/sdm/data.php");
        } else if (v.getId() == R.id.bt_acessar_custom_ws) {
            buscarCustomData(editAddress.getText().toString());
        }
    }

    private void buscarCustomData(String url) {
        CustomAsync tarefa = new CustomAsync();
        tarefa.execute(url);
    }

    private void buscarTexto(String url) {
        TextoAsync tarefa = new TextoAsync();
        tarefa.execute(url);
    }

    private void buscarData(String url) {
        DataAsync tarefa = new DataAsync();
        tarefa.execute(url);
    }

    private class CustomAsync extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                HttpURLConnection httpsURLConnection = (HttpURLConnection) (new URL(params[0])).openConnection();

                if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpsURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String temp;

                    while ((temp = bufferedReader.readLine()) != null) {
                        stringBuilder.append(temp);
                    }
                    jsonObject = new JSONObject(stringBuilder.toString());
                }

            } catch (JSONException jsone) {
                Log.e("SDM", "doInBackground: Erro no processamento do objeto JSON");
            } catch (Exception e) {
                Log.d("SDM", "doInBackground: Erro na recuperação da data");
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                tvCustomTexto.setText(json.toString());
            } else {
                tvCustomTexto.setText("Erro ao recuperar dados");
            }
        }
    }

    private class TextoAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder stringBuilder = new StringBuilder();

            try {
                HttpURLConnection httpsURLConnection = (HttpURLConnection) (new URL(params[0])).openConnection();

                if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpsURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String temp;

                    while ((temp = bufferedReader.readLine()) != null) {
                        stringBuilder.append(temp);
                    }
                }

            } catch (Exception e) {
                Log.d("SDM", "doInBackground: Erro na recuperação do texto");
            }

            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            tvTexto.setText(s);
        }
    }

    private class DataAsync extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                HttpURLConnection httpsURLConnection = (HttpURLConnection) (new URL(params[0])).openConnection();

                if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpsURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String temp;

                    while ((temp = bufferedReader.readLine()) != null) {
                        stringBuilder.append(temp);
                    }

                    jsonObject = new JSONObject(stringBuilder.toString());
                }

            } catch (JSONException jsone) {
                Log.e("SDM", "doInBackground: Erro no processamento do objeto JSON");
            } catch (Exception e) {
                Log.d("SDM", "doInBackground: Erro na recuperação da data");
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            String data = null, hora = null, ds = null;

            try {
                data = s.getInt("mday") + "/" + s.getInt("mon") + "/" + s.getInt("year");
                hora = s.getInt("hours") + ":" + s.getInt("minutes") + ":" + s.getInt("seconds");
                ds = s.getString("weekday");

            } catch (JSONException jsone) {
                Log.e("SDM", "Erro no processamento do objeto JSON");
            }

            tvData.setText(data + "\n" + hora + "\n" + ds);
            mProgress.setVisibility(View.GONE);
        }
    }
}
