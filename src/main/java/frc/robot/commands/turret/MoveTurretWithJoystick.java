package frc.robot.commands.turret;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TurretSubsystem;

public class MoveTurretWithJoystick extends Command{
    TurretSubsystem turret;
    DoubleSupplier opRightX;
    
    public MoveTurretWithJoystick (TurretSubsystem turret, DoubleSupplier opRightX) {
        this.turret = turret;
        this.opRightX = opRightX;
        addRequirements(turret);
    }

    @Override
    public void execute() {
        turret.setSpeed(opRightX.getAsDouble() * 0.05);
    }

    @Override
    public void end(boolean finished) {
        turret.setSpeed(0);
    }
}
