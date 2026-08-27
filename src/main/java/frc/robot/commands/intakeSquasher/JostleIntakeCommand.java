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

        if (intakeSquasher.getFredTheFrogsIntakeSquasherPosition() < -13.5 ) {
            intakeSquasher.setTargetPosition(-10);
            moveOut = false;
        }

        if (intakeSquasher.getFredTheFrogsIntakeSquasherPosition() > -13) {
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
