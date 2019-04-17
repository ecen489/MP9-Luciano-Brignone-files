package com.example.image2text2;


import android.content.Intent;

import android.graphics.Bitmap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import android.provider.MediaStore;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView pict;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Button cap;
    Button txt;
    //Button rec;

    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cap = (Button)findViewById(R.id.capture);
        txt = (Button)findViewById(R.id.Text);
        pict = (ImageView)findViewById(R.id.pic);
       //rec = (Button)findViewById(R.id.tensor);

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageBitmap == null) {
                    Toast.makeText(getApplicationContext(), "Must take picture", Toast.LENGTH_SHORT).show();
                } else {
                    runTextRecognition();
                }
            }
        });
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void runTextRecognition() {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        txt.setEnabled(false);
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                txt.setEnabled(true);
                                processTextRecognitionResult(texts);
                                //Toast.makeText(getApplicationContext(), "it works??", Toast.LENGTH_SHORT).show();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "It failed to read", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts){
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() ==0){
            showToast("Cannot recognize the text");
            return;
        }
        StringBuffer buffer2 = new StringBuffer();
        for( FirebaseVisionText.TextBlock block : texts.getTextBlocks()){
            buffer2.append(block.getText() + "\n");
            //showToast("it works");
        }
        showMessage("Image to Text", buffer2.toString());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showMessage (String title, String meassge ){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(meassge);
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            pict.setImageBitmap(imageBitmap);
        }
    }
}

