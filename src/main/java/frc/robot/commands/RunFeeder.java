package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FeederSubsystem;

public class RunFeeder extends Command{
    FeederSubsystem feeder;
    public RunFeeder (FeederSubsystem feeder) {
      this.feeder = feeder;
     addRequirements(feeder);   
    }
    @Override
    public void initialize (){
 
        
    }

    @Override
    public void execute() {
        feeder.setPower(1);


    }

    @Override
    public boolean isFinished() {
        return false;


    }

    @Override
    public void end(boolean finished) {
        feeder.setPower(0);
    }
}