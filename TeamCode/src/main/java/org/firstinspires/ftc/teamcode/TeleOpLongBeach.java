package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.mechanisms.StarterBotShoot2;

import java.util.List;

@TeleOp
public class TeleOpLongBeach extends LinearOpMode {

    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime profileTimer = new ElapsedTime();

    StarterBotShoot2 shooter = new StarterBotShoot2();
    Limelight3A limelight;
    private DcMotorEx motor;
    private DcMotorEx intake;

    private CRServo servoOne;
    private CRServo servoTwo;
    private Servo Hood;
    private Servo hood2;
    boolean shootingSingle = false;

    private int hoodAngle = 0;
    private double ticksperrev;
    private double targetRPM = 1000;
    private int phase = 0;
    private double triggerPower;
    private double currentRPM;
    private double distance;
    private double LLrpm;

    @Override
    public void runOpMode() throws InterruptedException {

        motor = hardwareMap.get(DcMotorEx.class, "shooter");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        Hood = hardwareMap.get(Servo.class, "hoodServo");
        hood2 = hardwareMap.get(Servo.class, "hoodServo2");
        servoOne = hardwareMap.get(CRServo.class, "servoOne");
        servoTwo = hardwareMap.get(CRServo.class, "servoTwo");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(60, 0, Math.toRadians(180)));
        shooter.init(hardwareMap);

        limelight.start();

        waitForStart();


        while (opModeIsActive()) {
            LLResult llresult = limelight.getLatestResult();

            if (llresult.isValid()) {
                List<LLResultTypes.FiducialResult> fiducialResults = llresult.getFiducialResults();

                for (LLResultTypes.FiducialResult fr : fiducialResults) {
                    if (fr.getFiducialId() == 20 || fr.getFiducialId() == 24) {
                        distance = getDistance(llresult.getTa());
                        if (distance > 210){
                            hoodAngle = 3;
                        } else if (distance < 210 && distance > 180){
                            hoodAngle = 2;
                        } else if (distance < 180 && distance > 90){
                            hoodAngle = 1;
                        } else if (distance < 90){
                            hoodAngle = 0;
                        }

                    }
                }
            } else {
                telemetry.addData("No Target", "Found");
            }

                drive.setDrivePowers(new PoseVelocity2d(
                        new Vector2d(
                                -gamepad1.left_stick_y * 0.85,
                                -gamepad1.left_stick_x * 0.85
                        ),
                        -gamepad1.right_stick_x * 1.25
                ));

            ticksperrev = motor.getVelocity() / 28 * 60;

            if (gamepad1.b && !shootingSingle) {
                profileTimer.reset();
                shootingSingle = true;
                timer.reset();
            }

            triggerPower = gamepad1.right_trigger;
            intake.setPower(-triggerPower);

            if (gamepad1.a){
                servoOne.setPower(-1);
            } else{
                servoOne.setPower(0);
            }


            /*if (gamepad1.rightBumperWasReleased()) {
                hoodAngle = hoodAngle + 1;
            } else if (hoodAngle > 3) {
                hoodAngle = 0;
            } else if (gamepad1.leftBumperWasReleased()) {
                hoodAngle = hoodAngle - 1;
            } else if (hoodAngle < 0) {
                hoodAngle = 3;
            }*/

            switch (hoodAngle) { //TODO make it so that it will change smoothly, instead of these presets
                case 0:
                   // targetRPM = 2500;
                    hood2.setPosition(.68);//.68

                    Hood.setPosition(0);
                    break;
                case 1: // ~100cm
                   // targetRPM = 2800;
                    hood2.setPosition(.58); //.58
                    Hood.setPosition(.110);
                    break;
                case 2: // ~200cm
                  //  targetRPM = 3100;
                    hood2.setPosition(.575); //.575
                    Hood.setPosition(.120);
                    break;
                case 3:
                    //targetRPM = 4250;
                    hood2.setPosition(.55); //.565
                    Hood.setPosition(.135);
                    break;
            }

            targetRPM = llRPM(distance);





            switch (phase) {
                case 1:
                    servoTwo.setPower(0);
                  //  servoOne.setPower(0);
                    break;
                case 2:
                    servoTwo.setPower(1); // outer servo
                  //  servoOne.setPower(-.45);
                    break;
                default:
                    servoTwo.setPower(0);
                   // servoOne.setPower(0);
            }

            if (shootingSingle) {

                currentRPM = motion_profile(targetRPM / 3, targetRPM, profileTimer.seconds());
                motor.setVelocity((currentRPM / 60) * 28);

                if (phase == 1 && (ticksperrev < targetRPM + 15 && ticksperrev > targetRPM - 15)) {
                    timer.reset();
                    phase = 2;
                } else if (phase == 2 && timer.seconds() > .25) {
                    shootingSingle = false;
                }


            } else {
                phase = 1;
                motor.setVelocity(0);
            }


            telemetry.addData("hood angle phase", hoodAngle);
            telemetry.addData("Distance (cm) w/ tA", distance);
            telemetry.addData("ticks/rev", ticksperrev);
            telemetry.addData("ty", llresult.getTy());
            telemetry.addData("tA", llresult.getTa());
            drive.updatePoseEstimate();
            telemetry.update();

        }
    }

    double getDistance(double tA) {
        double scale = 37010.51;
        double A = (scale/tA);
        double B = (1/2.029652);
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

        return Math.round(LLrpm);
    }

}
