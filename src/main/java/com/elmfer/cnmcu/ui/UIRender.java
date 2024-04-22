package com.elmfer.cnmcu.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.elmfer.cnmcu.mesh.MeshBuilder;
import com.elmfer.cnmcu.mesh.Meshes;
import com.elmfer.cnmcu.mesh.VertexFormat;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;

//Rendering implementation for UIs.
public class UIRender {
    public static final Matrix4f identity = new Matrix4f();
    public static MinecraftClient mc = MinecraftClient.getInstance();

    private static MeshBuilder meshBuilder = new MeshBuilder(VertexFormat.POS_COLOR);
    private static DrawContext drawContext;

    private static void arrangePositions(float positions[]) {
        if (positions[0] < positions[2]) {
            float i = positions[0];
            positions[0] = positions[2];
            positions[2] = i;
        }

        if (positions[1] < positions[3]) {
            float j = positions[1];
            positions[1] = positions[3];
            positions[3] = j;
        }
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        if (drawContext == null)
            return;

        drawContext.fill((int) left, (int) top, (int) right, (int) bottom, color);
        drawContext.draw();
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor,
            int endColor) {
        drawGradientRect(Direction.TO_BOTTOM, left, top, right, bottom, startColor, endColor);
    }

    public static void drawGradientRect(Direction direction, float left, float top, float right, float bottom,
            int startColor, int endColor) {
        Color c0 = new Color(startColor);
        Color c1 = new Color(endColor);

        float positions[] = { left, top, right, bottom };
        float verticies[] = { 0, 0, 0, 0, 0, 0, 0, 0 };
        arrangePositions(positions);
        direction.orient(left, top, right, bottom, verticies);

        Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
        VertexConsumer vertexConsumer = drawContext.getVertexConsumers().getBuffer(RenderLayer.getGui());
        vertexConsumer.vertex(matrix4f, verticies[0], verticies[1], 0).color(c1.r, c1.g, c1.b, c1.a).next();
        vertexConsumer.vertex(matrix4f, verticies[2], verticies[3], 0).color(c1.r, c1.g, c1.b, c1.a).next();
        vertexConsumer.vertex(matrix4f, verticies[4], verticies[5], 0).color(c0.r, c0.g, c0.b, c0.a).next();
        vertexConsumer.vertex(matrix4f, verticies[6], verticies[7], 0).color(c0.r, c0.g, c0.b, c0.a).next();

        drawContext.draw();
    }

    public static void drawHoveringText(String text, float x, float y) {
        drawHoveringText(Arrays.asList(text), x, y);
    }

    public static void drawHoveringText(List<String> lines, float x, float y) {
        // Not yet implemented
    }

    public static void drawVerticalLine(float x, float startY, float endY, int color) {
        if (endY < startY) {
            float i = startY;
            startY = endY;
            endY = i;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

    public static float getPartialTicks() {
        return mc.getTickDelta();
    }

    public static int getStringWidth(String text) {
        return mc.textRenderer.getWidth(text);
    }

    public static int getStringHeight() {
        return mc.textRenderer.fontHeight;
    }

    public static int getStringHeight(String text) {
        return mc.textRenderer.fontHeight;
    }

    public static int getCharWidth(int character) {
        String strChar = new String(Character.toChars(character));
        return mc.textRenderer.getWidth(strChar);
    }

    public static int getUIScaleFactor() {
        return (int) mc.getWindow().getScaleFactor();
    }

    public static int getWindowWidth() {
        return mc.getWindow().getWidth();
    }

    public static int getWindowHeight() {
        return mc.getWindow().getHeight();
    }

    public static int getUIwidth() {
        return mc.getWindow().getScaledWidth();
    }

    public static int getUIheight() {
        return mc.getWindow().getScaledHeight();
    }

    public static void drawString(String text, float x, float y, int color) {
        drawString(Anchor.TOP_LEFT, text, x, y, color);
    }

    public static void drawString(Anchor anchor, String text, float x, float y, int color) {
        float newPositions[] = { 0, 0 };
        anchor.anchor(text, x, y, newPositions);

//      mc.font.draw(identity, text, newPositions[0], newPositions[1], color);
        drawContext.drawText(mc.textRenderer, text, (int) newPositions[0], (int) newPositions[1], color, false);
//      drawContext.draw();
    }

    public static String getTextFormats(String src) {
        StringBuilder result = new StringBuilder();
        int[] unicodes = src.chars().toArray();

        for (int i = 0; i < unicodes.length; i++) {
            int unicode = unicodes[i];

            if (unicode == 167 && i + 1 < unicodes.length) {
                int formatKey = "0123456789abcdefklmnor".indexOf(String.valueOf((char) unicodes[i + 1]).toLowerCase());

                if (0 <= formatKey && formatKey < 22) {
                    result.appendCodePoint(167);
                    result.appendCodePoint(unicodes[i + 1]);
                }
                i++;
            }
        }

        return result.toString();
    }

    public static String splitStringToFit(String src, float maxWidth, String delimiter) {
        int[] unicodes = src.chars().toArray();

        float cursor = 0.0f;
        boolean boldStyle = false;

        int i = 0;
        int dlimLen = delimiter.length();
        int dlimCount = 0;
        int lstDlim = 0;
        for (i = 0; i < unicodes.length;) {
            int unicode = unicodes[i];

            StringBuilder dlimCheck = new StringBuilder();
            for (int j = i; j < Math.min(dlimLen + i, unicodes.length); j++)
                dlimCheck.appendCodePoint(unicodes[j]);

            if (0 < dlimLen && delimiter.equals(dlimCheck.toString())) {
                dlimCount++;
                lstDlim = i + dlimLen;
            }

            if (unicode == 167 && i + 1 < unicodes.length) {
                int formatKey = "0123456789abcdefklmnor".indexOf(String.valueOf((char) unicodes[i + 1]).toLowerCase());

                if (formatKey < 16)
                    boldStyle = false;
                else if (formatKey == 17)
                    boldStyle = true;
                i++;
            } else {
                cursor += getCharWidth(unicode) + (boldStyle ? 1 : 0);

                if (maxWidth < cursor) {
                    int numRead = i;
                    if (0 < dlimLen && 0 < dlimCount)
                        numRead = lstDlim;

                    StringBuilder result = new StringBuilder();
                    for (int j = 0; j < numRead; j++)
                        result.appendCodePoint(unicodes[j]);

                    return result.toString();
                }
            }
            i++;
        }

        return src;
    }

    public static void drawIcon(String iconKey, float x, float y, float scale, int color) {
        if (!Meshes.hasMesh(iconKey))
            return;

        Color c = new Color(color);

        meshBuilder.pushMatrix();
        meshBuilder.getModelView().translate(x, y, 0.0f);
        meshBuilder.getModelView().scale(scale, -scale, 1.0f);
        meshBuilder.setColorModulator(c.r / 255.0f, c.g / 255.0f, c.b / 255.0f, c.a / 255.0f);
        Meshes.get(iconKey).putMeshElements(meshBuilder);
        meshBuilder.popMatrix();
        meshBuilder.setColorModulator(1.0f, 1.0f, 1.0f, 1.0f);

        renderBatch();
    }

    public static void newFrame() {
        drawContext = new DrawContext(mc, mc.getBufferBuilders().getEntityVertexConsumers());
        meshBuilder.reset();
    }

    public static void renderBatch() {
        if (drawContext == null)
            return;

//      drawContext.draw();

        RenderSystem.enableBlend();
        RenderSystem.disableCull();

        ShaderProgram posColShader = GameRenderer.getPositionColorProgram();
        posColShader.modelViewMat.set(RenderSystem.getModelViewMatrix());
        posColShader.projectionMat.set(RenderSystem.getProjectionMatrix());
        posColShader.colorModulator.set(1.0f, 1.0f, 1.0f, 1.0f);
        posColShader.bind();
        meshBuilder.drawElements(GL11.GL_TRIANGLES);
        posColShader.unbind();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        meshBuilder.reset();
    }

    public static class Stencil {
        private static final int MAX_STACK_SIZE = 128;
        private static final List<StencilState> STATES_STACK = new ArrayList<>();

        static {
            STATES_STACK.add(new StencilState());
        }

        public static void enableTest() {
            GL11.glEnable(GL11.GL_STENCIL_TEST);
            getLast().testEnabled = true;
        }

        public static void disableTest() {
            GL11.glDisable(GL11.GL_STENCIL_TEST);
            getLast().testEnabled = false;
        }

        public static boolean isTestEnabled() {
            return getLast().testEnabled;
        }

        public static void enableWrite() {
            StencilState last = getLast();
            GL11.glStencilMask(last.mask);
            last.writeEnabled = true;
        }

        public static void disableWrite() {
            GL11.glStencilMask(0x00);
            getLast().writeEnabled = false;
        }

        public static void mask(int mask) {
            getLast().mask = mask;
        }

        public static int getMask() {
            return getLast().mask;
        }

        public static boolean isWritingEnabled() {
            return getLast().writeEnabled;
        }

        public static void clear() {
            GL11.glStencilMask(0xFF);
            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
            GL11.glStencilMask(getLast().mask);
        }

        public static void setFunction(int function, int reference) {
            StencilState last = getLast();
            GL11.glStencilFunc(function, reference, last.mask);
            last.function = function;
            last.referenceValue = reference;
        }

        public static int getFunction() {
            return getLast().function;
        }

        public static int getReferenceValue() {
            return getLast().referenceValue;
        }

        public static void setOperation(int fail, int zFail, int pass) {
            GL11.glStencilOp(fail, zFail, pass);

            StencilState last = getLast();
            last.stencilFailOperation = fail;
            last.zFailOperation = zFail;
            last.passOperation = pass;
        }

        public static int getFailOperation() {
            return getLast().stencilFailOperation;
        }

        public static int getZFailOperation() {
            return getLast().zFailOperation;
        }

        public static int getPassOperation() {
            return getLast().passOperation;
        }

        public static void pushStencilState() {
            if (STATES_STACK.size() == MAX_STACK_SIZE - 1)
                throw new RuntimeException("Exceeded max stencil stack size");

            STATES_STACK.add(new StencilState(getLast()));
            getLast().apply();
        }

        public static void popStencilState() {
            if (STATES_STACK.size() == 1)
                throw new RuntimeException("Popped stencil states too much");
            STATES_STACK.remove(getLast());

            getLast().apply();
        }

        public static void resetStencilState() {
            STATES_STACK.clear();
            STATES_STACK.add(new StencilState());

            getLast().apply();
        }

        private static StencilState getLast() {
            return STATES_STACK.get(STATES_STACK.size() - 1);
        }

        private static class StencilState {
            boolean testEnabled = false;
            boolean writeEnabled = false;
            int mask = 0xFF;

            int function = GL11.GL_ALWAYS;
            int referenceValue = 1;

            int stencilFailOperation = GL11.GL_KEEP;
            int zFailOperation = GL11.GL_KEEP;
            int passOperation = GL11.GL_KEEP;

            public StencilState() {
            }

            public StencilState(StencilState copy) {
                testEnabled = copy.testEnabled;
                writeEnabled = copy.writeEnabled;
                mask = copy.mask;
                function = copy.function;
                referenceValue = copy.referenceValue;
                stencilFailOperation = copy.stencilFailOperation;
                zFailOperation = copy.zFailOperation;
                passOperation = copy.passOperation;
            }

            void apply() {
                if (testEnabled)
                    GL11.glEnable(GL11.GL_STENCIL_TEST);
                else
                    GL11.glDisable(GL11.GL_STENCIL_TEST);

                if (writeEnabled)
                    GL11.glStencilMask(mask);
                else
                    GL11.glStencilMask(0x00);

                GL11.glStencilFunc(function, referenceValue, mask);
                GL11.glStencilOp(stencilFailOperation, zFailOperation, passOperation);
            }
        }
    }

    public static enum Direction {
        TO_LEFT, TO_RIGHT, TO_BOTTOM, TO_TOP;

        // Orient from the default TO_BOTTOM orientation
        private void orient(float left, float top, float right, float bottom, float verticies[]) {
            switch (this) {
            case TO_BOTTOM:
                verticies[0] = left;
                verticies[1] = bottom;

                verticies[2] = right;
                verticies[3] = bottom;

                verticies[4] = right;
                verticies[5] = top;

                verticies[6] = left;
                verticies[7] = top;
                return;
            case TO_LEFT:
                verticies[0] = left;
                verticies[1] = top;

                verticies[2] = left;
                verticies[3] = bottom;

                verticies[4] = right;
                verticies[5] = bottom;

                verticies[6] = right;
                verticies[7] = top;
                return;
            case TO_RIGHT:
                verticies[0] = right;
                verticies[1] = bottom;

                verticies[2] = right;
                verticies[3] = top;

                verticies[4] = left;
                verticies[5] = top;

                verticies[6] = left;
                verticies[7] = bottom;
                return;
            case TO_TOP:
                verticies[0] = right;
                verticies[1] = top;

                verticies[2] = left;
                verticies[3] = top;

                verticies[4] = left;
                verticies[5] = bottom;

                verticies[6] = right;
                verticies[7] = bottom;
                return;
            }
        }
    }

    public static enum Anchor {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT, MID_LEFT, CENTER, MID_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;

        private void anchor(String text, float x, float y, float newPosition[]) {
            int stringWidth = getStringWidth(text);
            int stringHeight = getStringHeight(text);

            switch (this) {
            case MID_LEFT:
            case CENTER:
            case MID_RIGHT:
                newPosition[1] = y - stringHeight / 2;
                break;
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                newPosition[1] = y - stringHeight;
                break;
            default:
                newPosition[1] = y;
                break;
            }

            switch (this) {
            case TOP_CENTER:
            case CENTER:
            case BOTTOM_CENTER:
                newPosition[0] = x - stringWidth / 2;
                break;
            case TOP_RIGHT:
            case MID_RIGHT:
            case BOTTOM_RIGHT:
                newPosition[0] = x - stringWidth;
                break;
            default:
                newPosition[0] = x;
                break;
            }
        }
    }

    private static class Color {
        int r, g, b, a;

        public Color(int intColor) {
            a = intColor >> 24 & 255;
            r = intColor >> 16 & 255;
            g = intColor >> 8 & 255;
            b = intColor & 255;
        }
    }
}