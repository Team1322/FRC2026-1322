package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.SystemVariables;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakeDefault extends Command{
    IntakeSubsystem intake; 
    public IntakeDefault (IntakeSubsystem intake) {
    this.intake = intake;
    addRequirements(intake);
    }

    @Override
    public void execute() {
        if (SystemVariables.reverseIntake){

            intake.setSpeed(-1);
        } else  {
        intake.setSpeed(0);
        }

    }

    @Override
    public void end(boolean finished) {
        intake.setSpeed(0);
    }

}
