package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
  SparkMax intakeMotor0;
  SparkMax intakeMotor1;

  public IntakeSubsystem() {
    intakeMotor0 = new SparkMax(IntakeConstants.INTAKE_MOTOR_0, MotorType.kBrushless);
    intakeMotor1 = new SparkMax(IntakeConstants.INTAKE_MOTOR_1, MotorType.kBrushless);

    SparkMaxConfig config0 = new SparkMaxConfig();
    config0.inverted(true);
    intakeMotor0.configure(config0, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    
    SparkMaxConfig config1 = new SparkMaxConfig();
    config1.inverted(true);
    config1.follow(intakeMotor0.getDeviceId(), true);
    intakeMotor1.configure(config1, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public void setSpeed(double speed) {
    intakeMotor0.set(speed);
  }
}
