/*******************************************************************************
* 
*   File: Basic3D.java
*   Authors:
* 
* 
*       Steven Phung, Roberto Ramirez
*   Class: CS 4450.01 - Computer Graphics
*
*   Assignment: Final Program Check Point 1
*   Date last modified: 3/5/2020
*
*   Purpose: Purpose of this class is to create our 640x480 window, using
*           display mode and a first person camera.
*
*******************************************************************************/
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class Basic3D {
    
    //Camera and display mode
    private FPCameraController fp = new FPCameraController(0f, 0f, 0f);
    private DisplayMode displayMode;
    
    //Method: start()
    //Purpose: Create window and initiate openGL, and render to window
    public void start(){
        try{
            createWindow();
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
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    //Method: main()
    //Purpose: Start program
    public static void main(String args[]){
        Basic3D basic = new Basic3D();
        basic.start();
    }
}
