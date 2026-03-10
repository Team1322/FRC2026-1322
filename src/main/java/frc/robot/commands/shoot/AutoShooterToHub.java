package frc.robot.commands.shoot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.SystemVariables;
import frc.robot.subsystems.ShooterSubsystem;

public class AutoShooterToHub extends Command  {
    ShooterSubsystem shooter;

    public AutoShooterToHub(ShooterSubsystem shooter)  {
        this.shooter = shooter;
        addRequirements(shooter);
    }

    @Override
    public void execute() {
        if (SystemVariables.runShooter) {
            shooter.setShootFromDistance();
        } else {
            shooter.stopShoot();
        }

    }

    @Override
    public void end(boolean finished)  {
        shooter.stopShoot();
    }
}
