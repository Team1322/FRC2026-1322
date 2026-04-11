package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.FeederConstants;

public class FeederSubsystem extends SubsystemBase {
  TalonFX feederMotor = new TalonFX(FeederConstants.FEEDER_MOTOR_ID);
  TalonFX feederFollower = new TalonFX(FeederConstants.FEEDER_FOLLOWER_ID);

// SparkMax kickerMotor;
  public FeederSubsystem() {
    // kickerMotor = new SparkMax(FeederConstants.KICKER_MOTOR_ID, MotorType.kBrushless);

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

    //  SparkMaxConfig config0 = new SparkMaxConfig();
    // config0.inverted(true);
    // config0.smartCurrentLimit(10);
    // kickerMotor.configure(config0, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

  }

  public void setSpeed(double speed) {
    feederMotor.set(speed);
    //kickerMotor.set(speed/3);
  }
}