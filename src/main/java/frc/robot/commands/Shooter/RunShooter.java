package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class RunShooter extends Command  {
    ShooterSubsystem shooter;

    public RunShooter(ShooterSubsystem shooter)  {
        this.shooter = shooter;
        addRequirements(shooter);
    }

    @Override
    public void execute() {
        shooter.setShootFromDistance();

    }

    @Override
    public void end(boolean finished)  {
        shooter.stopShoot();
    }
}
