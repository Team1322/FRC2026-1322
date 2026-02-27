package frc.robot.commands.complexCommands;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.commands.shoot.RunShooterToHub;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class EmptyHopper extends ParallelRaceGroup  {
    public EmptyHopper(FeederSubsystem feeder, ShooterSubsystem shooter, LiftSubsystem lift)  {
        addCommands(
            new RunShooterToHub(shooter),
            new ConditionalCommand(
                new ClearHopper (feeder, lift),
                new WaitCommand(0),
                () -> shooter.isShooterSpunUp()
            )
        );

    }
}