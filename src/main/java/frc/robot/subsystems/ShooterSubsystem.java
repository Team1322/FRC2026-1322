package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables;
import frc.robot.SystemVariables.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase {
    TalonFX shooterMotor = new TalonFX(ShooterConstants.SHOOT_MOTOR_ID);
    TalonFX shooterFollower = new TalonFX(ShooterConstants.SHOOT_FOLLWER_ID);
    double velo = 0;

    public ShooterSubsystem() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        config.Slot0.kP = 2;
        shooterMotor.getConfigurator().apply(config);
        shooterFollower.getConfigurator().apply(config);
        shooterFollower.setControl(new Follower(shooterMotor.getDeviceID(), MotorAlignmentValue.Opposed));

        SmartDashboard.putNumber("Shoot Velo", 0);

    }

    @Override
    public void periodic(){
        SmartDashboard.putNumber("Current Shoot Velocity", shooterMotor.getVelocity().getValueAsDouble());
        SmartDashboard.putNumber("Shoot Velo to Goal", getShootVelocity());
        SmartDashboard.putNumber("Distance to Goal", SystemVariables.turretDistanceFromGoal);

        SystemVariables.shooterUpToSpeed = isShooterSpunUp();
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
        return Math.abs(shooterMotor.getVelocity().getValueAsDouble() - velo) < 10 && velo > 5;
    }

    private double getShootVelocity() {
        double distance = SystemVariables.turretDistanceFromGoal;
        if (distance < 1.25) {
            distance = 1.25;
        }

        double sqrtNum = (distance * distance) * -10;
        double sqrtDenom = (2 * ShooterConstants.GOAL_HEIGHT) - (2 * distance * Math.tan(ShooterConstants.SHOOT_ANGLE));

        double vel = Math.sqrt(sqrtNum/sqrtDenom)
                / (Math.cos(ShooterConstants.SHOOT_ANGLE));
        return vel * 6.8;
    }
}