// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.epilogue.logging.NullBackend;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.SystemVariables.ShooterConstants;
import frc.robot.commands.drive.DriveToPose;
import frc.robot.commands.drive.FieldCentricControl;
import frc.robot.commands.feeder.RunFeeder;
import frc.robot.commands.intake.RunIntake;
import frc.robot.commands.lift.LiftToPosition;
import frc.robot.commands.shooter.RunShooter;
import frc.robot.commands.turret.RunTurretToTarget;
import frc.robot.commands.turret.RunTurretToWin;
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

        START HERE 2/14/26

       - Shoot Command
            - Change the isShootSpunUp method to actually check if it is at the target velo

        Do this as soon as we have a robot

        - ToDo on physical robot
            - Update Limelight firmware
            - ID NEOs
            - Update firmware on NEOs
            - Run through swerve generator and create TunerConstants.java file
            - Tune turret PID
            - Tune lift PID
            - Tune flywheel PID
            - Determine multiplier to convert velocity method from meters per second to rev per second
            - Drive To Pose PID
            - Drive To Poes Slew Rate Limit
            - Lift positions for each state
            - Full system tests to find potential issues

        If DONE with above and we don't have a robot, contine here

        - Improve code for comp
            - Create target 'states' for lift subsystem, aka Retracted, Intaking, and Climbed
            - Create handshaking for driver shoot button
                - Turns chassis to brake mode
                - Checks that shooter is up to speed
                - Checks that turret is in position
            - Create buttons for auto-driving over the bumps or trench (if we fit under trench)
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

    Command defaultCommand = new WaitCommand(1);


    /* Path follower */
    private final SendableChooser<Command> autoChooser = new SendableChooser<>();

    public RobotContainer() {
        autoChooser.setDefaultOption("Do Nothing", defaultCommand);

        autoChooser.addOption("Tuning", 
            new SequentialCommandGroup(
                new DriveToPose(
                    drive, 
                    new DriveToPoseObject(new Pose2d(1,1, Rotation2d.kZero)),
                    new DriveToPoseObject(new Pose2d(0,0, Rotation2d.kZero)),
                    new DriveToPoseObject(new Pose2d(2,2, Rotation2d.kZero)),
                    new DriveToPoseObject(new Pose2d(0,0, Rotation2d.kZero)),
                    new DriveToPoseObject(new Pose2d(4,4, Rotation2d.kZero)),
                    new DriveToPoseObject(new Pose2d(0,0, Rotation2d.kZero)),
                    new DriveToPoseObject(new Pose2d(8,8, Rotation2d.kZero)),
                    new DriveToPoseObject(new Pose2d(0,0, Rotation2d.kZero))
                )
        ));

        autoChooser.addOption("human blue", 
            new SequentialCommandGroup(
                new DriveToPose(
                    drive, 
                    new DriveToPoseObject(new Pose2d(0.407,0.702, Rotation2d.kZero))
                   
                ),
                new WaitCommand(2),
                new DriveToPose(
                    drive,
                    new DriveToPoseObject(new Pose2d(2.017,0.623, Rotation2d.kZero)),
                    new DriveToPoseObject(new Pose2d(2.017,3.144, Rotation2d.kZero))

                ),
  new DriveToPose(
                    drive,
                     new DriveToPoseObject(new Pose2d(1.605,3.144, Rotation2d.kZero))
  )
        ));

        autoChooser.addOption("Null", 
            new SequentialCommandGroup(
                new DriveToPose (drive, 
                    new DriveToPoseObject(new Pose2d(1.275, 6.943, Rotation2d.kCCW_90deg),0.25),
                    new DriveToPoseObject(new Pose2d(0.412, 6.928,Rotation2d.kCCW_90deg))
                ),
                new ParallelRaceGroup(
                    new RunIntake (intake), 
                    new DriveToPose (drive,
                    new DriveToPoseObject(new Pose2d(0.421, 4.941, Rotation2d.kCCW_90deg))
                    )
                ),
                new DriveToPose  (drive,
                    new DriveToPoseObject(new Pose2d(1.509, 4.956, Rotation2d.kZero),0.25),
                    new DriveToPoseObject(new Pose2d(1.509,4.188, Rotation2d.kZero) )
                )
        )   );
        

        SmartDashboard.putData("Auto Mode", autoChooser);

        configureBindings();
    }

    private void configureBindings() {

        drive.setDefaultCommand(new FieldCentricControl(drive, driverController));
        turret.setDefaultCommand(new RunTurretToWin(turret));
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

        operatorController.povUp().whileTrue(new RunShooter(shooter));

        operatorController.b().onTrue(new InstantCommand(() -> lift.setTargetPosition(100)));

        driverController.rightTrigger(0.5).whileTrue(new RunFeeder(feeder));
    }

    public Command getAutonomousCommand() {
        /* Run the path selected from the auto chooser */
        return autoChooser.getSelected();
    }

    public boolean readyForMatch() {
        return 
            !autoChooser.getSelected().equals(defaultCommand) && 
            SmartDashboard.getBoolean("Match Setup/Precise Pose Setup", false);
    }
}
