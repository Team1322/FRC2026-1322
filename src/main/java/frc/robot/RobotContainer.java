// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.SystemVariables.DrivetrainConstants;
import frc.robot.commands.drive.DriveToPose;
import frc.robot.commands.drive.FieldCentricControl;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.commands.intake.RunIntake;
import frc.robot.commands.lift.LiftToPosition;
import frc.robot.commands.turret.RunTurretToTarget;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.TurretSubsystem;

import frc.robot.subsystems.LiftSubsystem;

public class RobotContainer {
    /*
        TODO For 2/14/26 

        - Finish ShooterSubsystem
            - Build Commands
            - Add subsystem to RobotContainer

        - Make updates to turret
            - Create constants for limits in either direction
            - Change getCurrentPosition to convert motor rotations to degrees
            - Adjust command to utilize the getCurrentAngle method

        - Improve code for comp
            - Create target 'states' for lift subsystem, aka Retracted, Intaking, and Climbed
            - Create handshaking for driver shoot button
                - Turns chassis to brake mode
                - Checks that shooter is up to speed
                - Checks that turret is in position
            - Create buttons for auto-driving over the bumps
            - Create some autos we are likely to run
                - Drive to shoot, shoot
                - Drive to shoot, shoot, climb auto
                - Drive to human, pickup, drive to shoot, shoot
                - Drive to human, pickup, drive to shoot, shoot, climb
                - Drive to depot, pickup, drive to shoot, shoot
                - Drive to depot, pickup, drive to shoot, shoot, climb
            - If you are feeling up to it and everything else is done, create more complex autos
                - Drive to shoot, shoot, drive to mid-field, pickup, drive back, shoot
                - Drive to mid-field, pickup, drive back, shoot, maybe climb???
                - Drive to human, pickup, Drive to depot, pickup, drive to shoot, shoot, climb

        - Create fail-safes
            - Add overrides to shoot power if positional data is unknown
                - Create this as known spots to shoot and ignore vision data for this
            - Add overrides to turret angle if positional data is unknown
                - Same as shooter, just do the same for turret
            - Add a zero button for field-centric
    */

    private final CommandXboxController driverController = new CommandXboxController(0);

    CommandXboxController operatorController = new CommandXboxController(1);

    public final DriveSubsystem drive = TunerConstants.createDrivetrain();

    IntakeSubsystem intake = new IntakeSubsystem();
    FeederSubsystem feeder = new FeederSubsystem();
    TurretSubsystem turret = new TurretSubsystem();
    LiftSubsystem lift = new LiftSubsystem();

    private final Telemetry logger = new Telemetry(DrivetrainConstants.MaxSpeed);

    /* Path follower */
    private final SendableChooser<Command> autoChooser = new SendableChooser<>();

    public RobotContainer() {
        autoChooser.setDefaultOption("Do Nothing", new WaitCommand(1));

        autoChooser.addOption("Test", 
            new DriveToPose(
                drive, 
                new DriveToPoseObject(new Pose2d(2,0, Rotation2d.kZero), MetersPerSecond.of(2)),
                new DriveToPoseObject(new Pose2d(2,8, Rotation2d.k180deg), MetersPerSecond.of(2)),
                new DriveToPoseObject(new Pose2d(2,0, Rotation2d.k180deg), MetersPerSecond.of(2))
        ));

        SmartDashboard.putData("Auto Mode", autoChooser);

        configureBindings();
    }

    private void configureBindings() {

        drive.setDefaultCommand(new FieldCentricControl(drive, driverController));
        turret.setDefaultCommand(new RunTurretToTarget(turret));
        lift.setDefaultCommand(new LiftToPosition(lift));

        driverController.a().onTrue(new InstantCommand(() -> drive.setUseMT1(true)));
        driverController.b().onTrue(new InstantCommand(() -> drive.setUseMT2(true)));
        driverController.x().onTrue(new InstantCommand(() -> {
            drive.setUseMT1(false);
            drive.setUseMT2(false);
        }));

        operatorController.a().whileTrue(new RunIntake(intake));
        operatorController.x().onTrue(new InstantCommand(() -> turret.setTargetPosition(100)));
        operatorController.y().onTrue(new InstantCommand(() -> turret.setTargetPosition(0)));

        operatorController.b().onTrue(new InstantCommand(() -> lift.setTargetPosition(100)));

        driverController.rightTrigger(0.5).whileTrue(new RunFeeder(feeder));

        drive.registerTelemetry(logger::telemeterize);
    }

    public Command getAutonomousCommand() {
        /* Run the path selected from the auto chooser */
        return autoChooser.getSelected();
    }
}
