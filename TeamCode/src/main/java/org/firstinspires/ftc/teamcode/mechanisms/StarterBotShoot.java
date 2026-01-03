package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class StarterBotShoot {
    private DcMotorEx motor;
    private CRServo servoOne;
    private CRServo servoTwo;
    private double ticksPerRotation;
    private int phase = 1;
    private int mphase = 1;

    public void init(HardwareMap hwMap) {
        motor = hwMap.get(DcMotorEx.class, "shooter");
        servoOne = hwMap.get(CRServo.class, "servoOne");
        servoTwo = hwMap.get(CRServo.class, "servoTwo");
        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        ticksPerRotation = motor.getMotorType().getTicksPerRev();

    }



    public void shoot(double speed) {
        motor.setVelocity(ticksPerRotation/(1.766/speed));
    }

    public void load(double speed) {
        servoOne.setPower(-speed);
        servoTwo.setPower(speed);
    }


    public void shoot2(ElapsedTime timer) {
        //timer.reset();
        if (timer.seconds() > 3 && timer.seconds() < 5) { //3rd ball
            mphase = 2;
        } else if (timer.seconds() > 1.5 && timer.seconds() < 3) { //2nd ball
            mphase = 3;
        } else if (timer.seconds() < 10) { // 1st ball
            mphase = 4;
        } else {
            mphase = 1;
        }

        if ((timer.seconds() > 1.35 && timer.seconds() < 1.5) || (timer.seconds() > 2.85 && timer.seconds() < 3) || (timer.seconds() > 4.5 && timer.seconds() < 5)) {
            phase = 2; // load
        } else if ((timer.seconds() < .75) || (timer.seconds() > 1.5 && timer.seconds() < 2.25) || (timer.seconds() > 3.15 && timer.seconds() < 3.9)) {
            phase = 4;
        } else {
            phase = 1; // idle
        }


        switch (phase){
            case 1: // waiting
                servoTwo.setPower(0);
                servoOne.setPower(0);
                break;
            case 2: //loading
                servoTwo.setPower(.25); //.65
                servoOne.setPower(-0.25);
                break;
            case 3: //loading
                servoTwo.setPower(.5);
                servoOne.setPower(-.5);
                break;
            case 4:
                servoOne.setPower(1);
                servoTwo.setPower(-1);
                break;
            default:
                servoTwo.setPower(0);
                servoOne.setPower(0);
        }

        switch (mphase){
            case 1: // waiting
                //motor.setPower(0);
                motor.setVelocity(0);
                break;
            case 2: //loading
                //motor.setPower(.45); //1st ball
                motor.setVelocity(ticksPerRotation/(1.1766/.55)); //.475
                break;
            case 3: //loading
                //motor.setPower(.45); // 2nd ball
                motor.setVelocity(ticksPerRotation/(1.1766/.55)); //.475
                break;
            case 4: //loading
                //motor.setPower(.5); //3rd ball
                motor.setVelocity(ticksPerRotation/(1.1766/.5)); //.425
                break;
            default:
                //motor.setPower(0);
        }

    }

    public void antiload(double speed) {
        servoTwo.setPower(-speed);
        servoOne.setPower(speed);
    }

}
