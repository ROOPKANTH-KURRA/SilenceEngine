/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Sri Harsha Chilakapati
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.shc.silenceengine.tests;

import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.core.glfw.Cursor;
import com.shc.silenceengine.core.glfw.GLFW3;
import com.shc.silenceengine.core.glfw.Window;
import com.shc.silenceengine.graphics.Batcher;
import com.shc.silenceengine.graphics.Color;
import com.shc.silenceengine.graphics.opengl.GL3Context;
import com.shc.silenceengine.graphics.opengl.Program;
import com.shc.silenceengine.graphics.opengl.Shader;
import com.shc.silenceengine.graphics.opengl.Texture;
import com.shc.silenceengine.input.Keyboard;
import com.shc.silenceengine.utils.NativesLoader;
import com.shc.silenceengine.utils.TimeUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * This is the getting started example on the GLFW website ported to use the GLFW wrapper API.
 *
 * @author Sri Harsha Chilakapati
 */
public class GLFWTest
{
    private static Batcher batcher;

    public static void main(String[] args)
    {
        // Start with loading the natives
        NativesLoader.loadLWJGL();

        // Initialize GLFW library
        if (!GLFW3.init())
            return;

        Window.setHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        Window.setHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        Window.setHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, true);
        Window.setHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        Window.setHint(GLFW.GLFW_SAMPLES, 4);

        // Create a windowed mode window and its OpenGL context
        Window window = new Window(640, 480, "Hello World");

        // Make the window's context current
        window.makeCurrent();

        // Change cursor
        Texture cursorImage = Texture.fromResource("resources/cursor.png");
        Cursor cursor = new Cursor(cursorImage);

        window.setCursor(cursor);

        batcher = new Batcher();
        batcher.setVertexLocation(0);
        batcher.setColorLocation(1);
        batcher.setNormalLocation(2);
        batcher.setTexCoordLocation(3);

        Shader vs = new Shader(GL20.GL_VERTEX_SHADER);
        Shader fs = new Shader(GL20.GL_FRAGMENT_SHADER);

        vs.source(
                "#version 330 core                               \n" +

                // Just declare others for the sake of Batcher
                "layout (location = 0) in vec4 position;         \n" +
                "layout (location = 1) in vec4 color;            \n" +
                "layout (location = 2) in vec4 normal;           \n" +
                "layout (location = 3) in vec2 texCoords;        \n" +

                "out vec4 fsColor;                               \n" +

                "void main()                                     \n" +
                "{                                               \n" +
                "    gl_Position = position;                     \n" +
                "    fsColor = color;                            \n" +
                "}"
        );

        fs.source(
                "#version 330 core               \n" +

                "in vec4 fsColor;                \n" +
                "out vec4 fragColor;             \n" +

                "void main()                     \n" +
                "{                               \n" +
                "    fragColor = fsColor;        \n" +
                "}"
        );

        vs.compile();
        fs.compile();

        Program shaderProgram = new Program();

        shaderProgram.attach(vs);
        shaderProgram.attach(fs);
        shaderProgram.link();

        vs.dispose();
        fs.dispose();

        shaderProgram.use();

        window.setRefreshCallback(GLFWTest::glfwRefreshCallback);
        window.setSizeCallback(GLFWTest::glfwResizeCallback);

        // Initialize the input engine
        SilenceEngine.input.init();

        GLFW3.setSwapInterval(1);

        int frames = 0;
        int fps;
        double start, end;

        start = TimeUtils.currentSeconds();

        // Loop until the user closes the window
        while (!window.shouldClose())
        {
            // Tell the input engine to begin a new frame
            SilenceEngine.input.beginFrame();

            if (Keyboard.isClicked(Keyboard.KEY_ESCAPE))
                break;

            // Render here
            render();

            // Swap front and back buffers
            window.swapBuffers();

            // Poll for and process events
            GLFW3.pollEvents();

            // Tell the input engine to end the current frame
            SilenceEngine.input.endFrame();

            end = TimeUtils.currentSeconds();

            frames++;

            if (end - start > 1)
            {
                fps = frames;
                frames = 0;
                start = end;

                window.setTitle("FPS: " + fps);
            }
        }

        batcher.dispose();
        shaderProgram.dispose();

        window.destroy();
        cursor.destroy();

        GLFW3.terminate();
    }

    private static void glfwRefreshCallback(Window window)
    {
        render();
        window.swapBuffers();
    }

    private static void glfwResizeCallback(Window window, double width, double height)
    {
        GL3Context.viewport(0, 0, (float) width, (float) height);
        render();
        window.swapBuffers();
    }

    private static void render()
    {
        GL3Context.clear(GL11.GL_COLOR_BUFFER_BIT);

        batcher.begin();
        {
            batcher.vertex(+0.0f, +0.8f);
            batcher.color(Color.RED);

            batcher.vertex(+0.8f, -0.8f);
            batcher.color(Color.GREEN);

            batcher.vertex(-0.8f, -0.8f);
            batcher.color(Color.BLUE);
        }
        batcher.end();
    }
}
