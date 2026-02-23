package frc.robot.commands.lift;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftSubsystem;

public class LiftToPosition extends Command {
    LiftSubsystem lift;

    public LiftToPosition(LiftSubsystem lift) {
        this.lift = lift;
        addRequirements(lift);
    }

    @Override
    public void initialize() {
        lift.setTargetPosition(lift.getCurrentPosition());
    }

    @Override
    public void execute() {
        lift.moveTowardPosition();
    }

    @Override
    public void end(boolean finished) {
        lift.setSpeed(0);
    }
}