package org.niitp.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ParsingControllerTest {

    @Test
    public void parse() {
        double alpha = Double.parseDouble("-1.2");
        double omega = Double.parseDouble("0.2");
        double r = Double.parseDouble("6370000");
        double h = Double.parseDouble("510800");
        double f = Double.parseDouble("360") * Math.pow(10, -3);
        log.info("f " + f);
        log.info("h " + h);
        log.info("alpha " + alpha);

        double gamma = Math.acos(Math.cos(Math.toRadians(alpha)) * Math.cos(Math.toRadians(omega)));
        log.info("gamma " + Math.toDegrees(gamma));

        double rh = r + h;

        double m = (rh * (Math.cos(gamma) - Math.sqrt(Math.pow(r / rh, 2) - Math.pow(Math.sin(gamma), 2)))) / f;
        log.info("m " + m);

        double e = Math.asin((rh / r) * Math.sin(gamma));

        double gsd = m * 7.4 * Math.pow(10, -6) / Math.cos(e);
        log.info("gsd " + gsd);

        double slantRange = Math.abs(h / Math.cos(Math.toRadians(alpha)));
        log.info("slantRange " + slantRange);


        int i = 010;
        int j = 07;
        System.out.println(i);
        System.out.println(j);

        int k = 0x2222;
        int l = 0x000F;
        System.out.println(l&k);

        Assert.assertEquals("", "");
    }
}