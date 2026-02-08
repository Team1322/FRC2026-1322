package frc.robot.commands.feeder;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FeederSubsystem;

public class RunFeeder extends Command {
    FeederSubsystem feeder;

    public RunFeeder(FeederSubsystem feeder) {
        this.feeder = feeder;
        addRequirements(feeder);
    }

    @Override
    public void execute() {
        feeder.setSpeed(1);

    }

    @Override
    public void end(boolean finished) {
        feeder.setSpeed(0);
    }
}