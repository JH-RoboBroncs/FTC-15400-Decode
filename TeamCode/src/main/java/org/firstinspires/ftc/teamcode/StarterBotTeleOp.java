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

import org.firstinspires.ftc.teamcode.mechanisms.StarterBotShoot;

@TeleOp
public class StarterBotTeleOp extends LinearOpMode {

    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime profileTimer = new ElapsedTime();

    StarterBotShoot shooter = new StarterBotShoot();
    private DcMotorEx motor;
    private CRServo servoOne;
    private CRServo servoTwo;
    private Servo Hood;
    private Servo hood2;
    boolean shootingSingle = false;
    boolean sensToggle = true;


    private int hoodAngle = 0;
    private double ticksperrev;
    private double targetRPM = 1000;
    private int phase = 0;


    private double output_velocity;
    private double currentRPM;


    double currentX;
    double currentY;
    double ServoAngle2 = 1;

    @Override
    public void runOpMode() throws InterruptedException {
        //limelight = hardwareMap.get(Limelight3A.class, "limelight");
        // limelight.start();
        motor = hardwareMap.get(DcMotorEx.class, "shooter");
        Hood = hardwareMap.get(Servo.class, "hoodServo");
        hood2 = hardwareMap.get(Servo.class, "hoodServo2");
        servoOne = hardwareMap.get(CRServo.class, "servoOne");
        servoTwo = hardwareMap.get(CRServo.class, "servoTwo");


        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(60, 0, Math.toRadians(180)));
        shooter.init(hardwareMap);


        waitForStart();


        while (opModeIsActive()) {

            if (sensToggle) {
                drive.setDrivePowers(new PoseVelocity2d(
                        new Vector2d(
                                -gamepad1.left_stick_y * 0.85,
                                -gamepad1.left_stick_x * 0.85
                        ),
                        -gamepad1.right_stick_x * 1.25
                ));
            } else {
                drive.setDrivePowers(new PoseVelocity2d(
                        new Vector2d(
                                -gamepad1.left_stick_y * 0.325,
                                -gamepad1.left_stick_x * 0.325
                        ),
                        -gamepad1.right_stick_x * .25
                ));
            }


            ticksperrev = motor.getVelocity() / 28 * 60;


            if (gamepad2.dpadUpWasReleased()) {
                targetRPM = targetRPM + 250;
            } else if (gamepad2.dpadDownWasReleased()) {
                targetRPM = targetRPM - 250;
            }

            if (gamepad2.b && !shootingSingle) {
                profileTimer.reset();
                shootingSingle = true;
                timer.reset();
            }
           /* if (gamepad2.dpadRightWasReleased()) {
                ServoAngle2 = ServoAngle2 + .005;
            }
            if (gamepad2.dpadLeftWasReleased()) {
                ServoAngle2 = ServoAngle2 - .005;
            }*/


        if (gamepad2.rightBumperWasReleased()) {
            hoodAngle = hoodAngle + 1;
        } else if (hoodAngle > 3) {
            hoodAngle = 0;
        } else if (gamepad2.leftBumperWasReleased()) {
            hoodAngle = hoodAngle - 1;
        } else if (hoodAngle < 0) {
            hoodAngle = 3;
        }

        switch (hoodAngle) { // start from .75, go down from there
            case 0:
                targetRPM = 2500;
                hood2.setPosition(.68);//.68

                Hood.setPosition(0);
                break;
            case 1:
                targetRPM = 2800;
                hood2.setPosition(.58); //.58
                Hood.setPosition(.110);
                break;
            case 2:
                targetRPM = 3100;
                hood2.setPosition(.575); //.575
                Hood.setPosition(.120);
                break;
            case 3:
                targetRPM = 4250;
                hood2.setPosition(.55); //.565
                Hood.setPosition(.135);
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



        drive.updatePoseEstimate();
        telemetry.update();

    }
}





    double motion_profile(double maxAcceleration, double maxVelocity, double elapsed_time) {
        double acceleration_dt = maxVelocity / maxAcceleration;

        if (elapsed_time < acceleration_dt) return maxAcceleration * elapsed_time;

        return maxVelocity;
    }

}
