package frc.robot.commands.complexCommands;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.SystemVariables;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.commands.intakeSquasher.JostleIntakeCommand;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.FredTheFrogsIntakeSquasherSubsystem;

public class ClearHopper extends ParallelCommandGroup {
    public ClearHopper (FeederSubsystem feeder, FredTheFrogsIntakeSquasherSubsystem intakeSquasher) {
        addCommands(
            new ConditionalCommand(new RunFeeder(feeder), new WaitCommand(0), () -> SystemVariables.shooterUpToSpeed),
            new JostleIntakeCommand(intakeSquasher)
            );
    }

    
}
