package frc.robot.subsystems;

import com.andymark.jni.AM_CAN_HexBoreEncoder;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.LiftConstants;

public class LiftSubsystem extends SubsystemBase {

    TalonFX liftMotor = new TalonFX(LiftConstants.LIFT_MOTOR_ID);

    PIDController liftController = new PIDController(LiftConstants.KP, LiftConstants.KI, LiftConstants.KD);
    
    AM_CAN_HexBoreEncoder liftAbsoluteEncoder = new AM_CAN_HexBoreEncoder(LiftConstants.LIFT_SENSOR_ID);

    double targetPosition = 0;

    public LiftSubsystem() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        liftMotor.getConfigurator().apply(config);

        //liftAbsoluteEncoder.setOffsetDegrees(0);

        SmartDashboard.putNumber("Lift P", LiftConstants.KP);
        SmartDashboard.putNumber("Lift I", LiftConstants.KI);
        SmartDashboard.putNumber("Lift D", LiftConstants.KD);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Target Lift Position", targetPosition);
        SmartDashboard.putNumber("Current Lift Position", getCurrentPosition());

        liftController.setPID(
            SmartDashboard.getNumber("Lift P", LiftConstants.KP),
            SmartDashboard.getNumber("Lift I", LiftConstants.KI),
            SmartDashboard.getNumber("Lift D", LiftConstants.KD)
        );
    }

    public void setSpeed(double speed) {
        liftMotor.set(speed);
    }

    public double getCurrentPosition() {
        //return liftAbsoluteEncoder.getAngleDegrees();
        return liftMotor.getPosition().getValueAsDouble();
    }

    public void setTargetPosition(double targetPosition) {
        this.targetPosition = targetPosition;
    }

    public void moveTowardPosition() {
        setSpeed(liftController.calculate(getCurrentPosition(), targetPosition));
    }

}