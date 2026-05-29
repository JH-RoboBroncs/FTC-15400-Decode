package org.firstinspires.ftc.teamcode;
//Motors
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.List;

//Name and Position
@TeleOp(name = "RoachBotLongBeach", group = "Linear OpMode")
public class DriveShooterFC extends LinearOpMode {
    Limelight3A limelight;
    private double distance;
    boolean shootingSingle = false;
    private double currentRPM;
    private double targetRPM = 2800;


    // Drive motors
    private DcMotor frontLeftDrive, backLeftDrive, backRightDrive;
    private DcMotorEx shooter;
    private CRServo frontRightDrive;
    private Servo hood2, Hood;

    // Shooter
    private Servo toe;
    private boolean toeMoving = false;
    private ElapsedTime toeTimer = new ElapsedTime();
    private ElapsedTime profileTimer = new ElapsedTime();
    private ElapsedTime timer = new ElapsedTime();
    private double ticksperrev;

    // IMU
    private IMU imu;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        // Motor Name class
        frontLeftDrive = hardwareMap.get(DcMotor.class, "leftFront");
        backLeftDrive = hardwareMap.get(DcMotor.class, "leftBack");
        frontRightDrive = hardwareMap.get(CRServo.class, "rightFront");
        backRightDrive = hardwareMap.get(DcMotor.class, "rightBack");
//shooter name and servo shoot
        shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        Hood = hardwareMap.get(Servo.class, "hoodServo");
        hood2 = hardwareMap.get(Servo.class, "hoodServo2");
        toe = hardwareMap.get(Servo.class, "toe");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        imu = hardwareMap.get(IMU.class, "imu");

//motor derection
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        shooter.setDirection(DcMotor.Direction.REVERSE);

   //shooter and servo name and derection + Servo movement
        toe.setDirection(Servo.Direction.REVERSE);
        toe.scaleRange(0.0, 0.8);
        toe.setPosition(0.5); // start at neutral

    //Fuildcentric
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD, // Logo forward
                RevHubOrientationOnRobot.UsbFacingDirection.UP       // USB up
        );

        IMU.Parameters parameters = new IMU.Parameters(orientationOnRobot);
        imu.initialize(parameters);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        limelight.start();

        waitForStart();



        runtime.reset();
        imu.resetYaw();
        
   
        while (opModeIsActive()){
            LLResult llresult = limelight.getLatestResult();

            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
            double heading = orientation.getYaw(AngleUnit.RADIANS);
            limelight.updateRobotOrientation(heading);


            if (llresult.isValid()) {
                List<LLResultTypes.FiducialResult> fiducialResults = llresult.getFiducialResults();
               // Pose3D pose = llresult.getBotpose_MT2();
                for (LLResultTypes.FiducialResult fr : fiducialResults) {
                    if (fr.getFiducialId() == 20 || fr.getFiducialId() == 24) {
                        distance = getDistance(llresult.getTa());

                        targetRPM = llRPM(distance);

                        telemetry.addData("Distance (cm) w/ tA", distance);
                        telemetry.addData("ty", llresult.getTy());
                        telemetry.addData("tA", llresult.getTa());
                      //  telemetry.addData("botpose", pose.toString());
                    }
                }
            } else {
                telemetry.addData("No Target", "Found");
            }

            if (gamepad1.dpad_up) {
                hood2.setPosition(.68); //.58
                Hood.setPosition(0);
            } if (gamepad1.dpad_down){
                hood2.setPosition(.595); //.58
                Hood.setPosition(.085);
            }


            double y = -gamepad1.left_stick_y; // forward/back
            double x = -gamepad1.left_stick_x; // strafe
            double rx = gamepad1.right_stick_x / 3; // rotation


            double rotX = x * Math.cos(heading) - y * Math.sin(heading);
            double rotY = x * Math.sin(heading) + y * Math.cos(heading);

  
            double frontLeftPower = rotY + rotX + rx;
            double frontRightPower = rotY - rotX - rx;
            double backLeftPower = rotY - rotX + rx;
            double backRightPower = rotY + rotX - rx;

       
            double max = Math.max(
                    Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))
            );
            if (max > 1.0) {
                frontLeftPower /= max;
                frontRightPower /= max;
                backLeftPower /= max;
                backRightPower /= max;
            }

   //Motor powers
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);
//rest of controls


            if (gamepad1.b && !shootingSingle) {
                profileTimer.reset();
                shootingSingle = true;
                timer.reset();
            }

            if (gamepad1.x && !shootingSingle) {
                shooter.setPower(-.5);
            }

            ticksperrev = shooter.getVelocity() / 28 * 60;

            if (shootingSingle) {

                currentRPM = motion_profile(targetRPM / 3, targetRPM, profileTimer.seconds());
                shooter.setVelocity((currentRPM / 60) * 28);

                if (!toeMoving && (ticksperrev < targetRPM + 15 && ticksperrev > targetRPM - 15)) {
                    timer.reset();
                    toe.setPosition(0);
                    toeMoving = true;
                } else if (toeMoving && timer.seconds() > .25) {
                    shootingSingle = false;
                    toe.setPosition(0.55);
                    toeMoving = false;
                }


            } else {
                toe.setPosition(0.55);
                shooter.setVelocity(0);
            }

            // Reset heading
            if (gamepad1.options) imu.resetYaw();

            // Telemetry
            telemetry.addData("Heading (deg)", Math.toDegrees(heading));
            telemetry.addData("Toe Moving", toeMoving);
            telemetry.addData("Run Time", runtime.toString());
            telemetry.addData("shooter velocity", shooter.getVelocity());
            telemetry.addData("targetRPM", targetRPM);
            telemetry.update();
        }
    }


    double getDistance(double tA) {
        double scale = 36930.19;
        double A = (scale/tA);
        double B = (1/2.029076);
        double distance = (Math.pow(A, B));

        return distance;
    }

    double motion_profile(double maxAcceleration, double maxVelocity, double elapsed_time) {
        double acceleration_dt = maxVelocity / maxAcceleration;

        if (elapsed_time < acceleration_dt) return maxAcceleration * elapsed_time;

        return maxVelocity;
    }

    double llRPM(double distanceGiven) { //testing stuff
        double LLrpm = 1343.679 + 836.8928 * Math.exp(0.00410771*distanceGiven);

        return Math.round(LLrpm) * 1.5;
    }

}
