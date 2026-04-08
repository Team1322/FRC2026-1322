package frc.robot.commands.intakeSquasher;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FredTheFrogsIntakeSquasherSubsystem;

public class JostleIntakeCommand extends Command{
    FredTheFrogsIntakeSquasherSubsystem intakeSquasher;
    boolean moveOut = false;
   

    public JostleIntakeCommand(FredTheFrogsIntakeSquasherSubsystem intakeSquasher) {
        this.intakeSquasher = intakeSquasher;
       
        addRequirements(intakeSquasher);
    }
    
    @Override
    public void initialize() {
        intakeSquasher.setTargetPosition(-8);
    }

    @Override
    public void execute() {

        if (intakeSquasher.getFredTheFrogsIntakeSquasherPositionL() < -13.5 &&intakeSquasher.getFredTheFrogsIntakeSquasherPositionR() < -13.5 && moveOut == true) {
            intakeSquasher.setTargetPosition(-8);
            moveOut = false;
        }

        if (intakeSquasher.getFredTheFrogsIntakeSquasherPositionL() > -10 &&intakeSquasher.getFredTheFrogsIntakeSquasherPositionR() > -10 && moveOut == false) {
            intakeSquasher.setTargetPosition(-14);
            moveOut = true;
        }

        intakeSquasher.runFredTheFrogsIntakeSquasherToTarget();
    }

    @Override
    public void end(boolean finished) {
        intakeSquasher.setSpeed(0);
    }
}
