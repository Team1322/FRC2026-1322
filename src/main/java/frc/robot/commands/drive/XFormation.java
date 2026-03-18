package frc.robot.commands.drive;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.SystemVariables.DrivetrainConstants;
import frc.robot.subsystems.DriveSubsystem;

public class XFormation extends Command {
    DriveSubsystem drive;
    CommandXboxController driverController;

    public XFormation (DriveSubsystem drive, CommandXboxController driverController) {
        this.drive = drive;
        this.driverController = driverController;
        addRequirements(drive);
    }

    @Override
    public void execute() {
        drive.setControl(drive.brake);
    }

    @Override
    public boolean isFinished() {
        return 
            (Math.abs(driverController.getLeftX()) > 0.1) ||
            (Math.abs(driverController.getLeftY()) > 0.1) ||
            (Math.abs(driverController.getRightX()) > 0.1);
    }
}
