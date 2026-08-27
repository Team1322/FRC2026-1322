package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.FeederConstants;

public class FeederSubsystem extends SubsystemBase {
  TalonFX feederMotor = new TalonFX(FeederConstants.FEEDER_MOTOR_ID);
  TalonFX feederFollower = new TalonFX(FeederConstants.FEEDER_FOLLOWER_ID);

  public FeederSubsystem() {

    // Basic Config
    TalonFXConfiguration config = new TalonFXConfiguration();
    config.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake;

    //Current Limits
    config.CurrentLimits.StatorCurrentLimit = 120;
    config.CurrentLimits.SupplyCurrentLimit = 80;
    config.CurrentLimits.SupplyCurrentLowerLimit = 60;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.CurrentLimits.StatorCurrentLimitEnable = true;

    //Applying Configs
    feederMotor.getConfigurator().apply(config);
    feederFollower.getConfigurator().apply(config);

    // Setting up the follower
    feederFollower.setControl(new Follower(feederMotor.getDeviceID(), MotorAlignmentValue.Opposed));


  }

  public void setSpeed(double speed) {
    feederMotor.set(speed);
  }
}