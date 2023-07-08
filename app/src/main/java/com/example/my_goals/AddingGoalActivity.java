package com.example.my_goals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.my_goals.models.Goal;
import com.example.my_goals.models.Session;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddingGoalActivity extends AppCompatActivity  {

    ImageView im_goal;
    EditText ed_goal_title, ed_goal_description, ed_date_start, ed_date_end;
    Button bt_submit, bt_go_back;
    Spinner spin_catergory;

    StorageReference sref;
    DatabaseReference dbref;
    Uri path;
    String url;

    //getting userid from the Session class
    String user_id = Session.SessionDetails.userUI.toString();

       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_goal);

        im_goal = findViewById(R.id.img_add_goal_upload);
        ed_goal_title = findViewById(R.id.et_add_goal_title_det);
        ed_goal_description = findViewById(R.id.ed_add_goal_description);
        ed_date_start = findViewById(R.id.ed_add_goal_start_det);
        ed_date_end = findViewById(R.id.ed_add_goal_end_det);
        bt_submit = findViewById(R.id.bt_add_goal_submit);
        bt_go_back = findViewById(R.id.bt_add_goal_go_back);
        spin_catergory = (Spinner) findViewById(R.id.spinner_add_goal_categories);



           //initialise storage
           sref = FirebaseStorage.getInstance().getReference("Goals images");
           //when user will click on imageview will be open devices directory to upload an image file
           im_goal.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   //on click on image will be upload image from device
                   Intent i = new Intent();
                   i.setType("image/*");
                   i.setAction(Intent.ACTION_GET_CONTENT);
                   startActivityForResult(i, 101);
               }
           });
           //user will press button go back
           bt_go_back.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   startActivity(new Intent(AddingGoalActivity.this, DashboardActivity.class));
               }
           });

           //when user will press button submit
           bt_submit.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   //checking if fields are empty
                   if(ed_goal_title.getText().toString().isEmpty() ||
                           ed_goal_description.getText().toString().isEmpty()  ||
                           ed_date_start.getText().toString().isEmpty() ||
                            ed_date_end.getText().toString().isEmpty()) {
                       Toast.makeText(AddingGoalActivity.this, "All fields should be completed", Toast.LENGTH_SHORT).show();
                       return;
                   }

                   dbref = FirebaseDatabase.getInstance().getReference("Goals");
                   //To get new key, generated by Firebase db
                   String key = dbref.push().getKey();
                   if (path == null) {
                       Goal new_goal = new Goal(ed_goal_title.getText().toString(), spin_catergory.getSelectedItem().toString(),ed_goal_description.getText().toString(), ed_date_start.getText().toString(), ed_date_end.getText().toString(),  null, user_id);
                       //write new goal in db
                       dbref.child(dbref.push().getKey()).setValue(new_goal).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               //on successful adding new topic, showing user message
                               Toast.makeText(AddingGoalActivity.this, "New goal was added successful", Toast.LENGTH_LONG).show();
                               //moving to Topic list activity
                               startActivity(new Intent(AddingGoalActivity.this, DashboardActivity.class));
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               //in case unsuccessful adding new topic in db
                               Toast.makeText(AddingGoalActivity.this, "Goal adding failed, check connection", Toast.LENGTH_LONG).show();
                           }
                       });

                   } else
                   //if image of eatery was picked
                   {
                       StorageReference reference = sref.child(key + "." + getExtension(path));
                       reference.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                               reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {
                                       //in case of successful upload of image

                                       url = uri.toString();
                                       Toast.makeText(AddingGoalActivity.this, "Photo was uploaded successful", Toast.LENGTH_SHORT).show();
                                       Goal new_goal = new Goal(ed_goal_title.getText().toString(), spin_catergory.getSelectedItem().toString(),ed_goal_description.getText().toString(), ed_date_start.getText().toString(), ed_date_end.getText().toString(),  url, user_id);

                                       //write eatery in database
                                       dbref.child(key).setValue(new_goal).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void unused) {
                                               Toast.makeText(AddingGoalActivity.this, "Goal was added successful", Toast.LENGTH_LONG).show();
                                               startActivity(new Intent(AddingGoalActivity.this, DashboardActivity.class));
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               //in case of write restaurant data in database
                                               Toast.makeText(AddingGoalActivity.this, "Goal adding failed, check connection", Toast.LENGTH_LONG).show();

                                           }
                                       });
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       //delete image from storage in case of failure upload image in db
                                       reference.delete();
                                       Toast.makeText(AddingGoalActivity.this, "Upload failed, check connection", Toast.LENGTH_LONG).show();

                                   }
                               });
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(AddingGoalActivity.this, "Upload failed, check connection", Toast.LENGTH_LONG).show();

                           }
                       }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                           }
                       });
                   }
               }
           });

    }

    //indicated action in case of picked an image from device
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //RESULT_OK - if user has picked an image and checking if was got in activity
        if(requestCode == 101 && resultCode == RESULT_OK && data.getData() != null){
            Log.i("adding goal activity", "not null image");
            Picasso.get().load(data.getData()).fit().into(im_goal);
            path = data.getData();
        }
    }
    //to get file extension
    private String getExtension(Uri path){
        ContentResolver resolver = getContentResolver(); //access to content model
        MimeTypeMap map = MimeTypeMap.getSingleton(); //maps MIME-types to file extensions
        return map.getExtensionFromMimeType(resolver.getType(path));
    }
    //add menu of app
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    //indicated where to navigate for each option of menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_dashboad:
                startActivity(new Intent(this,DashboardActivity.class));
                return true;
            case R.id.menu_calendar:
                startActivity(new Intent(this, CalendarActivity.class));
                return true;
            case R.id.menu_progress_analytics:s:
            startActivity(new Intent(this, ProgressAnalytics.class));
                return true;

            case R.id.menu_sign_out: {
                Session.SessionDetails.logout();
                startActivity(new Intent(this, RegisterActivity.class));
            }
            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}