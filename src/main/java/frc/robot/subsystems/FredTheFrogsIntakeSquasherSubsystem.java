   package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.IntakeSquashConstants;

public class FredTheFrogsIntakeSquasherSubsystem extends SubsystemBase {


  PIDController leftMotorController = new PIDController(IntakeSquashConstants.KP, IntakeSquashConstants.KI, IntakeSquashConstants.KD);
  PIDController rightMotorController = new PIDController(IntakeSquashConstants.KP, IntakeSquashConstants.KI, IntakeSquashConstants.KD);

  SparkMax leftIntakeMotor;
  double targetPosition = 0;
  SparkMax rightIntakeMotor;

  public FredTheFrogsIntakeSquasherSubsystem() {
    leftIntakeMotor = new SparkMax(IntakeSquashConstants.INTAKE_SQUASH_MOTOR_0, MotorType.kBrushless);
    rightIntakeMotor = new SparkMax(IntakeSquashConstants.INTAKE_SQUASH_MOTOR_1, MotorType.kBrushless);


    SparkMaxConfig config0 = new SparkMaxConfig();
    config0.inverted(true);
    config0.smartCurrentLimit(60);
    config0.openLoopRampRate(0.1);
    leftIntakeMotor.configure(config0, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);


    SparkMaxConfig config1 = new SparkMaxConfig();
    config1.inverted(false);
    config1.smartCurrentLimit(60);
    config1.openLoopRampRate(0.1);
    rightIntakeMotor.configure(config1, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    // TODO Comp Code 
    // rightIntakeMotor.getEncoder().setPosition(0);
    // leftIntakeMotor.getEncoder().setPosition(0);

    // Temp Code 
    rightIntakeMotor.getEncoder().setPosition(-14);
    leftIntakeMotor.getEncoder().setPosition(-14);

  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Current intake left Position", getFredTheFrogsIntakeSquasherPositionL());
    SmartDashboard.putNumber("Current intake right  Position", getFredTheFrogsIntakeSquasherPositionR());
  }

  public void setSpeed(double speed) {
    if (Math.abs(speed) > 0.5) speed = Math.copySign(0.5, speed);
    leftIntakeMotor.set(speed);
  }

  public void setSpeed2(double speed) {
    if (Math.abs(speed) > 0.5) speed = Math.copySign(0.5, speed);
    rightIntakeMotor.set(speed);
  }

  public double getFredTheFrogsIntakeSquasherPositionL () {
    return leftIntakeMotor.getEncoder().getPosition();
  }

  public double getFredTheFrogsIntakeSquasherPositionR () {
    return rightIntakeMotor.getEncoder().getPosition();
  }

  public void setTargetPosition (double targetPosition) {
    this.targetPosition = targetPosition;
  }

  public void runFredTheFrogsIntakeSquasherToTarget() {
    setSpeed(leftMotorController.calculate(getFredTheFrogsIntakeSquasherPositionL(), targetPosition));
    setSpeed2(rightMotorController.calculate(getFredTheFrogsIntakeSquasherPositionR(), targetPosition));
  }

  public void resetPosition() {
    rightIntakeMotor.getEncoder().setPosition(-14);
    leftIntakeMotor.getEncoder().setPosition(-14);
  }


}
