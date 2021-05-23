package org.spbstu.aleksandrov;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.entities.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WorldLoader {

    private static final Json json = new Json();
    private static final File dir = new File("android/assets/worldData");
    private static final File[] directoryListing = dir.listFiles();

    public static MyWorld getWorld() {

        int i = ThreadLocalRandom.current().nextInt(0, 1 + 1);

        File file = directoryListing[i];
        List<Entity> entities = json.fromJson(ArrayList.class, Gdx.files.internal(file.getPath()));
        return new MyWorld(entities, i);
    }
}
