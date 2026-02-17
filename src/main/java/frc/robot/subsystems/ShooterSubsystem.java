package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables;
import frc.robot.SystemVariables.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase {
    TalonFX shooterMotor = new TalonFX(40);
    double velo = 0;

    public ShooterSubsystem() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        shooterMotor.getConfigurator().apply(config);
    }

    public void setShootFromDistance() {
        setShootVelocity(getShootVelocity());
    }

    public void setShootVelocity(double velo) {
        this.velo = velo;
        shooterMotor.setControl(new VelocityVoltage(velo));
    }

    public void stopShoot() {
        shooterMotor.stopMotor();
    }
    
    public boolean isShooterSpunUp()  {
        return false;
    }

    private double getShootVelocity() {
        double distance = SystemVariables.turretDistanceFromGoal;
        double vel = Math.sqrt(
                (-(distance * distance) * 10)
                        / (2 * ShooterConstants.GOAL_HEIGHT - 2 * distance * Math.tan(ShooterConstants.SHOOT_ANGLE)))
                / (Math.cos(ShooterConstants.SHOOT_ANGLE));
        return vel;
    }
}