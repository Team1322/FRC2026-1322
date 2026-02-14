package frc.robot.commands.Shooter;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class RunShooter extends Command  {
    ShooterSubsystem Shooter;

    public RunShooter(ShooterSubsystem Shooter)  {
        this.Shooter = Shooter;
        addRequirements(Shooter);
    }

    @Override
    public void execute() {
        Shooter.shoot();

    }

    @Override
    public void end(boolean finished)  {
        Shooter.stopShoot();
    }
}
