package frc.robot.commands.autoStuff;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.commands.shoot.RunShooter;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class EmptyHopper extends ParallelRaceGroup  {
    public EmptyHopper(FeederSubsystem feeder, ShooterSubsystem shooter)  {
        addCommands(
            new RunShooter(shooter),
            new ConditionalCommand(
                new RunFeeder (feeder),
                new WaitCommand(0),
                () -> shooter.isShooterSpunUp()
            )
        );

    }
}