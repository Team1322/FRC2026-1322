// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.complexCommands.ClearHopper;
import frc.robot.commands.drive.FieldCentricControl;
import frc.robot.SystemVariables.LiftConstants.LiftStates;
import frc.robot.commands.feeder.ReverseFeeder;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.commands.intake.RunIntake;
import frc.robot.commands.lift.LiftToPosition;
import frc.robot.commands.lift.MoveLiftWithJoystick;
import frc.robot.commands.shoot.AutoShooterToHub;
import frc.robot.commands.shoot.RunShooterOverride;
import frc.robot.commands.shoot.RunShooterToHub;
import frc.robot.commands.turret.MoveTurretWithJoystick;
import frc.robot.commands.turret.RunTurretToHub;
import frc.robot.commands.turret.RunTurretToTarget;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.TurretSubsystem;

import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class RobotContainer {
    /*
        TODO For Comp

        - ToDo on physical robot
            - Drive To Pose PID
            - Drive To Poes Slew Rate Limit
            - Full system tests to find potential issues

        If DONE with above and we don't have a robot, contine here

        - Improve code for comp
            - Create handshaking for driver shoot button
                - Turns chassis to brake mode
            

    */

    CommandXboxController driverController = new CommandXboxController(0);
    CommandXboxController operatorController = new CommandXboxController(1);

    DriveSubsystem drive = TunerConstants.createDrivetrain();
    IntakeSubsystem intake = new IntakeSubsystem();
    FeederSubsystem feeder = new FeederSubsystem();
    TurretSubsystem turret = new TurretSubsystem();
    LiftSubsystem lift = new LiftSubsystem();
    ShooterSubsystem shooter = new ShooterSubsystem();

    public RobotContainer() {


    
        configureBindings();
    }

    private void configureBindings() {

        drive.setDefaultCommand(new FieldCentricControl(drive, driverController));
        turret.setDefaultCommand(new RunTurretToHub(turret));
        //turret.setDefaultCommand(new RunTurretToTarget(turret));
        //turret.setDefaultCommand(new MoveTurretWithJoystick(turret, () -> operatorController.getRightX()));
        //lift.setDefaultCommand(new MoveLiftWithJoystick(lift, () -> operatorController.getLeftY()));
        lift.setDefaultCommand(new LiftToPosition(lift));
        shooter.setDefaultCommand(new AutoShooterToHub(shooter));

        operatorController.leftTrigger(0.5).whileTrue(new RunIntake(intake));
        operatorController.rightTrigger(0.5).whileTrue(new RunShooterToHub(shooter));

        operatorController.povUp().onTrue(new InstantCommand(() -> lift.setTargetState(LiftStates.COMPACT)));
        operatorController.povDown().onTrue(new InstantCommand(() -> lift.setTargetState(LiftStates.INTAKE)));

        operatorController.a().whileTrue(new RunShooterOverride(shooter, 50).alongWith(new RunTurretToTarget(turret, 0))); //At tower override pos

        operatorController.leftBumper().whileTrue(new MoveLiftWithJoystick(lift, () -> operatorController.getLeftY()));
        operatorController.rightBumper().whileTrue(new MoveTurretWithJoystick(turret, () -> operatorController.getRightX()));

        driverController.rightTrigger(0.5).whileTrue(new ClearHopper(feeder, lift));
        driverController.leftTrigger(0.5).whileTrue(new ReverseFeeder(feeder));
        driverController.a().onTrue(
            new InstantCommand(() -> 
                drive.resetPose(new Pose2d(drive.getCurrentPose().getTranslation(), DriverStation.getAlliance().get() == Alliance.Blue ? Rotation2d.kZero : Rotation2d.k180deg))
        ));
        driverController.leftBumper().onTrue(new InstantCommand(() -> lift.setTargetState(LiftStates.COMPACT)))
            .onFalse(new InstantCommand(() -> lift.setTargetState(LiftStates.CLIMBED)));
        driverController.rightBumper().whileTrue(new RunFeeder(feeder));

        operatorController.x().onTrue(new InstantCommand(() -> lift.deployLiftServo()));
        operatorController.b().onTrue(new InstantCommand(() -> lift.resetLiftServo()));

    }

}
