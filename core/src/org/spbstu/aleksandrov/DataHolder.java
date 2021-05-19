package org.spbstu.aleksandrov;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.Player;
import org.spbstu.aleksandrov.model.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.spbstu.aleksandrov.model.MyWorld.SCALE;

public class DataHolder {

    //libgdx Preferences can not hold custom classes

    private static final List<MyWorld> worldList = new ArrayList<>();

    private static void create() {

        Platform.resetIdCounter();

        final MyWorld myWorld_0 = new MyWorld(
                new Player(),
                List.of(
                        new Rocket(new Vector2((28.5f) * SCALE, (3700 + 25f) * SCALE)),
                        new Ground(new Vector2(0, 0)),
                        new Platform(new Vector2(0, 3700 * SCALE)), new Platform(new Vector2(70 * SCALE, 2500 * SCALE)),
                        new Asteroid(new Vector2(440 * SCALE, 3135 * SCALE), true, 2)
                ), 0
        );

        Platform.resetIdCounter();

        final MyWorld myWorld_1 = new MyWorld(
                new Player(),
                List.of(
                        new Rocket(new Vector2((572 + 28.5f) * SCALE, (7731 + 30f) * SCALE)),
                        new Ground(new Vector2(0, 0)),
                        new Platform(new Vector2(572 * SCALE, 7731 * SCALE)),
                        new Platform(new Vector2(520 * SCALE, 6970 * SCALE)),
                        new Platform(new Vector2(570 * SCALE, 6270 * SCALE)),
                        new Platform(new Vector2(1319 * SCALE, 5619 * SCALE)),
                        new Platform(new Vector2(1174 * SCALE, 4782 * SCALE)),
                        new Platform(new Vector2(313 * SCALE, 4582 * SCALE)),
                        new Platform(new Vector2(310 * SCALE, 3538 * SCALE)),
                        new Platform(new Vector2(1388 * SCALE, 2734 * SCALE)),
                        new Platform(new Vector2(375 * SCALE, 2243 * SCALE)),
                        new Platform(new Vector2(636 * SCALE, 1511 * SCALE)),
                        new Platform(new Vector2(1018 * SCALE, 548 * SCALE)),
                        new Asteroid(new Vector2(570 * SCALE, 7239 * SCALE), false, 1),
                        new Asteroid(new Vector2(700 * SCALE, 7098 * SCALE), false, 1),
                        new Asteroid(new Vector2(263 * SCALE, 6642 * SCALE), true, 2),
                        new Asteroid(new Vector2(1315 * SCALE, 6303 * SCALE), true, 3),
                        new Asteroid(new Vector2(1230 * SCALE, 1724 * SCALE), false, 1),
                        new Asteroid(new Vector2(1306 * SCALE, 5169 * SCALE), true, 3),
                        new Asteroid(new Vector2(555 * SCALE, 4781 * SCALE), false, 0),
                        new Asteroid(new Vector2(293 * SCALE, 3863 * SCALE), true, 2),
                        new Asteroid(new Vector2(557 * SCALE, 3520 * SCALE), true, 2),
                        new Asteroid(new Vector2(915 * SCALE, 2536 * SCALE), true, 1),
                        new Asteroid(new Vector2(655 * SCALE, 2533 * SCALE), true, 2),
                        new Coin(new Vector2(600 * SCALE, 7550 * SCALE)),
                        new Coin(new Vector2(639 * SCALE, 7371 * SCALE)),
                        new Coin(new Vector2(611 * SCALE, 7108 * SCALE)),
                        new Coin(new Vector2(500 * SCALE, 6852 * SCALE)),
                        new Coin(new Vector2(611 * SCALE, 7108 * SCALE)),
                        new Coin(new Vector2(327 * SCALE, 6587 * SCALE)),
                        new Coin(new Vector2(445 * SCALE, 6323 * SCALE)),
                        new Coin(new Vector2(870 * SCALE, 6423 * SCALE)),
                        new Coin(new Vector2(1300 * SCALE, 6268 * SCALE)),
                        new Coin(new Vector2(1366 * SCALE, 5913 * SCALE)),
                        new Coin(new Vector2(1292 * SCALE, 5385 * SCALE)),
                        new Coin(new Vector2(1244 * SCALE, 4960 * SCALE)),
                        new Coin(new Vector2(988 * SCALE, 4780 * SCALE)),
                        new Coin(new Vector2(914 * SCALE, 4772 * SCALE)),
                        new Coin(new Vector2(817 * SCALE, 4785 * SCALE)),
                        new Coin(new Vector2(493 * SCALE, 4740 * SCALE)),
                        new Coin(new Vector2(182 * SCALE, 4417 * SCALE)),
                        new Coin(new Vector2(198 * SCALE, 3934 * SCALE)),
                        new Coin(new Vector2(333 * SCALE, 3663 * SCALE)),
                        new Coin(new Vector2(575 * SCALE, 3306 * SCALE)),
                        new Coin(new Vector2(816 * SCALE, 3050 * SCALE)),
                        new Coin(new Vector2(1264 * SCALE, 2909 * SCALE)),
                        new Coin(new Vector2(1202 * SCALE, 2414 * SCALE)),
                        new Coin(new Vector2(825 * SCALE, 2534 * SCALE)),
                        new Coin(new Vector2(512 * SCALE, 5608 * SCALE)),
                        new Coin(new Vector2(410 * SCALE, 2054 * SCALE)),
                        new Coin(new Vector2(522 * SCALE, 1721 * SCALE)),
                        new Coin(new Vector2(802 * SCALE, 1449 * SCALE)),
                        new Coin(new Vector2(1100 * SCALE, 1281 * SCALE)),
                        new Coin(new Vector2(996 * SCALE, 997 * SCALE)),
                        new Coin(new Vector2(546 * SCALE, 7051 * SCALE)),
                        new Coin(new Vector2(799 * SCALE, 748 * SCALE))
                ), 1
        );
        worldList.add(myWorld_0);

        worldList.add(myWorld_1);
    }

    public static MyWorld getWorld() {

        worldList.clear();
        create();

        int i = ThreadLocalRandom.current().nextInt(0, 1 + 1);

        return worldList.get(i);

    }
}
