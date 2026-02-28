package frc.robot.commands.complexCommands;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.SystemVariables;
import frc.robot.SystemVariables.LiftConstants.LiftStates;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.LiftSubsystem;

public class ClearHopper extends ParallelCommandGroup {
    public ClearHopper (FeederSubsystem feeder, LiftSubsystem lift) {
        addCommands(
            new ConditionalCommand(new RunFeeder(feeder), new WaitCommand(0), () -> true), //() -> SystemVariables.shooterUpToSpeed),
            new SequentialCommandGroup(
                new InstantCommand(() -> lift.setTargetState(LiftStates.DISTURB)),
                new WaitCommand(0.3),
                new InstantCommand(() -> lift.setTargetState(LiftStates.INTAKE)),
                new WaitCommand(0.3)
            )
            );
    }

    
}
