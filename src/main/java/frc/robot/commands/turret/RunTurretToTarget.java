package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.TurretSubsystem;

public class RunTurretToTarget extends Command {
    TurretSubsystem turret;
    double target;

    public RunTurretToTarget(TurretSubsystem turret, double target) {
        this.turret = turret;
        this.target = target;
        addRequirements(turret);
    }

    @Override
    public void execute() {
        turret.setTargetPosition(target);
        turret.runTurretToTarget();
    }

    @Override
    public void end(boolean finished) {
        turret.setSpeed(0);
    }
    
}