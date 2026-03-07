package frc.robot.subsystems;

import com.andymark.jni.AM_CAN_HexBoreEncoder;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables;
import frc.robot.SystemVariables.ShooterConstants;
import frc.robot.SystemVariables.TurretConstants;

public class TurretSubsystem extends SubsystemBase {
    TalonFX turretMotor = new TalonFX(TurretConstants.TURRET_MOTOR_ID);
    double targetPosition = 0;
    PIDController turretController = new PIDController(TurretConstants.KP, TurretConstants.KI, TurretConstants.KD);
    AM_CAN_HexBoreEncoder turretAbsoluteEncoder;

    
    StructPublisher<Pose3d> turretPosePublisher = NetworkTableInstance.getDefault()
        .getStructTopic("Turret Pose", Pose3d.struct).publish();

    public TurretSubsystem() {
        turretAbsoluteEncoder = new AM_CAN_HexBoreEncoder(TurretConstants.TURRET_SENSOR_ID);
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        config.MotorOutput.PeakForwardDutyCycle = 0.3;
        config.MotorOutput.PeakReverseDutyCycle = -0.3;
        turretMotor.getConfigurator().apply(config);
        SmartDashboard.putNumber("Turret P", TurretConstants.KP);
        SmartDashboard.putNumber("Turret I", TurretConstants.KI);
        SmartDashboard.putNumber("Turret D", TurretConstants.KD);
        
        //turretAbsoluteEncoder.setZeroHere();
        resetMotorEncoder();
    }

    @Override
    public void periodic() {
        turretPosePublisher.set(new Pose3d(
            SystemVariables.turretPose.getX(), 
            SystemVariables.turretPose.getY(), 
            ShooterConstants.SHOOTER_HEIGHT, 
            new Rotation3d(SystemVariables.turretPose.getRotation().plus(Rotation2d.fromDegrees(targetPosition)))
        ));

        SmartDashboard.putNumber("Turret Angle To Hub", getTargetAngleToHub().getDegrees());
        SmartDashboard.putNumber("Encoder Pose", getEncoderAngle());

        SmartDashboard.putNumber("Target Turret Position", targetPosition);
        SmartDashboard.putNumber("Current Turret Position", getCurrentPosition());
        turretController.setPID(
            SmartDashboard.getNumber("Turret P", TurretConstants.KP),
            SmartDashboard.getNumber("Turret I", TurretConstants.KI),
            SmartDashboard.getNumber("Turret D", TurretConstants.KD)
        );
    }

    public void setSpeed(double speed) {
        turretMotor.set(speed);

    }

    public double getEncoderAngle() {
        turretAbsoluteEncoder.getTelemetry();
        double angle = turretAbsoluteEncoder.getAngleDegrees();
        if (angle > 180) {
            angle -= 360;
        }
        return angle * TurretConstants.ENCODER_CONVERSION_FACTOR;
    }

    public double getCurrentPosition() {
        //return turretAbsoluteEncoder.getAngleDegrees();
        return turretMotor.getPosition().getValueAsDouble()  * TurretConstants.MOTOR_CONVERSION_FACTOR;
    }

    public void resetMotorEncoder() {
        double turretAngle = getEncoderAngle();
        turretMotor.setPosition(turretAngle / TurretConstants.MOTOR_CONVERSION_FACTOR);
    }

    public void setTargetPosition(double targetPositon) {

        SmartDashboard.putNumber("Raw Turret Target", targetPositon);

        if (targetPositon < TurretConstants.RIGHT_LIMIT) {
            targetPositon = TurretConstants.RIGHT_LIMIT;
        }
        if (targetPositon > TurretConstants.LEFT_LIMIT) {
            targetPositon = TurretConstants.LEFT_LIMIT;
        }
        this.targetPosition = targetPositon;
        
    }

    public void runTurretToTarget() {
        setSpeed(turretController.calculate(getCurrentPosition(), targetPosition));
    }

    public Rotation2d getTargetAngleToHub() {
        double angleToGoal = SystemVariables.turretAngleToGoal.getDegrees(); //This is the angle from the turret to the goal
        double angleOfRobot = SystemVariables.turretZeroDirection.getDegrees(); //This is the angle of the robot used to offset our math
        double returnAngle = angleToGoal - angleOfRobot;

        if (returnAngle > 180) {
            returnAngle -= 360;
        } else if (returnAngle < -180) {
            returnAngle += 360;
        }

        return Rotation2d.fromDegrees(returnAngle);
    }
}
