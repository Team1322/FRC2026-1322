package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.controller.PIDController;
import frc.robot.Telemetry;

public class LiftSubsystem {
       
           TalonFX liftMotor=new TalonFX(50);
       
              PIDController liftController=new PIDController(0.001, 0, 0);
        
              double targetPosition=0;
       
           public LiftSubsystem() {
       TalonFXConfiguration config=new TalonFXConfiguration();
       config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
       config.MotorOutput.NeutralMode=NeutralModeValue.Brake;
       liftMotor.getConfigurator().apply(config);
         }
       
       public void setPower (double power) {
       liftMotor.set(power);
       }
       
       public double getCurrentPosition() {
           return liftMotor.getPosition().getValueAsDouble();
       }
       
       public void setTargetPosition(double targetPosition) {
           this.targetPosition=targetPosition;
       }
       
       public void runPID() {
           setPower(liftController.calculate(getCurrentPosition(),targetPosition));
       }
       
}