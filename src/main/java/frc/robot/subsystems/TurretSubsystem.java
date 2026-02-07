package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class TurretSubsystem extends SubsystemBase {
    TalonFX turretMotor=new TalonFX(41); 
double targetPositon = 0;
PIDController turretController=new PIDController(0.001, 0, 0);
    public TurretSubsystem() {
TalonFXConfiguration config=new TalonFXConfiguration();
config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
config.MotorOutput.NeutralMode=NeutralModeValue.Brake;
turretMotor.getConfigurator().apply(config);
  }

public void setPower (double power) {
turretMotor.set(power);

    
}
public double getCurrentPosition() {
return turretMotor.getPosition().getValueAsDouble();

}
public void setTargetPositon(double targetPositon) {
this.targetPositon = targetPositon;
}
public void runTurretToTarget(){
    setPower( turretController.calculate(getCurrentPosition(),targetPositon));
}
}
