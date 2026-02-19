package frc.robot.commands.lift;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftSubsystem;

public class MoveLiftWithJoystick extends Command{
    LiftSubsystem lift;
    DoubleSupplier opLeftY;
    
    public MoveLiftWithJoystick (LiftSubsystem lift, DoubleSupplier opLeftY) {
        this.lift = lift;
        this.opLeftY = opLeftY;
        addRequirements(lift);
    }

    @Override
    public void execute() {
        lift.setSpeed(opLeftY.getAsDouble() * 0.1);
    }

    @Override
    public void end(boolean finished) {
        lift.setSpeed(0);
    }
}
