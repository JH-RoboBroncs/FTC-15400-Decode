package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.mechanisms.StarterBotShoot;

@TeleOp
public class StarterBotTeleOp extends LinearOpMode {

    StarterBotShoot shooter = new StarterBotShoot();
    private DcMotor motor;

    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        shooter.init(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            drive.setDrivePowers(new PoseVelocity2d(
                    new Vector2d(
                            -gamepad1.left_stick_y,
                            gamepad1.left_stick_x
                    ),
                    -gamepad1.right_stick_x
            ));

            if (gamepad1.a){
                shooter.shoot(0.5);
            } else {
                shooter.shoot(0);
            }

            if (gamepad1.b) {
                shooter.load(0.5);
                } else {
                shooter.load(0);
            }

        }

    }


}
