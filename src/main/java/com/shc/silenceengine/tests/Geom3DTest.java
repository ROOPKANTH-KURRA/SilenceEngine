package com.shc.silenceengine.tests;

import com.shc.silenceengine.collision.Collision3D;
import com.shc.silenceengine.core.Display;
import com.shc.silenceengine.core.Game;
import com.shc.silenceengine.core.ResourceLoader;
import com.shc.silenceengine.geom3d.Cuboid;
import com.shc.silenceengine.geom3d.Sphere;
import com.shc.silenceengine.graphics.Batcher;
import com.shc.silenceengine.graphics.Color;
import com.shc.silenceengine.graphics.OrthoCam;
import com.shc.silenceengine.graphics.PerspCam;
import com.shc.silenceengine.graphics.TrueTypeFont;
import com.shc.silenceengine.graphics.opengl.Primitive;
import com.shc.silenceengine.input.Keyboard;
import com.shc.silenceengine.math.Vector3;
import com.shc.silenceengine.utils.RenderUtils;
import com.shc.silenceengine.utils.TimeUtils;

/**
 * @author Sri Harsha Chilakapati
 */
public class Geom3DTest extends Game
{
    private PerspCam camera;
    private OrthoCam hudCam;

    private Cuboid cube;
    private Sphere sphere;

    private TrueTypeFont hudFont;

    public void init()
    {
        ResourceLoader loader = ResourceLoader.getInstance();
        int fontID = loader.defineFont("Hobo Std", TrueTypeFont.STYLE_NORMAL, 16);
        loader.startLoading();

        hudFont = loader.getFont(fontID);

        camera = new PerspCam().initProjection(70, Display.getAspectRatio(), 0.01f, 1000f);
        camera.setPosition(new Vector3(-2, -2, 5));
        camera.lookAt(Vector3.ZERO);

        hudCam = new OrthoCam().initProjection(Display.getWidth(), Display.getHeight());

        cube = new Cuboid(new Vector3(), 1, 1, 1);
        sphere = new Sphere(new Vector3(), 1);
    }

    public void update(float delta)
    {
        if (Keyboard.isClicked(Keyboard.KEY_ESCAPE))
            end();

        if (!Keyboard.isPressed(Keyboard.KEY_SPACE))
        {
            float movement = (float) Math.sin(TimeUtils.currentSeconds()) * 2;

            Vector3 position = cube.getPosition();

            position.x = movement;
            position.y = movement;
            position.z = movement;

            cube.setPosition(position);
            sphere.setPosition(position.negate());

            cube.rotate(1, 1, 1);
            sphere.rotate(1, 1, 1);
        }

        if (Keyboard.isPressed(Keyboard.KEY_W))
            camera.moveForward(delta);

        if (Keyboard.isPressed(Keyboard.KEY_S))
            camera.moveBackward(delta);

        if (Keyboard.isPressed(Keyboard.KEY_A))
            camera.moveLeft(delta);

        if (Keyboard.isPressed(Keyboard.KEY_D))
            camera.moveRight(delta);

        if (Keyboard.isPressed(Keyboard.KEY_Q))
            camera.moveUp(delta);

        if (Keyboard.isPressed(Keyboard.KEY_E))
            camera.moveDown(delta);

        if (Keyboard.isPressed(Keyboard.KEY_UP))
            camera.rotateX(1);

        if (Keyboard.isPressed(Keyboard.KEY_DOWN))
            camera.rotateX(-1);

        if (Keyboard.isPressed(Keyboard.KEY_LEFT))
            camera.rotateY(1);

        if (Keyboard.isPressed(Keyboard.KEY_RIGHT))
            camera.rotateY(-1);
    }

    public void render(float delta, Batcher batcher)
    {
        camera.apply();

        boolean intersects = sphere.intersects(cube);
        Collision3D.Response response = Collision3D.getResponse();

        if (!intersects)
        {
            RenderUtils.fillPolyhedron(batcher, cube, Color.BLUE);
            RenderUtils.fillPolyhedron(batcher, sphere, Color.DARK_RED);
        }
        else
        {
            batcher.begin(Primitive.LINES);
            {
                batcher.vertex(cube.getPosition());
                batcher.color(Color.RED);

                batcher.vertex(cube.getPosition().subtract(response.getMinimumTranslationVector()));
                batcher.color(Color.RED);
            }
            batcher.end();
        }

        RenderUtils.tracePolyhedron(batcher, cube, Color.GREEN);
        RenderUtils.tracePolyhedron(batcher, sphere, Color.GREEN);
        RenderUtils.tracePolyhedron(batcher, cube.getBounds(), Color.WHITE);
        RenderUtils.tracePolyhedron(batcher, sphere.getBounds(), Color.WHITE);

        hudCam.apply();
        hudFont.drawString(batcher, "Intersection: " + intersects, 10, 10);
        hudFont.drawString(batcher, "\nSphere Inside Cube: " + response.isAInsideB(), 10, 12);
        hudFont.drawString(batcher, "\n\nCube Inside Sphere: " + response.isBInsideA(), 10, 14);
        hudFont.drawString(batcher, "\n\n\nOverlap Distance: " + response.getOverlapDistance(), 10, 16);
        hudFont.drawString(batcher, "\n\n\n\nOverlap Axis: " + response.getOverlapAxis(), 10, 18);
        hudFont.drawString(batcher, "\n\n\n\n\nMinimum Translation Vector: " + response.getMinimumTranslationVector(), 10, 20);
    }

    public void resize()
    {
        camera.initProjection(70, Display.getAspectRatio(), 0.01f, 1000f);
        hudCam.initProjection(Display.getWidth(), Display.getHeight());
    }

    public void dispose()
    {
        ResourceLoader.getInstance().dispose();
    }

    public static void main(String[] args)
    {
        new Geom3DTest().start();
    }
}
