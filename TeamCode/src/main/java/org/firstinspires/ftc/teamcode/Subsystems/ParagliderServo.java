package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ParagliderServo {
    private final Servo paragliderServo;
    private final Servo bananaServo;

    public ParagliderServo(HardwareMap hardwareMap) {
        paragliderServo = hardwareMap.get(Servo.class, "paragliderServo");
        bananaServo = hardwareMap.get(Servo.class, "bananaServo");

        //paragliderServo.setPosition(1);
        //bananaServo.setPosition(0);
    }

    public void setPosition(double position) {
        paragliderServo.setPosition(position);
    }

    public double getTargetPosition() {
        return paragliderServo.getPosition();
    }

    public void up() {
        paragliderServo.setPosition(1);
        bananaServo.setPosition(0);
    }
    public void mid() {paragliderServo.setPosition(.5);}
    public void down() {
        paragliderServo.setPosition(0);
        bananaServo.setPosition(1);
    }
}