package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables.TurretConstants;


public class TurretSubsystem extends SubsystemBase {
    TalonFX turretMotor = new TalonFX(TurretConstants.TURRET_MOTOR_ID);
    double targetPosition = 0;
    PIDController turretController = new PIDController(TurretConstants.KP, TurretConstants.KI, TurretConstants.KD);

    public TurretSubsystem() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        turretMotor.getConfigurator().apply(config);
           SmartDashboard.putNumber("turret P",TurretConstants.KP );
           SmartDashboard.putNumber("turret I", TurretConstants.KI);
           SmartDashboard.putNumber("turret D",TurretConstants.KD);
    }

    @Override
    public void periodic(){
        SmartDashboard.putNumber("Target Turret Position", targetPosition);
        SmartDashboard.putNumber("Current Turret Position", getCurrentPosition());

        turretController.setPID(
            SmartDashboard.getNumber("turret P",TurretConstants.KP ),
            SmartDashboard.getNumber("turret I", TurretConstants.KI),
            SmartDashboard.getNumber( "turret D",TurretConstants.KD)
        );
    } 

    public void setPower(double power) {
        turretMotor.set(power);

    }

    public double getCurrentPosition() {
        return turretMotor.getPosition().getValueAsDouble();

    }

    public void setTargetPosition(double targetPositon) {
        this.targetPosition = targetPositon;
    }

    public void runTurretToTarget() {
        setPower(turretController.calculate(getCurrentPosition(), targetPosition));
    }
}
