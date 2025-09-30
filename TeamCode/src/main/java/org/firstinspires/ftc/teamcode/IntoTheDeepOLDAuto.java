package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.ParagliderServo;

@Config
@Autonomous(name="Peanur", group="Linear OpMode")
@Disabled
public class IntoTheDeepOLDAuto extends LinearOpMode {

    private ElapsedTime timer = new ElapsedTime();

    public class ParagliderServo {
        private Servo paragliderServo;
        private Servo bananaServo;

        public ParagliderServo(HardwareMap hardwareMap){
            paragliderServo = hardwareMap.get(Servo.class, "paragliderServo");
            bananaServo = hardwareMap.get(Servo.class, "bananaServo");
        }

        public class ServoOpen implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                paragliderServo.setPosition(1);
                bananaServo.setPosition(0);
                return false;
            }
        }

        public class ServoClose implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                paragliderServo.setPosition(0);
                bananaServo.setPosition(1);
                return false;
            }
        }

        public Action servoOpen() {
            return new ServoOpen();
        }
        public Action servoClose() {
            return new ServoClose();
        }
    }

    public class WristServo {
        private Servo wristServo;

        public WristServo(HardwareMap hardwareMap) {
            wristServo = hardwareMap.get(Servo.class, "wristServo");
        }

        public class ServoDown implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                wristServo.setPosition(1);
                return false;
            }
        }

        public class ServoUp implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                wristServo.setPosition(.3);
                return false;
            }
        }

        public class ServoToward implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                wristServo.setPosition(0);
                return false;
            }
        }

        public Action servoDown() {
            return new WristServo.ServoDown();
        }
        public Action servoUp() {return new WristServo.ServoUp();}
        public Action servoToward() {
            return new WristServo.ServoToward();
        }
    }

    public class PincherServo {
        private Servo pincherServo;

        public PincherServo(HardwareMap hardwareMap){
            pincherServo = hardwareMap.get(Servo.class, "pincherServo");
        }

        public class ServoClopen implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                pincherServo.setPosition(1);
                return false;
            }
        }

        public class ServoClosen implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                pincherServo.setPosition(0);
                return false;
            }
        }

        public class ServoClid implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                pincherServo.setPosition(.55);
                return false;
            }
        }

        public Action servoClopen() {
            return new ServoClopen();
        }
        public Action servoClosen () {
            return new ServoClosen();
        }
        public Action servoClid () {
            return new ServoClid();
        }

    }

    public class Lift {
        private CRServo lift;
        private DcMotor rightFront;

        public Lift(HardwareMap hardwareMap) {
            lift = hardwareMap.get(CRServo.class, "leftLift");
            lift.setDirection(CRServo.Direction.FORWARD);

            rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        }

        public class LiftUp implements Action {
            private boolean initialized = false;

            // actions are formatted via telemetry packets as below
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                //powers on motor, if it is not on
                if (!initialized) {
                    lift.setPower(-0.8);
                    initialized = true;
                }

                if (rightFront.getCurrentPosition() > -5400) {
                    return true;
                } else {
                    lift.setPower(0);
                    return false;
                }
            }
        }

        public class LiftDown implements Action {
            private boolean initialized = false;

            // actions are formatted via telemetry packets as below
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                //powers on motor, if it is not on
                if (!initialized) {
                    lift.setPower(0.4);
                    initialized = true;
                }

                if (rightFront.getCurrentPosition() < -1000) {
                    return true;
                } else {
                    lift.setPower(0);
                    return false;
                }
            }
        }
        //TODO This could break everything
        public class LiftDoubleDown implements Action {
            private boolean initialized = false;

            // actions are formatted via telemetry packets as below
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                //powers on motor, if it is not on
                if (!initialized) {
                    lift.setPower(0.5);
                    initialized = true;
                }

                if (rightFront.getCurrentPosition() < 0) {
                    return true;
                } else {
                    lift.setPower(0);
                    return false;
                }
            }
        }
        public Action liftDoubleDown() {
            return new LiftDoubleDown();
        }
        public Action liftDown() {
            return new LiftDown();
        }

        public Action liftUp() {
            return new LiftUp();
        }
    }

    public class PunchArm {
        private CRServo punchArm;

        public PunchArm(HardwareMap hardwareMap) {
            punchArm = hardwareMap.get(CRServo.class, "punchArm");
            punchArm.setDirection(CRServo.Direction.FORWARD);

        }

        public class PunchOut implements Action {
            private boolean initialized = false;
            // actions are formatted via telemetry packets as below
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                //powers on motor, if it is not on
                if (!initialized) {
                    punchArm.setPower(-0.8);
                    initialized = true;
                }

                timer.reset();
                while (timer.seconds() < 4 && opModeIsActive()) {
                    return true;
                }
                return false;

//                if (timer.seconds() < 4) {
//                    return true;
//                } else {
//                    punchArm.setPower(0);
//                    return false;
//                }
            }
        }

        public class PunchLil implements Action {
            private boolean initialized = false;
            // actions are formatted via telemetry packets as below
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                timer.reset();
                //powers on motor, if it is not on
                if (!initialized) {
                    punchArm.setPower(-0.3);
                    initialized = true;
                }

                if (timer.seconds() < 1) {
                    return true;
                } else {
                    punchArm.setPower(0);
                    return false;
                }
            }
        }

        public class PunchIn implements Action {
            private boolean initialized = false;
            // actions are formatted via telemetry packets as below
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                timer.reset();
                //powers on motor, if it is not on
                if (!initialized) {
                    punchArm.setPower(0.8);
                    initialized = true;
                }

                if (timer.seconds() < 2) {
                    return true;
                } else {
                    punchArm.setPower(0);
                    return false;
                }
            }
        }

        //turns these into actions to be used in actions.runblocking (question mark?)
        public Action PunchOut() {
            return new PunchOut();
        }
        public Action PunchIn() {
            return new PunchIn();
        }
        public Action PunchLil() {
            return new PunchLil();
        }
    }

    @Override
    public void runOpMode() {
        // instantiate your MecanumDrive at a particular pose.
        Pose2d initialPose = new Pose2d(33, 66, Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        // make a Lift instance

        Lift lift = new Lift(hardwareMap);
        ParagliderServo paragliderServo = new ParagliderServo(hardwareMap);
        WristServo wristServo = new WristServo(hardwareMap);
        PincherServo pincherServo = new PincherServo(hardwareMap);
        PunchArm punchArm = new PunchArm(hardwareMap);

        int visionOutputPosition = 1;
        //36, 47

        // actionBuilder builds from the drive steps passed to it
        TrajectoryActionBuilder tab1 = drive.actionBuilder(initialPose)
                .splineTo(new Vector2d(55,53), Math.toRadians(70))
                .waitSeconds(1);
        TrajectoryActionBuilder alignment = drive.actionBuilder(initialPose)
                .waitSeconds(0.5)
                .strafeTo(new Vector2d(36, 47))
                .turn(Math.toRadians(50))
                .waitSeconds(3);
        Action trajectoryActionCloseOut = tab1.endTrajectory().fresh()
                .waitSeconds(.5)
                .strafeTo(new Vector2d(34, 12))
                .turn(Math.toRadians(130))
                .lineToX(28)
                .build();

        while (!isStopRequested() && !opModeIsActive()) {
            int position = visionOutputPosition;
            telemetry.addData("Position during Init", position);
            telemetry.update();
        }

        int startPosition = visionOutputPosition;
        telemetry.addData("Starting Position", startPosition);
        telemetry.addData("Encoders: ", lift.rightFront.getCurrentPosition());
        telemetry.update();
        waitForStart();

        if (isStopRequested()) return;

        Action trajectoryActionChosen = null;
        // red/blu alliance question mark
        if (startPosition == 1) {
            trajectoryActionChosen = tab1.build();
        } else if (startPosition == 2) {
            trajectoryActionChosen = alignment.build();
        }

        Actions.runBlocking(
                //sequential action runs things in order
                new SequentialAction(
                        trajectoryActionChosen,
                        lift.liftUp(),
                        paragliderServo.servoOpen(),


                        alignment.build(),
                        lift.liftDoubleDown(),

                        //main function thing, then the subfunction or whatever idk what to name u feel me
                        punchArm.PunchOut(),
                        pincherServo.servoClopen(),
                        wristServo.servoDown(),
                        pincherServo.servoClosen(),
                        wristServo.servoUp(),
                        punchArm.PunchIn(),
                        wristServo.servoToward(),
                        paragliderServo.servoClose(),
                        pincherServo.servoClid(),
                        punchArm.PunchLil(),

                        trajectoryActionChosen,
                        lift.liftUp(),
                        paragliderServo.servoOpen(),

                        trajectoryActionCloseOut,
                        lift.liftDown()
                )
        );
    }
}
