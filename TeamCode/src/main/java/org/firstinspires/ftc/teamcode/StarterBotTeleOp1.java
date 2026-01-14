package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.mechanisms.StarterBotShoot;

@TeleOp
public class StarterBotTeleOp1 extends LinearOpMode {

    private ElapsedTime timer = new ElapsedTime();
    private Limelight3A limelight;

    StarterBotShoot shooter = new StarterBotShoot();
    private DcMotor motor;
    private Servo Hood;
    boolean shooting = false;
    boolean shootingSingle = false;
    boolean sensToggle = true;
    private int hoodAngle = 0;
    private double ticksperrev;

    double currentX;
    double currentY;

    @Override
    public void runOpMode() throws InterruptedException {
        //limelight = hardwareMap.get(Limelight3A.class, "limelight");
       // limelight.start();
        motor = hardwareMap.get(DcMotorEx.class, "shooter");
        Hood = hardwareMap.get(Servo.class, "hoodServo");


        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(60, 0, Math.toRadians(180)));
        shooter.init(hardwareMap);


        waitForStart();

        ticksperrev = motor.getCurrentPosition();

        while (opModeIsActive()) {
            if (sensToggle) {
                drive.setDrivePowers(new PoseVelocity2d(
                        new Vector2d(
                                -gamepad1.left_stick_y * 0.85,
                                -gamepad1.left_stick_x * 0.85
                        ),
                        -gamepad1.right_stick_x * 1.5
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
           /* if (gamepad2.a){
                shooter.shoot(.45);
            } else {
                shooter.shoot(0);
            }

            if (gamepad2.b) {
                shooter.load(.55);
                } else {
                shooter.load(0);
            }*/



            if (gamepad2.x && !shooting) {
                shooting = true;
                timer.reset();
            }

            if (gamepad2.b && !shootingSingle) {
                shootingSingle = true;
                timer.reset();
            }




            if(gamepad2.rightBumperWasReleased()) {
                hoodAngle = hoodAngle + 1;
            } else if (hoodAngle > 3) {
                hoodAngle = 0;
            } else if(gamepad2.leftBumperWasReleased()) {
                hoodAngle = hoodAngle - 1;
            } else if (hoodAngle < 0) {
                hoodAngle = 3;
            }

            switch (hoodAngle){
                case 0:
                    Hood.setPosition(0);
                    break;
                case 1:
                    Hood.setPosition(.115);
                    break;
                case 2:
                    Hood.setPosition(.125);
                    break;
                /*case 3:
                    Hood.setPosition(.13);
                    break;*/
                case 3: //4
                    Hood.setPosition(.145);
                    break;

                //default:
                // Hood.setPosition(0);
            }



            if (shootingSingle) {
                shooter.singleShoot(timer, hoodAngle);

                // Stop after full cycle (adjust time as needed)
                if (timer.seconds() > 2.25) {   // <-- duration of full cycle
                    shootingSingle = false;

                }
            } else if (shooting) {
                shooter.shoot2(timer);

                // Stop after full cycle (adjust time as needed)
                if (timer.seconds() > 5) {   // <-- duration of full cycle
                    shooting = false;

                }
            } else {
                // shooter.shoot(-.05);
                shooter.antiload(.15);
            }


          /*  if (gamepad1.yWasReleased() && sensToggle) {
                sensToggle = false;
            } else if (gamepad1.yWasReleased() && !sensToggle) {
                sensToggle = true;
            } */





            telemetry.addData("anglecase", hoodAngle);
            telemetry.addData("sensitivity", sensToggle);
            telemetry.addData("time", timer);
            telemetry.addData("motorspeed", motor.getPower());
            telemetry.addData("servopos", Hood.getPosition());
            telemetry.addData("ticks/rev", ticksperrev);
           // telemetry.addData("rpm", ticks/60 )


                    /*if (result.isValid()) {
                        List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                        for (LLResultTypes.FiducialResult fr : fiducialResults) {
                            if (fr.getFiducialId() == 20 && Math.abs(result.getTx()) > 0.25) {
                                double headingError = result.getTx(); //desiredTag.ftcPose.bearing;
                                turn = -Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                                moveRobot(0, 0, turn);
                                telemetry.addData("Tag valid", fr.getFiducialId());
                            }

                        } */

            drive.updatePoseEstimate();
            telemetry.update();

        }

    }


}
