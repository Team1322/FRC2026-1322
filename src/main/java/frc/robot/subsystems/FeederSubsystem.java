package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.FeederConstants;

public class FeederSubsystem extends SubsystemBase {
  TalonFX feederMotor = new TalonFX(FeederConstants.FEEDER_MOTOR_ID);

  public FeederSubsystem() {
    TalonFXConfiguration config = new TalonFXConfiguration();
    config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    feederMotor.getConfigurator().apply(config);
  }

  public void setSpeed(double speed) {
    feederMotor.set(speed);
  }
}