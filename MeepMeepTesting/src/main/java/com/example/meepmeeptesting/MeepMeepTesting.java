package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(60, -12, Math.toRadians(180)))
                .waitSeconds(1)
                .setTangent(180)
                .strafeTo(new Vector2d(0,-12))
                //.splineToConstantHeading(new Vector2d(-50, -43), (3 * Math.PI / 2));
                .splineToLinearHeading(new Pose2d(-24, -24 , Math.toRadians(230)), (Math.PI / 2)) // -55,-55
                .waitSeconds(9.5)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(36, -12 , Math.toRadians(180)), (Math.PI / 2)) // -55,-55
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}