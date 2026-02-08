package frc.robot;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import frc.robot.generated.TunerConstants;

public class SystemVariables {

    public static boolean elevatorAtTarget = false;
    public static boolean armClearOfObstacles = true;

    //public static boolean 
    public static final class DrivetrainConstants {
        public static final double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
        public static final double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity
    }
    
    public static final class IntakeConstants {
        public static final int INTAKE_MOTOR_0 = 20;
        public static final int INTAKE_MOTOR_1 = 21;
    }

    public static final class FeederConstants {
        public static final int FEEDER_MOTOR_ID = 30;
    }

    public static final class ShooterConstants {
        public static final int SHOOT_MOTOR_ID = 40;
        public static final double HEIGHT = 0.0;
        public static final double SHOOT_ANGLE = 0.0;
    }

     public static final class TurretConstants {
        public static final int TURRET_MOTOR_ID = 41;
        public static final double KP = 0.12;
        public static final double KI = 0.005;
        public static final double KD = 0.002;
    }

    public static final class LiftConstants {
        public static final int LIFT_MOTOR_ID = 50;
        public static final double MAX_POWER = 1;
        public static final double KP = 0.12;
        public static final double KI = 0.005;
        public static final double KD = 0.002;
        public static final double POSE_TOLERANCE = .25;
    }

}
