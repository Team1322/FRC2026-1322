   package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.IntakeSquashConstants;

public class FredTheFrogsIntakeSquasherSubsystem extends SubsystemBase {


  PIDController leftMotorController = new PIDController(IntakeSquashConstants.KP, IntakeSquashConstants.KI, IntakeSquashConstants.KD);
  PIDController rightMotorController = new PIDController(IntakeSquashConstants.KP, IntakeSquashConstants.KI, IntakeSquashConstants.KD);

  TalonFX intakeMoverMotor;
  double targetPosition = 0;

  public FredTheFrogsIntakeSquasherSubsystem() {
    intakeMoverMotor = new TalonFX(IntakeSquashConstants.INTAKE_SQUASH_MOTOR_0);
     TalonFXConfiguration config = new TalonFXConfiguration();
    config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake;

    //Current Limits
    config.CurrentLimits.StatorCurrentLimit = 90;
    config.CurrentLimits.SupplyCurrentLimit = 70;
    config.CurrentLimits.SupplyCurrentLowerLimit = 60;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.CurrentLimits.StatorCurrentLimitEnable = true;
 intakeMoverMotor.getConfigurator().apply(config);



    // Temp Code 
    // rightIntakeMotor.getEncoder().setPosition(-14);
    // leftIntakeMotor.getEncoder().setPosition(-14);

  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Current intake left Position", getFredTheFrogsIntakeSquasherPositionL());
  }

  public void setSpeed(double speed) {
    //if (Math.abs(speed) > 0.75) speed = Math.copySign(0.75, speed);

    intakeMoverMotor.set(speed);
  }


  public double getFredTheFrogsIntakeSquasherPositionL () {
    return intakeMoverMotor.getPosition().getValueAsDouble();
  }

  

  public void setTargetPosition (double targetPosition) {
    this.targetPosition = targetPosition;
  }

  public void runFredTheFrogsIntakeSquasherToTarget() {
    setSpeed(leftMotorController.calculate(getFredTheFrogsIntakeSquasherPositionL(), targetPosition));
  
  }

  public void resetPosition() {
    intakeMoverMotor.setPosition(0);
  }


}
