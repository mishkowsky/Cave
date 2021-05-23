package org.spbstu.aleksandrov.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import org.spbstu.aleksandrov.view.WorldRenderer;

public class Utils {

    public static Button createButton(String name) {

        TextureAtlas mAtlas =
                new TextureAtlas("packed/" + name + "/" + name + "_button.atlas");
        TextureRegionDrawable drawableUp =
                new TextureRegionDrawable(mAtlas.findRegion(name + "_button"/*+ "_up"*/));
        TextureRegionDrawable drawableDown =
                new TextureRegionDrawable(mAtlas.findRegion(name + "_button"/*+ "_down"*/));
        TextureRegionDrawable drawableChecked =
                new TextureRegionDrawable(mAtlas.findRegion(name + "_button"/*+ "_checked"*/));

        Button.ButtonStyle btnStyle = new Button.ButtonStyle(drawableUp, drawableDown, drawableChecked);
        Button button = new Button(btnStyle);
        button.setOrigin(Align.center);
        button.setDebug(WorldRenderer.DEBUG);
        button.setTransform(true);
        return button;
    }

    public static BitmapFont createFont() {
        BitmapFont font;
        Texture fontT = new Texture(Gdx.files.internal("android/assets/font.png"));
        fontT.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("android/assets/font.fnt"), new TextureRegion(fontT), false);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.setUseIntegerPositions(false);
        font.setColor(0f, 0f, 0f, 1f);
        return font;
    }

    public static void addImgToTable(Table table, String path, float scale, float height, float width) {

        Texture texture = new Texture(path);
        Image img = new Image(texture);

        img.setScale(scale);
        img.setOrigin(Align.center);

        table.add(img).width(width).height(height);
        table.row();
    }
}
