package com.teamtwo.engine.Physics.Collisions;

import com.teamtwo.engine.Physics.RigidBody;
import com.teamtwo.engine.Utilities.MathUtil;
import org.jsfml.system.Vector2f;

/**
 * A class which contains information about a collision pair
 * @author James Bulman
 */
public class Pair {

    /** The first body to test for collision */
    private RigidBody A;
    /** The second body to test for collision */
    private RigidBody B;

    /** The normalised direction which is colliding the least */
    private Vector2f axis;
    /** The amount the two bodies overlap each other */
    private float overlap;

    /**
     * Creates a new collision pair from the two bodies supplied
     * @param bodyA The first body to test collision on
     * @param bodyB The second body to test collision on
     */
    public Pair(RigidBody bodyA, RigidBody bodyB) {
        A = bodyA;
        B = bodyB;

        axis = null;
        overlap = 0;
    }

    /**
     * Evaluates whether the two bodies overlap using separating axis theorem
     * @return True if they are overlapping otherwise false
     */
    public boolean evaluate() {

        // Separating Axis Theorem Collision Detection

        if(((A.getMask() & B.getCategory()) == 0) || ((B.getMask() & A.getCategory()) == 0))
            return false;

        // If they are both static then don't resolve
        if(MathUtil.isZero(A.getInvMass() + B.getInvMass()))
            return false;

        Vector2f[] vertsA = A.getShape().getTransformed();
        Vector2f[] vertsB = B.getShape().getTransformed();

        AABB aabbA = new AABB(vertsA);
        AABB aabbB = new AABB(vertsB);

        if(!AABB.overlaps(aabbA, aabbB)) return false;

        Vector2f[] normalsA = A.getShape().getNormals();
        Vector2f[] normalsB = B.getShape().getNormals();

        float minA, maxA;
        float minB, maxB;

        overlap = Float.MAX_VALUE;

        for(int i = 0; i < normalsA.length; i++) {
            minA = maxA = MathUtil.dot(normalsA[i], vertsA[0]);
            minB = maxB = MathUtil.dot(normalsA[i], vertsB[0]);

            for(int j = 1; j < vertsA.length; j++) {
                float o = MathUtil.dot(normalsA[i], vertsA[j]);

                if(o < minA) {
                    minA = o;
                }
                else if(o > maxA) {
                    maxA = o;
                }
            }

            for(int j = 1; j < vertsB.length; j++) {
                float o = MathUtil.dot(normalsA[i], vertsB[j]);

                if(o < minB) {
                    minB = o;
                }
                else if(o > maxB) {
                    maxB = o;
                }
            }

            if(minA > maxB || minB > maxA) {
                return false;
            }
            else {
                float o = Math.min(maxA, maxB) - Math.max(minA, minB);
                if(o < overlap) {
                    overlap = o;
                    axis = normalsA[i];
                }
            }
        }

        for(int i = 0; i < normalsB.length; i++) {
            minA = maxA = MathUtil.dot(normalsB[i], vertsA[0]);
            minB = maxB = MathUtil.dot(normalsB[i], vertsB[0]);

            for(int j = 1; j < vertsA.length; j++) {
                float o = MathUtil.dot(normalsB[i], vertsA[j]);

                if(o < minA) {
                    minA = o;
                }
                else if(o > maxA) {
                    maxA = o;
                }
            }

            for(int j = 1; j < vertsB.length; j++) {
                float o = MathUtil.dot(normalsB[i], vertsB[j]);

                if(o < minB) {
                    minB = o;
                }
                else if(o > maxB) {
                    maxB = o;
                }
            }

            if(minA > maxB || minB > maxA) {
                return false;
            }
            else {
                float o = Math.min(maxA, maxB) - Math.max(minA, minB);
                if(o < overlap) {
                    overlap = o;
                    axis = normalsB[i];
                }
            }
        }

        return true;
    }

    /**
     * This applies the impulse to make bodies repel from each other if they collide
     */
    public void apply() {
        if (axis == null)
            return;


        Vector2f centre = Vector2f.sub(A.getTransform().getPosition(), B.getTransform().getPosition());
        if(MathUtil.dot(centre, axis) > 0) {
            axis = Vector2f.neg(axis);
        }

        // Choose which restitution value to use
        float e = Math.min(A.getRestitution(), B.getRestitution());

        // Calculate the relative velocity between both bodies
        Vector2f rv = Vector2f.sub(B.getVelocity(), A.getVelocity());

        // If they are not moving towards each other then don't apply collision
        if(MathUtil.dot(rv, axis) > 0) return;

        // Work out the magnitude of the impulse
        float impulseMag = -(1 + e) * (MathUtil.dot(rv, axis));
        impulseMag /= (A.getInvMass() + B.getInvMass());

        // Work out the actual impulse to apply
        Vector2f impulse = new Vector2f(axis.x * impulseMag, axis.y * impulseMag);

        // Apply Impulse to body A
        A.applyImpulse(Vector2f.neg(impulse));

        // Apply Impulse to body B
        B.applyImpulse(impulse);
    }

    /**
     * This deals with objects that sink into each other<br>
     * More visible when dealing with very small bodies colliding with very large bodies, or bodies resting on static ones
     */
    public void correctPosition() {
        if(axis == null)
            return;
        float correctionVal = (Math.max(overlap - 0.05f, 0.0f) / (A.getInvMass() + B.getInvMass())) * 0.2f;

        Vector2f correction = new Vector2f(correctionVal * axis.x, correctionVal * axis.y);

        Vector2f positionA = Vector2f.sub(A.getTransform().getPosition(),
                new Vector2f(correction.x * A.getInvMass(), correction.y * A.getInvMass()));

        Vector2f positionB = Vector2f.add(B.getTransform().getPosition(),
                new Vector2f(correction.x * B.getInvMass(), correction.y * B.getInvMass()));

        A.setTransform(positionA, A.getTransform().getAngle());
        B.setTransform(positionB, B.getTransform().getAngle());
    }
}
