package at.vintagestory.modelcreator.enums;

import at.vintagestory.modelcreator.util.GameMath;
import at.vintagestory.modelcreator.util.Vec3f;

public class BlockFacing
{
	public static byte HorizontalFlags = 1 | 2 | 4 | 8;
    public static byte VerticalFlags = 16 | 32;

    // Right Handed Coordinate System
    // http://www.matrix44.net/cms/notes/opengl-3d-graphics/coordinate-systems-in-opengl
    public static BlockFacing NORTH = new BlockFacing("North", "north", 1, 0, 2, new Vec3f(0, 0, -1), new Vec3f(0.5f, 0.5f, 0f), EnumAxis.Z);
    public static BlockFacing EAST = new BlockFacing("East", "east", 2, 1, 3, new Vec3f(1, 0, 0), new Vec3f(1f, 0.5f, 0.5f), EnumAxis.X);
    public static BlockFacing SOUTH = new BlockFacing("South", "south", 4, 2, 0, new Vec3f(0, 0, 1), new Vec3f(0.5f, 0.5f, 1f), EnumAxis.Z);
    public static BlockFacing WEST = new BlockFacing("West", "west", 8, 3, 1, new Vec3f(-1, 0, 0), new Vec3f(0, 0.5f, 0.5f), EnumAxis.X);


    public static BlockFacing UP = new BlockFacing("Up", "up",      16, 4, 5, new Vec3f(0, 1, 0), new Vec3f(0.5f, 1, 0.5f), EnumAxis.Y);
    public static BlockFacing DOWN = new BlockFacing("Down", "down",  32, 5, 4, new Vec3f(0, -1, 0), new Vec3f(0.5f, 0, 0.5f), EnumAxis.Y);

    /// <summary>
    /// All block faces in the order of N, E, S, W, U, D
    /// </summary>
    public static BlockFacing[] ALLFACES = new BlockFacing[] { NORTH, EAST, SOUTH, WEST, UP, DOWN };

    public static BlockFacing[] HORIZONTALS = new BlockFacing[] { NORTH, EAST, SOUTH, WEST };
    public static BlockFacing[] VERTICALS = new BlockFacing[] { UP, DOWN };
    public static BlockFacing[] HORIZONTALS_ANGLEORDER = new BlockFacing[] { EAST, NORTH, WEST, SOUTH };

    int index;
    byte flag;
    int oppositeIndex;
    Vec3f facingVector;
    Vec3f planeCenter;
    String code;
    EnumAxis axis;
    
    float angleP;
    float angleT;
    
    public String Name;

    public byte GetFlag() { return flag; } 
    public int GetIndex() { return index; }
    public Vec3f GetFacingVector() { return facingVector; } // Plane Normal
    public Vec3f GetPlaneCenter() { return planeCenter; }
    public String GetCode() { return code; }
    public boolean GetIsHorizontal() { return index <= 3; }
    public boolean GetIsVertical() { return index >= 4; }
    public EnumAxis GetAxis() { return axis; }
    
    public float GetPAngle() { return angleP; }
    public float GetTAngle() { return angleT; }

    private BlockFacing(String name, String code, int flag, int index, int oppositeIndex, Vec3f facingVector, Vec3f planeCenter, EnumAxis axis)
    {
        this.index = index;
        this.flag = (byte)flag;
        this.code = code;
        this.oppositeIndex = oppositeIndex;
        this.facingVector = facingVector;
        this.planeCenter = planeCenter;
        this.axis = axis;
        
        Name = name;
        
        angleP = (float)Math.acos(facingVector.Y);
        angleT = (float)Math.atan(facingVector.Z / facingVector.X);
    }

    public BlockFacing GetOpposite()
    {
        return ALLFACES[oppositeIndex];
    }

    public static BlockFacing FromCode(String code)
    {
        code = code.toLowerCase();

        switch(code)
        {
            case "north": return NORTH;
            case "south": return SOUTH;
            case "east": return EAST;
            case "west": return WEST;
            case "up": return UP;
            case "down": return DOWN;
        }

        return null;
    }

    public static BlockFacing FromVector(float[] vec)
    {
        if (vec[0] != 0)
        {
            return vec[0] > 0 ? EAST : WEST;
        }
        if (vec[1] != 0)
        {
            return vec[1] > 0 ? UP : DOWN;
        }
        if (vec[2] != 0)
        {
            return vec[2] > 0 ? NORTH : SOUTH;
        }

        return null;
    }

    public static BlockFacing HorizontalFromAngle(float radiant)
    {
        int index = ((int)(Math.round(radiant * GameMath.RAD2DEG / 90))) % 4;

        return HORIZONTALS_ANGLEORDER[index];
    }
    


    public static BlockFacing FromNormal(Vec3f vec)
    {
        float smallestAngle = GameMath.PI;
        BlockFacing facing = null;


        for (int i = 0; i < ALLFACES.length; i++)
        {
            BlockFacing f = ALLFACES[i];
            float angle = (float)Math.acos(f.facingVector.Dot(vec));

            if (angle < smallestAngle)
            {
                smallestAngle = angle;
                facing = f;
            }
        }

        return facing;
    }



    public String ToString()
    {
        return code;
    }


    public static boolean FlagContains(byte flag, BlockFacing facing)
    {
        return (flag & facing.flag) > 0;
    }

    public static boolean FlagContainsHorizontals(byte flag)
    {
        return (flag & HorizontalFlags) > 0;
    }

}