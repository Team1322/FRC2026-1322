package frc.robot.subsystems;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.LimelightHelpers;
import frc.robot.SystemVariables;
import frc.robot.SystemVariables.FieldConstants;
import frc.robot.SystemVariables.ShooterConstants;
import frc.robot.SystemVariables.TurretConstants;
import frc.robot.generated.TunerConstants.TunerSwerveDrivetrain;

/**
 * Class that extends the Phoenix 6 SwerveDrivetrain class and implements
 * Subsystem so it can easily be used in command-based projects.
 */
public class DriveSubsystem extends TunerSwerveDrivetrain implements Subsystem {

    public final SwerveRequest.FieldCentric fieldCentric = new SwerveRequest.FieldCentric()
            .withDriveRequestType(DriveRequestType.Velocity); // Use open-loop control for drive motors

    public final SwerveRequest.FieldCentricFacingAngle driveToPoseController = new SwerveRequest.FieldCentricFacingAngle()
            .withForwardPerspective(SwerveRequest.ForwardPerspectiveValue.BlueAlliance)
            .withDriveRequestType(DriveRequestType.Velocity)
            .withHeadingPID(7, 0, 0);

    public final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();

    StructPublisher<Pose2d> robotPosePublisher = NetworkTableInstance.getDefault()
        .getStructTopic("Robot Pose", Pose2d.struct).publish();

    StructPublisher<Pose2d> shootTargetPublisher = NetworkTableInstance.getDefault()
        .getStructTopic("Shooting Target", Pose2d.struct).publish();

    SendableChooser<Pose2d> poseOptions = new SendableChooser<>();

    ///////////////////////////////////// Drive to Pose Controllers /////////////////////////////////////
    private final PIDController translationalController = new PIDController(5, 0, 0.08);
    private final SlewRateLimiter accelerationLimiter = new SlewRateLimiter(100, -4, 0); 

    private static final double kSimLoopPeriod = 0.005; // 5 ms
    private Notifier m_simNotifier = null;
    private double m_lastSimTime;
    private boolean useMT1, useMT2;

    /* Blue alliance sees forward as 0 degrees (toward red alliance wall) */
    private static final Rotation2d kBlueAlliancePerspectiveRotation = Rotation2d.kZero;
    /* Red alliance sees forward as 180 degrees (toward blue alliance wall) */
    private static final Rotation2d kRedAlliancePerspectiveRotation = Rotation2d.k180deg;
    /* Keep track if we've ever applied the operator perspective before or not */
    private boolean m_hasAppliedOperatorPerspective = false;

    /**
     * Constructs a CTRE SwerveDrivetrain using the specified constants.
     * <p>
     * This constructs the underlying hardware devices, so users should not
     * construct
     * the devices themselves. If they need the devices, they can access them
     * through
     * getters in the classes.
     *
     * @param drivetrainConstants Drivetrain-wide constants for the swerve drive
     * @param modules             Constants for each specific module
     */
    public DriveSubsystem(
            SwerveDrivetrainConstants drivetrainConstants,
            SwerveModuleConstants<?, ?, ?>... modules) {
        super(drivetrainConstants, modules);

        if (Utils.isSimulation()) {
            startSimThread();
        }

        LimelightHelpers.setCameraPose_RobotSpace("limelight-main", 
        Units.inchesToMeters(12.25),    // Forward offset (meters)
        Units.inchesToMeters(0),    // Side offset (meters)
        Units.inchesToMeters(10.25),    // Height offset (meters)
            0.0,    // Roll (degrees)
            0,   // Pitch (degrees)
            25     // Yaw (degrees)
        );

        poseOptions.setDefaultOption("All Zeros", Pose2d.kZero);
        poseOptions.addOption("Blue Goal", new Pose2d(3.6, 4, Rotation2d.kZero));
        poseOptions.addOption("Blue Depot Side", new Pose2d(3.6, 6, Rotation2d.kZero));
        poseOptions.addOption("Blue Outpost Side", new Pose2d(3.570, 0.641, Rotation2d.kZero));
        poseOptions.addOption("Red Goal", new Pose2d(12.9, 4, Rotation2d.k180deg));
        poseOptions.addOption("Red Depot Side", new Pose2d(12.9, 2, Rotation2d.k180deg));
        poseOptions.addOption("Red Outpost Side", new Pose2d(12.9, 6, Rotation2d.k180deg));
        
        SmartDashboard.putBoolean("Match Setup/Initial Pose Setup", true);
        SmartDashboard.putBoolean("Match Setup/Precise Pose Setup", false);

        SmartDashboard.putData("Pose Options/Option Selector", poseOptions);
        SmartDashboard.putBoolean("Pose Options/Seed Pose?", false);

        SmartDashboard.putBoolean("PoseSeeding/Seed Pose?", false);
        SmartDashboard.putNumber("PoseSeeding/Seed Pose X", 0);
        SmartDashboard.putNumber("PoseSeeding/Seed Pose Y", 0);
        SmartDashboard.putNumber("PoseSeeding/Seed Pose Angle", 0);

        SmartDashboard.putNumber("DriveToPose/P", translationalController.getP());
        SmartDashboard.putNumber("DriveToPose/I", translationalController.getI());
        SmartDashboard.putNumber("DriveToPose/D", translationalController.getD());
    }
    
    @Override
    public void periodic() {
        //Update Shooter Handshake
        SystemVariables.turretDistanceFromGoal = getDistanceFromShot();
        SystemVariables.turretAngleToGoal = getAngleToGoal();
        SystemVariables.turretZeroDirection = getTurretPose().getRotation();

        seedPoseFromDash();
        seedPoseFromOptions();
        setLimelightMode();

        if (useMT1)
            updatePoseWithMT1();
        else if (useMT2)
            updatePoseWithMT2();

        robotPosePublisher.set(getCurrentPose());
        shootTargetPublisher.set(new Pose2d(getShootTarget(), Rotation2d.kZero));
        SystemVariables.turretPose = getTurretPose();

        /*
         * Periodically try to apply the operator perspective.
         * If we haven't applied the operator perspective before, then we should apply
         * it regardless of DS state.
         * This allows us to correct the perspective in case the robot code restarts
         * mid-match.
         * Otherwise, only check and apply the operator perspective if the DS is
         * disabled.
         * This ensures driving behavior doesn't change until an explicit disable event
         * occurs during testing.
         */
        if (!m_hasAppliedOperatorPerspective || DriverStation.isDisabled()) {
            DriverStation.getAlliance().ifPresent(allianceColor -> {
                setOperatorPerspectiveForward(
                        allianceColor == Alliance.Red
                                ? kRedAlliancePerspectiveRotation
                                : kBlueAlliancePerspectiveRotation);
                m_hasAppliedOperatorPerspective = true;
            });
        }
    }

    /////////////////////////////////////////////////// Getters ///////////////////////////////////////////////////
    
    public Pose2d getCurrentPose() {
        return this.getState().Pose;
    }

    public Pose2d getTurretPose() {
        return getCurrentPose().transformBy(TurretConstants.TURRET_LOCATION);
    }

    public double getDistanceFromShot() {
        return getShootTarget().getDistance(getTurretPose().getTranslation());
    }

    public Rotation2d getAngleToGoal() {
        return absoluteAngleFromPose(getShootTarget(), getTurretPose().getTranslation());
    }

    private Translation2d getShootTarget() {
        Translation2d target = DriverStation.getAlliance().get() == Alliance.Red ? FieldConstants.RED_GOAL : FieldConstants.BLUE_GOAL;
        target = target.minus(new Translation2d(getState().Speeds.vxMetersPerSecond * 0.3, getState().Speeds.vyMetersPerSecond * 0.3));
        return target;
    }

    ////////////////////////////////////////////////// Setters ///////////////////////////////////////////////////////////////
    
    public void setPose(Pose2d newPose) {
        resetPose(newPose);
    }

    public void seedPoseFromDash() {
        if (SmartDashboard.getBoolean("PoseSeeding/Seed Pose?", false)) {
            SmartDashboard.putBoolean("PoseSeeding/Seed Pose?", false);
            setPose(new Pose2d(
                SmartDashboard.getNumber("PoseSeeding/Seed Pose X", 0),
                SmartDashboard.getNumber("PoseSeeding/Seed Pose Y", 0),
                Rotation2d.fromDegrees(SmartDashboard.getNumber("PoseSeeding/Seed Pose Angle", 0))
            ));
        }
    }

    public void seedPoseFromOptions() {
         if (SmartDashboard.getBoolean("Pose Options/Seed Pose?", false)) {
            SmartDashboard.putBoolean("Pose Options/Seed Pose?", false);
            setPose(poseOptions.getSelected());
        }
    }

    ////////////////////////////////////////////////// Drive To Pose Methods //////////////////////////////////////////////////

    /**
     * 
     * @param drivingPose Pose that will be the target pose for the translational
     *                    controller
     * @param anglePose   Pose that will set the angle the robot will drive in
     */

    public void driveToPosition(Pose2d drivingPose, Pose2d anglePose, LinearVelocity maxSpeed) {

        translationalController.setPID(
            SmartDashboard.getNumber("DriveToPose/P", translationalController.getP()),
            SmartDashboard.getNumber("DriveToPose/I", translationalController.getI()),
            SmartDashboard.getNumber("DriveToPose/D", translationalController.getD())
        );

        double distanceAway = distanceFromPose(drivingPose, getCurrentPose()) + distanceFromPose(drivingPose, anglePose);

        SmartDashboard.putNumber("DriveToPose/Distance Away", distanceAway);

        // Determine the sent velocity of the robot in meters per second
        double translationalOutput = translationalController.calculate(distanceAway);
        translationalOutput = MathUtil.clamp(translationalOutput, -maxSpeed.in(MetersPerSecond),
                maxSpeed.in(MetersPerSecond));
        translationalOutput = accelerationLimiter.calculate(translationalOutput);

        // Apply velocity in the direction of the anglePose
        Rotation2d angleToPose = absoluteAngleFromPose(getCurrentPose(), anglePose);
        setControl(
                driveToPoseController
                        .withVelocityX(translationalOutput * Math.cos(angleToPose.getRadians()))
                        .withVelocityY(translationalOutput * Math.sin(angleToPose.getRadians()))
                        .withTargetDirection(anglePose.getRotation()));
    }


    ///////////////////////////////////////////////// Limelight Methods /////////////////////////////////////////////////

    public void setLimelightMode() {
        if (SmartDashboard.getBoolean("Match Setup/Initial Pose Setup", true) && !useMT1) {
            SmartDashboard.putBoolean("Match Setup/Precise Pose Setup", false);
            setUseMT1(true);
        } else if (SmartDashboard.getBoolean("Match Setup/Precise Pose Setup", false) && !useMT2) {
            SmartDashboard.putBoolean("Match Setup/Initial Pose Setup", false);
            setUseMT2(true);
        }
    }

    public void setUseMT1(boolean useMT1) {
        if (useMT1)
            useMT2 = false;
        this.useMT1 = useMT1;
    }

    public void setUseMT2(boolean useMT2) {
        if (useMT2)
            useMT1 = false;
        this.useMT2 = useMT2;
    }

    public void updatePoseWithMT1() {
        boolean updateVision = true;

        LimelightHelpers.PoseEstimate mt1 = LimelightHelpers.getBotPoseEstimate_wpiBlue("limelight-main");
        if (mt1 != null) {
            System.out.println("Alex is the Best!");

            if (mt1.tagCount == 1 && mt1.rawFiducials.length == 1) {
                if (mt1.rawFiducials[0].ambiguity > .7) {
                    updateVision = false;
                }
                if (mt1.rawFiducials[0].distToCamera > 3) {
                    updateVision = false;
                }
            }
            if (mt1.tagCount == 0) {
                updateVision = false;
            }
            if (updateVision) {
                setVisionMeasurementStdDevs(VecBuilder.fill(.5, .5, Math.toRadians(1)));
                addVisionMeasurement(
                        mt1.pose,
                        Utils.fpgaToCurrentTime(mt1.timestampSeconds));
            }
        }
    }

    public void updatePoseWithMT2() {
        boolean updateVision = true;

        LimelightHelpers.SetRobotOrientation("limelight-main", getCurrentPose().getRotation().getDegrees(), 0, 0, 0, 0, 0);
        LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight-main");
        if (Math.abs(getPigeon2().getAngularVelocityZWorld().getValueAsDouble()) > 720) { // if our angular velocity is
                                                                                          // greater than 720 degrees
                                                                                          // per second, ignore vision
                                                                                          // updates
            updateVision = false;
        }

        if (mt2 != null) {
            if (mt2.tagCount == 0) {
                updateVision = false;
            }
            if (updateVision) {

                setVisionMeasurementStdDevs(VecBuilder.fill(0.15, 0.15, 9999999)); // x and Y were 0.7
                addVisionMeasurement(
                        new Pose2d(mt2.pose.getTranslation(), getCurrentPose().getRotation() ),
                        Utils.fpgaToCurrentTime(mt2.timestampSeconds));
            }
        }
    }

    private void startSimThread() {
        m_lastSimTime = Utils.getCurrentTimeSeconds();

        /* Run simulation at a faster rate so PID gains behave more reasonably */
        m_simNotifier = new Notifier(() -> {
            final double currentTime = Utils.getCurrentTimeSeconds();
            double deltaTime = currentTime - m_lastSimTime;
            m_lastSimTime = currentTime;

            /* use the measured time delta, get battery voltage from WPILib */
            updateSimState(deltaTime, RobotController.getBatteryVoltage());
        });
        m_simNotifier.startPeriodic(kSimLoopPeriod);
    }

    /////////////////////////// Pose Utility Methods /////////////////////////////////////////////////////////////////////////////

    /**
     * Method gets the distance of a specified point from another point using a
     * third point as a rotational reference
     * 
     * @param measurementPose
     * @param lineOrigin
     * @param lineXPos
     * @return
     */
    public Translation2d translationFromLine(Pose2d measurementPose, Pose2d lineOrigin, Pose2d lineXPos) {
        double lineAngle = Math.atan2(lineXPos.getY() - lineOrigin.getY(), lineXPos.getX() - lineOrigin.getX());
        lineXPos = lineXPos.rotateAround(lineOrigin.getTranslation(), Rotation2d.fromRadians(-lineAngle));
        measurementPose = measurementPose.rotateAround(lineOrigin.getTranslation(), Rotation2d.fromRadians(-lineAngle));

        lineXPos = lineXPos.transformBy(new Transform2d(lineOrigin.getTranslation(), Rotation2d.kZero));
        measurementPose = measurementPose.transformBy(new Transform2d(lineOrigin.getTranslation(), Rotation2d.kZero));

        return measurementPose.getTranslation();
    }

    public double distanceFromPose(Pose2d measurementPose, Pose2d origin) {
        return Math.sqrt(Math.pow(measurementPose.getX() - origin.getX(), 2)
                + Math.pow(measurementPose.getY() - origin.getY(), 2));
    }

    public Rotation2d absoluteAngleFromPose(Pose2d measurementPose, Pose2d origin) {
        return Rotation2d.fromRadians(
                Math.atan2(measurementPose.getY() - origin.getY(), measurementPose.getX() - origin.getX()));
    }

    public Rotation2d absoluteAngleFromPose(Translation2d measurementPose, Translation2d origin) {
        return Rotation2d.fromRadians(
                Math.atan2(measurementPose.getY() - origin.getY(), measurementPose.getX() - origin.getX()));
    }

}
