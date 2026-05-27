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
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous
public class LongBeachAuto extends LinearOpMode {

    private ElapsedTime timer = new ElapsedTime();
    private TelemetryPacket packet = new TelemetryPacket();
    Pose2d initialPose = new Pose2d(60, 12, Math.toRadians(180));
    private DcMotorEx intake;
    private double ng = 1;
    private double shotAngle;
    private double intakeAngle;

    public class AprilTagss {

        double currentRPM;
        double targetRPM = 3100;

        private ElapsedTime profileTimer = new ElapsedTime();
        private ElapsedTime testy = new ElapsedTime();

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
        }

        public class rev implements Action {  // not working rn
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                    timer.reset();
                    profileTimer.reset();
                    shooter.setPower(0);
                    hoodServo.setPosition(.120);
                    hoodServo2.setPosition(.575);
                }

                currentRPM = motion_profile(targetRPM / 3, targetRPM, profileTimer.seconds());
                shooter.setVelocity((currentRPM / 60) * 28);

                return opModeIsActive(); //true;
            }
        }

        public class shooting implements Action {  // not working rn
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                    testy.reset();
                    servoOne.setPower(0);
                    servoTwo.setPower(0);
                }

               if (testy.seconds() < 4.5 ){
                   servoOne.setPower(-1);
                   servoTwo.setPower(1);
                   return true;
               } else {
                   servoOne.setPower(0);
                   servoTwo.setPower(0);
                   return false;
               }
            }
        }

        double motion_profile(double maxAcceleration, double maxVelocity, double elapsed_time) {
            double acceleration_dt = maxVelocity / maxAcceleration;

            if (elapsed_time < acceleration_dt) return maxAcceleration * elapsed_time;

            return maxVelocity;
        }

        public Action rev() {
            return new LongBeachAuto.AprilTagss.rev();
        }
        public Action shooting() {
            return new LongBeachAuto.AprilTagss.shooting();
        }

        public Action intake(double speed) {
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket packet) {
                    intake.setPower(speed);
                    return false;
                }
            };
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {

        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        Pose2d localizerPose = drive.localizer.getPose();
        intake = hardwareMap.get(DcMotorEx.class, "intake");

        AprilTagss aprilTags = new AprilTagss();

        if (gamepad1.rightBumperWasReleased()) { // RED SIDE
            ng = 1;
            intakeAngle = Math.toRadians(270);
            shotAngle = Math.toRadians(45);
        } else if (gamepad1.leftBumperWasReleased()) { //BLUE SIDE
            ng = -1;
            intakeAngle = Math.toRadians(90);
            shotAngle = Math.toRadians(135);
        }

        TrajectoryActionBuilder startToShoot = drive.actionBuilder(initialPose)
                .setTangent(180)
                .strafeTo(new Vector2d(0,-12))
                .strafeToLinearHeading(new Vector2d(-24, (ng*24)), shotAngle);

        TrajectoryActionBuilder intakeOne = startToShoot.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(-9.5, (ng*24)), intakeAngle)
                .stopAndAdd(aprilTags.intake(1))
                .strafeTo(new Vector2d(-9.5,(ng*50)))
                .strafeTo(new Vector2d(-9.5,(ng*23)))
                .stopAndAdd(aprilTags.intake(0))
                .strafeToLinearHeading(new Vector2d(-24, (ng*24)), shotAngle);

        TrajectoryActionBuilder intakeTwo = intakeOne.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(13,(ng*24)), intakeAngle)
                .stopAndAdd(aprilTags.intake(1))
                .strafeTo(new Vector2d(13,(ng*50)))
                .strafeTo(new Vector2d(13,(ng*23)))
                .stopAndAdd(aprilTags.intake(0))
                .strafeToLinearHeading(new Vector2d(-24, (ng*24)), shotAngle);



        waitForStart();

        Actions.runBlocking(
                new ParallelAction(
                        aprilTags.rev(),
                new SequentialAction(
                        startToShoot.build(),
                        aprilTags.shooting(),
                        intakeOne.build(),
                        aprilTags.shooting(),
                        intakeTwo.build(),
                        aprilTags.shooting()
                ))
        );

        while (opModeIsActive()){
            drive.updatePoseEstimate();
            packet.fieldOverlay().setStroke("#3F51B5");
            Drawing.drawRobot(packet.fieldOverlay(), drive.localizer.getPose());
            FtcDashboard.getInstance().sendTelemetryPacket(packet);
            telemetry.addData("time", timer.seconds());
            telemetry.update();
        }
    }



}