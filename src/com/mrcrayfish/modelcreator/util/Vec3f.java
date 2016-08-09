package com.mrcrayfish.modelcreator.util;

public class Vec3f
{
    public float X;
    public float Y;
    public float Z;

    public Vec3f()
    {

    }

    public Vec3f(float x, float y, float z)
    {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public float Length()
    {
        return (float)Math.sqrt(X * X + Y * Y + Z * Z);
    }

    public float Dot(Vec3f a)
    {
        return X * a.X + Y * a.Y + Z * a.Z;
    }
    
    public float Dot(float[] vec3)
    {
        return X * vec3[0] + Y * vec3[1] + Z * vec3[2];
    }

    public void Add(float x, float y, float z)
    {
        this.X += x;
        this.Y += y;
        this.Z += z;
    }

    public void Mul(float multiplier)
    {
        this.X *= multiplier;
        this.Y *= multiplier;
        this.Z *= multiplier;
    }


    public void Normalize()
    {
        float length = Length();

        X /= length;
        Y /= length;
        Z /= length;
    }


    public float Distance(Vec3f vec)
    {
        return (float)Math.sqrt(
            (X - vec.X) * (X - vec.X) +
            (Y - vec.X) * (Y - vec.Y) +
            (Z - vec.X) * (Z - vec.Z)
        );
    }


    /// <summary>
    /// Substracts x from each coordinate if the coordinate if positive, otherwise it is added. If 0, the value is unchanged. The value must be a positive number
    /// </summary>
    /// <param name="x"></param>
    /// <returns></returns>
    public void ReduceBy(float val)
    {
        X = X > 0 ? Math.max(0, X - val) : Math.min(0, X + val);
        Y = Y > 0 ? Math.max(0, Y - val) : Math.min(0, Y + val);
        Z = Z > 0 ? Math.max(0, Z - val) : Math.min(0, Z + val);
    }


    public Vec3f NormalizedCopy()
    {
        float length = Length();
        return new Vec3f(
              X / length,
              Y / length,
              Z / length
        );
    }

  
    public void Set(float x, float y, float z)
    {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }


}