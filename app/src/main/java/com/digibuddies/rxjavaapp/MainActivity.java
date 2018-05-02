package com.digibuddies.rxjavaapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.reactivestreams.Subscription;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    Button contact;

    private CompositeDisposable mCompositeDisposable;

    private countryadapter mAdapter;

    ContactsProvider contactsProvider;

    List<Contact> fetchedcontacts;

   List<Data.Worldpopulation> countryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCompositeDisposable = new CompositeDisposable();
        initRecyclerView();
        loadJSON();
        contact = (Button)findViewById(R.id.contacts);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int MyVersion = Build.VERSION.SDK_INT;
                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (!checkIfAlreadyhavePermission()) {
                        requestForSpecificPermission();
                    }
                    else  fetchcontacts();
                }
                else  fetchcontacts();
                Log.d("bings","click");
            }
        });
            }
    private void initRecyclerView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void loadJSON() {

        apiinterface requestInterface = new Retrofit.Builder()
                .baseUrl("http://www.androidbegin.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(apiinterface.class);

        mCompositeDisposable.add(requestInterface.register()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Data dataList) {
        countryList = dataList.worldpopulation;
        mAdapter = new countryadapter(countryList,MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void handleError(Throwable error) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchcontacts();
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void fetchcontacts(){

        Snackbar snackbar = Snackbar
                .make(mRecyclerView, "Exporting Contacts...", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        Observable.fromCallable(() -> {
            contactsProvider = new ContactsProvider(this);
            fetchedcontacts = contactsProvider.getContacts();
            try {
                exportContatcInCSV();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("contacts", String.valueOf(fetchedcontacts.get(0).name));
            return false;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                snackbar.dismiss();
                Snackbar snackbar2 = Snackbar
                            .make(mRecyclerView, "Successfully exported to contact.zip in SDCard/Contacts", Snackbar.LENGTH_LONG);
                snackbar2.show();
                });
    }

    public void exportContatcInCSV() throws IOException {
        {

            File folder = new File(getFilesDir().getAbsolutePath()
                    + "/Contacts");
            File target = new File(Environment.getExternalStorageDirectory()
                    + "/Contacts");

            if (!folder.exists())
                folder.mkdir();
            if (!target.exists())
                target.mkdir();



            final String filename = folder.toString() + "/" + "contact.csv";

            new Thread() {
                public void run() {
                    try {

                        FileWriter fw = new FileWriter(filename);

                        int i=0;

                        fw.append("Name");
                        fw.append(',');

                        fw.append("Number");
                        fw.append(',');

                        fw.append("Email");
                        fw.append(',');

                        fw.append('\n');

                        if (fetchedcontacts.size()>0) {
                            do {
                                Contact contact = fetchedcontacts.get(i);
                                fw.append(contact.name);
                                fw.append(',');

                                fw.append(contact.phone);
                                fw.append(',');

                                fw.append(contact.email);
                                fw.append(',');

                                fw.append('\n');
                                i++;

                            } while (i<fetchedcontacts.size());
                        }
                        fw.close();
                        zip();

                    } catch (Exception e) {
                    }
                   }
            }.start();

        }

    }

    private void zip(){
        String source = getFilesDir().getAbsolutePath()+"/Contacts/"+"contact.csv";
        byte[] buf = new byte[1024];

        try {

            String target = Environment.getExternalStorageDirectory().toString()+"/Contacts/"+"contact.zip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target));
                FileInputStream in = new FileInputStream(source);
                out.putNextEntry(new ZipEntry(source.substring(source.lastIndexOf("/") + 1)));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
                out.close();
        } catch (IOException e) {
        }
    }

}

