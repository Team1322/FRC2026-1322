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
        //Basic Config
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Coast;

        //PID Values
        config.Slot0.kP = 1;
        config.Slot0.kI = 0.01;
        config.Slot0.kD = 0;

        //Current Limits
        config.CurrentLimits.StatorCurrentLimit = 80;
        config.CurrentLimits.SupplyCurrentLimit = 50;
        config.CurrentLimits.SupplyCurrentLowerLimit = 40;
        config.CurrentLimits.SupplyCurrentLimitEnable = true;
        config.CurrentLimits.StatorCurrentLimitEnable = true;

        //Apply Config
        shooterMotor.getConfigurator().apply(config);
        shooterFollower.getConfigurator().apply(config);

        //Setup Follower
        shooterFollower.setControl(new Follower(shooterMotor.getDeviceID(), MotorAlignmentValue.Opposed));
    }

    @Override
    public void periodic(){
        SmartDashboard.putNumber("Current Shoot Velocity", shooterMotor.getVelocity().getValueAsDouble());
        SmartDashboard.putNumber("Shoot Velo to Goal", getShootVelocity());
        SmartDashboard.putNumber("Distance to Goal", SystemVariables.turretDistanceFromGoal);

        SystemVariables.shooterUpToSpeed = isShooterSpunUp();
    }

    public boolean isShooterSpunUp()  {
        return Math.abs(shooterMotor.getVelocity().getValueAsDouble() - velo) < 20 && velo > 5;
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
    

    private double getShootVelocity() {
        double distance = SystemVariables.turretDistanceFromGoal;
        
        if (distance < 1.25) {
            distance = 1.25;
        } else if (distance > 10) {
            distance = 10;
        }

        double difference = Math.abs(SystemVariables.turretZeroDirection.minus(SystemVariables.turretAngleToGoal).getDegrees());

        double sqrtNum = (distance * distance) * -10;
        double sqrtDenom = (2 * ShooterConstants.GOAL_HEIGHT) - (2 * distance * Math.tan(ShooterConstants.SHOOT_ANGLE));

        double vel = Math.sqrt(sqrtNum/sqrtDenom)
                / (Math.cos(ShooterConstants.SHOOT_ANGLE));

        vel *= 6.25; //6.2
        vel *= ((difference / 18) / 100) + 1;
        return vel;
    }
}