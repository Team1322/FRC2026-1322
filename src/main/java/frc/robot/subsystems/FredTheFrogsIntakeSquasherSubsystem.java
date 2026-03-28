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
 
    
      PIDController fredTheFrogsIntakeSquasherController = new PIDController(IntakeSquashConstants.KP, IntakeSquashConstants.KI, IntakeSquashConstants.KD);

  SparkMax intakeSquasherMotor;
  SparkMax intakeSquasherFollower;
  double targetPosition = 0;
  
  public FredTheFrogsIntakeSquasherSubsystem() {
    intakeSquasherMotor = new SparkMax(IntakeSquashConstants.INTAKE_SQUASH_MOTOR_0, MotorType.kBrushless);
    intakeSquasherFollower = new SparkMax(IntakeSquashConstants.INTAKE_SQUASH_MOTOR_1, MotorType.kBrushless);

    SparkMaxConfig config0 = new SparkMaxConfig();
    config0.inverted(true);
    config0.smartCurrentLimit(40, 20);
    intakeSquasherMotor.configure(config0, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    
    SparkMaxConfig config1 = new SparkMaxConfig();
    config1.inverted(false);
    //config1.follow(intakeMotor0.getDeviceId(), true);
    config1.smartCurrentLimit(40, 20);
    config1.follow(IntakeSquashConstants.INTAKE_SQUASH_MOTOR_0, true);
    intakeSquasherFollower.configure(config1, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public void setSpeed(double speed) {
    intakeSquasherMotor.set(speed);
  }

  public double getFredTheFrogsIntakeSquasherPosition () {
    return intakeSquasherMotor.getAlternateEncoder().getPosition();
  }
  public void setTargetPosition (double targetPosition) {
        SmartDashboard.putNumber("Raw Intake Squasher Target", targetPosition);

        if (targetPosition < TurretConstants.RIGHT_LIMIT) {
            targetPosition = getFredTheFrogsIntakeSquasherPosition();
        }
        if (targetPosition > TurretConstants.LEFT_LIMIT) {
            targetPosition = getFredTheFrogsIntakeSquasherPosition();
        }
        this.targetPosition = targetPosition;
  }

   public void runFredTheFrogsIntakeSquasherToTarget() {
        setSpeed(fredTheFrogsIntakeSquasherController.calculate(getFredTheFrogsIntakeSquasherPosition(), targetPosition));
            }
}
