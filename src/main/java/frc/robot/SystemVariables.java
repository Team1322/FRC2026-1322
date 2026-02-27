package frc.robot;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import frc.robot.generated.TunerConstants;

public class SystemVariables {

    public static double turretDistanceFromGoal = 0;
    public static Rotation2d turretAngleToGoal = Rotation2d.kZero, turretZeroDirection = Rotation2d.kZero;
    public static boolean shooterUpToSpeed = false;
    public static Pose2d turretPose = Pose2d.kZero;

    //public static boolean 
    public static final class DrivetrainConstants {
        public static final double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
        public static final double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity
        public static final double DRIVE_TO_POSE_TOLERANCE = Units.inchesToMeters(1);
    }
    
    public static final class IntakeConstants {
        public static final int INTAKE_MOTOR_0 = 20;
        //public static final int INTAKE_MOTOR_1 = 21;
    }

    public static final class FeederConstants {
        public static final int FEEDER_MOTOR_ID = 30;
        public static final int FEEDER_FOLLOWER_ID = 31;
    }

    public static final class ShooterConstants {
        public static final int SHOOT_MOTOR_ID = 40;
        public static final int SHOOT_FOLLWER_ID = 41;
        public static final double SHOOTER_HEIGHT = Units.inchesToMeters(22);
        public static final double GOAL_HEIGHT = 1.8 - SHOOTER_HEIGHT;
        public static final double SHOOT_ANGLE = Units.degreesToRadians(90 - 30);
    }

     public static final class TurretConstants {
        public static final int TURRET_MOTOR_ID = 42;
        public static final int TURRET_SENSOR_ID = 42;
        public static final double KP = 0.01;
        public static final double KI = 0.00;
        public static final double KD = 0.0002;
       public static final double RIGHT_LIMIT = -90;
       public static final double LEFT_LIMIT = 90;
       public static final double MOTOR_CONVERSION_FACTOR = (360.0 / 20);
       public static final double ENCODER_CONVERSION_FACTOR = (1.0 / 6.666);
        public static final Transform2d TURRET_LOCATION = new Transform2d(
            Units.inchesToMeters(8),
            Units.inchesToMeters(0), 
            Rotation2d.kZero
        );
    }

    public static final class LiftConstants {
        public static final int LIFT_MOTOR_ID = 50;
        public static final int LIFT_SENSOR_ID = 50;
        public static final double KP = 0.06;
        public static final double KI = 0.0;
        public static final double KD = 0.003;
        public static final double POSE_TOLERANCE = .25;

        public static enum LiftStates{
            COMPACT,
            INTAKE,
            CLIMBED,
            DISTURB
        }
    }

    public static final class FieldConstants{
        public static final Translation2d RED_GOAL = new Translation2d(Units.inchesToMeters(650.12 - 181.56),Units.inchesToMeters(158.32));
        public static final Translation2d BLUE_GOAL = new Translation2d(Units.inchesToMeters(181.56),Units.inchesToMeters(158.32));
    }

}
