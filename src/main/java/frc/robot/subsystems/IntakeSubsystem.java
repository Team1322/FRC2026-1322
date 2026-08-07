package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.FeederConstants;
import frc.robot.SystemVariables.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
  TalonFX intakeMotor = new TalonFX(IntakeConstants.INTAKE_MOTOR_ID);
  TalonFX intakeFollower = new TalonFX(IntakeConstants.INTAKE_FOLLOWER_ID);

  public IntakeSubsystem() {

    TalonFXConfiguration config = new TalonFXConfiguration();
    config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake;

    //Current Limits
    config.CurrentLimits.StatorCurrentLimit = 90;
    config.CurrentLimits.SupplyCurrentLimit = 70;
    config.CurrentLimits.SupplyCurrentLowerLimit = 60;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.CurrentLimits.StatorCurrentLimitEnable = true;

    //Applying Configs
    intakeMotor.getConfigurator().apply(config);
    intakeFollower.getConfigurator().apply(config);

    //Follower setup
     intakeFollower.setControl(new Follower(intakeMotor.getDeviceID(), MotorAlignmentValue.Opposed));
  }
    public void setSpeed(double speed) {
    intakeMotor.set(speed);
    }
}
