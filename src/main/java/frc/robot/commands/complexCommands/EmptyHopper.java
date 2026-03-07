package frc.robot.commands.complexCommands;

import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import frc.robot.commands.shoot.RunShooterToHub;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class EmptyHopper extends ParallelRaceGroup  {
    public EmptyHopper(FeederSubsystem feeder, ShooterSubsystem shooter, LiftSubsystem lift)  {
        addCommands(
            new RunShooterToHub(shooter),
            new ClearHopper(feeder, lift)
        );

    }
}