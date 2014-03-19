package com.example.tomafotoitexico;

import java.io.File;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;


public class CameraMain extends Activity {
	private static final int REQUEST_CODE_TOMAR = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_main);
        if (! detectarCamara(this)) {        	
        	//1 - desactiva el boton
        	View btnCapturar = findViewById(R.id.imageButton1);
        	btnCapturar.setEnabled(false);
        	
        	//2 - muestra dialogo de error
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage(R.string.cameraNotFoundMsg).
        	setTitle(R.string.cameraNotFoundTitle).
        	setNeutralButton(R.string.close, 
        			new DialogInterface.OnClickListener() {						
        			@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();							
					}
        	});
        	AlertDialog dlg = builder.create();
        	dlg.show();        	
        }
        
        cargarFoto();
        
    }      
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera_main, menu);
        return true;
    }
    
    public void tomarFoto(View view) {
    	this.startActivityForResult(new Intent(Intent.ACTION_CAMERA_BUTTON), REQUEST_CODE_TOMAR);    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if ( requestCode == REQUEST_CODE_TOMAR && resultCode == RESULT_OK) {
    		cargarFoto();
    	}    	
    }
    
    public void configurar(View view) {
    	this.startActivity(new Intent(Intent.ACTION_CONFIGURATION_CHANGED));
    }
    
    private void cargarFoto(){
    	File fotoActual = CameraTomar.obtenerArchivo();
        if ( fotoActual.exists()) {
        	ImageView imgView = (ImageView) findViewById(R.id.imgActual);
        	Drawable d = Drawable.createFromPath(fotoActual.getPath());
        	imgView.setImageDrawable(d);        	
        }
    }
    
    private static boolean detectarCamara(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
        
    
}
