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
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.Timer;
import java.util.TimerTask;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.mechanisms.StarterBotShoot;

@TeleOp
public class StarterBotTeleOp1 extends LinearOpMode {

    private ElapsedTime timer = new ElapsedTime();


    private Limelight3A limelight;

    StarterBotShoot shooter = new StarterBotShoot();
    private DcMotorEx motor;
    private CRServo servoOne;
    private CRServo servoTwo;
    private Servo Hood;
    boolean shooting = false;
    boolean shootingSingle = false;
    boolean sensToggle = true;
    boolean resetTimer = false;
    private double count = 0;

    private int hoodAngle = 0;
    private double ticksperrev;
    private double targetRPM = 1000;
    private int phase = 0;

    private Timer time = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            shootingSingle = false;
            phase = 1;
        }
    };

    double currentX;
    double currentY;

    @Override
    public void runOpMode() throws InterruptedException {
        //limelight = hardwareMap.get(Limelight3A.class, "limelight");
       // limelight.start();
        motor = hardwareMap.get(DcMotorEx.class, "shooter");
        Hood = hardwareMap.get(Servo.class, "hoodServo");
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
            }*/

            /*if (gamepad2.dpad_left) {
                shooter.load(.55);
                } else {
                shooter.load(0);
            }*/



            if(gamepad2.dpadUpWasReleased()) {
                targetRPM = targetRPM + 250;
            } else if(gamepad2.dpadDownWasReleased()) {
                targetRPM = targetRPM - 250;
            }


            ticksperrev = motor.getVelocity()/28 * 60; //ticks per second -> rpm

         /*   if (gamepad2.y) {
                motor.setVelocity((targetRPM/60)*28);  //target RPM/60(seconds)*28 (ticks/revolution)
                if (ticksperrev < targetRPM + 200 && ticksperrev > targetRPM - 200) {
                    shooter.load(.55);
                } else {
                    shooter.load(0);
                }
            } else {
                motor.setVelocity(0);
            } */



            if (gamepad2.x && !shooting) {
                shooting = true;
                timer.reset();
            }

            if (gamepad2.b && !shootingSingle) {
                shootingSingle = true;
                phase = 1;
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
                    targetRPM = 2750;
                    Hood.setPosition(0);
                    break;
                case 1:
                    targetRPM = 2600;
                    Hood.setPosition(.115);
                    break;
                case 2:
                    targetRPM = 3250;
                    Hood.setPosition(.125);
                    break;
                /*case 3:
                    Hood.setPosition(.13);
                    break;*/
                case 3: //4
                    targetRPM = 4500;
                    Hood.setPosition(.145);
                    break;

                //default:
                // Hood.setPosition(0);
            }

            switch (phase){
                case 1: // waiting
                    servoTwo.setPower(0);
                    servoOne.setPower(0);
                    break;
                case 2:
                    timer.reset();
                    servoOne.setPower(-.25);
                    servoTwo.setPower(.25);
                    time.schedule(task, 1);
                    break;
                case 3:
                    timer.reset();
                    break;
                default:
                    servoTwo.setPower(0);
                    servoOne.setPower(0);
            }







            if (shootingSingle) {

                motor.setVelocity((targetRPM/60)*28);

                if (phase == 1 && (ticksperrev < targetRPM + 100 && ticksperrev > targetRPM - 100)) {
                  phase = 2;
                  timer.reset();

                   if (timer.seconds() > .25) {
                       phase = 1;
                   }
                } 

               /* if (ticksperrev < targetRPM + 100 && ticksperrev > targetRPM - 100 && !resetTimer) {
                    phase = 2;
                    resetTimer = true;

                }*/


            } else {
                phase = 1;
               // resetTimer = false;
                motor.setVelocity(0);




            /*else if (shooting) {
                shooter.shoot2(timer);

                // Stop after full cycle (adjust time as needed)
                if (timer.seconds() > 5) {   // <-- duration of full cycle
                    shooting = false;

                }*/
            }


          /*  if (gamepad1.yWasReleased() && sensToggle) {
                sensToggle = false;
            } else if (gamepad1.yWasReleased() && !sensToggle) {
                sensToggle = true;
            } */





            telemetry.addData("anglecase", hoodAngle);
         //   telemetry.addData("sensitivity", sensToggle);
            telemetry.addData("time", timer);
            telemetry.addData("shootingsingle", shootingSingle);
            telemetry.addData("ticks/rev", ticksperrev);
            telemetry.addData("targetRPM", targetRPM);
            telemetry.addData("phase", phase);




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
