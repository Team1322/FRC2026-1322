package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;

public class RunIntake extends Command{
    IntakeSubsystem intake; 
    public RunIntake (IntakeSubsystem intake) {
    this.intake = intake;
    addRequirements(intake);
    }

    @Override
    public void initialize() {


    }
     
    @Override
    public void execute() {
        intake.setPower(1);

    }

    @Override
    public boolean isFinished(){
        return false;

    }

    @Override
    public void end(boolean finished) {
        intake.setPower(0);
    }
}
