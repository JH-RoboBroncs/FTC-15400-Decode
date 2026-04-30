package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Drive + Shooter RC (full power)", group = "Linear OpMode")
public class DriveShooterRC extends LinearOpMode {

    // Drive motors
    private DcMotor frontLeftDrive;
    private DcMotor backLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backRightDrive;

    // Shooter system
    private CRServo shooter;
    private Servo toe;
    private boolean toeMoving = false;
    private ElapsedTime toeTimer = new ElapsedTime();

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        // ===== Hardware Map =====
        frontLeftDrive  = hardwareMap.get(DcMotor.class, "leftFront");
        backLeftDrive   = hardwareMap.get(DcMotor.class, "leftBack");
        frontRightDrive = hardwareMap.get(DcMotor.class, "rightFront");
        backRightDrive  = hardwareMap.get(DcMotor.class, "rightBack");

        shooter = hardwareMap.get(CRServo.class, "Shooter");
        toe     = hardwareMap.get(Servo.class, "toe");

        // ===== Motor Directions =====
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // ===== Servo Setup =====
        toe.setDirection(Servo.Direction.REVERSE);
        toe.scaleRange(0.0, 0.8);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // ===== Main Loop =====
        while (opModeIsActive()) {

            /* =====================
               DRIVE CONTROLS
               ===================== */
            double axial   = -gamepad1.left_stick_y;   // forward/back
            double lateral = -gamepad1.left_stick_x;   // strafe
            double yaw     =  (gamepad1.right_stick_x);  // turn

            double frontLeftPower  = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower   = axial - lateral + yaw;
            double backRightPower  = axial + lateral - yaw;

            double max = Math.max(
                    Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))
            );

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }

            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);

            /* =====================
               SHOOTER CONTROLS
               ===================== */
            if (gamepad1.right_bumper) {
                shooter.setPower(-3.9);   // shoot
            } 
            else if (gamepad1.x) {
                shooter.setPower(.5);
            }
            else {
                shooter.setPower(0);
            }
            
            /* =====================
               TOE SERVO CONTROLS
               ===================== */
                // Start toe motion
                if (gamepad1.left_bumper && !toeMoving) {
                    toe.setPosition(0.0);
                    toeTimer.reset();
                    toeMoving = true;
                }
                
                // After 1 second, return toe
                if (toeMoving && toeTimer.seconds() >= 1.0) {
                    toe.setPosition(0.5);
                    toeMoving = false;
                }
                
                // Manual reset
                if (gamepad1.a) {
                    toe.setPosition(0.5);
                    toeMoving = false;
                }


            /* =====================
               TELEMETRY
               ===================== */
            telemetry.addData("Run Time", runtime.toString());
            telemetry.addData("Shooter Power", shooter.getPower());
            telemetry.addData("Toe Position", toe.getPosition());
            telemetry.update();
        }
    }
}
