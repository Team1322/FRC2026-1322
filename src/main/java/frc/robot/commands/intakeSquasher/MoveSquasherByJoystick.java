package frc.robot.commands.intakeSquasher;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FredTheFrogsIntakeSquasherSubsystem;

public class MoveSquasherByJoystick extends Command {

    FredTheFrogsIntakeSquasherSubsystem intakeSquahser;
    DoubleSupplier joystickSpeed;

    public MoveSquasherByJoystick (FredTheFrogsIntakeSquasherSubsystem intakeSquahser, DoubleSupplier joystickSpeed) {
        this.intakeSquahser = intakeSquahser;
        this.joystickSpeed = joystickSpeed;
        addRequirements(intakeSquahser);
    }

    @Override
    public void execute() {
        intakeSquahser.setSpeed(joystickSpeed.getAsDouble());
        intakeSquahser.setSpeed2(joystickSpeed.getAsDouble());
    }

    @Override
    public void end(boolean finished) {
        intakeSquahser.setSpeed(0);
    }
    
}
