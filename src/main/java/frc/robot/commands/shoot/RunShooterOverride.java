package frc.robot.commands.shoot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;

public class RunShooterOverride extends Command  {
    ShooterSubsystem shooter;
    double velo;

    public RunShooterOverride(ShooterSubsystem shooter, double velo)  {
        this.shooter = shooter;
        this.velo = velo;
        addRequirements(shooter);
    }

    @Override
    public void execute() {
        shooter.setShootVelocity(velo);
        //shooter.setShootFromDistance();

    }

    @Override
    public void end(boolean finished)  {
        shooter.stopShoot();
    }
}
