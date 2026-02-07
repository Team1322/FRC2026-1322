package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase{

    TalonFX intakeMotor=new TalonFX(20);
 
    public IntakeSubsystem() {
TalonFXConfiguration config=new TalonFXConfiguration();
config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
config.MotorOutput.NeutralMode=NeutralModeValue.Brake;
intakeMotor.getConfigurator().apply(config);
  }

public void setPower (double power) {
intakeMotor.set(power);
}
}
