package frc.robot.commands.shoot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;

public class RunShooterToHub extends Command  {
    ShooterSubsystem shooter;

    public RunShooterToHub(ShooterSubsystem shooter)  {
        this.shooter = shooter;
        addRequirements(shooter);
    }

    @Override
    public void execute() {
        shooter.setShootVelocity(SmartDashboard.getNumber("Shoot Velo", 0));
        //shooter.setShootFromDistance();

    }

    @Override
    public void end(boolean finished)  {
        shooter.stopShoot();
    }
}
