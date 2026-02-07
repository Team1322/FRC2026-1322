package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class FeederSubsystem {
   TalonFX feederMotor=new TalonFX(30); 

    public FeederSubsystem() {
TalonFXConfiguration config=new TalonFXConfiguration();
config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
config.MotorOutput.NeutralMode=NeutralModeValue.Brake;
feederMotor.getConfigurator().apply(config);
  }

public void setPower (double power) {
feederMotor.set(power);
}
}