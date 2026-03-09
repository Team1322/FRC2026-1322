package frc.robot;

import static edu.wpi.first.units.Units.MetersPerSecond;



import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.SystemVariables.LiftConstants.LiftStates;
import frc.robot.commands.complexCommands.ClearHopper;
import frc.robot.commands.drive.DriveToPose;
import frc.robot.commands.intake.RunIntake;

public class AutonChooser {

    public enum AllianceColor { RED, BLUE }
    public enum StartingLocations { DEPOT, CENTER, OUTPOST }
    public enum ClimbLocations { NONE, DEPOT_FRONT, DEPOT_BACK, OUTPOST_FRONT, OUTPOST_BACK }
    
    
    private final SendableChooser<AllianceColor> allianceChooser = new SendableChooser<>();
    private final SendableChooser<StartingLocations> locationChooser = new SendableChooser<>();
    private final SendableChooser<ClimbLocations> climbChooser = new SendableChooser<>();
    private final SendableChooser<Command> autoChooser = new SendableChooser<>();

    private final Pose2d blueDepotShotLocation = new Pose2d(2, 5, Rotation2d.kZero);
    private final Pose2d blueCenterShotLocation = new Pose2d(2, 4.25, Rotation2d.kZero);
    private final Pose2d blueOutpostShotLocation = new Pose2d(2, 2.5, Rotation2d.kZero);
    private final Pose2d redDepotShotLocation = new Pose2d(14.5, 3, Rotation2d.k180deg);
    private final Pose2d redCenterShotLocation = new Pose2d(14.5, 4.75, Rotation2d.k180deg);
    private final Pose2d redOutpostShotLocation = new Pose2d(14.5, 5.5, Rotation2d.k180deg);

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

        climbChooser.setDefaultOption("None", ClimbLocations.NONE);
        climbChooser.addOption("Depot Front", ClimbLocations.DEPOT_FRONT);
        climbChooser.addOption("Depot Back", ClimbLocations.DEPOT_BACK);
        climbChooser.addOption("Outpost Front", ClimbLocations.OUTPOST_FRONT);
        climbChooser.addOption("Outpost Back", ClimbLocations.OUTPOST_BACK);
        SmartDashboard.putData("Climb Location", climbChooser);

        autoChooser.setDefaultOption("Do Nothing", new WaitCommand(1));
        SmartDashboard.putData("Auto Selection", autoChooser);

        SmartDashboard.putBoolean("Auton/Build Chooser?", false);
    }

    public void createChooser() {
        if (allianceChooser.getSelected().equals(AllianceColor.RED)) {



            switch (locationChooser.getSelected()) {
                case DEPOT:

                    autoChooser.addOption("Red Depot Collection", new SequentialCommandGroup(
                        new DriveToPose(r.drive, 
                            new DriveToPoseObject(new Pose2d(12.900, 2.000,Rotation2d.kZero))
                        ),

                        new ParallelRaceGroup(
                            new DriveToPose(r.drive, 
                                new DriveToPoseObject(new Pose2d(16.100, 1.200,Rotation2d.fromDegrees(-90))),
                                new DriveToPoseObject(new Pose2d(16.100, 3.000,Rotation2d.fromDegrees(-90)))
                            ),
                            new RunIntake(r.intake)
                        ),

                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, 
                            new DriveToPoseObject (redDepotShotLocation)
                        ),

                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    autoChooser.addOption("Red Depot Fan Center",new SequentialCommandGroup (
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject (redCenterShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    autoChooser.addOption("Red Depot Fan Outpost",new SequentialCommandGroup (
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject (redOutpostShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    autoChooser.addOption("Red Depot Fan Depot",new SequentialCommandGroup (
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject (redDepotShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    break;
                
                case CENTER:
                
                    autoChooser.addOption("Red Center Fan Depot", new SequentialCommandGroup(
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject(redDepotShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    autoChooser.addOption("Red Center Fan Outpost",new SequentialCommandGroup(
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject(redOutpostShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    autoChooser.addOption("Red Center Fan Center", new SequentialCommandGroup(
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive,new DriveToPoseObject(redCenterShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    break;

                case OUTPOST:
                   
                    autoChooser.addOption("Red Outpost Collection", 
                        new SequentialCommandGroup(
                            new DriveToPose(
                                r.drive, 
                                new DriveToPoseObject(new Pose2d(16.123,7.375, Rotation2d.kZero))
                            
                            ),
                            new WaitCommand(2),
                            new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                            new DriveToPose(
                                r.drive,
                                new DriveToPoseObject(new Pose2d(14.662,7.375, Rotation2d.kZero)),
                                new DriveToPoseObject(redOutpostShotLocation)

                            ),
                        new ClearHopper(r.feeder, r.lift),
                            new DriveToPose(
                                r.drive,
                                new DriveToPoseObject(new Pose2d(15.078,4.742, Rotation2d.kZero))
                            ),
                            new WaitCommand(1),
                            new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                            
                    ));

                    autoChooser.addOption("Red Outpost Fan Outpost",new SequentialCommandGroup (
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject (redOutpostShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                     autoChooser.addOption("Red Outpost Fan Center",new SequentialCommandGroup (
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject (redCenterShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                     autoChooser.addOption("Red Outpost Fan Depot",new SequentialCommandGroup (
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject (redDepotShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    break;
            }

        } else {

            switch (locationChooser.getSelected()) {
                case DEPOT:

                    autoChooser.addOption("Blue Depot Collection", 
                        new SequentialCommandGroup(
                            new DriveToPose (r.drive, 
                                new DriveToPoseObject(new Pose2d(1.275, 6.943, Rotation2d.kCCW_90deg),0.25),
                                new DriveToPoseObject(new Pose2d(0.412, 6.928,Rotation2d.kCCW_90deg))
                            ),
                            new ParallelRaceGroup(
                                new RunIntake (r.intake), 
                                new DriveToPose (r.drive,
                                new DriveToPoseObject(new Pose2d(0.421, 4.941, Rotation2d.kCCW_90deg))
                                )
                            ),
                            new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                            new DriveToPose  (r.drive,
                                new DriveToPoseObject(new Pose2d(1.509, 4.956, Rotation2d.kZero),0.25),
                                new DriveToPoseObject(blueDepotShotLocation)
                            ),
                            new WaitCommand(1),
                            new ClearHopper(r.feeder, r.lift).withTimeout(5),
                            new InstantCommand(() -> {SystemVariables.runShooter = false;})
                        ));

                     autoChooser.addOption("Blue Depot Fan Depot", new SequentialCommandGroup(
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject(blueDepotShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    break;

                case CENTER:

                    autoChooser.addOption("Blue Center Fan Depot", new SequentialCommandGroup(
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject(blueDepotShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    autoChooser.addOption("Blue Center Fan Outpost", new SequentialCommandGroup(
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject(blueOutpostShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    autoChooser.addOption("Blue Center Fan Center", new SequentialCommandGroup(
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject(blueCenterShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));
                    
                    break;

                case OUTPOST:
                   
                    autoChooser.addOption("Blue Outpost Collection", 
                        new SequentialCommandGroup(
                            new DriveToPose(
                                r.drive, 
                                new DriveToPoseObject(new Pose2d(0.407,0.702, Rotation2d.kZero))
                            
                            ),
                            new WaitCommand(2),
                            new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                            new DriveToPose(
                                r.drive,
                                new DriveToPoseObject(new Pose2d(2.017,0.623, Rotation2d.kZero), 0.5),
                                new DriveToPoseObject(blueOutpostShotLocation)

                            ),
                            new WaitCommand(1),
                            new ClearHopper(r.feeder, r.lift).withTimeout(5),
                            new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    autoChooser.addOption("Blue Outpost Fan Outpost", new SequentialCommandGroup(
                        new InstantCommand(() -> {SystemVariables.runShooter = true;}),
                        new DriveToPose(r.drive, new DriveToPoseObject(blueOutpostShotLocation)),
                        new WaitCommand(1),
                        new ClearHopper(r.feeder, r.lift).withTimeout(5),
                        new InstantCommand(() -> {SystemVariables.runShooter = false;})
                    ));

                    break;
            }




        }
    }
    public Command getClimbCommand() {
        //TODO: Populate options
        if (allianceChooser.getSelected().equals(AllianceColor.RED)) {
            switch (climbChooser.getSelected()) {
                case NONE:
                    return new WaitCommand(1);
                case DEPOT_FRONT:
                    return new SequentialCommandGroup(
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.COMPACT)),
                        new DriveToPose(r.drive, 
                            new DriveToPoseObject(new Pose2d(14.5, 3.8, Rotation2d.k180deg), 0.25),
                            new DriveToPoseObject(new Pose2d(15, 3.8, Rotation2d.k180deg), MetersPerSecond.of(0.5))
                        ),
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.CLIMBED)),
                        new WaitCommand(5)
                    );
                case DEPOT_BACK: 
                    return new SequentialCommandGroup(
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.COMPACT)),
                        new DriveToPose(r.drive, 
                            new DriveToPoseObject(new Pose2d(14.75, 3.1, Rotation2d.kZero), 0.1),
                            new DriveToPoseObject(new Pose2d(16.1, 3.1, Rotation2d.kZero), 0.1),
                            new DriveToPoseObject(new Pose2d(16.1, 3.8, Rotation2d.kZero), 0.1, MetersPerSecond.of(1)),
                            new DriveToPoseObject(new Pose2d(15.9, 3.8, Rotation2d.kZero), MetersPerSecond.of(0.5))
                        ),
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.CLIMBED)),
                        new WaitCommand(5)
                    );
                case OUTPOST_FRONT:
                    return new SequentialCommandGroup(
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.COMPACT)),
                        new DriveToPose(r.drive, 
                            new DriveToPoseObject(new Pose2d(14.5, 4.8, Rotation2d.k180deg), 0.25),
                            new DriveToPoseObject(new Pose2d(15, 4.8, Rotation2d.k180deg), MetersPerSecond.of(0.5))
                        ),
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.CLIMBED)),
                        new WaitCommand(5)
                    );
                case OUTPOST_BACK:
                    return new SequentialCommandGroup(
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.COMPACT)),
                        new DriveToPose(r.drive, 
                            new DriveToPoseObject(new Pose2d(14.75, 5.5, Rotation2d.kZero), 0.1),
                            new DriveToPoseObject(new Pose2d(16.1, 5.5, Rotation2d.kZero), 0.1),
                            new DriveToPoseObject(new Pose2d(16.1, 4.8, Rotation2d.kZero), 0.1, MetersPerSecond.of(1)),
                            new DriveToPoseObject(new Pose2d(15.9, 4.8, Rotation2d.kZero), MetersPerSecond.of(0.5))
                        ),
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.CLIMBED)),
                        new WaitCommand(5)
                    );
            }
        } else {
            switch (climbChooser.getSelected()) {
                case NONE:
                    return new WaitCommand(1);
                case DEPOT_FRONT:
                    return new SequentialCommandGroup(
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.COMPACT)),
                        new DriveToPose(r.drive, 
                            new DriveToPoseObject(new Pose2d(2, 4.1, Rotation2d.kZero), 0.25),
                            new DriveToPoseObject(new Pose2d(1.4, 4.1, Rotation2d.kZero), MetersPerSecond.of(0.5))
                        ),
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.CLIMBED)),
                        new WaitCommand(5)
                    );
                case DEPOT_BACK:
                    return new SequentialCommandGroup(
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.COMPACT)),
                        new DriveToPose(r.drive, 
                            new DriveToPoseObject(new Pose2d(1.5, 5, Rotation2d.k180deg), 0.1),
                            new DriveToPoseObject(new Pose2d(0.4, 5, Rotation2d.k180deg), 0.1),
                            new DriveToPoseObject(new Pose2d(0.4, 4.1, Rotation2d.k180deg), 0.1, MetersPerSecond.of(1)),
                            new DriveToPoseObject(new Pose2d(0.7, 4.1, Rotation2d.k180deg), MetersPerSecond.of(0.5))
                        ),
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.CLIMBED)),
                        new WaitCommand(5)
                    );
                case OUTPOST_FRONT:
                    return new SequentialCommandGroup(
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.COMPACT)),
                        new DriveToPose(r.drive, 
                            new DriveToPoseObject(new Pose2d(2, 3.3, Rotation2d.kZero), 0.25),
                            new DriveToPoseObject(new Pose2d(1.4, 3.3, Rotation2d.kZero), MetersPerSecond.of(0.5))
                        ),
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.CLIMBED)),
                        new WaitCommand(5)
                    );
                case OUTPOST_BACK:
                    return new SequentialCommandGroup(
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.COMPACT)),
                        new DriveToPose(r.drive, 
                            new DriveToPoseObject(new Pose2d(1.5, 2.5, Rotation2d.k180deg), 0.1),
                            new DriveToPoseObject(new Pose2d(0.4, 2.5, Rotation2d.k180deg), 0.1),
                            new DriveToPoseObject(new Pose2d(0.4, 3.3, Rotation2d.k180deg), 0.1, MetersPerSecond.of(1)),
                            new DriveToPoseObject(new Pose2d(0.7, 3.3, Rotation2d.k180deg), MetersPerSecond.of(0.5))
                        ),
                        new InstantCommand(() -> r.lift.setTargetState(LiftStates.CLIMBED)),
                        new WaitCommand(5)
                    );
            }
        }
        return new WaitCommand(1);
    }


    public void updateClass() {
        if (SmartDashboard.getBoolean("Auton/Build Chooser?", false)) {
            SmartDashboard.putBoolean("Auton/Build Chooser?", false);
            createChooser();
            SmartDashboard.putData("Auto Selection", autoChooser);
        }
    }

    public Command getSelectedAuton() {
        return autoChooser.getSelected().andThen(getClimbCommand());
    }
}
