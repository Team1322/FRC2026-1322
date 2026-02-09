package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;

public class RunIntake extends Command{
    IntakeSubsystem intake; 
    public RunIntake (IntakeSubsystem intake) {
    this.intake = intake;
    addRequirements(intake);
    }

    @Override
    public void execute() {
        intake.setSpeed(1);

    }

    @Override
    public void end(boolean finished) {
        intake.setSpeed(0);
    }
}
