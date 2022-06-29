package cz.uhk.brabec.graphics.utils;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class TextHUD {

    private TextRenderer renderer;
    private Font font;

    public TextHUD(String fileName) {
        loadFont(fileName);
    }

    private void loadFont(String fileName) {
        try (InputStream stream = Resources.loadFile(fileName, ResourceType.TrueTypeFont)) {
            font = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Font.PLAIN, 24.0f);
            renderer = new TextRenderer(font, true, true);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    public void display(GLAutoDrawable glAutoDrawable, int x, int y, String text) {
        if (glAutoDrawable == null)
            return;
        GL2 gl = glAutoDrawable.getGL().getGL2();

        int shaderProgram = pushAll(glAutoDrawable);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glFrontFace(GL2.GL_CCW);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);

        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glViewport(0, 0, glAutoDrawable.getSurfaceWidth(),
                glAutoDrawable.getSurfaceHeight());

        renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        renderer.beginRendering(glAutoDrawable.getSurfaceWidth(),
                glAutoDrawable.getSurfaceHeight());
        renderer.draw(text, x, y);
        renderer.endRendering();

        popAll(glAutoDrawable, shaderProgram);
    }

    private int pushAll(GLAutoDrawable glDrawable) {
        if (glDrawable == null)
            return 0;
        GL2 gl = glDrawable.getGL().getGL2();

        int[] shaderProgram = new int[1];
        gl.glUseProgram(0);
        gl.glGetIntegerv(GL2.GL_CURRENT_PROGRAM, shaderProgram, 0);
        gl.glPushAttrib(GL2.GL_ENABLE_BIT);
        gl.glPushAttrib(GL2.GL_DEPTH_BUFFER_BIT);
        gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
        gl.glPushAttrib(GL2.GL_TEXTURE_BIT);
        gl.glPushAttrib(GL2.GL_POLYGON_BIT);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glDepthMask(false);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        return shaderProgram[0];
    }

    private void popAll(GLAutoDrawable glDrawable, int shaderProgram) {
        if (glDrawable == null)
            return;
        GL2 gl = glDrawable.getGL().getGL2();

        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glPopAttrib();
        gl.glPopAttrib();
        gl.glPopAttrib();
        gl.glPopAttrib();
        gl.glPopAttrib();
        gl.glUseProgram(shaderProgram);
    }
}
