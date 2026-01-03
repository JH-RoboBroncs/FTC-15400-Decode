package org.firstinspires.ftc.teamcode;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.IMU;

@Autonomous
public class fixAuto extends LinearOpMode {



    private ElapsedTime timer = new ElapsedTime();

    private DcMotor frontLeftDrive = null;  //  Used to control the left front drive wheel
    private DcMotor frontRightDrive = null;  //  Used to control the right front drive wheel
    private DcMotor backLeftDrive = null;  //  Used to control the left back drive wheel
    private DcMotor backRightDrive = null;  //  Used to control the right back drive wheel

    double turn = 0;
    private IMU imu;
    double currentX;
    double currentY;







    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d initialPose = new Pose2d(60, -12, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        Pose2d localizerPose = drive.localizer.getPose();

        boolean targetFound = false;    // Set to true when an AprilTag target is detected
        double turn = 0;        // Desired turning power/speed (-1 to +1)


        frontLeftDrive = hardwareMap.get(DcMotor.class, "leftFront");
        frontRightDrive = hardwareMap.get(DcMotor.class, "rightFront");
        backLeftDrive = hardwareMap.get(DcMotor.class, "leftBack");
        backRightDrive = hardwareMap.get(DcMotor.class, "rightBack");




        TrajectoryActionBuilder poo = drive.actionBuilder(initialPose)
                .waitSeconds(1)
                .lineToX(40);
                //.setTangent(180)
                //.splineToConstantHeading(new Vector2d(-50, -43), (3 * Math.PI / 2));
                //.splineToLinearHeading(new Pose2d(-55, -55 , Math.toRadians(230)), (3*Math.PI / 2));


        Action trajectoryActionCloseOut = poo.endTrajectory().fresh()
                .waitSeconds(3)
                .lineToX(20)
                //.splineToLinearHeading(new Pose2d( 60, 45, Math.toRadians(270)), (3*Math.PI / 2)) // 60, -60, Math.toRadians(270)
                .build();



        waitForStart();

        TelemetryPacket packet = new TelemetryPacket();
        packet.fieldOverlay().setStroke("#3F51B5");
        Drawing.drawRobot(packet.fieldOverlay(), localizerPose);
        FtcDashboard.getInstance().sendTelemetryPacket(packet);

        telemetry.update();
        drive.updatePoseEstimate();


        Actions.runBlocking(
                new SequentialAction(
                        poo.build(),
                        trajectoryActionCloseOut
                )
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