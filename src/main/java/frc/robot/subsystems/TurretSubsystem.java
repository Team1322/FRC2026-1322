package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.SystemVariables;
import frc.robot.SystemVariables.TurretConstants;

public class TurretSubsystem extends SubsystemBase {
    TalonFX turretMotor = new TalonFX(TurretConstants.TURRET_MOTOR_ID);
    double targetPosition = 0;
    PIDController turretController = new PIDController(TurretConstants.KP, TurretConstants.KI, TurretConstants.KD);

    public TurretSubsystem() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        config.MotorOutput.PeakForwardDutyCycle = 0.3;
        config.MotorOutput.PeakReverseDutyCycle = -0.3;
        turretMotor.getConfigurator().apply(config);
        SmartDashboard.putNumber("Turret P", TurretConstants.KP);
        SmartDashboard.putNumber("Turret I", TurretConstants.KI);
        SmartDashboard.putNumber("Turret D", TurretConstants.KD);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Target Turret Position", targetPosition);
        SmartDashboard.putNumber("Current Turret Position", getCurrentPosition());

        // turretController.setPID(
        //     SmartDashboard.getNumber("Turret P", TurretConstants.KP),
        //     SmartDashboard.getNumber("Turret I", TurretConstants.KI),
        //     SmartDashboard.getNumber("Turret D", TurretConstants.KD)
        // );
    }

    public void setSpeed(double speed) {
        turretMotor.set(speed);

    }

    public double getCurrentPosition() {
        return turretMotor.getPosition().getValueAsDouble()  * TurretConstants.CONVERSION_FACTOR;

    }

    public void setTargetPosition(double targetPositon) {
       
        if (targetPositon < TurretConstants.RIGHT_LIMIT) {
            targetPositon = TurretConstants.RIGHT_LIMIT;
        }
        if (targetPositon > TurretConstants.LEFT_LIMIT) {
            targetPositon = TurretConstants.LEFT_LIMIT;
        }
        this.targetPosition = targetPositon;
        
    }

    public void runTurretToTarget() {
        setSpeed(turretController.calculate(getCurrentPosition(), targetPosition));
    }

    public Rotation2d getTargetAngleToHub() {
        double angleToGoal = SystemVariables.turretAngleToGoal.getDegrees(); //This is the angle from the turret to the goal
        double angleOfRobot = SystemVariables.turretZeroDirection.getDegrees(); //This is the angle of the robot used to offset our math
        return Rotation2d.fromDegrees(angleToGoal - angleOfRobot);
    }
}
