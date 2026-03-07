package frc.robot.commands.lift;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.SystemVariables.LiftConstants.LiftStates;
import frc.robot.subsystems.LiftSubsystem;

public class HopperNotStill extends Command {
    LiftSubsystem lift;

    public HopperNotStill(LiftSubsystem lift){
        this.lift = lift;
        addRequirements(lift);
    }
    
     @Override
    public void initialize() {
        lift.setTargetState(LiftStates.DISTURB);

    }
 @Override
    public void execute() {
        lift.moveTowardPosition();

        if (lift.isLiftAtDisturb()) {
            lift.setTargetState(LiftStates.INTAKE);
        }
        else if (lift.isLiftAtIntake()) {
            lift.setTargetState(LiftStates.DISTURB);
        }
    }

    @Override
    public void end(boolean finished) {
        lift.setSpeed(0);
        lift.setTargetState(LiftStates.INTAKE);
    }

}
