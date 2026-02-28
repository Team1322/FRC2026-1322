package frc.robot.commands.complexCommands;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.SystemVariables;
import frc.robot.SystemVariables.LiftConstants.LiftStates;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.commands.lift.HopperNotStill;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.LiftSubsystem;

public class ClearHopper extends ParallelCommandGroup {
    public ClearHopper (FeederSubsystem feeder, LiftSubsystem lift) {
        addCommands(
            new ConditionalCommand(new RunFeeder(feeder), new WaitCommand(0), () -> SystemVariables.shooterUpToSpeed),
           new HopperNotStill(lift)
            
            );
    }

    
}
