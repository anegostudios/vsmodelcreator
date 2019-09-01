package at.vintagestory.modelcreator.util;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

public class QUtil
{
	public static Quaternion ToQuaternion(double yaw, double pitch, double roll) // yaw (Z), pitch (Y), roll (X)
	{
	    // Abbreviations for the various angular functions
	    double cy = Math.cos(yaw * 0.5);
	    double sy = Math.sin(yaw * 0.5);
	    double cp = Math.cos(pitch * 0.5);
	    double sp = Math.sin(pitch * 0.5);
	    double cr = Math.cos(roll * 0.5);
	    double sr = Math.sin(roll * 0.5);

	    double w = cy * cp * cr + sy * sp * sr;
	    double x = cy * cp * sr - sy * sp * cr;
	    double y = sy * cp * sr + cy * sp * cr;
	    double z = sy * cp * cr - cy * sp * sr;

	    Quaternion q = new Quaternion((float)x, (float)y, (float)z, (float)w);
	    q.normalise();
	    
	    return q;
	}
	
	


    public static double[] ToEulerAngles(Quaternion q)
    {
    	double[] angles = new double[3];

        // roll (x-axis rotation)
        double sinr_cosp = +2.0 * (q.w * q.x + q.y * q.z);
        double cosr_cosp = +1.0 - 2.0 * (q.x * q.x + q.y * q.y);
        angles[2] = Math.atan2(sinr_cosp, cosr_cosp);

        // pitch (y-axis rotation)
        double sinp = +2.0 * (q.w * q.y - q.z * q.x);
        if (Math.abs(sinp) >= 1)
            angles[1] = Math.PI / 2 * Math.signum(sinp); // use 90 degrees if out of range
        else
            angles[1] = Math.asin(sinp);

        // yaw (z-axis rotation)
        double siny_cosp = +2.0 * (q.w * q.z + q.x * q.y);
        double cosy_cosp = +1.0 - 2.0 * (q.y * q.y + q.z * q.z);  
        angles[0] = Math.atan2(siny_cosp, cosy_cosp);

        return angles;
    }
    
    
    public static double[] MatrixToEuler(float[] matrix) {
        Quaternion q = new Quaternion();
        Matrix4f mat = new Matrix4f();
        mat.m00 = matrix[0];
        mat.m01 = matrix[1];
        mat.m02 = matrix[2];
        mat.m03 = matrix[3];
        mat.m10 = matrix[4];
        mat.m11 = matrix[5];
        mat.m12 = matrix[6];
        mat.m13 = matrix[7];
        mat.m20 = matrix[8];
        mat.m21 = matrix[9];
        mat.m22 = matrix[10];
        mat.m23 = matrix[11];
        mat.m30 = matrix[12];
        mat.m31 = matrix[13];
        mat.m32 = matrix[14];
        mat.m33 = matrix[15];
        Quaternion.setFromMatrix(mat, q);

        
        return QUtil.ToEulerAngles(q);
    }
}
