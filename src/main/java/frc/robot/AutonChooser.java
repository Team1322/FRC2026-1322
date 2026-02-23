package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutonChooser {

    public enum AllianceColor { RED, BLUE }
    public enum StartingLocations { DEPOT, CENTER, OUTPOST }
    
    
    private final SendableChooser<AllianceColor> allianceChooser = new SendableChooser<>();
    private final SendableChooser<StartingLocations> locationChooser = new SendableChooser<>();
    private SendableChooser<Command> autoChooser = new SendableChooser<>();
    private final SendableChooser<Command> blankChooser = new SendableChooser<>();

    RobotContainer r;
    public AutonChooser (RobotContainer r) {
        this.r = r;

        allianceChooser.setDefaultOption("Blue", AllianceColor.BLUE);
        allianceChooser.addOption("Red", AllianceColor.RED);
        SmartDashboard.putData("Alliance Color", allianceChooser);

        locationChooser.setDefaultOption("Depot", StartingLocations.DEPOT);
        locationChooser.addOption("Center", StartingLocations.CENTER);
        locationChooser.addOption("Outpost", StartingLocations.OUTPOST);
        SmartDashboard.putData("Starting Location", locationChooser);

        blankChooser.setDefaultOption("Do Nothing", new WaitCommand(1));
        SmartDashboard.putData("Auto Selection", blankChooser);

        SmartDashboard.putBoolean("Auton/Build Chooser?", false);
    }

    public void createChooser() {
        autoChooser = blankChooser;
        if (allianceChooser.getSelected().equals(AllianceColor.RED)) {
            switch (locationChooser.getSelected()) {
                case DEPOT:
                    break;
                case CENTER:
                    break;
                case OUTPOST:
                    break;
            }
        } else {
            switch (locationChooser.getSelected()) {
                case DEPOT:
                    break;
                case CENTER:
                    break;
                case OUTPOST:
                    break;
            }
        }
    }



    public void updateClass() {
        if (SmartDashboard.getBoolean("Auton/Build Chooser?", false)) {
            SmartDashboard.putBoolean("Auton/Build Chooser?", false);
            createChooser();
            SmartDashboard.putData("Auto Selection", autoChooser);
        }
    }
}
