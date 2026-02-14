package org.firstinspires.ftc.teamcode;


import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.IMU;



import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

@Autonomous
public class TestSimpleAuto extends LinearOpMode {

    //private Limelight3A limelight;

    private ElapsedTime timer = new ElapsedTime();

    private DcMotor frontLeftDrive = null;  //  Used to control the left front drive wheel
    private DcMotor frontRightDrive = null;  //  Used to control the right front drive wheel
    private DcMotor backLeftDrive = null;  //  Used to control the left back drive wheel
    private DcMotor backRightDrive = null;  //  Used to control the right back drive wheel

    double turn = 0;
    private IMU imu;
    double currentX;
    double currentY;




    public class AprilTagss {

        Pose2d initialPose = new Pose2d(60, -12, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        Pose2d localizerPose = drive.localizer.getPose();
        double ticksPerRotation;


        private CRServo servoOne;
        private CRServo servoTwo;
        private Servo hoodServo;
        private Servo hoodServo2;
        private DcMotorEx shooter;


        public AprilTagss() {
            servoOne = hardwareMap.get(CRServo.class, "servoOne");
            servoTwo = hardwareMap.get(CRServo.class, "servoTwo");
            hoodServo = hardwareMap.get(Servo.class, "hoodServo");
            hoodServo2 = hardwareMap.get(Servo.class, "hoodServo2");

            shooter = hardwareMap.get(DcMotorEx.class, "shooter");
            shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            ticksPerRotation = shooter.getMotorType().getTicksPerRev();
        }


        public class faceTag implements Action {
            private boolean initialized = false;

            // actions are formatted via telemetry packets as below
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
              //  LLResult result = limelight.getLatestResult();

                final double TURN_GAIN = 0.05;   //  Turn Control "Gain".  e.g. Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)
                final double MAX_AUTO_TURN = 0.2;
                //powers on motor, if it is not on
                if (!initialized) {
                    initialized = true;
                }

                    double robotYaw = drive.localizer.getPose().heading.toDouble();
                  //  limelight.updateRobotOrientation(Math.toDegrees(-robotYaw));



                    Pose2d mt2BotPos = new Pose2d(currentX *72, currentY*72, robotYaw);
                    Pose2d pose = drive.localizer.getPose();
                    telemetry.addData("heading (drive)", Math.toDegrees(pose.heading.toDouble()));
                    packet.fieldOverlay().setStroke("#3F51B5");
                    Drawing.drawRobot(packet.fieldOverlay(), mt2BotPos);
                    FtcDashboard.getInstance().sendTelemetryPacket(packet);


                    /*if (result.isValid()) {
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

                                if (botpose_mt2 != null) {
                                double x = botpose_mt2.getPosition().x;
                                double y = botpose_mt2.getPosition().y;
                                double h = botpose_mt2.getOrientation().getYaw();
                                currentX = x;
                                currentY = y;
                                Pose2d poopose = new Pose2d(currentX, currentY, robotYaw);

                                telemetry.addData("MT2 Location:", poopose);
                            }
                        } else {
                            telemetry.addLine("No Tag");

                        }*/

                    drive.updatePoseEstimate();
                    telemetry.update();
                    return opModeIsActive();//true;
            }
        }



        public class rangedShoot implements Action {  // not working rn
            private boolean initialized = false;

            private int phase = 0;
            private int mphase = 0;
            private int tarPM = 0;
            private boolean servoing;
            private double currentRPM;
            private double targetRPM;
            private double ticksperrev;
            private double blabhblag;
            private ElapsedTime profileTimer = new ElapsedTime();
            private ElapsedTime timer2 = new ElapsedTime();

            // actions are formatted via telemetry packets as below
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {

                //powers on motor, if it is not on
                if (!initialized) {
                    initialized = true;
                    timer.reset();
                    shooter.setPower(0);
                    servoTwo.setPower(0);
                    servoOne.setPower(0);
                    hoodServo.setPosition(.11);
                    hoodServo2.setPosition(.58);
                    profileTimer.reset();
                    currentRPM = 0;
                    phase = 1;
                    blabhblag = 0;
                }
                ticksperrev = shooter.getVelocity() / 28 * 60;

                currentRPM = motion_profile(targetRPM / 3, targetRPM, profileTimer.seconds());
                shooter.setVelocity((currentRPM / 60) * 28);
                //timer.reset();
                if (timer.seconds() >4 && timer.seconds() <10) {
                    tarPM = 1;
                } else {
                    tarPM = 0;
                }
// WORK YOU FAT FUCKING CHUD
                if (phase == 1 && (ticksperrev < targetRPM + 15 && ticksperrev > targetRPM - 15) && blabhblag < 2) {
                    timer2.reset();
                    phase = 2;
                    blabhblag = blabhblag +1;
                } else if (phase == 2 && timer2.seconds() > .25) {
                    phase = 1;
                }




                switch (tarPM) {
                    case 0:
                        targetRPM = 0;
                        break;
                    case 1:
                        targetRPM = 2800;
                        break;
                }

                switch (phase) {
                    case 1:
                        servoTwo.setPower(0);
                        servoOne.setPower(0);
                        break;
                    case 2:
                        servoOne.setPower(-.25);
                        servoTwo.setPower(.25);
                        break;
                    default:
                        servoTwo.setPower(0);
                        servoOne.setPower(0);
                }



                packet.fieldOverlay().setStroke("#3F51B5");
                Drawing.drawRobot(packet.fieldOverlay(), localizerPose);
                FtcDashboard.getInstance().sendTelemetryPacket(packet);

                telemetry.addData("profileTimer", profileTimer.seconds());
                telemetry.addData("time2", timer2.seconds());
                telemetry.addData("time", timer.seconds());

                telemetry.update();
                drive.updatePoseEstimate();

                return opModeIsActive(); //true;
            }

        }


        double motion_profile(double maxAcceleration, double maxVelocity, double elapsed_time) {
            double acceleration_dt = maxVelocity / maxAcceleration;

            if (elapsed_time < acceleration_dt) return maxAcceleration * elapsed_time;

            return maxVelocity;
        }



        //turns these into actions to be used in actions.runblocking (question mark?)
        public Action faceTag() {
            return new TestSimpleAuto.AprilTagss.faceTag();
        }

        public Action rangedShoot() {
            return new TestSimpleAuto.AprilTagss.rangedShoot();
        }

    }


    @Override
    public void runOpMode() throws InterruptedException {



        boolean targetFound = false;    // Set to true when an AprilTag target is detected
        double turn = 0;        // Desired turning power/speed (-1 to +1)
       // limelight = hardwareMap.get(Limelight3A.class, "limelight");

        frontLeftDrive = hardwareMap.get(DcMotor.class, "leftFront");
        frontRightDrive = hardwareMap.get(DcMotor.class, "rightFront");
        backLeftDrive = hardwareMap.get(DcMotor.class, "leftBack");
        backRightDrive = hardwareMap.get(DcMotor.class, "rightBack");

        AprilTagss aprilTags = new AprilTagss();

      //  limelight.pipelineSwitch(0);
        //limelight.setPollRateHz(150);
       // limelight.start();


        TrajectoryActionBuilder poo = aprilTags.drive.actionBuilder(aprilTags.initialPose)
                .waitSeconds(1)
                .setTangent(180)
                .strafeTo(new Vector2d(0,-12))
                //.splineToConstantHeading(new Vector2d(-50, -43), (3 * Math.PI / 2));
               .splineToLinearHeading(new Pose2d(-24, -24 , Math.toRadians(230)), (Math.PI / 2)); // -55,-55


        Action trajectoryActionCloseOut = poo.endTrajectory().fresh()
                .waitSeconds(9.5)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(36, -12 , Math.toRadians(180)), (Math.PI / 2)) // -55,-55
               // .splineToConstantHeading(new Vector2d(36, -12), (3*Math.PI / 2))
                .build();



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
                new ParallelAction(
                        aprilTags.rangedShoot(),
                new SequentialAction(
                        poo.build(),
                        trajectoryActionCloseOut
                ))
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
//miles was here