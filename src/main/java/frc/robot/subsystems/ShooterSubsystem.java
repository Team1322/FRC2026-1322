package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.SystemVariables.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase {
    TalonFX ShooterMotor = new TalonFX(40);

    public ShooterSubsystem() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        ShooterMotor.getConfigurator().apply(config);
    }

    public void shoot() {
        ShooterMotor.setControl(new VelocityVoltage(getShootVelocity()));
    }

    public void stopShoot() {
        ShooterMotor.stopMotor();
    }

    private double getShootVelocity() {
        double distance = 0.0;
        double vel = Math.sqrt(
                (-(distance * distance) * 10)
                        / (2 * ShooterConstants.HEIGHT - 2 * distance * Math.tan(ShooterConstants.SHOOT_ANGLE)))
                / (Math.cos(ShooterConstants.SHOOT_ANGLE));
        return vel;
    }
}