// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.SystemVariables.LiftConstants.LiftStates;
import frc.robot.commands.drive.FieldCentricControl;
import frc.robot.commands.feeder.ReverseFeeder;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.commands.intake.RunIntake;
import frc.robot.commands.lift.LiftToPosition;
import frc.robot.commands.lift.MoveLiftWithJoystick;
import frc.robot.commands.shoot.RunShooterOverride;
import frc.robot.commands.shoot.RunShooterToHub;
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
            - Tune flywheel PID
            - Determine multiplier to convert velocity method from meters per second to rev per second
            - Drive To Pose PID
            - Drive To Poes Slew Rate Limit
            - Full system tests to find potential issues

        If DONE with above and we don't have a robot, contine here

        - Improve code for comp
            - Create handshaking for driver shoot button
                - Turns chassis to brake mode
                - Checks that shooter is up to speed
                - Checks that turret is in position
            - Determine shoot target when feeding instead of scoring
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
        lift.setDefaultCommand(new MoveLiftWithJoystick(lift, () -> operatorController.getLeftY()));
        //lift.setDefaultCommand(new LiftToPosition(lift));



        operatorController.leftTrigger(0.5).whileTrue(new RunIntake(intake));
        operatorController.rightTrigger(0.5).whileTrue(new RunShooterToHub(shooter));

        operatorController.povUp().onTrue(new InstantCommand(() -> lift.setTargetState(LiftStates.COMPACT)));
        operatorController.povDown().onTrue(new InstantCommand(() -> lift.setTargetState(LiftStates.INTAKE)));

        operatorController.a().whileTrue(new RunShooterOverride(shooter, 50).andThen(new RunTurretToTarget(turret, 0))); //At tower override pos


        driverController.rightTrigger(0.5).whileTrue(new RunFeeder(feeder));
        driverController.leftTrigger(0.5).whileTrue(new ReverseFeeder(feeder));
    }

}
