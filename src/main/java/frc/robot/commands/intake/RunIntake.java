package frc.robot.commands.intake;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.SystemVariables;
import frc.robot.subsystems.IntakeSubsystem;

public class RunIntake extends Command{
    IntakeSubsystem intake; 
    Timer timer = new Timer();
    boolean timerStarted = false;
    public RunIntake (IntakeSubsystem intake) {
    this.intake = intake;
    addRequirements(intake);
    }

    @Override
    public void execute() {
        if  (SystemVariables.intakeDeployed) {
            if (SystemVariables.intakeHasReversed) {
                intake.setSpeed(1);
            } else {
                intake.setSpeed(-1);
                if (timerStarted) SystemVariables.intakeHasReversed = timer.get() > 0.1;
                else { timerStarted = true;
                    timer.start();
                }
            }
        }

    }

    @Override
    public void end(boolean finished) {
        intake.setSpeed(0);
    }
}
