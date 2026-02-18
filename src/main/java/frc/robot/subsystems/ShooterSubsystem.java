package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables;
import frc.robot.SystemVariables.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase {
    TalonFX shooterMotor = new TalonFX(ShooterConstants.SHOOT_MOTOR_ID);
    TalonFX shooterFollower = new TalonFX(ShooterConstants.SHOOT_FOLLWER_ID);

    public ShooterSubsystem() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        shooterMotor.getConfigurator().apply(config);
        shooterFollower.getConfigurator().apply(config);
        shooterFollower.setControl(new Follower(shooterMotor.getDeviceID(), MotorAlignmentValue.Opposed));
    }

    public void shoot() {
        shooterMotor.setControl(new VelocityVoltage(getShootVelocity()));
    }

    public void stopShoot() {
        shooterMotor.stopMotor();
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