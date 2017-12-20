package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.Random;

import org.bukkit.util.Vector;
import org.junit.Test;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.math.Vector3;

public class MathUtilTest {

    @Test
    public void testMatrixRotation() {
        Matrix4x4 transform = new Matrix4x4();
        transform.translateRotate(2.0, 3.0, 4.0, -12.0f, 34.0f);

        Vector3 vec = new Vector3();
        transform.transformPoint(vec);
        assertEquals(2.0, vec.x, 0.00001);
        assertEquals(3.0, vec.y, 0.00001);
        assertEquals(4.0, vec.z, 0.00001);

        Vector yawPitchRoll = transform.getYawPitchRoll();
        assertEquals(-12.0, yawPitchRoll.getX(), 0.001);
        assertEquals(34.0, yawPitchRoll.getY(), 0.001);
        assertEquals(0.0, yawPitchRoll.getZ(), 0.001);

        transform.rotateZ(30.0);
        yawPitchRoll = transform.getYawPitchRoll();
        assertEquals(-12.0, yawPitchRoll.getX(), 0.001);
        assertEquals(34.0, yawPitchRoll.getY(), 0.001);
        assertEquals(30.0, yawPitchRoll.getZ(), 0.001);

        transform.scale(1.5, 2.5, 3.5);
        yawPitchRoll = transform.getYawPitchRoll();
        assertEquals(-17.988, yawPitchRoll.getX(), 0.001);
        assertEquals(34.0, yawPitchRoll.getY(), 0.001);
        assertEquals(19.107, yawPitchRoll.getZ(), 0.001);

        // Try a bunch of random yaw/pitch/roll values and see that they all work
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            Vector result;
            Vector rotation = new Vector(180.0 * rand.nextDouble() - 90.0,
                                         180.0 * rand.nextDouble() - 90.0,
                                         180.0 * rand.nextDouble() - 90.0);

            transform = new Matrix4x4();
            transform.rotateYawPitchRoll(rotation);
            result = transform.getYawPitchRoll();
            assertEquals(rotation.getX(), result.getX(), 0.001);
            assertEquals(rotation.getY(), result.getY(), 0.001);
            assertEquals(rotation.getZ(), result.getZ(), 0.001);
        }
    }

    @Test
    public void testQuaternionRotation() {
        // Perform the same rotations with a normal 4x4 transform matrix, and a quaternion
        Matrix4x4 transform = new Matrix4x4();
        Quaternion quaternion = new Quaternion();
        transform.rotateX(20.0);
        quaternion.multiply(Quaternion.fromAxisAngles(1.0, 0.0, 0.0, 20.0));
        transform.rotateY(-33.4);
        quaternion.multiply(Quaternion.fromAxisAngles(0.0, 1.0, 0.0, -33.4));
        transform.rotateZ(12.4);
        quaternion.multiply(Quaternion.fromAxisAngles(0.0, 0.0, 1.0, 12.4));
        Matrix4x4 quaternion_transform = quaternion.toMatrix4x4();

        // Confirm that the quaternion transformation is closely equal to the other one
        double[] a = new double[16];
        double[] b = new double[16];
        transform.toArray(a);
        quaternion_transform.toArray(b);
        for (int i = 0; i < 16; i++) {
            assertEquals(a[i], b[i], 0.0001);
        }

        // Also confirm that the yaw/pitch/roll values are equal
        // There will always be some error involved, because the calculations are different
        Vector ypr_a = transform.getYawPitchRoll();
        Vector ypr_b = quaternion.getYawPitchRoll();
        assertEquals(ypr_a.getX(), ypr_b.getX(), 0.001);
        assertEquals(ypr_a.getY(), ypr_b.getY(), 0.001);
        assertEquals(ypr_a.getZ(), ypr_b.getZ(), 0.001);

        // Also test the optimized Quaternion rotateX/Y/Z functions
        quaternion = new Quaternion();
        quaternion.rotateX(20.0);
        quaternion.rotateY(-33.4);
        quaternion.rotateZ(12.4);
        quaternion_transform = quaternion.toMatrix4x4();
        quaternion_transform.toArray(b);
        for (int i = 0; i < 16; i++) {
            assertEquals(a[i], b[i], 0.0001);
        }

        // Also test Matrix multiplication with a quaternion
        // This time, the matrix is in a state that is not the identity matrix
        transform = new Matrix4x4();
        transform.translate(20.0, 32.0, -53.0);
        transform.rotateYawPitchRoll(53.0, 34.0, 90.0);
        transform.translate(100.3, -33.2, 95.3);
        quaternion_transform = transform.clone();

        quaternion = new Quaternion();
        transform.rotateYawPitchRoll(-50.3, 34.0, 12.03);
        quaternion.rotateYawPitchRoll(-50.3, 34.0, 12.03);
        quaternion_transform.rotate(quaternion);

        transform.toArray(a);
        quaternion_transform.toArray(b);
        for (int i = 0; i < 16; i++) {
            assertEquals(a[i], b[i], 0.0001);
        }

        // Verify that the 2D vector based rotateX/Y/Z functions for
        // Quaternion and Matrix4x4 do the same thing.
        transform = new Matrix4x4();
        quaternion = new Quaternion();
        transform.rotateX(2.0, 3.0);
        quaternion.rotateX(2.0, 3.0);
        transform.rotateY(-1.3, 2.5);
        quaternion.rotateY(-1.3, 2.5);
        transform.rotateZ(-0.6, 1.12);
        quaternion.rotateZ(-0.6, 1.12);
        quaternion_transform = quaternion.toMatrix4x4();
        transform.toArray(a);
        quaternion_transform.toArray(b);
        for (int i = 0; i < 16; i++) {
            assertEquals(a[i], b[i], 0.0001);
        }
    }

    @Test
    public void testQuaternionFromToRotation() {
        for (int i = 0; i < 100000; i++) {
            Vector u = randUnitVec();
            Vector v = randUnitVec();

            Quaternion a = Quaternion.fromToRotation(u, v);
            a.transformPoint(u);

            assertEquals(u.getX(), v.getX(), 0.0000001);
            assertEquals(u.getY(), v.getY(), 0.0000001);
            assertEquals(u.getZ(), v.getZ(), 0.0000001);
        }

        Vector fwd_v = new Vector(0.5, 0.3, 1.2);
        Quaternion fwd_a = Quaternion.fromToRotation(new Vector(0.0, 0.0, 1.0), fwd_v);
        Quaternion fwd_b = Quaternion.fromForwardToRotation(fwd_v);
        assertEquals(fwd_a.getX(), fwd_b.getX(), 0.0000001);
        assertEquals(fwd_a.getY(), fwd_b.getY(), 0.0000001);
        assertEquals(fwd_a.getZ(), fwd_b.getZ(), 0.0000001);
    }

    // random number between -1.0 and 1.0
    private static double randUnit() {
        return -2.0 * Math.random() + 1.0;
    }

    // random unit vector
    private static Vector randUnitVec() {
        return new Vector(randUnit(), randUnit(), randUnit()).normalize();
    }
}
