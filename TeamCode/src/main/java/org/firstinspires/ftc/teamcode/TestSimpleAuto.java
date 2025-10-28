package org.firstinspires.ftc.teamcode;


import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.IMU;



import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Autonomous
public class TestSimpleAuto extends LinearOpMode {



    private Limelight3A limelight;

    private ElapsedTime timer = new ElapsedTime();

    private DcMotor frontLeftDrive = null;  //  Used to control the left front drive wheel
    private DcMotor frontRightDrive = null;  //  Used to control the right front drive wheel
    private DcMotor backLeftDrive = null;  //  Used to control the left back drive wheel
    private DcMotor backRightDrive = null;  //  Used to control the right back drive wheel

    double turn = 0;
    private IMU imu;


    public class AprilTagss {


        public class faceTag implements Action {
            private boolean initialized = false;

            // actions are formatted via telemetry packets as below
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                LLResult result = limelight.getLatestResult();

                Pose2d initialPose = new Pose2d(0, 0, 0);
                MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);


                final double TURN_GAIN = 0.05;   //  Turn Control "Gain".  e.g. Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)
                final double MAX_AUTO_TURN = 0.2;
                //powers on motor, if it is not on
                if (!initialized) {
                    initialized = true;
                }


                while (opModeIsActive()) {

                    drive.updatePoseEstimate();
                    double robotYaw = drive.localizer.getPose().heading.toDouble();
                    limelight.updateRobotOrientation(robotYaw);

                    if (result.isValid()) {
                        List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                        for (LLResultTypes.FiducialResult fr : fiducialResults) {
                            if (fr.getFiducialId() == 20 && Math.abs(result.getTx()) > 0.25) {
                                double headingError = result.getTx(); //desiredTag.ftcPose.bearing;
                                turn = -Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                                moveRobot(0, 0, turn);
                                telemetry.addData("Tag valid", fr.getFiducialId());
                            }

                        } if (result.isValid()) {
                            Pose3D botpose_mt2 = result.getBotpose_MT2();
                            Pose3D botpose_mt1 = result.getBotpose();
                                if (botpose_mt2 != null) {
                                //double x = botpose_mt2.getPosition().x;
                                //double y = botpose_mt2.getPosition().y;
                                double x = botpose_mt1.getPosition().x;
                                double y = botpose_mt1.getPosition().y;
                                double heading = botpose_mt1.getOrientation().getYaw();
                                telemetry.addData("MT1 Location:", "(" + x + ", " + y + ")");
                                Pose2d mt2BotPos = new Pose2d(x *72, y*72, heading);
                                telemetry.addData("Pose2d", mt2BotPos);
                            }
                        } else {
                            telemetry.addLine("No Tag");
                            moveRobot(0, 0, 0);
                        }
                        telemetry.update();
                    }
                    return true;
                }
                return false;
            }
        }


        public class getPosition implements Action {
            private boolean initialized = false;

            // actions are formatted via telemetry packets as below
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                LLResult result = limelight.getLatestResult();
                Pose2d initialPose = new Pose2d(0, 0, 0);
                MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

                //powers on motor, if it is not on
                if (!initialized) {
                    initialized = true;
                }


                while (opModeIsActive()) {
                    drive.updatePoseEstimate();

                    // First, tell Limelight which way your robot is facing
                    double robotYaw = drive.localizer.getPose().heading.toDouble();
                    limelight.updateRobotOrientation(robotYaw);
                    if (result != null && result.isValid()) {
                        Pose3D botpose_mt2 = result.getBotpose_MT2();
                        if (botpose_mt2 != null) {
                            double x = botpose_mt2.getPosition().x;
                            double y = botpose_mt2.getPosition().y;
                            telemetry.addData("MT2 Location:", "(" + x + ", " + y + ")");
                        }
                        telemetry.update();
                    }
                    return true;
                }
                return false;
            }
        }




        //turns these into actions to be used in actions.runblocking (question mark?)
        public Action faceTag() {
            return new TestSimpleAuto.AprilTagss.faceTag();
        }

        public Action getPos() {
            return new TestSimpleAuto.AprilTagss.getPosition();
        }

    }


    @Override
    public void runOpMode() throws InterruptedException {



        boolean targetFound = false;    // Set to true when an AprilTag target is detected
        double turn = 0;        // Desired turning power/speed (-1 to +1)
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        frontLeftDrive = hardwareMap.get(DcMotor.class, "leftFront");
        frontRightDrive = hardwareMap.get(DcMotor.class, "rightFront");
        backLeftDrive = hardwareMap.get(DcMotor.class, "leftBack");
        backRightDrive = hardwareMap.get(DcMotor.class, "rightBack");

        AprilTagss aprilTags = new AprilTagss();

        limelight.pipelineSwitch(0);
        //limelight.setPollRateHz(150);
        limelight.start();

        waitForStart();

        /*while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();


            // Step through the list of detected tags and look for a matching tag
            if (result.isValid()) {
                List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                for (LLResultTypes.FiducialResult fr : fiducialResults) {
                    if (fr.getFiducialId() == 21) {
                        telemetry.addData("Pattern", "GPP");
                    } else if (fr.getFiducialId() == 22) {
                        telemetry.addData("Pattern", "PGP");
                    } else if (fr.getFiducialId() == 23) {
                        telemetry.addData("Pattern", "PPG");
                    }

                }
            }
            telemetry.update();
        }*/



        Actions.runBlocking(
                // new SequentialAction(
                // )
                new ParallelAction(
                        aprilTags.faceTag()
                       // aprilTags.getPos()
                )
        );


    }

    public void moveRobot(double x, double y, double yaw) {
        // Calculate wheel powers.
        double frontLeftPower = -x - y - yaw;
        double frontRightPower = x - y + yaw;
        double backLeftPower = -x + y - yaw;
        double backRightPower = x + y + yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        // Send powers to the wheels.
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }

}
