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
import frc.robot.commands.drive.XFormation;
import frc.robot.SystemVariables.DrivetrainConstants;
import frc.robot.SystemVariables.LiftConstants.LiftStates;
import frc.robot.commands.feeder.ReverseFeeder;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.commands.intake.RunIntake;
import frc.robot.commands.lift.HopperNotStill;
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

    CommandXboxController driverController = new CommandXboxController(0);
    CommandXboxController operatorController = new CommandXboxController(1);

    public DriveSubsystem drive = TunerConstants.createDrivetrain();
    public IntakeSubsystem intake = new IntakeSubsystem();
    public FeederSubsystem feeder = new FeederSubsystem();
    public TurretSubsystem turret = new TurretSubsystem();
    public LiftSubsystem lift = new LiftSubsystem();
    public ShooterSubsystem shooter = new ShooterSubsystem();

    public RobotContainer() {
        configureBindings();
    }

    private void configureBindings() {

        /////////////////////////////////// Default ////////////////////////////////////////////

        drive.setDefaultCommand(new FieldCentricControl(drive, driverController));
        turret.setDefaultCommand(new RunTurretToHub(turret));
        lift.setDefaultCommand(new LiftToPosition(lift));
        shooter.setDefaultCommand(new AutoShooterToHub(shooter));
        //turret.setDefaultCommand(new RunTurretToTarget(turret));
        //turret.setDefaultCommand(new MoveTurretWithJoystick(turret, () -> operatorController.getRightX()));
        //lift.setDefaultCommand(new MoveLiftWithJoystick(lift, () -> operatorController.getLeftY()));

        /////////////////////////////////// Driver ////////////////////////////////////////////
        
        //Reset Field Centric
        driverController.a().onTrue(
            new InstantCommand(() -> 
                drive.resetPose(new Pose2d(drive.getCurrentPose().getTranslation(), DriverStation.getAlliance().get() == Alliance.Blue ? Rotation2d.kZero : Rotation2d.k180deg))
        ));

        driverController.rightTrigger(0.5).whileTrue(new ClearHopper(feeder));
        driverController.leftTrigger(0.5).whileTrue(new ReverseFeeder(feeder));

        driverController.leftBumper().onTrue(new InstantCommand(() -> lift.setTargetState(LiftStates.COMPACT)))
            .onFalse(new InstantCommand(() -> lift.setTargetState(LiftStates.INTAKE)));
        driverController.povUp().whileTrue(new RunFeeder(feeder));

        driverController.b().onTrue(new XFormation(drive, driverController));

        driverController.rightBumper().onTrue(new InstantCommand(() -> {SystemVariables.currentMaxSpeed = 2;}));
        driverController.rightBumper().onFalse(new InstantCommand(() -> {SystemVariables.currentMaxSpeed = DrivetrainConstants.MaxSpeed;}));

        /////////////////////////////////// Operator ////////////////////////////////////////////

        operatorController.leftTrigger(0.5).whileTrue(new RunIntake(intake));
        operatorController.rightTrigger(0.5).whileTrue(new RunShooterToHub(shooter));

        operatorController.povUp().onTrue(new InstantCommand(() -> lift.setTargetState(LiftStates.COMPACT)));
        operatorController.povDown().onTrue(new InstantCommand(() -> lift.setTargetState(LiftStates.INTAKE)));

        operatorController.x().whileTrue(new HopperNotStill(lift));

        
        //Overrides
        operatorController.a().whileTrue(new RunShooterOverride(shooter, 50).alongWith(new RunTurretToTarget(turret, 0))); //At tower override pos

        operatorController.leftBumper().toggleOnTrue(new MoveLiftWithJoystick(lift, () -> operatorController.getLeftY()));
        operatorController.rightBumper().toggleOnTrue(new MoveTurretWithJoystick(turret, () -> operatorController.getRightX()));


    }

}
