package frc.robot.commands.feeder;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.SystemVariables;
import frc.robot.subsystems.FeederSubsystem;

public class RunFeeder extends Command {
    FeederSubsystem feeder;
    Timer timer = new Timer();
    Boolean timerStarted = true;

    public RunFeeder(FeederSubsystem feeder) {
        this.feeder = feeder;
        timer.start();
        addRequirements(feeder);
    }

    @Override
    public void execute() {
        if (SystemVariables.shooterUpToSpeed) {
            if (timer.get() > 3) {
                feeder.setSpeed(-1); 
                if (timer.get() > 3.2) {
                    timer.reset();
                }

            } else feeder.setSpeed(1);
        } else {
            feeder.setSpeed(0);
        }

        
        
        

    }

    @Override
    public void end(boolean finished) {
        feeder.setSpeed(0);
    }
}