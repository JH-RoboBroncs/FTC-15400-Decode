package org.firstinspires.ftc.teamcode;
//Motors
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.List;

//Name and Position
@TeleOp(name = "Drive + Shooter FC(Full Power)", group = "Linear OpMode")
public class DriveShooterFC extends LinearOpMode {
    Limelight3A limelight;
    private double LLHeight = 27.2;
    private double LLAngle = 0;
    private double ATHeight = 76;
    private double distance;

    // Drive motors
    private DcMotor frontLeftDrive, backLeftDrive, frontRightDrive, backRightDrive;

    // Shooter
    private CRServo shooter;
    private Servo toe;
    private boolean toeMoving = false;
    private ElapsedTime toeTimer = new ElapsedTime();

    // IMU
    private IMU imu;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        // Motor Name class
        frontLeftDrive = hardwareMap.get(DcMotor.class, "leftFront");
        backLeftDrive = hardwareMap.get(DcMotor.class, "leftBack");
        frontRightDrive = hardwareMap.get(DcMotor.class, "rightFront");
        backRightDrive = hardwareMap.get(DcMotor.class, "rightBack");
//shooter name and servo shoot
        shooter = hardwareMap.get(CRServo.class, "Shooter");
        toe = hardwareMap.get(Servo.class, "toe");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        imu = hardwareMap.get(IMU.class, "imu");

//motor derection
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

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

                        telemetry.addData("Distance (cm) w/ tA", distance);
                        telemetry.addData("ty", llresult.getTy());
                        telemetry.addData("tA", llresult.getTa());
                      //  telemetry.addData("botpose", pose.toString());
                    }
                }
            } else {
                telemetry.addData("No Target", "Found");
            }


            double y = -gamepad1.left_stick_y; // forward/back
            double x = -gamepad1.left_stick_x; // strafe
            double rx = gamepad1.right_stick_x; // rotation


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
    
            // Start toe motion
            if (gamepad1.left_bumper && !toeMoving) {
                toe.setPosition(0.0);
                toeTimer.reset();
                toeMoving = true;
            }

            // After 1 second, return toe
            if (toeMoving && toeTimer.seconds() >= 1.0) {
                toe.setPosition(0.5);
                toeMoving = false;
            }

            // Manual reset
            if (gamepad1.y) {
                toe.setPosition(0.5);
                toeMoving = false;
            }
        if (gamepad1.right_bumper) {
                shooter.setPower(-3.9);   // shoot
            } 
            else if (gamepad1.x) {
                shooter.setPower(.5);
            }
            else {
                shooter.setPower(0);
            }
            if (toeMoving && toeTimer.seconds() >= 1.0) {
             toe.setPosition(0.5);
             toeMoving = false;
            }
            // Reset heading
            if (gamepad1.options) imu.resetYaw();

            // Telemetry
            telemetry.addData("Heading (deg)", Math.toDegrees(heading));
            telemetry.addData("Toe Moving", toeMoving);
            telemetry.addData("Run Time", runtime.toString());
            telemetry.update();
        }
    }

    double getDistanceOLDDONOTUSE(double ty) {
        double heightDifference = ATHeight - LLHeight;
        double angleToTarget = LLAngle + ty;

        return heightDifference/Math.tan(Math.toRadians(angleToTarget));
    }


    double getDistance(double tA) {
        double scale = 36930.19;
        double A = (scale/tA);
        double B = (1/2.029076);
        double distance = (Math.pow(A, B));

        return distance;
    }

}
