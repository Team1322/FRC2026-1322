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
import edu.wpi.first.math.geometry.Rotation2d;
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
import frc.robot.SystemVariables.DrivetrainConstants;
import frc.robot.SystemVariables.FieldConstants;
import frc.robot.SystemVariables.TurretConstants;
import frc.robot.generated.TunerConstants.TunerSwerveDrivetrain;

/**
 * Class that extends the Phoenix 6 SwerveDrivetrain class and implements
 * Subsystem so it can easily be used in command-based projects.
 */
public class DriveSubsystem extends TunerSwerveDrivetrain implements Subsystem {

    public final SwerveRequest.FieldCentric fieldCentric = new SwerveRequest.FieldCentric()
            .withDriveRequestType(DriveRequestType.Velocity); // Use open-loop control for drive motors

    public final SwerveRequest.RobotCentric robotCentric = new SwerveRequest.RobotCentric()
            .withDriveRequestType(DriveRequestType.Velocity); // Use open-loop control for drive motors

    public final SwerveRequest.FieldCentricFacingAngle driveToPoseController = new SwerveRequest.FieldCentricFacingAngle()
            .withForwardPerspective(SwerveRequest.ForwardPerspectiveValue.BlueAlliance)
            .withDriveRequestType(DriveRequestType.Velocity)
            .withMaxAbsRotationalRate(RotationsPerSecond.of(0.75).in(RadiansPerSecond))
            .withHeadingPID(7, 0, 0);

    public final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();

    StructPublisher<Pose2d> robotPosePublisher = NetworkTableInstance.getDefault()
        .getStructTopic("Robot Pose", Pose2d.struct).publish();

    StructPublisher<Pose2d> shootTargetPublisher = NetworkTableInstance.getDefault()
        .getStructTopic("Shooting Target", Pose2d.struct).publish();

    SendableChooser<Pose2d> poseOptions = new SendableChooser<>();

    ///////////////////////////////////// Drive to Pose Controllers /////////////////////////////////////
    private final PIDController translationalController = new PIDController(2, 0, 0.08);
    private final SlewRateLimiter accelerationLimiter = new SlewRateLimiter(100, -3, 0); 

    private static final double kSimLoopPeriod = 0.005; // 5 ms
    private Notifier m_simNotifier = null;
    private double m_lastSimTime;

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
            translationalController.setP(5);
        }

        //configNeutralMode(NeutralModeValue.Coast);

        LimelightHelpers.setCameraPose_RobotSpace("limelight-main", 
        Units.inchesToMeters(11.75),    // Forward offset (meters)
        Units.inchesToMeters(0),    // Side offset (meters)
        Units.inchesToMeters(9.5),    // Height offset (meters)
            0.0,    // Roll (degrees)
            30,   // Pitch (degrees)
            0     // Yaw (degrees)
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

    }
    
    @Override
    public void periodic() {
        //Update Shooter Handshake
        SystemVariables.turretDistanceFromGoal = getDistanceFromShot();
        SystemVariables.turretAngleToGoal = getAngleToGoal();
        SystemVariables.turretZeroDirection = getTurretPose().getRotation();

        updatePoseWithLimelight();
        seedPoseFromOptions();

        robotPosePublisher.set(getCurrentPose());
        shootTargetPublisher.set(new Pose2d(getDynamicShootTarget(), Rotation2d.kZero));
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
        return getDynamicShootTarget().getDistance(getTurretPose().getTranslation());
    }

    public Rotation2d getAngleToGoal() {
        return absoluteAngleFromPose(getDynamicShootTarget(), getTurretPose().getTranslation());
    }

    private Translation2d getStaticShootTarget() {
        Translation2d target = DriverStation.getAlliance().get() == Alliance.Red ? FieldConstants.RED_GOAL : FieldConstants.BLUE_GOAL;
        
        if (DriverStation.getAlliance().get() == Alliance.Red && getCurrentPose().getX() < 11.9) {
            // 3 Main Targets: Goal, Outpost Side Feed, Depot Side Feed
            if (getCurrentPose().getY() < 4) {
                target = FieldConstants.RED_FEED_DEPOT_SIDE;
            } else {
                target = FieldConstants.RED_FEED_OUTPOST_SIDE;
            }
        } else if (DriverStation.getAlliance().get() == Alliance.Blue && getCurrentPose().getX() > 4.6) {
            if (getCurrentPose().getY() > 4) {
                target = FieldConstants.BLUE_FEED_DEPOT_SIDE;
            } else {
                target = FieldConstants.BLUE_FEED_OUTPOST_SIDE;
            }
        }
        return target;
    }

    private Translation2d getDynamicShootTarget() {
        Translation2d target = getStaticShootTarget();

        double currentVeloX = getState().Speeds.vxMetersPerSecond;
        double currentVeloY = getState().Speeds.vyMetersPerSecond;

        double rotationalToLinearVelo = getPigeon2().getAngularVelocityZWorld().getValueAsDouble() * (Math.PI / 180.0);
        rotationalToLinearVelo *= distanceFromPose(getTurretPose(), getCurrentPose());

        double poseAngle = absoluteAngleFromPose(getTurretPose(), getCurrentPose()).getRadians();
        
        currentVeloX += rotationalToLinearVelo * Math.cos(poseAngle);
        currentVeloY -= rotationalToLinearVelo * Math.sin(poseAngle);
        
        //Translational Adjustment
        target = target.minus(new Translation2d(currentVeloX * 1.3, currentVeloY * 1.3));
        return target;
    }

    ////////////////////////////////////////////////// Setters ///////////////////////////////////////////////////////////////
    
    public void setPose(Pose2d newPose) {
        resetPose(newPose);
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

    public void updatePoseWithLimelight() {
        boolean updateVision = true;
        int minTagCount = 1;

        if (DriverStation.isAutonomous()) {
            minTagCount = 2;
        }

        LimelightHelpers.SetRobotOrientation("limelight-main", getCurrentPose().getRotation().getDegrees(), 0, 0, 0, 0, 0);
        LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight-main");

        LimelightHelpers.PoseEstimate mt1 = LimelightHelpers.getBotPoseEstimate_wpiBlue("limelight-main");

        if (mt2 != null && Math.abs(getPigeon2().getAngularVelocityZWorld().getValueAsDouble()) < 720) {

            Rotation2d currentAngle = null;
            if (mt1 != null) {
                if (mt1.tagCount >= 2) {
                    currentAngle = mt1.pose.getRotation();
                }
            }

            if (currentAngle == null) {
                currentAngle = getCurrentPose().getRotation();
            }

        
            if (mt2.tagCount >= minTagCount) {
                updateVision = false;
            }
            if (updateVision) {

                setVisionMeasurementStdDevs(VecBuilder.fill(0.3, 0.3, 1)); // x and Y were 0.7
                addVisionMeasurement(
                        new Pose2d(mt2.pose.getTranslation(), currentAngle ),
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
