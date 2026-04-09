package frc.robot.commands.intakeSquasher;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FredTheFrogsIntakeSquasherSubsystem;

public class RunIntakeToTarget extends Command {
   FredTheFrogsIntakeSquasherSubsystem opositeturret;
   

    public RunIntakeToTarget(FredTheFrogsIntakeSquasherSubsystem turret) {
        this.opositeturret = turret;
       
        addRequirements(turret);
    }

    @Override
    public void initialize() {
        opositeturret.setTargetPosition(-14);

    }

    @Override
    public void execute() {
        opositeturret.runFredTheFrogsIntakeSquasherToTarget();
    }

    @Override
    public void end(boolean finished) {
        opositeturret.setSpeed(0);
    }
    
}