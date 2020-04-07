/*******************************************************************************
* 
*   File: Block.java
*   Authors: Steven Phung, Roberto Ramirez, Alan Trieu
*   Class: CS 4450.01 - Computer Graphics
*
*   Assignment: Final Program Check Point 2
*   Date last modified: 4/6/2020
*
*   Purpose: Purpose of this class is to create our different types of blocks.
*
*******************************************************************************/
public class Block {
    private boolean isActive;
    private BlockType type;
    private float x, y, z;
    
    //Enumerated list of block types
    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);
        
        private int BlockID;
        
        //Method: BlockType
        //Purpose: Set block type to specific ID
        BlockType(int i) {
            BlockID = i;
        }
        
        //Method: getID
        //Purpose: Return the BlockID
        public int getID() {
            return BlockID;
        }
        
        //Method: setID
        //Purpose: Set BlockID to new ID
        public void setID(int i) {
            BlockID = i;
        }
    }
    
    //Method: Block
    //Purpose: Create a block object with a specified block type
    public Block(BlockType type) {
        this.type = type;
    }
    
    //Method: setCoords
    //Purpose: Set x, y, z coordinates
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //Method: isActive
    //Purpose: Return whether or not block is active
    public boolean isActive() {
        return isActive;
    }
    
    //Method: setActive
    //Purpose: Set block to active or not active
    public void setActive(boolean active) {
        isActive = active;
    }
    
    //Method: getID
    //Purpose: Return BlockID
    public int getID() {
        return type.getID();
    }
}
