/*******************************************************************************
* 
*   File: Chunk.java
*   Authors: Steven Phung, Roberto Ramirez, Alan Trieu
*   Class: CS 4450.01 - Computer Graphics
*
*   Assignment: Final Program Check Point 2
*   Date last modified: 4/6/2020
*
*   Purpose: Purpose of this class is to create a chunk of blocks.
*
*******************************************************************************/
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block [][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    
    private int VBOTextureHandle;
    private Texture texture;
    
    private SimplexNoise noise;
    private int seed;
    
    public void render() {
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);
            glTexCoordPointer(2,GL_FLOAT,0,0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    public void rebuildMesh(float startX, float startY, float startZ) {
        int[][] materialBoundaries = new int[4][4];
        //sand
        materialBoundaries[0][0] = r.nextInt(10); //x-min
        materialBoundaries[0][1] = r.nextInt(10) + 10; //x-max
        materialBoundaries[0][2] = r.nextInt(10); //z-min
        materialBoundaries[0][3] = r.nextInt(10) + 10; //z-max
        //water
        materialBoundaries[1][0] = r.nextInt(10); //x-min
        materialBoundaries[1][1] = r.nextInt(10) + 10; //x-max
        materialBoundaries[1][2] = r.nextInt(10); //z-min
        materialBoundaries[1][3] = r.nextInt(10) + 10; //z-max
        //dirt
        materialBoundaries[2][0] = r.nextInt(10); //x-min
        materialBoundaries[2][1] = r.nextInt(10) + 10; //x-max
        materialBoundaries[2][2] = r.nextInt(10); //z-min
        materialBoundaries[2][3] = r.nextInt(10) + 10; //z-max
        //stone
        materialBoundaries[3][0] = r.nextInt(10); //x-min
        materialBoundaries[3][1] = r.nextInt(10) + 10; //x-max
        materialBoundaries[3][2] = r.nextInt(10); //z-min
        materialBoundaries[3][3] = r.nextInt(10) + 10; //z-max
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        int height;
        
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        for(float x = 0; x < CHUNK_SIZE; x += 1) {
            for(float z = 0; z < CHUNK_SIZE; z += 1) {
                height = (int) (((noise.getNoise((int)x + (int)startX/2, (int)z + (int)startZ/2)) + 1) / 2 * 10) + 5;
                for(float y = 0; y <= height; y++){
                    if(y <= height) {
                        //Spawn sand and water at top level
                        if(y == height) {
                            //Check if spawning water or sand is a valid spawn
                            if(checkMaterialBoundaries(0, (int)x, (int)y, (int)z, materialBoundaries)) {
                                Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Sand);
                            } else if(checkMaterialBoundaries(1, (int)x, (int)y, (int)z, materialBoundaries)) {
                                Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Water);
                            } else {
                                Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Grass);
                            }
                        //Spawn bedrock at bottom level
                        } else if(y == 0) {
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Bedrock);
                        //Spawn dirt and stone in between
                        } else {
                            double rand = r.nextDouble();
                            if(rand <= 0.5)
                                Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Dirt);
                            else
                                Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Stone);
                        }
                    }
                    VertexPositionData.put(createCube((float)(startX + x * CUBE_LENGTH), (float)(startY + y * CUBE_LENGTH), (float)(startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x + (int)startX][(int) y][(int) z + (int)startZ])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int) x + (int)startX][(int) y][(int) z + (int)startZ]));
                }
            }
        }
        
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    private boolean checkMaterialBoundaries(int type, int x, int y, int z, int[][] bounds){
        if(x >= bounds[type][0] &&
           x <= bounds[type][1] &&
           z >= bounds[type][2] &&
           z <= bounds[type][3])
        {
            return true;
        }
        return false;
    }
    
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for(int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
            //Top
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            //Bottom
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            //Front
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            //Back
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            //Left
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            //Right
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z
        };
    }
    
    private float[] getCubeColor(Block block) {
        return new float[] { 1, 1, 1 };
    }
    
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f;
        switch(block.getID()) {
            case 0://grass
                return new float[] {
                // BOTTOM QUAD
                x + offset*3, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // TOP
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1};
                
            case 1://sand
                return new float[] {
                // BOTTOM QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // TOP
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // FRONT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2, 
                x + offset*2, y + offset*2,
                // BACK QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // LEFT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // RIGHT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2};
                
            case 2://water
                return new float[] {
                // BOTTOM QUAD
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // TOP
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // FRONT QUAD
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1, 
                x + offset*14, y + offset*1,
                // BACK QUAD
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // LEFT QUAD
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                // RIGHT QUAD
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1}; 
                
            case 3://dirt
                return new float[] {
                // BOTTOM QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // TOP
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1, 
                x + offset*2, y + offset*1,
                // BACK QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // LEFT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // RIGHT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1};
                
            case 4://stone
                return new float[] {
                // BOTTOM QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // TOP
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // FRONT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1, 
                x + offset*1, y + offset*1,
                // BACK QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // LEFT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                // RIGHT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1};
              
            case 5://bedrock
                return new float[] {
                // BOTTOM QUAD
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // TOP
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2, 
                x + offset*1, y + offset*2,
                // BACK QUAD
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // LEFT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // RIGHT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2};
                
            default:
                return new float[] { 1, 1, 1 };
        }
    }
    
    public Chunk(int startX, int startY, int startZ, int s) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        } catch (Exception e) {
            System.out.println("Error!");
        }
        seed = s;
        noise = new SimplexNoise(120, 0.5, seed);
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for(int x = 0; x < CHUNK_SIZE; x++) {
            for(int y = 0; y < CHUNK_SIZE; y++) {
                for(int z = 0; z < CHUNK_SIZE; z++) {
                    if(y == 0) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    } else if(r.nextFloat() > 0.8f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    } else if(r.nextFloat() > 0.6f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    } else if(r.nextFloat() > 0.4f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    } else if(r.nextFloat() > 0.2f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    } else {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    }
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
}
