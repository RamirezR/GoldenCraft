/*******************************************************************************
* 
*   File: FPCameraController.java
*   Authors: Steven Phung, Roberto Ramirez, Alan Trieu
*   Class: CS 4450.01 - Computer Graphics
*
*   Assignment: Final Program Check Point 1
*   Date last modified: 3/5/2020
*
*   Purpose: Purpose of this class is to create our camera and control its
*           movement logic, and to render polygons. 
*
*******************************************************************************/
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

public class FPCameraController {
    
    //3D vector to store the camera's position in
    private Vector3f position = null;
    private Vector3f IPostion = null;
    
    // the rotation around the Y axis of the camera
    private float yaw = 0.0f;
    
    // the roation around the X axis of the camera
    private float pitch = 0.0f;
    private Vector3Float me;
    
    //Constructor: FPCameraController
    //Purpose: Instantiate an FPCameraController object 
    public FPCameraController(float x, float y, float z){
        // instantiate postion Vector3f to the x y z parameters
        position = new Vector3f(x, y, z);
        IPostion = new Vector3f(x, y, z);
        
        IPostion.x = 0f;
        IPostion.y = 15f;
        IPostion.z = 0f;
    }
    
    //Method: yaw(float)
    //Purpose: Used to increment yaw
    public void yaw(float amount){
        // increment the yaw by the amount parameter
        yaw += amount;
    }
    
    //Method: pitch(float)
    //Purpose: increment the camera's current yaw rotation
    public void pitch(float amount){
        // increment the pitch by the amount parameter
        pitch -= amount;
    }
    
    //Method: walkForward(float)
    //Purpose: moves the camera forward relative to its current rotation (yaw)
    public void walkForward(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    //Method: walkBackwards(float)
    //Purpose: moves the camera backwards relative to its current rotation (yaw)
    public void walkBackwards(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }
    
    //Method: strafeLeft(float)
    //Purpose: strafes the camera left relative to its its current rotation (yaw) 
    public void strafeLeft(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians( yaw - 90 ));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    //Method: strafeLeft(float)
    //Purpose: strafes the camera right relative to its its current rotation (yaw) 
    public void strafeRight(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians( yaw + 90 ));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    //Method: moveUp(float)
    //Purpose: moves the camera up relative to its current rotation (yaw)
    public void moveUp(float distance){
        position.y -= distance;
    }
    
    //Method: moveDown(float)
    //Purpose: moves the camera down
    public void moveDown(float distance){
        position.y += distance;
    }
    
    //Purpose: translates and rotate the matrix so that it looks through the camera
    // this does basically what gluLookAt() does
    public void lookThrough(){
        // rotate the pitch around the X axis 
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        // rotate the yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        // translate to the position vectors's location
        glTranslatef(position.x, position.y, position.z);
    }
    
    //Method: gameLoop()
    //Purpose: game loop that allows user to interact with game world using camera
    public void gameLoop(){
        FPCameraController camera = new FPCameraController(0, 0, -2); //Start outside of cube
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f;                    // length of frame
        float lastTime = 0.0f;              // when the last frame was
        long time;
        float mouseSensitivity = 0.09f;
        float movementSpeed = 0.35f;
        Mouse.setGrabbed(true);             // hide the mouse
        
        // keep looping till the display window is closed the ESC key is down
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            time = Sys.getTime();
            lastTime = time;
        
            // distance in mouse movement from the last getDX() call
            dx = Mouse.getDX();
            // distance in mouse movement from the last getDY() call
            dy = Mouse.getDY();
            
            // controll camera yaw from x movement from the mouse 
            camera.yaw(dx * mouseSensitivity);
            // controll camera pitch from y movement from the mouse
            camera.pitch(dy * mouseSensitivity);
            
            // when passing the distance to move we multiply the movementSpeed with dt this is a time scale
            // so if its a slow frame you move more then a fast frame so on a slow computer you move just as fast as on a fast computer
            if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)){        // move forward
                camera.walkForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)){        // move backward
                camera.walkBackwards(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)){        // strafe left
                camera.strafeLeft(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){        // strafe right
                camera.strafeRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){    // move up
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){   // move down
                camera.moveDown(movementSpeed);
            }
            
            // set the modelview matrix back to the identity
            glLoadIdentity();
            // look through the camera before you draw anything
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            // you would draw your scene here
            
            render();
            // draw the buffer to the screen
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
    
    private void render(){
        try{
            glBegin(GL_QUADS);
                // top
                glColor3f(0.0f, 0.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, 1.0f);
                // bottom
                glColor3f(0.0f, 1.0f, 0.0f);
                glVertex3f(1.0f, -1.0f, 1.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);
                // front
                glColor3f(1.0f, 0.0f, 0.0f);
                glVertex3f(1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, 1.0f);
                // back
                glColor3f(1.0f, 0.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);
                // left side
                glColor3f(1.0f, 1.0f, .0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);
                // right side
                glColor3f(0.0f, 1.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);
                glVertex3f(1.0f, 1.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
                // top
                glColor3f(0.0f,0.0f,0.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, 1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
                // bottom
                glVertex3f(1.0f, -1.0f, 1.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
                // front
                glVertex3f(1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, 1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
                // back
                glVertex3f(1.0f, -1.0f, -1.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
                // left
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
                // right
                glVertex3f(1.0f, 1.0f, -1.0f);
                glVertex3f(1.0f, 1.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);
            glEnd();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
