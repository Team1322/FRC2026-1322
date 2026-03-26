   package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.andymark.jni.AM_CAN_HexBoreEncoder;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.IntakeConstants;
import frc.robot.SystemVariables.IntakeSquashConstants;
import frc.robot.SystemVariables.TurretConstants;

public class FredTheFrogsIntakeSquasherSubsystem {
    SparkMax intakeMotor0;
    SparkMax intakeMotor1;
     double targetPosition = 0;
      PIDController fredTheFrogsIntakeSquasherController = new PIDController(IntakeSquashConstants.KP, IntakeSquashConstants.KI, IntakeSquashConstants.KD);

  public FredTheFrogsIntakeSquasherSubsystem() {
    intakeMotor0 = new SparkMax(IntakeSquashConstants.INTAKE_SQUASH_MOTOR_0, MotorType.kBrushless);
    intakeMotor1 = new SparkMax(IntakeSquashConstants.INTAKE_SQUASH_MOTOR_1, MotorType.kBrushless);

    SparkMaxConfig config0 = new SparkMaxConfig();
    config0.inverted(true);
    config0.smartCurrentLimit(40, 20);
    intakeMotor0.configure(config0, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    
    SparkMaxConfig config1 = new SparkMaxConfig();
    config1.inverted(false);
    //config1.follow(intakeMotor0.getDeviceId(), true);
    config1.smartCurrentLimit(40, 20);
    intakeMotor1.configure(config1, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public void setSpeed(double speed) {
    intakeMotor0.set(speed);
    intakeMotor1.set(speed);
  }
  public double GetFredTheFrogsIntakeSquasherPosition () {
    //return turretAbsoluteEncoder.getAngleDegrees();
        return intakeMotor0.getAlternateEncoder().getPosition();
  }
  public void setTargetPosition (double targetPosition) {
        SmartDashboard.putNumber("Raw Intake Squasher Target", targetPosition);

        if (targetPosition < TurretConstants.RIGHT_LIMIT) {
            targetPosition = GetFredTheFrogsIntakeSquasherPosition();
        }
        if (targetPosition > TurretConstants.LEFT_LIMIT) {
            targetPosition = GetFredTheFrogsIntakeSquasherPosition();
        }
        this.targetPosition = targetPosition;
  }

   public void runFredTheFrogsIntakeSquasherToTarget() {
        setSpeed(fredTheFrogsIntakeSquasherController.calculate(GetFredTheFrogsIntakeSquasherPosition(), targetPosition));
            }
}
