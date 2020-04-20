/*******************************************************************************
* 
*   File: Basic3D.java
*   Authors: Steven Phung, Roberto Ramirez, Alan Trieu
*   Class: CS 4450.01 - Computer Graphics
*
*   Assignment: Final Program Check Point 1
*   Date last modified: 4/20/2020
*
*   Purpose: Purpose of this class is to create our 640x480 window, using
*           display mode and a first person camera.
*
*******************************************************************************/
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Basic3D {
    
    //Camera and display mode
    private FPCameraController fp;
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    //Method: start()
    //Purpose: Create window and initiate openGL, and render to window
    public void start(){
        try{
            createWindow();
            fp = new FPCameraController(0f, 0f, 0f, 1);
            initGL();
            fp.gameLoop();      //render();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //Method: createWindow()
    //Purpose: create a 640x480 window
    private void createWindow() throws Exception{
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++){
            if(d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32 ){
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Goldencraft");
        Display.create();
    }
    
    //Method: initGL()
    //Purpose: initiate gl properties
    private void initGL(){
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0
    }
    
    //Method: initLightArrays()
    //Purpose: initiate light arrays
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
        }
    
    //Method: main()
    //Purpose: Start program
    public static void main(String args[]){
        Basic3D basic = new Basic3D();
        basic.start();
    }
}
