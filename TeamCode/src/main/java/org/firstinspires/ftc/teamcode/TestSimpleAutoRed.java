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
public class TestSimpleAutoRed extends LinearOpMode {

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

        Pose2d initialPose = new Pose2d(60, 12, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        Pose2d localizerPose = drive.localizer.getPose();
        double ticksPerRotation;


        private CRServo servoOne;
        private CRServo servoTwo;
        private Servo hoodServo;
        private DcMotorEx shooter;


        public AprilTagss() {
            servoOne = hardwareMap.get(CRServo.class, "servoOne");
            servoTwo = hardwareMap.get(CRServo.class, "servoTwo");
            hoodServo = hardwareMap.get(Servo.class, "hoodServo");

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
            private boolean servoing;


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
                    hoodServo.setPosition(.125);
                }

                if (timer.seconds() > 5.6 && timer.seconds() < 7.2){
                    hoodServo.setPosition(0.125);
                } else if (timer.seconds() > 7.25 && timer.seconds() < 9) {
                    hoodServo.setPosition(0.13);
                } else {
                    hoodServo.setPosition(0.125);
                }



                //timer.reset();
                if (timer.seconds() > 7.2 && timer.seconds() < 9.65) { //3rd ball
                    mphase = 2;
                } else if (timer.seconds() > 5.6 && timer.seconds() < 7.15) { //2nd
                    mphase = 4;
                } else if (timer.seconds() > 4 && timer.seconds() < 5.6) { //1st
                    mphase = 3;
                } else {
                    mphase = 1;
                }

                if ((timer.seconds() > 5.25 && timer.seconds() < 5.5) || (timer.seconds() > 7 && timer.seconds() < 7.35) || (timer.seconds() > 8.75 && timer.seconds() < 9.5)) {
                    phase = 2; // load
                    servoing = true;
                } else if (timer.seconds()< 4) {
                    phase = 4;
                } else {
                    phase = 1; // idle
                    servoing = false;
                }




                switch (phase){
                    case 1: // waiting
                        servoTwo.setPower(0);
                        servoOne.setPower(0);
                        break;
                    case 2: //loading
                        servoTwo.setPower(.2); //.65
                        servoOne.setPower(-0.2);
                        break;
                    case 3: //loading
                        servoTwo.setPower(.5);
                        servoOne.setPower(-.5);
                        break;
                    case 4:
                        servoOne.setPower(.85);
                        servoTwo.setPower(-.85);
                        break;
                    default:
                        servoTwo.setPower(0);
                        servoOne.setPower(0);
                }

                switch (mphase){
                    case 1: // waiting
                        shooter.setVelocity(0);
                        break;
                    case 2: //3rd ball
                        shooter.setVelocity(ticksPerRotation/(1.1766/.65)); //625
                        break;
                    case 3: //1st
                        shooter.setVelocity(ticksPerRotation/(1.1766/.55)); //525
                        break;
                    case 4: //2nd
                        shooter.setVelocity(ticksPerRotation/(1.1766/.65)); //625
                        break;
                    default:
                        //motor.setPower(0);
                }


                packet.fieldOverlay().setStroke("#3F51B5");
                Drawing.drawRobot(packet.fieldOverlay(), localizerPose);
                FtcDashboard.getInstance().sendTelemetryPacket(packet);

                telemetry.addData("loading", servoing);
                telemetry.addData("time", timer.seconds());

                telemetry.update();
                drive.updatePoseEstimate();

                return opModeIsActive(); //true;
            }

        }





        //turns these into actions to be used in actions.runblocking (question mark?)
        public Action faceTag() {
            return new TestSimpleAutoRed.AprilTagss.faceTag();
        }

        public Action rangedShoot() {
            return new TestSimpleAutoRed.AprilTagss.rangedShoot();
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
                //.splineToConstantHeading(new Vector2d(-50, -43), (3 * Math.PI / 2));
                .lineToYLinearHeading(0,Math.toRadians(180))
                .waitSeconds(.5)
                .splineToLinearHeading(new Pose2d(-24, 24 , Math.toRadians(140)), ( Math.PI / 2)); // -55,-55


        Action trajectoryActionCloseOut = poo.endTrajectory().fresh()
                .waitSeconds(9.5)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(36, 12 , Math.toRadians(180)), (Math.PI / 2)) // -55,-55
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