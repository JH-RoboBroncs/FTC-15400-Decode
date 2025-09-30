package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.hardware.rev.RevSPARKMini;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class PunchArm {
    private final CRServo PunchArm;

    public PunchArm(HardwareMap hardwareMap) {
        PunchArm = hardwareMap.get(CRServo.class, "punchArm");

        PunchArm.setDirection(CRServo.Direction.REVERSE); //Lift.setDirection(CRServo.Direction.REVERSE);
    }

    public void setPower(double power) {
        PunchArm.setPower(power);
    }
}