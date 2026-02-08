package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.TurretSubsystem;

public class RunTurretToTarget extends Command {
    TurretSubsystem turret;

    public RunTurretToTarget(TurretSubsystem turret) {
        this.turret = turret;
        addRequirements(turret);
    }

    @Override
    public void execute() {
        turret.runTurretToTarget();
    }

    @Override
    public void end(boolean finished) {
        turret.setPower(0);
    }
}