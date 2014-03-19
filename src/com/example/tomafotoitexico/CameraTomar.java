package com.example.tomafotoitexico;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

public class CameraTomar extends Activity 
implements PictureCallback {
	
	private static final String FILE_NAME = "ITexicoIMG.JPG";
	private static final String IMG_FOLDER = "ITexico";
	private static final String TAG = "CameraTomar";
	
	private Camera mCamera;
	private CameraPreview mPreview;
	private Parameters mParameters;
	private FrameLayout mFrameLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_tomar);        
        mFrameLayout = (FrameLayout) findViewById(R.id.camera_preview);        
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		this.setResult(RESULT_CANCELED);
		mCamera = abrirCamara();
		
		if ( mCamera == null ) {
			Log.d(TAG, "No se pudo abrir la camara");
			return;
		}
		mParameters = mCamera.getParameters();
        
        // inicializar flash
        String flashMode = mParameters.getFlashMode();
        ToggleButton btnFlash = (ToggleButton) findViewById(R.id.btn_flash);
        if ( flashMode != null )
        {
        	//si el flash esta disponible, activalo o desactivalo
        	if ( flashMode == Parameters.FLASH_MODE_OFF )
        		btnFlash.setChecked(false);
        	else
        		btnFlash.setChecked(true);        		
        }
        else
        {
        	// flash no disponible, oculta el boton
        	btnFlash.setVisibility(View.GONE);
        }        

        // crear la vista con el preview de la camara
        mPreview = new CameraPreview(this, mCamera);
        mFrameLayout.addView(mPreview);
	}
	
	@Override
	protected void onPause() {
		mFrameLayout.removeView(mPreview);
		mPreview = null;
		if ( mCamera != null ) {
			mCamera.release();
			mCamera = null;
		}			
		super.onPause();
	}
	
	public void onFlashClicked(View view){
		boolean estaEncendido = ((ToggleButton) view).isChecked();
		if ( estaEncendido ) 
			mParameters.setFlashMode(Parameters.FLASH_MODE_ON);
		else
			mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
	}
	
	public void onTomarClicked(View view) 
			throws InterruptedException {
		mCamera.takePicture(null, null, this);		
	}
	
	@Override
    public void onPictureTaken(byte[] data, Camera camera) {
    	File pictureFile;
    	try {
    		pictureFile = obtenerArchivo();    		
    	}
    	catch (Exception e) {
    		Log.d(TAG, "Error al crear el archivo de imagen, verificar permisos de almacenamiento: " +
    				e.getMessage());
            return;
    	}	        

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            this.setResult(RESULT_OK);            
        } catch (FileNotFoundException e) {
            Log.d(TAG, "No se encontro el archivo: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "No se pudo acceder al archivo: " + e.getMessage());
        }	    	   
        
		this.finish();
    }
	
	public static File obtenerArchivo(){
    	//crea una subcarpeta dentro de DIRECTORY_PICTURES
		File sysPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File mediaStorageDir = new File(sysPath, IMG_FOLDER);
 
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "No se pudo crear directorio para almacenar fotos");
                return null;
            }
        }
 
        // instancia el File apuntando al destino        
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + FILE_NAME);
        return mediaFile;
    }
	
	private Camera abrirCamara(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultCam = prefs.getString(CameraSettings.KEY_CAMERA_NUM, null);                
        Camera c = null; // regresa null si no se pudo abrir una camara
        try {
        	if ( defaultCam == null)
        		c = Camera.open();
        	else
        		c = Camera.open(Integer.parseInt(defaultCam));
        }
        catch (Exception e){
        	Log.d(TAG, "Error al abrir la camara: " + e.getMessage());
        }
        return c; 
    }      
	
}
