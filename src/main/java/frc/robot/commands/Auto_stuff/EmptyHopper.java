package frc.robot.commands.Auto_stuff;

import com.ctre.phoenix6.controls.PositionVoltage;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.commands.Shooter.RunShooter;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class EmptyHopper extends ParallelRaceGroup  {
    public EmptyHopper(FeederSubsystem feeder, ShooterSubsystem shooter)  {
        addCommands(
            new RunShooter(shooter),
            new ConditionalCommand(new RunFeeder (feeder),
            new WaitCommand(0),
            () -> shooter.
            )
            );

    }
}