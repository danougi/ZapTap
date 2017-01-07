package com.betterclever.zaptap.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.betterclever.zaptap.Constants;
import com.betterclever.zaptap.ZapTapGame;
import com.betterclever.zaptap.objects.Ripple;

/**
 * Created by betterclever on 05/01/17.
 */

public class HomeScreen extends InputAdapter implements Screen {

    private static final String TAG = HomeScreen.class.getSimpleName();

    ExtendViewport viewport;

    Ripple playRippleButton;
    Ripple testRipple;
    Ripple testRipple2;

    Color backgroundColor;
    Color destColor;
    ColorAction colorAction;
    float counter;

    SpriteBatch batch;
    ShapeRenderer renderer;

    Texture scoreTrophy;
    ZapTapGame game;

    BitmapFont logoFont;
    BitmapFont otherFont;

    FPSLogger fpsLogger;

    public HomeScreen(ZapTapGame zapTapGame) {
        game = zapTapGame;
    }

    @Override
    public void show() {

        fpsLogger = new FPSLogger();

        backgroundColor = new Color(Color.TEAL);
        destColor = new Color(Color.MAGENTA);
        colorAction = new ColorAction();
        colorAction.setColor(backgroundColor);
        colorAction.setDuration(2f);
        colorAction.setEndColor(destColor);

        scoreTrophy = new Texture("trophy.png");
        renderer = new ShapeRenderer();
        playRippleButton = new Ripple(renderer,
                            new Vector2(Constants.WORLD_WIDTH/2,Constants.WORLD_HEIGHT/3),
                            60,
                            100);

        testRipple = new Ripple(renderer,
                new Vector2(Constants.WORLD_WIDTH/4,Constants.WORLD_HEIGHT/3),
                60,
                100);

        testRipple2 = new Ripple(renderer,
                new Vector2(3*Constants.WORLD_WIDTH/4,Constants.WORLD_HEIGHT/3),
                60,
                100);

        batch = new SpriteBatch();
        viewport = new ExtendViewport(Constants.WORLD_WIDTH,Constants.WORLD_HEIGHT);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Teacher_a.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 100;
        parameter.color= Color.WHITE;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        logoFont = generator.generateFont(parameter);
        generator.dispose();

        Gdx.input.setInputProcessor(this);
        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("Quantify-Bold.ttf"));
        parameter.size = 30;
        otherFont = generator2.generateFont(parameter);
        generator2.dispose();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(backgroundColor.r,backgroundColor.g,backgroundColor.b,backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //fpsLogger.log();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        renderer.setProjectionMatrix(viewport.getCamera().combined);

        playRippleButton.render(delta);
        testRipple.render(delta);
        testRipple2.render(delta);

        chameleonizeTheBackground(delta);
        writeLogo();

        batch.begin();
        batch.draw(scoreTrophy,3*Constants.WORLD_WIDTH/4-30,Constants.WORLD_HEIGHT/4+10,60,60);
        batch.end();

    }

    private void writeLogo() {
        batch.begin();
        logoFont.draw(batch,"Zap Tap",Constants.WORLD_WIDTH/4,5*Constants.WORLD_HEIGHT/6);
        otherFont.draw(batch,"PLAY",Constants.WORLD_WIDTH/2-55,Constants.WORLD_HEIGHT/3+10);
        batch.end();
    }

    private void chameleonizeTheBackground(float delta) {
        counter+= delta;

        colorAction.act(delta);

        if(counter>2){
            counter-=2;
            destColor = destColor.set(MathUtils.random(0.3f,0.8f),MathUtils.random(0.3f,0.8f),MathUtils.random(0.3f,0.8f),1);
            colorAction.reset();
            colorAction.setColor(backgroundColor);
            colorAction.setDuration(2f);
            colorAction.setEndColor(destColor);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean value = super.touchDown(screenX, screenY, pointer, button);

        Vector2 position = viewport.unproject(new Vector2(screenX,screenY));
        if(playRippleButton.isTouched(position)){
            game.startPlay();
        }

        if(testRipple.isTouched(position)){
            Gdx.app.log(TAG,"Here I tapped");
            game.getPlayGameServices().signIn();
            Gdx.app.log(TAG, String.valueOf(game.getPlayGameServices().isSignedIn()));
        }

        if(testRipple2.isTouched(position)){
            game.getPlayGameServices().showScore();
        }

        return value;
    }
}