package com.techpro.chat.ticklechat.activity.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.internal.Utility;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.models.user.UserModel;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.AppUtils;
import com.techpro.chat.ticklechat.utils.UtilityImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vishalrandive on 11/04/16.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView ivProfileIcon;
    private EditText password;
    private EditText confirm_password;
    private EditText profilename;
    private EditText profileemail;
    private EditText profilephone,countrycode;
    private EditText profile_date;
    private TextView tvBtnMale;
    private TextView tvBtnFemale;
    private TextView submit;
    private ProgressDialog dialog;
    private String gender = "m";
    private Bitmap selectedBitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initUi();
    }

    private void initUi()
    {
        confirm_password =  (EditText) findViewById(R.id.confirm_password);
        password =  (EditText) findViewById(R.id.password);
        ivProfileIcon = (ImageView) findViewById(R.id.iv_profile_icon);
        profilename = (EditText) findViewById(R.id.profilename);
        profileemail = (EditText) findViewById(R.id.profileemail);
        profilephone = (EditText) findViewById(R.id.profilephone);
        profile_date = (EditText) findViewById(R.id.profile_date);
        countrycode = (EditText) findViewById(R.id.countrycode);
        tvBtnMale = (TextView) findViewById(R.id.tv_btn_male);
        tvBtnFemale = (TextView) findViewById(R.id.tv_btn_female);
        submit = (TextView) findViewById(R.id.submit);

        tvBtnMale.setSelected(true);
        tvBtnMale.setOnClickListener(this);
        tvBtnFemale.setOnClickListener(this);
        submit.setOnClickListener(this);
        ivProfileIcon.setOnClickListener(this);
        if (DataStorage.UserDetails != null) {
            if (DataStorage.UserDetails.getProfile_image() != null) {
                byte[] decodedString = Base64.decode(DataStorage.UserDetails.getProfile_image().getBytes(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivProfileIcon.setImageBitmap(decodedByte);
            }
            profilename.setText(DataStorage.UserDetails.getName());
            profileemail.setText(DataStorage.UserDetails.getEmail());
            profilephone.setText(DataStorage.UserDetails.getPhone());
            profile_date.setText(DataStorage.UserDetails.getBirthday());
            if (DataStorage.UserDetails.getGender().equals("m")) {
                tvBtnFemale.setSelected(false);
                tvBtnMale.setSelected(true);
            } else {

                tvBtnMale.setSelected(false);
                tvBtnFemale.setSelected(true);
            }
        }
    }

    @Override
    public void onClick (View v)
    {
        switch (v.getId())
        {
            case R.id.submit:
                if (profilename.getText().toString().equals("") || profile_date.getText().toString().equals("") ||
                        profilephone.getText().toString().equals("") || profileemail.getText().toString().equals("") ||
                        confirm_password.getText().toString().equals("") || password.getText().toString().equals("") || countrycode.getText().toString().equals("") ) {
                    Toast.makeText(getApplicationContext(),"Please enter complete details.", Toast.LENGTH_LONG).show();
                } else if (!confirm_password.equals(password)) {
                    Toast.makeText(getApplicationContext(),"Invalid Password.", Toast.LENGTH_LONG).show();
                 } else if (!AppUtils.isNetworkConnectionAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                } else {
                    String profileImage  = "";
                    if (selectedBitmap != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        profileImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                    }
                    dialog = ProgressDialog.show(LoginActivity.this, "Loading", "Please wait...", true);

                    callUpdateUserDataService(Integer.parseInt(DataStorage.UserDetails.getId()), profilename.getText().toString(),
                            gender,profile_date.getText().toString(), profilephone.getText().toString(),
                            profileemail.getText().toString(), profileImage,countrycode.getText().toString(),password.getText().toString());
                }
                break;

            case R.id.iv_profile_icon:
                // TODO: 30/10/16

                selectImage();
                break;

            case R.id.tv_btn_male:
                // TODO: 30/10/16
                tvBtnFemale.setSelected(false);
                tvBtnMale.setSelected(true);
                gender = "m";
                break;

            case R.id.tv_btn_female:
                // TODO: 30/10/16
                tvBtnMale.setSelected(false);
                tvBtnFemale.setSelected(true);
                gender = "f";
                break;
        }
    }

    /*
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private synchronized void callUpdateUserDataService(int userid, String name,String gender,String dob,String phone,String email,String profile_image,String code,String pass) {
        //Getting webservice instance which we need to call
        Call<UserModel> callForUserDetailsFromID = (ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId())
                .create(ApiInterface.class)).registeruser(name,gender,dob,phone,email,profile_image,code,pass);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response != null) {
                    UserDetailsModel getUserDetails = response.body().getBody();
                    DataStorage.UserDetails = getUserDetails;
                    finish();
                    Log.e("profile", "Success  callLoginService : " + getUserDetails);
                    Log.e("profile", "Success  getUserDetails.getId() : " + getUserDetails.getId());

                } else {
                    Toast.makeText(getApplicationContext(), R.string.failmessage, Toast.LENGTH_LONG).show();
                    Log.e("profile", "Success callTickles_Service but null response");
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(getApplicationContext(), R.string.failmessage, Toast.LENGTH_LONG).show();
                Log.e("profile", t.toString());
                dialog.dismiss();
            }
        });
    }

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case UtilityImage.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= UtilityImage.checkPermission(LoginActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        selectedBitmap = thumbnail;
        ivProfileIcon.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        selectedBitmap = bm;
        ivProfileIcon.setImageBitmap(bm);
    }

}