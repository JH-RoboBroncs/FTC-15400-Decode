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
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.mechanisms.StarterBotShoot;

@TeleOp
public class StarterBotTeleOp extends LinearOpMode {

    private ElapsedTime timer = new ElapsedTime();
    private Limelight3A limelight;

    StarterBotShoot shooter = new StarterBotShoot();
    private DcMotor motor;
    boolean shooting = false;

    double currentX;
    double currentY;

    @Override
    public void runOpMode() throws InterruptedException {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.start();
        motor = hardwareMap.get(DcMotorEx.class, "shooter");

        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(60, 0, Math.toRadians(180)));
        shooter.init(hardwareMap);


        waitForStart();

        while (opModeIsActive()) {
            drive.setDrivePowers(new PoseVelocity2d(
                    new Vector2d(
                            -gamepad1.left_stick_y *0.85,
                            -gamepad1.left_stick_x *0.85
                    ),
                    -gamepad1.right_stick_x
            ));

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



            if (shooting) {
                shooter.shoot2(timer);

                // Stop after full cycle (adjust time as needed)
                if (timer.seconds() > 5) {   // <-- duration of full cycle
                    shooting = false;
                    shooter.shoot(0);           // stop motor
                    shooter.load(0);            // stop servos
                }
            }



            LLResult result = limelight.getLatestResult();
            double robotYaw = drive.localizer.getPose().heading.toDouble();
            limelight.updateRobotOrientation(Math.toDegrees(robotYaw));

            Pose2d pose = drive.localizer.getPose();
            Pose2d mt2pose = new Pose2d(currentX*72, currentY*72, robotYaw);
            telemetry.addData("shooting", shooting);
            telemetry.addData("time", timer);
            telemetry.addData("motorspeed", motor.getPower());



                    /*if (result.isValid()) {
                        List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                        for (LLResultTypes.FiducialResult fr : fiducialResults) {
                            if (fr.getFiducialId() == 20 && Math.abs(result.getTx()) > 0.25) {
                                double headingError = result.getTx(); //desiredTag.ftcPose.bearing;
                                turn = -Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                                moveRobot(0, 0, turn);
                                telemetry.addData("Tag valid", fr.getFiducialId());
                            }

                        } */if (result.isValid()) {
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

            }

            TelemetryPacket packet = new TelemetryPacket();
            packet.fieldOverlay().setStroke("#3F51B5");
            Drawing.drawRobot(packet.fieldOverlay(), mt2pose);
            FtcDashboard.getInstance().sendTelemetryPacket(packet);

            drive.updatePoseEstimate();
            telemetry.update();

        }

    }


}
