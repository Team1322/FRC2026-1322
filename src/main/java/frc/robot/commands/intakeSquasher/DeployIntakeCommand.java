package frc.robot.commands.intakeSquasher;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FredTheFrogsIntakeSquasherSubsystem;
import frc.robot.subsystems.IntakeSubsystem;

public class DeployIntakeCommand extends Command {
    FredTheFrogsIntakeSquasherSubsystem intakeSquasher;
     IntakeSubsystem intake;
    
    public DeployIntakeCommand (FredTheFrogsIntakeSquasherSubsystem intakeSquasher, IntakeSubsystem intake) {
        this.intakeSquasher = intakeSquasher;
        this.intake = intake;
        addRequirements(intakeSquasher, intake);
    }

    @Override
    public void initialize() {
        intakeSquasher.setTargetPosition(-14);
    }

    @Override
    public void execute() {
        intake.setSpeed(-0.5);
        intakeSquasher.runFredTheFrogsIntakeSquasherToTarget();
    }

    @Override
    public boolean isFinished() {
        return intakeSquasher.getFredTheFrogsIntakeSquasherPositionL() < -13.5 &&intakeSquasher.getFredTheFrogsIntakeSquasherPositionR() < -13.5;
    }

    @Override
    public void end(boolean finished) {
        intake.setSpeed(0);
        intakeSquasher.setSpeed(0);
        intakeSquasher.setSpeed2(0);

    }
}
