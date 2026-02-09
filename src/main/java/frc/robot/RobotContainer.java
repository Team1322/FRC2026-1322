// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.SystemVariables.DrivetrainConstants;
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
