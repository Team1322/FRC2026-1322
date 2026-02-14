package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TurretSubsystem;

public class RunTurretToWin extends Command {
    TurretSubsystem turret;

      public RunTurretToWin(TurretSubsystem turret) {
        this.turret = turret;
        addRequirements(turret);
    }

    @Override
    public void execute() {
        turret.setTargetPosition( turret.getTargetAngle().getDegrees());
        turret.runTurretToTarget();
    }

    @Override
    public void end(boolean finished) {
        turret.setSpeed(0);
    }
}
