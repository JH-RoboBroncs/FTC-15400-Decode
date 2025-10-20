package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.mechanisms.StarterBotShoot;

@TeleOp
public class StarterBotTeleOp extends LinearOpMode {

    //StarterBotShoot shooter = new StarterBotShoot();

    private DcMotor motor;
    private CRServo servoOne;
    private CRServo servoTwo;

    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        //shooter.init(hardwareMap);

        waitForStart();

        motor = hardwareMap.get(DcMotor.class, "shooter");
        servoOne = hardwareMap.get(CRServo.class, "servoOne");
        servoTwo = hardwareMap.get(CRServo.class, "servoTwo");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        while (opModeIsActive()) {
            drive.setDrivePowers(new PoseVelocity2d(
                    new Vector2d(
                            -gamepad1.left_stick_y,
                            gamepad1.left_stick_x
                    ),
                    -gamepad1.right_stick_x
            ));

            if (gamepad1.a){
                shoot(1);
                //shooter.shoot(1);
            } else if (gamepad1.b) {
                //shooter.load(0.5);
                load(1);
            }

        }

    }

    public void shoot(double speed) {
        motor.setPower(speed);
    }

    public void load(double speed) {
        servoOne.setPower(speed);
        servoTwo.setPower(speed);
    }

}
