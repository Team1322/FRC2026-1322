package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import static frc.robot.SystemVariables.LiftConstants;
import frc.robot.subsystems.LiftSubsystem;

public class LiftToPosition extends Command {
    LiftSubsystem lift;
    double targetPosition;

    public LiftToPosition(LiftSubsystem lift, double targetPosition) {
        this.lift = lift;
        this.targetPosition = targetPosition;
        addRequirements(lift);
    }

    @Override
    public void initialize() {
        lift.setTargetPosition(targetPosition);

    }

    @Override
    public void execute() {
        lift.moveTowardPosition();
    }

    @Override
    public boolean isFinished() {
        return Math.abs(targetPosition - lift.getCurrentPosition()) < LiftConstants.POSE_TOLERANCE;
    }

    @Override
    public void end(boolean finished) {
        lift.setPower(0);
    }
}