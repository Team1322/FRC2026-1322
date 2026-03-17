package frc.robot.subsystems;

import com.andymark.jni.AM_CAN_HexBoreEncoder;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.LiftConstants;
import frc.robot.SystemVariables.LiftConstants.LiftStates;

public class LiftSubsystem extends SubsystemBase {

    TalonFX liftMotor = new TalonFX(LiftConstants.LIFT_MOTOR_ID);

    PIDController liftController = new PIDController(LiftConstants.KP, LiftConstants.KI, LiftConstants.KD);
    
    AM_CAN_HexBoreEncoder liftAbsoluteEncoder = new AM_CAN_HexBoreEncoder(LiftConstants.LIFT_SENSOR_ID);

    double targetPosition = 0;

    Servo servo = new Servo(0);

    public LiftSubsystem() {
        //Basic Config
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        //Apply Config
        liftMotor.getConfigurator().apply(config);

        //liftAbsoluteEncoder.setZeroHere();
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Target Lift Position", targetPosition);
        SmartDashboard.putNumber("Current Lift Position", getCurrentPosition());
    }

    public void setSpeed(double speed) {
        liftMotor.set(speed);
    }

    public double getCurrentPosition() {
        return getEncoderAngle();
    }

    public void deployLiftServo() {
        servo.set(0.5);
    }

    public void resetLiftServo() {
        servo.set(0);
    }

    public double getEncoderAngle() {
        liftAbsoluteEncoder.getTelemetry();
        double angle = liftAbsoluteEncoder.getAngleDegrees();
        if (angle > 180) {
            angle -= 360;
        }
        return angle;
    }

    public void setTargetPosition(double targetPosition) {
        this.targetPosition = targetPosition;
    }

    public void setTargetState(LiftStates targetState) {
        switch (targetState) {
            case COMPACT:
                setTargetPosition(65);
                break;
            case INTAKE:
                setTargetPosition(0);
                break;
            case CLIMBED:
                setTargetPosition(5);
                break;
            case DISTURB:
                setTargetPosition(30);
                break;

        }
    }

public boolean isLiftAtDisturb(){
return Math.abs(getCurrentPosition() - 30)<0.5;
}

    public boolean isLiftAtIntake(){
return Math.abs(getCurrentPosition() - 0)<0.5;
}



    public void moveTowardPosition() {
        setSpeed(liftController.calculate(getCurrentPosition(), targetPosition));
    }

}