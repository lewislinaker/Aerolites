package com.teamtwo.engine.Physics.Collisions;

import org.jsfml.system.Vector2f;

/**
 * A class which represents an Axis-Aligned Bounding Box, used within the broad phase collision detection
 */
public class AABB {

    /** The position of the centre of the AABB */
    private Vector2f centre;
    /** The half width and height of the AABB */
    private Vector2f halfSize;

    /**
     * Creates an AABB at (0, 0) with a with and height of 0
     */
    public AABB() {
        centre = Vector2f.ZERO;
        centre = Vector2f.ZERO;
    }

    /**
     * Creates an AABB at the centre specified with the half size given
     * @param centre The centre of the AABB
     * @param halfSize The half width and height of the AABB
     */
    public AABB(Vector2f centre, Vector2f halfSize) {
        this.centre = centre;
        this.halfSize = halfSize;
    }

    /**
     * Finds the minimum and maximum bounds of the given vertices and creates an AABB using them
     * @param vertices The vertices to create the AABB from
     */
    public AABB(Vector2f[] vertices) {
        float minX, maxX;
        float minY, maxY;

        // Initial Values
        minX = maxX = vertices[0].x;
        minY = maxY = vertices[0].y;

        for(int i = 1; i < vertices.length; i++) {
            // Find min and max x values
            if(vertices[i].x < minX) {
                minX = vertices[i].x;
            }
            else if(vertices[i].x > maxX) {
                maxX = vertices[i].x;
            }

            // Find min and max y values
            if(vertices[i].y < minY) {
                minY = vertices[i].y;
            }
            else if(vertices[i].y > maxY) {
                maxY = vertices[i].y;
            }
        }

        // Find the centre
        centre = new Vector2f((maxX + minX) / 2f, (maxY + minY) / 2f);
        // Find the half width and height
        halfSize = new Vector2f((maxX - minX) / 2f, (maxY - minY) / 2f);
    }

    /**
     * Moves the centre of the AABB to the position given
     * @param centre The new centre to move to
     */
    public void setCentre(Vector2f centre) { this.centre = centre; }

    /**
     * Resizes the AABB to the the given half width and half height
     * @param halfSize The new half width and height to resize to
     */
    public void setHalfSize(Vector2f halfSize) { this.halfSize = halfSize; }

    /**
     * Gets the current centre of the AABB
     * @return The centre
     */
    public Vector2f getCentre() { return centre; }

    /**
     * Gets the current half width and height of the AABB
     * @return The half size
     */
    public Vector2f getHalfSize() { return halfSize; }

    /**
     * Checks if two AABBs overlap
     * @param one The first AABB to check
     * @param two The second AABB to check
     * @return If the two AABBs overlap then true, otherwise false
     */
    public static boolean overlaps(AABB one, AABB two) {
        // Check x axis
        if(Math.abs(one.centre.x - two.centre.x) > one.halfSize.x + two.halfSize.x)
            return false;
        // Check y axis
        if(Math.abs(one.centre.y - two.centre.y) > one.halfSize.y + two.halfSize.y)
            return false;

        // They overlap
        return true;
    }
}
