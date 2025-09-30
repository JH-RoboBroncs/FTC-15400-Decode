package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.hardware.rev.RevSPARKMini;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift {
    private final CRServo Lift;

    public Lift(HardwareMap hardwareMap) {
        Lift = hardwareMap.get(CRServo.class, "leftLift");

        Lift.setDirection(CRServo.Direction.REVERSE); //Lift.setDirection(CRServo.Direction.REVERSE);

//        Lift.setZeroPowerBehavior(DcMotorSimple.ZeroPowerBehavior.BRAKE);
//
//        Lift.setMode(DcMotorSimple.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setPower(double power) {
//        Lift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        Lift.setPower(power);
    }
}
