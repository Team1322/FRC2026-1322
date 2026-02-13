package frc.robot;

import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.units.measure.LinearVelocity;
import frc.robot.SystemVariables.DrivetrainConstants;

/**
 * Defines the way drive to pose works by using 3 possible parameters
 * <p>
 * All DriveToPoseObjects must have a Pose2d passed in, this is the target for this move
 * <p>
 * The class also holds an optional parameter of distance (in meters) until the move contines on and also makes it a continous move
 * <p>
 * Another optional parameter is the stepSpeed, which is the max speed the robot can acheive during that move
 * <ul>
 *   <li> Defined by doing MetersPerSecond.of(double);
 */
public class DriveToPoseObject {

    private final Pose2d pose;
    private final double distanceUntilBypass;
    private final LinearVelocity stepSpeed;
    
    public DriveToPoseObject(Pose2d pose, double distanceUntilBypass, LinearVelocity stepSpeed) {
        this.pose = pose;
        this.distanceUntilBypass = distanceUntilBypass;
        this.stepSpeed = stepSpeed;
    }

    public DriveToPoseObject(Pose2d pose, double distanceUntilBypass) {
        this(pose, distanceUntilBypass, MetersPerSecond.of(DrivetrainConstants.MaxSpeed));
    }

    public DriveToPoseObject(Pose2d pose, LinearVelocity stepSpeed) {
        this(pose, 0, stepSpeed);
    }

    public DriveToPoseObject(Pose2d pose) {
        this(pose, 0);
    }

    public Pose2d getPose() {
        return pose;
    }

    public double getDistanceUntilBypass() {
        return distanceUntilBypass;
    }

    public boolean isFineMove() {
        return getDistanceUntilBypass() == 0;
    }

    public LinearVelocity getMaxSpeed() {
        return stepSpeed;
    }
}
