package com.writercorporation.maudit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.writercorporation.adapeter.RecyclerImageAdapter;
import com.writercorporation.database.DatabaseManager;
import com.writercorporation.model.ImageModel;
import com.writercorporation.model.Login;
import com.writercorporation.network.CustomService;
import com.writercorporation.network.CustomServiceImage;
import com.writercorporation.utils.AppConstant;
import com.writercorporation.utils.BitmapLoader;
import com.writercorporation.utils.ConnectionDetector;
import com.writercorporation.utils.GPSTracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.writercorporation.maudit.SiteListFragment.isExternalStorageWritable;

public class ImageCapture extends AppCompatActivity implements GridSelected {

    ActionBar actionBar;
    Toolbar toolbar;
    RecyclerImageAdapter imageAdapter;
    RecyclerView.LayoutManager gridLayoutManager;
    ItemObject itemObject;
    int position;
    ImageView photo;
    TextView editTxt;
    DatabaseManager dManager;
    RecyclerView recycler_view;
    List<ItemObject> itemList = new ArrayList<ItemObject>();
    ArrayList<Bitmap> value = new ArrayList<Bitmap>();
    AppConstant check;
    String defaultPath;
    File file;
    GPSTracker gps;
    ArrayList<Login> isHousekeepingList;
    String isHousekeeping;

    ConnectionDetector connectionDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);
        value.clear();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        check = new AppConstant();
        setSupportActionBar(toolbar);
        dManager = DatabaseManager.getInstance();
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        itemList = getAllItemList();
        connectionDetector=new ConnectionDetector(this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        defaultPath = getPathOfImage();
        file = new File(defaultPath);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(gridLayoutManager);
        gps = new GPSTracker(this);
        try{
            isHousekeepingList=dManager.getHousekeeping();
            isHousekeeping = isHousekeepingList.get(0).getIsHousekeeping();
        }
        catch (Exception e)
        {

        }
        imageAdapter = new RecyclerImageAdapter(itemList, ImageCapture.this, this);
        recycler_view.setAdapter(imageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call_log_submit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch ((id)) {
            case R.id.submit_callLog:
                if(value.size()>=1) {
                    showAlertDialog();
                }
                else
                    check.showErrorMessage("Please take at least one picture");
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<ImageModel> getSaveImage() {
        ArrayList<ImageModel> arrayListImage = new ArrayList<ImageModel>();
        try {
            JSONObject rootObject = new JSONObject();
            rootObject.put("PTranID", check.getPTransID());
            rootObject.put("SiteId", check.getSiteID());
            JSONArray jsonArray = new JSONArray();
            for (Bitmap object : value) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("AuditImage",BitmapLoader.BitMapToString(object));
                jsonArray.put(jsonObject);
            }
            rootObject.put("ImageData", jsonArray);
            ImageModel model = new ImageModel();

            model.setJsonString(rootObject.toString());
            model.setpTransId(check.getPTransID());
            model.setSiteId(check.getSiteID());
            model.setSyncStatus("UNSYNC");
            arrayListImage.add(model);
        } catch (Exception e) {
            return null;
        }
        return arrayListImage;
    }

    private List<ItemObject> getAllItemList() {


        List<ItemObject> allItems = new ArrayList<ItemObject>();
        allItems.add(new ItemObject("Edit", textAsBitmap("+", 70, R.color.select), false));

        return allItems;
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);

        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (300 + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, 0, yPos, paint);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK && requestCode ==  position) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ImageCapture.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                        return;
                    }
                    loadImage();
                }
                loadImage();
            }


        } catch (Exception e) {
           // e.printStackTrace();

           check.showErrorMessage(""+e.getMessage());
        }

    }

    private void loadImage(){
        Bitmap captureImage = new BitmapLoader().loadBitmap(defaultPath, 350, 300,check.getSiteCode());
        value.add(position, captureImage);

        ItemObject obj = new ItemObject("Edit", resizeImage(captureImage, 300, 300), true);

        Uri uri = null;
        if(isExternalStorageWritable()){
            //uri = Uri.fromFile(new File(LoginActivity.this.getExternalFilesDir(null) + "/wsgMauditapk/" + filenamme));
            uri = FileProvider.getUriForFile(ImageCapture.this, getApplicationContext().getPackageName() + ".provider", file);
        }else{
            uri = Uri.fromFile(file);//"MAudit_1.0.0.5.apk"
        }
        //obj.setFileUri(Uri.fromFile(file));
        obj.setFileUri(uri);
        if (itemList.size() == position + 1) {
            itemList.add(itemList.size() - 1, obj);
            recycler_view.scrollToPosition(recycler_view.getChildCount());

        } else {
            itemList.set(position, obj);
        }

        imageAdapter.notifyDataSetChanged();

    }




    public void deleteImageFromGallery(ContentResolver resolver, String captureimageid) {
        Cursor c = null;
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=?", new String[]{captureimageid});

        String[] projection = {MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA, BaseColumns._ID,};

        Log.i("InfoLog", "on activityresult Uri u " + u.toString());

        try {
            if (u != null) {
                c = resolver.query(u, projection, null, null, null);

            }
            if ((c != null) && (c.moveToLast())) {
                ContentResolver cr = resolver;

                @SuppressWarnings("unused")
                int i = cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + c.getString(3), null);

            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public Bitmap resizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    public String getPathOfImage() {
        Date currDate = new Date();
        long timeStamp = currDate.getTime();
        String folderPath = "";
        if(isExternalStorageWritable()){
            //uri = Uri.fromFile(new File(LoginActivity.this.getExternalFilesDir(null) + "/wsgMauditapk/" + filenamme));
            folderPath = this.getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/maudit/images/";
        }else{
            folderPath = Environment.getExternalStorageDirectory()
                    + "/maudit/images/";
        }
        File folder = new File(folderPath);
        if (!folder.exists())
            folder.mkdirs();

        String extStorageDirectory = folder.toString();
        String newPath = "maudit_" + timeStamp + ".png";
        File newfolder = new File(folder, newPath);
        try {
            if (!newfolder.exists())
                newfolder.createNewFile();

        } catch (Exception e) {
           /// e.printStackTrace();
        }
        return extStorageDirectory + "/maudit_" + timeStamp + ".png";
    }

    @Override
    public void onGridSelect(ItemObject itemObject, View view, int position) {
        this.itemObject = itemObject;
        this.position = position;
        if(value.size()<12) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            defaultPath = getPathOfImage();
            file = new File(defaultPath);
            Uri outputfile = null;//Uri.fromFile(file);

            if(isExternalStorageWritable()){
                //uri = Uri.fromFile(new File(LoginActivity.this.getExternalFilesDir(null) + "/wsgMauditapk/" + filenamme));
                outputfile = FileProvider.getUriForFile(ImageCapture.this, getApplicationContext().getPackageName() + ".provider", file);
            }else{
                outputfile = Uri.fromFile(file);//"MAudit_1.0.0.5.apk"
            }


            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputfile);
            startActivityForResult(intent, recycler_view.getChildLayoutPosition(view));
            photo = (ImageView) view.findViewById(R.id.site_photo);
            editTxt = (TextView) view.findViewById(R.id.edit_lable);
        }
        else
            check.showErrorMessage("Allowed to capture 12 images only");
    }

   /* @Override
    protected void onStop() {
        unregisterReceiver(broadCast);
        super.onStop();
    }*/

    class StoreImagesAsync extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ImageCapture.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                ArrayList<ImageModel> imgArr = getSaveImage();
                if(imgArr==null)
                {
                    throw new Exception();

                }
                //dManager.insertImageModel(getSaveImage());
                dManager.insertImageModel(imgArr);
                dManager.setColorDatabase(check.getSiteID());

            } catch (SQLException se) {
                return se.getMessage();
            } catch (Exception e) {
              //  e.printStackTrace();
                return e.getMessage();
            }
            return "SAVED";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pd != null)
                pd.dismiss();

            if(!s.equals("SAVED")){
                //check.showErrorMessage("ERROR #104 "+s);
                check.showErrorMessage(getString(R.string.error_104_unexpected_error));
                return;
            }
            if(connectionDetector.isConnectingToInternet())
            {
                try {

                    Intent intent = new Intent(Intent.ACTION_SYNC, null, ImageCapture.this, CustomService.class);
                    Intent intentImge = new Intent(Intent.ACTION_SYNC, null, ImageCapture.this, CustomServiceImage.class);
                    ImageCapture.this.startService(intent);
                    ImageCapture.this.startService(intentImge);

                }
                catch (Exception e){}
            }

            Intent intent = new Intent(ImageCapture.this, SiteListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            check.clearPrefs();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     /*   if(broadCast!=null)
            unregisterReceiver(broadCast);*/
    }
    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("info!");
        builder.setMessage("Are you sure to complete " + check.getSiteCode() + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new StoreImagesAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        });

        builder.show();
    }
    @Override
    public void onBackPressed() {
        check.showErrorMessage("Can't Move Back.");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    loadImage();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ImageCapture.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
