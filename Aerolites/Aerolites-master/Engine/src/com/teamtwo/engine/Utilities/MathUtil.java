package com.teamtwo.engine.Utilities;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector3f;

/**
 * Various useful maths functions
 */
public final class MathUtil {

    /**
     * A floating point representation of Pi
     */
    public static final float PI = (float)Math.PI;

    /**
     * A floating point representation of 2 * {@link #PI};
     */
    public static final float PI2 = PI * 2f;

    /**
     * A very small value for checking if floating point numbers are zero
     */
    public static final float EPSILON = 1E-6f;

    /**
     * Used to convert angles from Degrees to Radians
     */
    public static final float DEG_TO_RAD = PI / 180f;

    /**
     * Used to convert angles from Radians to Degrees
     */
    public static final float RAD_TO_DEG = 180f / PI;

    // Private constructor to prevent instantiation
    private MathUtil() {}

    /**
     * Calculates the square of the given number
     * @param number The number to square
     * @return The square
     */
    public static float square(float number) { return number * number; }

    /**
     * A floating point conversion for {@link Math#cos(double)}
     * @param radians The angle, in radians
     * @return The cosine of the angle
     */
    public static float cos(float radians) { return (float)Math.cos(radians); }

    /**
     * A floating point conversion for {@link Math#sin(double)}
     * @param radians The angle, in radians
     * @return The sine of the angle
     */
    public static float sin(float radians) { return (float)Math.sin(radians); }

    /**
     * Generates a random integer between the values specified, [min, max)
     * @param min The lower bound to generate between
     * @param max The upper bound to generate between
     * @return A random integer between the values
     */
    public static int randomInt(int min, int max) {
        return min + (int)(Math.random() * (max - min));
    }

    /**
     * Generates a random float between the values specified, [min, max)
     * @param min The lower bound to generate between
     * @param max The upper bound to generate between
     * @return A random float between the values
     */
    public static float randomFloat(float min, float max) {
        return min + (float)(Math.random() * (max - min));
    }

    /**
     * Gets the linear interpolation value at the given value between the two points
     * @param begin The beginning value
     * @param end The end value
     * @param value The progression between the begin and end points
     * @return The linear interpolation between the two values
     */
    public static float lerp(float begin, float end, float value) {
        value = clamp(value, 0, 1);
        return (end * value) + (begin * (1 - value));
    }

    /**
     * Gets the linear interpolation colour between the two given colours
     * @param beginColour The beginning colour
     * @param endColour The ending colour
     * @param value The progression between the begin and end colours
     * @return The linear interpolation colour between the given ones
     */
    public static Color lerpColour(Color beginColour, Color endColour, float value) {
        value = clamp(value, 0, 1);
        int r = (int) ((endColour.r * value) + (beginColour.r * (1 - value)));
        int g = (int) ((endColour.g * value) + (beginColour.g * (1 - value)));
        int b = (int) ((endColour.b * value) + (beginColour.b * (1 - value)));

        return new Color(r, g, b);
    }

    /**
     * Clamps the given value between the min and max values
     * @param value The value to clamp
     * @param min The minimum that value can be
     * @param max The maximum that value can be
     * @return If value greater than max then max, else if value less than min then min, else value
     */
    public static float clamp(float value, float min, float max) {
        float end;

        if(max < min) {
            end = max;
            max = min;
            min = end;
        }

        end = value;

        if(value > max) {
            end = max;
        }
        else if(value < min) {
            end = min;
        }

        return end;
    }

    /**
     * Rounds a value to the specified number of decimal places
     * @param value The value to round
     * @param places The number of decimal places to leave
     * @return The value given rounded to the decimal places specified
     */
    public static float round(float value, int places) {
        int multi = (int)Math.pow(10, places);
        return (float)Math.round(value * multi) / (float)multi;
    }

    /**
     * Checks if the given floating point value is less than {@link MathUtil#EPSILON}
     * @param value The value to check
     * @return True if the value is smaller than {@link MathUtil#EPSILON}, otherwise False
     */
    public static boolean isZero(float value) {
        return Math.abs(value) < EPSILON;
    }

    /**
     * Checks if the absolute value of the given floating point value is less than the tolerance given
     * @param value The value to check
     * @param tolerance The tolerance to check if the value is less than
     * @return True if the value is smaller than tolerance, otherwise False
     */
    public static boolean isZero(float value, float tolerance) {
        return Math.abs(value) < tolerance;
    }

    /**
     * Will change an angles range to be between {@link #PI} and -{@link #PI}
     * @param radians the angle you wish to normalize in radians
     * @return the angle in radians between 180 degrees and -179 degrees
     */
    public static float normalizeAngle(float radians) {
        while (radians <= -MathUtil.PI) radians += MathUtil.PI2;
        while (radians > MathUtil.PI) radians -= MathUtil.PI2;
        return radians;
    }

    // Vector2 Methods -- Vector2f doesn't have these for some reason

    /**
     * Gets the length of the given vector
     * @param vector The vector to find the length of
     * @return The length of the vector
     */
    public static float length(Vector2f vector) {
        return (float) Math.sqrt((vector.x * vector.x) + (vector.y * vector.y));
    }

    /**
     * Gets the length of the vector given square
     * @param vector The vector to find the length of
     * @return The square length of the vector
     */
    public static float lengthSq(Vector2f vector) {
        return (vector.x * vector.x) + (vector.y * vector.y);
    }

    /**
     * Returns the normalised vector of the given one
     * @param vector The vector to normalise
     * @return The normalised vector
     */
    public static Vector2f normalise(Vector2f vector) {
        float len = length(vector);

        if(len != 0) {
            return new Vector2f(vector.x / len, vector.y / len);
        }

        return Vector2f.ZERO;
    }

    /**
     * Calculates the resultant dot product of the vectors supplied
     * @param a The first vector to perform the dot product on
     * @param b The second vector to perform the dot product on
     * @return The resultant dot product between the two vectors
     */
    public static float dot(Vector2f a, Vector2f b) {
        return (a.x * b.x) + (a.y * b.y);
    }

    /**
     * Calculates the cross product of the vectors supplied
     * @param a The first vector to perform the cross product on
     * @param b The second vector to perform the cross product on
     * @return The resultant cross product between the two vectors
     */
    public static float cross(Vector2f a, Vector2f b) {
        return (a.x * b.y) - (a.y * b.x);
    }

    /**
     * Converts a Vector3f to a Vector2f using just the x and y components
     * @param v The Vector3f to convert
     * @return The x and y components as a Vector2f
     */
    public static Vector2f toVector2f(Vector3f v) { return new Vector2f(v.x, v.y); }

    /**
     * returns the mid point of two vectors
     * @param a the first vector
     * @param b the second vector
     * @return the mid point vector
     */
    public static Vector2f midPoint(Vector2f a, Vector2f b){
        return new Vector2f((a.x + b.x) / 2f, (a.y + b.y) / 2f);
    }
}
